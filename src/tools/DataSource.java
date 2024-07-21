package tools;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;
import install.DirectoryManager;
import utilities.SwiPredObject;

/**
 * DataSource represents where something came from
 * This is (primarily) used to keep track of what database a protein sequence came from
 * Note that PFAM *seems* to have the same sequences as Uniprot
 * 
 * @author Benjy Strauss
 *
 */

public enum DataSource implements SwiPredObject {
	CUSTOM, RCSB_FASTA, RCSB_PDB, NCBI, GENBANK, UNIPROT, 
	PFAM, DSSP, SWISSPROT, OTHER, UNSPECIFIED, UNIPARC;
	
	/**
	 * 
	 * @param src
	 * @return
	 */
	public static DataSource parse(char src) {
		return parse("" + src);
	}
	
	/**
	 * 
	 * @param arg
	 * @return
	 */
	public static DataSource parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		
		switch(arg) {
		case "r":
		case "rcsb":
		case "rcsb-fasta":			return RCSB_FASTA;
		case "pdb":
		case "rcsb-pdb":			return RCSB_PDB;
		case "p":
		case "pfam":				return PFAM;
		case "g":
		case "gen-bank":
		case "genbank":				return GENBANK;
		case "u":
		case "uniprot":
		case "uniprotkb":			return UNIPROT;
		case "uniparc":				return UNIPARC;
		case "d":
		case "dssp":				return DSSP;
		case "s":
		case "swiss-prot":
		case "swissprot":			return SWISSPROT;
		case "n":
		case "ncbi":				return NCBI;
		case "o":
		case "other":				return OTHER;		
		default:					throw new UnmappedEnumValueException("Fasta source not supported.");
		}
	}
	
	public static String[] manual() {
		String[] manual = {
				"'c' = Custom",
				"'d' = DSSP",
				"'g' = GenBank",
				"'o' = other",
				"'p' = PFAM",
				"'r' = RCSB-PDB",
				"'s' = SwissProt",
				"'u' = UniprotKB"
		};
		
		return manual;
	}
	
	public String fastaFolder() { 
		switch(this) {
		case RCSB_FASTA:		return DirectoryManager.FILES_FASTA_RCSB;
		case RCSB_PDB:			return DirectoryManager.FILES_FASTA_PDB;
		case UNSPECIFIED:		return DirectoryManager.FILES_FASTA + "/other";
		default:				return DirectoryManager.FILES_FASTA + "/" + toString().toLowerCase();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String fastaFolderPref() { return fastaFolder() + "/"; }
	
	public String toString() {
		String retval = super.toString();
		retval = retval.toLowerCase();
		retval = retval.replaceAll("_", "-");
		return retval;
	}

	/**
	 * A "Hard mapping" is defined where each of the residues is guaranteed to
	 * be at the correct index upon being read in
	 * @return if the file is a hard mapping
	 */
	public boolean hardMapping() {
		switch(this) {
		case RCSB_PDB:
		case UNIPROT:
		case DSSP:				return true;
		default:				return false;
		}
	}

	public boolean hasInsertCodes() {
		switch(this) {
		case RCSB_PDB:
		case DSSP:				return true;
		default:				return false;
		}
	}
}
