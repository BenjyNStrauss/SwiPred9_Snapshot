package tools;

import java.util.Objects;

import assist.exceptions.NotYetImplementedError;
import biology.amino.BioMolecule;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
//import biology.tools.BioTools;
import chem.AminoAtom;
import install.DirectoryManager;
//import setup.installation.FileManager;
import system.SwiPred;
import tools.download.fasta.FastaDownloader;
import utilities.LocalToolBase;
import utilities.exceptions.LookupException;

/**
 * Base class used to download data
 * Everything in this class is static, there is no benefit from extending and making an instance of this class
 * 
 * @author Benjy Strauss
 *
 */

public abstract class Lookup extends LocalToolBase {
	public static final String NULL_ID = "Error: Null Chain ID.";
	public static final String NULL_PROTEIN = "Error: Protein not specified.";
	public static final String NULL_CHAIN = "Error: Protein chain not specified.";
	
	public static final String DSSP_EXT = ".dssp";
	protected static final String PDB_EXT = ".pdb";
	
	public static final String RUN_BLAST_ENTROPY = DirectoryManager.FILES_TOOLS_BLAST + "/bin/blastall";
	public static final String myDriver = "org.sqlite.JDBC";
	//public static final String jbioDBURL = "jdbc:sqlite:"+ FileManager.CHARGE_DB_PATH;
	
	public static final int REGION_MATCH_DEFAULT = 10;
	
	public static final int N = 0;
	public static final int HN = 1;
	public static final int Cα = 2;
	public static final int Cβ = 3;
	public static final int CP = 4;
	public static final int O = 5;
	
	public static final String[] ATOM_CODES = {
			AminoAtom.AMINO_N,  AminoAtom.AMINO_HN, AminoAtom.AMINO_Cα,
			AminoAtom.AMINO_Cβ, AminoAtom.AMINO_CP, AminoAtom.AMINO_O };
	
	public static String getFastaPath(ChainID id) throws LookupException {
		Objects.requireNonNull(id, "Error, no id specified!");
		return getFastaPath(id, SwiPred.getShell().fastaSrc());
	}
	
	public static String getFastaPath(ChainID id, DataSource source) throws LookupException {
		Objects.requireNonNull(id, "Chain ID cannot be null!");
		Objects.requireNonNull(source, "Source cannot be null!");
		
		StringBuilder builder = new StringBuilder(source.fastaFolderPref());
		switch(source) {
		case RCSB_FASTA:
		case GENBANK:		builder.append(id.uniqueSaveID() + TXT);	break;
		case RCSB_PDB:		builder.append(id.protein() + PDB_EXT);		break;
		case UNIPROT:
			if(id.uniprot() == null) {
				getUniprotFromRCSB(id);
			}
			
			if(id.uniprot() == null) { return null; }
			builder.append(id.uniprot() + TXT);
		break;
		case DSSP:			builder.append(id.protein().toLowerCase() + DSSP);		break;
		default:			throw new NotYetImplementedError("Fasta location is not implemented yet for: " + source);
		}
		
		return builder.toString();
	}
	
	/**
	 * Alerts the user if 2 residue types (that should be the same) are different
	 * if the residues are different: print a warning
	 * @param chain: 
	 * @param newChain:
	 * @param index: the index of both chains to check
	 */
	public static final String checkResidueTypeAt(ProteinChain chain, ProteinChain newChain, int index) {
		//qp("Checking Residue Type");
		BioMolecule a1 = chain.get(index);
		BioMolecule a2 = (index < newChain.size()) ?  newChain.get(index) : null;
		char c1 = '~';
		char c2 = '~';
		
		if(a1 != null) { c1 = a1.toChar(); } else { return null; }
		if(a2 != null) { c2 = a2.toChar(); } else { return null; }
		
		if(c1 != c2) {
			String msg = "WARNING: Index[" + String.format("%03d", index) + "] " + chain.name() + "(" + chain.getMetaData().source()
				+ ") and " + newChain.name() + "(" + newChain.getMetaData().source() + ") {" + c1 + " vs " + c2 + "}";
			qpl(msg);
			return msg;
		}
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @param src
	 */
	public static void detectDownloadErrors(ChainID id, DataSource src) {
		Objects.requireNonNull(id, "Error: I cannot download based on a null ID!");
		switch(src) {
		case GENBANK:
		case NCBI:
		case RCSB_FASTA:
			Objects.requireNonNull(id.chain(), "Error: I cannot download based on a null chain!");
		case DSSP:
		case RCSB_PDB:
			Objects.requireNonNull(id.protein(), "Error: I cannot download based on a null protein!");
			break;
		case UNIPROT:
			Objects.requireNonNull(id.chain(), "Error: I cannot download based on a uniprot code!");
		case PFAM:
		case SWISSPROT:
		case UNSPECIFIED:
		default:
			error("Error: I don't know how to download Fastas from "+ src);
		}
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws LookupException 
	 */
	public static void getUniprotFromRCSB(ChainID id) throws LookupException {
		Objects.requireNonNull(id, NULL_ID);
		Objects.requireNonNull(id.protein(), NULL_PROTEIN);
		Objects.requireNonNull(id.chain(), NULL_CHAIN);
		
		String pdbFile = FastaDownloader.verify(id, DataSource.RCSB_PDB, 1);
		if(pdbFile == null || !fileExists(pdbFile)) {
			throw new LookupException("Error!  No PDB file found for "+id + " (Verify Failed!)");
		}
		
		String dbref = null;
		for(String line: getFileLines(pdbFile)) {
			String[] fields = line.split("\\s+");
			if(fields[0].equals("DBREF")) {
				if(!fields[2].equals(id.chain())) { 
					continue;
				} else {
					dbref = line;
				}
			}
		}
		
		if(dbref != null) {
			String[] fields = dbref.split("\\s+");
			switch(fields[5]) {
			case "UNP":
				id.setUniprot(fields[6]);
				return;
			case "GB": //we found a genbank ID, might as well record it…
				try {
					id.setGI(fields[6]);
				} catch (NumberFormatException nfe) {
					qerr("Found GI for " + id.toString() + "; but GI is not a number: " + fields[6]);
				}
				
				throw new LookupException("Warning!  Uniprot ID not found in PDB file for: " +
						id + " (found genbank ID instead)");
			}
		} else {
			throw new LookupException("Warning!  Uniprot ID not found in PDB file for: " + id);
		}
	}
}
