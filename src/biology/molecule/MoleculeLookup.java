package biology.molecule;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import assist.util.LabeledHash;
import assist.util.LabeledSet;
import biology.molecule.types.*;
import utilities.LocalToolBase;

/**
 * Intended to be a combination of all other Ligand Databases
 * @author Benjamin Strauss
 *
 */

public final class MoleculeLookup extends LocalToolBase {
	private static final String LIGAND_LOOKUP_URL = "https://www.rcsb.org/ligand/";
	private static final String MOLECULE_NAME = "<h4 class=\"break\" id=\"moleculeName\">";
	private static final String CLOSE_H4 = "</h4>";
	
	public static final int RECURSION_LIMIT = Short.MAX_VALUE;
	private static final int MAX_ENUM_VAL_LENGTH = 32;
	
	public static final LabeledHash<String, Integer> MISSED_CODES = new LabeledHash<String, Integer>();
	private static final LabeledHash<String, String> MISSED_CODE_NAMES = new LabeledHash<String, String>();
	
	public static boolean log_new_ligands = true;
	
	private static final LabeledHash<String, MoleculeType> TABLE = new LabeledHash<String, MoleculeType>() {
		private static final long serialVersionUID = 1L;
		{
			for(AminoType amino: AminoType.values())				{ put( amino.code,  amino); }
			for(AminoNonPolymer amino: AminoNonPolymer.values())	{ put( amino.code,  amino); }
			for(Ligand ligand: Ligand.values())						{ put(ligand.code, ligand); }
			for(NucleoType nucleo: NucleoType.values())				{ put(nucleo.code, nucleo); }
			for(AtomType atom: AtomType.values())					{ put(  atom.code,   atom); }
			for(AminoCombination amino: AminoCombination.values())	{ put( amino.code,  amino); }
			
			put("G",   NucleoType.GUANOSINE_5_1P);
			put("C",   NucleoType.CYTIDINE_5_1P);
			put("U",   NucleoType.URIDINE_5_1P);
			put("I",   NucleoType.INOSINIC_ACID);
			
			put("DG",  NucleoType.DEOXYGUANOSINE_2_5_1P);
			
			put("TMP", NucleoType.THYMIDINE_5_1P);
			put("UMP", NucleoType.DEOXYURIDINE_2_5_1P);
		}
	};
	
	private static final LabeledHash<Integer, MoleculeType> INT_TABLE = new LabeledHash<Integer, MoleculeType>() {
		private static final long serialVersionUID = 1L;
		{
			for(AminoType amino: AminoType.values())				{ put( amino.pubChem_id,  amino); }
			for(AminoCombination amino: AminoCombination.values())	{ put( amino.pubChem_id,  amino); }
			
			put(97963, AminoType.O_Methyl_Serine);
		}
	};
	
	private static final LabeledHash<Character, MoleculeType> CHAR_TABLE = new LabeledHash<Character, MoleculeType>() {
		private static final long serialVersionUID = 1L;
		{
			for(AminoType amino: AminoType.values()) {
				if(amino.utf16_letter != '?') {
					put(amino.utf16_letter,  amino);
				}
			}
		}
	};
	
	private static final LabeledSet<MoleculeType> INCLUDE_IN_PDBSEQ = new LabeledSet<MoleculeType>() {
		private static final long serialVersionUID = 1L;
		{
			add(Ligand.Acetyl);
		}
	};
	
	private MoleculeLookup() { }
	
	public static MoleculeType get(String code) { return TABLE.get(code); }
	
	public static MoleculeType parse(String pdb_code) {
		Objects.requireNonNull(pdb_code, "Code was null.");
		if(pdb_code.length() == 0) { throw new NullPointerException("String had no contents."); }
		
		if(recognized(pdb_code)) {
			return TABLE.get(pdb_code);
		} else {
			if(!MISSED_CODES.containsKey(pdb_code)) {
				if(log_new_ligands) {
					logCode(pdb_code);
				} else {
					qp("Code: \""+pdb_code+"\" is unknown to SwiPred, please look up at \""+LIGAND_LOOKUP_URL+pdb_code+"\"");	
				}
				MISSED_CODES.put(pdb_code, 1);
			} else {
				int occ = MISSED_CODES.get(pdb_code);
				MISSED_CODES.put(pdb_code, occ+1);
			}
			
			return Ligand.UNKNOWN;
		}
	}
	
	public static MoleculeType parse(int pubChemID) { return INT_TABLE.get(pubChemID); }
	
	public static MoleculeType parse(char letter) { return CHAR_TABLE.get(letter); }
	
	public static AminoType parseAmino(char code) { 
		MoleculeType mType = parse(code);
		if(mType instanceof AminoType) {
			return (AminoType) mType;
		} else {
			return AminoType.INVALID;
		}
	}
	
	public static boolean recognized(String pdb_code) { return TABLE.containsKey(pdb_code); }
	
	public static boolean includeInSequence(MoleculeType type) {
		if(type instanceof AminoType) { return true; }
		if(type instanceof AminoCombination) { return true; }
		if(INCLUDE_IN_PDBSEQ.contains(type)) { return true; }
		return false;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static AminoType standardize(AminoType type) {
		int limit = RECURSION_LIMIT;
		while(!isStandard(type)) {
			MoleculeType mt = TABLE.get(type.baseForm);
			if(mt instanceof AminoType) {
				type = (AminoType) mt;
			} else {
				qerr("Warning: AminoType "+type+" standardizes to a non-amino acid!");
				return AminoType.OTHER;
			}
			--limit;
			if(limit == 0) { break; }
		}
		
		return (isStandard(type)) ? type : AminoType.OTHER;
	}
	
	public static MoleculeType standardize(AminoNonPolymer type) {
		int limit = RECURSION_LIMIT;
		while(!isStandard(type)) {
			MoleculeType mt = TABLE.get(type.baseForm);
			if(mt instanceof AminoNonPolymer) {
				type = (AminoNonPolymer) mt;
			} else if(mt instanceof AminoType) {
				return standardize ((AminoType) mt);
			} else {
				qerr("Warning: AminoType "+type+" standardizes to a non-amino acid!");
				return AminoType.OTHER;
			}
			--limit;
			if(limit == 0) { break; }
		}
		
		return (isStandard(type)) ? type : AminoType.OTHER;
	}
	
	/**
	 * 
	 * @param type
	 * @return true if type is a standard amino acid
	 */
	public static boolean isStandard(MoleculeType type) {
		if(type instanceof AminoType) {
			return ((AminoType) type).baseForm.equals(((AminoType) type).code);
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	private static boolean logCode(String code) {
		//first have to download the files...
		InputStream url;
		
		ArrayList<String> buffer = new ArrayList<String>();
		Scanner ligandScanner = null;
		
		try {
			//qp("url = " + RCSB_URL+id.protein()+DISPLAY);
			url = new URL(LIGAND_LOOKUP_URL+code).openStream();
			
			ligandScanner = new Scanner(url);
			while(ligandScanner.hasNextLine()){
				buffer.add(ligandScanner.nextLine());
			}
		} catch(java.net.UnknownHostException uhe) {
			error("UnknownHostException " + uhe.getMessage() + " " + code);
		} catch (IOException e1) {
			error("IOException: " + e1.getMessage() + " for code=" + code);
			//TODO: clear line below?
		} finally {
			if(ligandScanner != null) {
				ligandScanner.close();
			}
		}
		
		String target = null;
		
		for(String str: buffer) {
			if(str.contains(MOLECULE_NAME)) {
				target = str.substring(str.indexOf(MOLECULE_NAME));
				break;
			}
		}
		
		if(target == null) { 
			qp("Code: \""+code+"\" is unknown to SwiPred and cannot be automatically downloaded from the PDB,\n\tplease look up at \""+LIGAND_LOOKUP_URL+code+"\"");
			return false;
		}
		
		int fin = target.indexOf(CLOSE_H4);
		if(fin == -1) {
			qp("Code: \""+code+"\" is unknown to SwiPred and cannot be automatically downloaded from the PDB\n "
					+ "due to an HTML anomaly please look up at \""+LIGAND_LOOKUP_URL+code+"\"");
			return false;
		}
		
		target = target.substring(MOLECULE_NAME.length(), fin);
		//target is now the wanted value…
		target = target.toLowerCase();
		MISSED_CODE_NAMES.put(code, target);
		
		StringBuilder enumValNameBuilder = new StringBuilder();
		boolean representable = true;
		if(target.contains("'")) { representable = false; }
		if(target.contains("~")) { representable = false; }
		if(target.contains("(")) { representable = false; }
		if(target.contains("[")) { representable = false; }
		if(target.contains("/")) { representable = false; }
		if(target.length() > MAX_ENUM_VAL_LENGTH) { representable = false; } 
		
		if(representable) {
			String metaTarget = target.replaceAll("-", "_");
			metaTarget = metaTarget.replaceAll(",", "\\$");
			metaTarget = metaTarget.replaceAll(" ", "__");
			char[] array = metaTarget.toCharArray();
			if(Character.isDigit(array[0])) { enumValNameBuilder.append('_'); }
			enumValNameBuilder.append(Character.toUpperCase(array[0]));
			for(int index = 1; index < array.length; ++index) {
				if(array[index-1] == '_' || array[index-1] == ' ') {
					enumValNameBuilder.append(Character.toUpperCase(array[index]));
				} else {
					enumValNameBuilder.append(array[index]);
				}
			}
			enumValNameBuilder.append("(\""+code.toUpperCase()+"\"),");
		} else {
			enumValNameBuilder.append("__"+code.toUpperCase()+"__(\""+code.toUpperCase());
			enumValNameBuilder.append("\", \"");
			
			char[] array = target.toCharArray();
			enumValNameBuilder.append(Character.toUpperCase(array[0]));
			for(int index = 1; index < array.length; ++index) {
				enumValNameBuilder.append((!Character.isAlphabetic(array[index-1])) ? Character.toUpperCase(array[index]) : array[index]);
			}
			enumValNameBuilder.append("\"),");
		}
		
		appendFileLines("files/logs/new-pdb-codes.txt", enumValNameBuilder.toString());
		return true;
	}
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static String toProperCase(String arg) {
		char[] array = arg.toLowerCase().toCharArray();
		StringBuilder enumValNameBuilder = new StringBuilder();
		
		enumValNameBuilder.append(Character.toUpperCase(array[0]));
		for(int index = 1; index < array.length; ++index) {
			enumValNameBuilder.append((!Character.isAlphabetic(array[index-1])) ? Character.toUpperCase(array[index]) : array[index]);
		}
		return enumValNameBuilder.toString();
	}
	
	/**
	 * Replaces the letters of modified residues with their more standard counterparts
	 * @param sequence
	 * @return
	 */
	public static String proteinogenify(String sequence) {
		char[] seq = sequence.toCharArray();
		for(int ii = 0; ii < seq.length; ++ii) {
			while((seq[ii] < 65 || seq[ii] > 90) && seq[ii] != '_') {
				seq[ii] = parseAmino(seq[ii]).standardize().letter;
			}
		}
		return new String(seq);
	}
}
