package biology.molecule.types;

import utilities.SwiPredObject;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public interface MoleculeType extends SwiPredObject {
	final PeptideLink $_  = PeptideLink.NON_LINK;
	final PeptideLink $D  = PeptideLink.D;
	final PeptideLink $L  = PeptideLink.L;
	final PeptideLink $DL = PeptideLink.DL;
	final PeptideLink $P  = PeptideLink.PEPTIDE_LIKE;
	final PeptideLink $DNA  = PeptideLink.DNA;
	final PeptideLink $RNA  = PeptideLink.RNA;
	
	final PeptideLink $DS  = PeptideLink.D_SACC;
	
	final int NO_PUBCHEM = -3;
	final int NEED_PUBCHEM = -4;
	
	final char NOT_PROVIDED = '#';
	
	final String ALANINE 		= "ALA";
	final String CYSTEINE		= "CYS";
	final String ASPARTIC_ACID	= "ASP";
	final String GLUTAMIC_ACID	= "GLU";
	final String PHENYLALANINE	= "PHE";
	final String GLYCINE		= "GLY";
	final String HISTIDINE		= "HIS";
	final String ISOLEUCINE		= "ILE";
	final String LYSINE			= "LYS";
	final String LEUCINE		= "LEU";
	final String METHIONINE		= "MET";
	final String ASPARAGINE		= "ASN";
	final String PROLINE		= "PRO";
	final String GLUTAMINE		= "GLN";
	final String ARGININE		= "ARG";
	final String SERINE			= "SER";
	final String THREONINE		= "THR";
	final String VALINE			= "VAL";
	final String TRYPTOPHAN		= "TRP";
	final String TYROSINE		= "TYR";
	
	final String BETA_ALANINE	= "BAL";
	final String NORLEUCINE		= "NLE";
	final String NORVALINE		= "NVA";
	final String ORNITHINE		= "ORN";
	final String SELENOCYSTEINE = "SEC";
	final String SELENOMETHIONINE = "SME";
	
	final String D_ALANINE		= "DAL";
	final String D_CYSTEINE		= "DCY";
	final String D_ASPARTIC_ACID= "DAS";
	final String D_GLUTAMIC_ACID= "DGL";
	final String D_PHENYLALANINE= "DPN";
	final String D_HISTIDINE	= "DHI";
	final String D_ISOLEUCINE	= "DIL";
	final String D_LYSINE		= "DLY";
	final String D_LEUCINE		= "DLE";
	final String D_METHIONINE	= "MED";
	final String D_ASPARAGINE	= "DSG";
	final String D_PROLINE		= "DPR";
	final String D_GLUTAMINE	= "DGN";
	final String D_ARGININE		= "DAR";
	final String D_SERINE		= "DSN";
	final String D_THEORINE		= "DTH";
	final String D_VALINE		= "DVA";
	final String D_TRYPTOPHAN	= "DTR";
	final String D_TYROSINE		= "DTY";
	
	final String HOMOCYSTEINE	= "HCS";
	final String SARCOSINE		= "SAR";
	
	final String THYRONINE		= "ŤŤŤ";
	
	final String __OTHER__		= "UNK";
	
	final char WATER = '☵';
	
	public final char DNA_A = 'Ȧ';
	public final char DNA_C = 'Ċ';
	public final char DNA_G = 'Ġ';
	public final char DNA_T = 'Ṫ';
	public final char DNA_U = 'Ụ'; //can't find 'U' with dot above
	public final char DNA_I = 'İ';
	public final char DNA_X = 'Ż';
	public final char DNA_OTHER = 'Ṅ';
	public final char DNA_MULTI = 'Ẇ';
	
	public default char toChar() { return 'X'; }
	
	//char letter();
	//char utf_16_letter();
	
	public String toCode();
	
	public default String fixString(String str) {
		String retVal = str;
		retVal = retVal.replaceAll("\\$", ",");
		retVal = retVal.replaceAll("__", " ");
		retVal = retVal.replaceAll("_", "-");
		//retVal = retVal.replaceAll("-Acid", " Acid");
		if(retVal.startsWith("-")) {
			retVal = retVal.substring(1);
		}
		
		retVal = retVal.replaceAll("$OP$", "(");
		retVal = retVal.replaceAll("$CP$", ")");
		
		return retVal.trim();
	}
	
}
