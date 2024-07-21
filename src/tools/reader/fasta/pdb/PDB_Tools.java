package tools.reader.fasta.pdb;

import java.io.File;

import assist.base.Assist;
import assist.util.LabeledHash;
import assist.util.LabeledSet;
import biology.amino.AminoAcid;
import biology.amino.BioMolecule;
import biology.amino.InsertCode;
import biology.molecule.types.AminoType;
import biology.molecule.types.MoleculeType;
import install.DirectoryManager;
import tools.DataSource;
import tools.download.fasta.IncompleteFileException;
import tools.reader.fasta.SequenceReaderBase;
import tools.reader.fasta.exceptions.PDBSanityCheckException;

/**
 * https://files.wwpdb.org/pub/pdb/doc/format_descriptions/Format_v33_Letter.pdf
 * @author Benjamin Strauss
 *
 */

public abstract class PDB_Tools extends SequenceReaderBase {
	public static final DataSource MY_SOURCE = DataSource.RCSB_PDB;
	public static final String PDB_PATH = DirectoryManager.FILES_FASTA_PDB + "/";
	
	protected static final String ATOM = "ATOM";
	protected static final String DBREF = "DBREF";
	protected static final String DBREF1 = "DBREF1";
	protected static final String DBREF2 = "DBREF2";
	protected static final String ENGINEERED_MUTATION = "ENGINEERED MUTATION";
	protected static final String EXPRESSION = "EXPRESSION";
	protected static final String HEADER = "HEADER";
	protected static final String HELIX = "HELIX";
	protected static final String HETATM = "HETATM";
	protected static final String HETNAM = "HETNAM";
	protected static final String HETSYN = "HETSYN";
	protected static final String INSERTION = "INSERTION";
	protected static final String REMARK = "REMARK";
	protected static final String SEQADV = "SEQADV";
	protected static final String SEQRES = "SEQRES";
	protected static final String SHEET = "SHEET";
	protected static final String SSBOND = "SSBOND";
	protected static final String TAG = "TAG";
	protected static final String TER = "TER";
	protected static final String UNP = "UNP";
	
	private static final LabeledHash<String, int[]> INDICES = new LabeledHash<String, int[]>() {
		private static final long serialVersionUID = 1L;
		{
			put(ATOM,	  new int[]{6, 11, 16, 17, 20, 22, 26, 27, 38, 46, 54, 60, 66, 78, 80});
			put("AUTHOR", new int[]{6, 10, 79});
			put("COMPND", new int[]{6, 10, 80});
			put(DBREF, 	  new int[]{6, 11, 13, 18, 19, 24, 25, 32, 41, 54, 60, 61, 67, 68});
			put(DBREF1,	  new int[]{6, 11, 13, 18, 19, 24, 25, 32, 67});
			put(DBREF2,	  new int[]{6, 11, 13, 40, 55, 67});
			put("EXPDTA", new int[]{6, 10, 79});
			put("HEADER", new int[]{6, 50, 59, 66});
			put(HETATM,	  new int[]{6, 11, 16, 17, 20, 22, 26, 27, 38, 46, 54, 60, 66, 78, 80});
			put(HETNAM,	  new int[]{6, 10, 14, 70});
			put(HETSYN,	  new int[]{6, 10, 14, 70});
			put("KEYWDS", new int[]{6, 10, 79});
			put("REVDAT", new int[]{6, 10, 12, 22, 27, 32, 45, 52, 59, 66});
			put(SEQADV,	  new int[]{6, 11, 15, 17, 22, 23, 28, 38, 42, 48, 70});
			//last index ends at 70, however not needed for splitting...
			put(SEQRES,	  new int[]{6, 10, 12, 17, 22, 26, 30, 34, 38, 42, 46, 50, 54, 58, 62, 66});
			put("SOURCE", new int[]{6, 10, 79});
			put(SSBOND,   new int[]{6, 10, 14, 16, 21, 22, 28, 30, 35, 36, 65, 72, 78});
			put(TER,	  new int[]{6, 11, 16, 17, 20, 22, 26, 27});
			put("TITLE ", new int[]{6, 10, 80});
			
		}
	};
	
	private static final LabeledHash<String, int[]> REMARK_INDICES = new LabeledHash<String, int[]>() {
		private static final long serialVersionUID = 1L;
		{
			put("465", new int[]{6, 10, 14, 18, 20});
			put("470", new int[]{6, 10, 14, 18, 20, 26});
		}
	};
	
	private static final LabeledSet<String> DBREFS = new LabeledSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(DBREF);		add(DBREF1);	add(DBREF2);
		}
	};
	
	//HETATM is NOT supposed to be here
	private static final LabeledSet<String> HETS = new LabeledSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(HETNAM);	add(HETSYN);
		}
	};
	
	//HETATM is supposed to be here
	private static final LabeledSet<String> USEFUL = new LabeledSet<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(ATOM);		add(HETATM);
			add(SEQADV);	add(SEQRES);	add(REMARK);
		}
	};
	
	protected static final String DEBUG_PDB = "❀";
	
	protected static final boolean DEBUG_VIEW_PROGRESS = false;
	public static boolean insertion_codes_in_uniprot = true;
	
	//We are fairly certain of these indices
	private static final int[] PDB_INDICES = {
			6, 12, 17, 20, 22, 27, 38, 46, 54, 60, 66
	};
	
	private static final StringBuilder QUICK_BUILDER = new StringBuilder();
	
	protected static final String padFront(InsertCode insertCode, int i) {
		int spaces = i-insertCode.toString().length();
		QUICK_BUILDER.setLength(0);
		for(int ii = 0; ii < spaces; ++ii) {
			QUICK_BUILDER.append(" ");
		}
		QUICK_BUILDER.append(insertCode.toString());
		return QUICK_BUILDER.toString();
	}
	
	protected static final BioMolecule makeAmino(MoleculeType type) {
		if(type instanceof AminoType) {
			return new AminoAcid((AminoType) type);
		} else {
			return new BioMolecule(type);
		}
	}
	
	/**
	 * Parse character by character
	 * @param fields
	 * @return
	 */
	@Deprecated
	protected static String[] fixFields(String line, File fasta) {
		String[] newFields = null;
		
		try {
			newFields = Assist.splitStringOnIndicies(line, PDB_INDICES);
		} catch (StringIndexOutOfBoundsException SIOOBE) {
			error("Parsing failed on line: ");
			error("\t"+ line);
			error("\t of PDB file: "+ fasta.getPath());
			throw new IncompleteFileException();
		}
		
		for(int ii = 0; ii < newFields.length; ++ii) {
			newFields[ii] = newFields[ii].trim();
		}
		
		sanityCheck(fasta, line, newFields);
		
		return newFields;
	}
	
	/**
	 * Parse character by character
	 * @param fields
	 * @return
	 */
	public static String[] getFields(String line) {
		if(line.startsWith(REMARK)) { return getFieldsRemark(line); }
		
		int[] array = INDICES.get(line.substring(0, 6).trim());
		
		if(array == null) { error(line); }
		
		String[] fields = Assist.splitStringOnIndicies(line, array);
		for(int index = 0; index < fields.length; ++index) {
			fields[index] = fields[index].trim();
		}
		
		return fields;
	}
	
	protected static String[] getFieldsRemark(String line) {
		int[] array = REMARK_INDICES.get(line.substring(7, 10).trim());
		
		String[] fields = Assist.splitStringOnIndicies(line, array);
		for(int index = 0; index < fields.length; ++index) {
			fields[index] = fields[index].trim();
		}
		
		return fields;
	}

	protected static boolean isUseful(String line) {
		if(line.length() < 6) {
			return USEFUL.contains(line.trim());
		} else {
			return USEFUL.contains(line.substring(0, 6).trim());
		}
	}
	
	protected static boolean isDBREF(String line) {
		if(line.length() < 6) {
			return DBREFS.contains(line.trim());
		} else {
			return DBREFS.contains(line.substring(0, 6).trim());
		}
	}
	
	protected static boolean isHet(String line) {
		if(line.length() < 6) {
			return HETS.contains(line.trim());
		} else {
			return HETS.contains(line.substring(0, 6).trim());
		}
	}
	
	/**
	 * Perform a sanity check on a split of the line – ensure that none of 
	 * the fields have spaces
	 * 
	 * @param fasta
	 * @param line
	 * @param newFields
	 */
	@Deprecated
	private static void sanityCheck(File fasta, String line, String[] newFields) {
		//do a sanity check on the split
		for(int ii = 0; ii < newFields.length; ++ii) {
			if(newFields[ii].contains(" ")) {
				// && newFields[ii].replaceAll("\\s+", "").length() == 3
				if(ii == 2) { continue; }
				throw new PDBSanityCheckException(fasta.getPath(), line, newFields, ii);
			}
		}
	}
}
