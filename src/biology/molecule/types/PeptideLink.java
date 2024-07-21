package biology.molecule.types;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum PeptideLink {
	NON_LINK("NON-POLYMER"),
	DL("PEPTIDE LINKING"), 
	
	L("L-PEPTIDE LINKING"), 
	D("D-PEPTIDE LINKING"),
	
	L_SACC("L-SACCHARIDE"),
	D_SACC("D-SACCHARIDE"),
	DL_SACC("SACCHARIDE"),
	
	//L_SACC_ALPHA("L-SACCHARIDE, ALPHA LINKING"),
	D_SACC_ALPHA("D-SACCHARIDE, ALPHA LINKING"),
	
	L_SACC_β("L-SACCHARIDE, BETA LINKING"),
	D_SACC_β("D-SACCHARIDE, BETA LINKING"),
	
	Lβ("L-BETA-PEPTIDE, C-GAMMA LINKING"),
	L_AMINO_TERMINUS("L-PEPTIDE NH3 AMINO TERMINUS"),
	
	PEPTIDE_LIKE("PEPTIDE-LIKE"),
	
	DNA("DNA LINKING"),
	DNA_5("DNA OH 5 PRIME TERMINUS"),
	RNA("RNA LINKING"), 
	;
	
	public final String pdbText;
	
	private PeptideLink(String text) {
		pdbText = text;
	}
}
