package modules.encode.tokens;

import biology.molecule.types.AminoType;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

@SuppressWarnings("unused")
public final class TokenLoss extends LocalToolBase {
	
	private static final char[] HELIX = { 'G', 'H', 'I', 'P' };
	private static final char[] SHEET = { 'B', 'E' };
	private static final char[] OTHER = { 'C', 'S', 'T' };
	
	private TokenLoss() { }
	
	/**
	 * 
	 * @param predicted
	 * @param actual
	 * @return
	 */
	public static double getLoss(AminoToken predicted, AminoToken actual) {
		double loss = 0.0;
		
		String rawPredType = predicted.toString().split("-")[0];
		String rawRealType =    actual.toString().split("-")[0];
		
		char rawPred2nd = predicted.toString().split("-")[1].charAt(0);
		char rawReal2nd =    actual.toString().split("-")[1].charAt(0);
		
		AminoType predType = (rawPredType.equals("LIG")) ? AminoType.INVALID : AminoType.parse(rawPredType);
		AminoType realType = (rawRealType.equals("LIG")) ? AminoType.INVALID : AminoType.parse(rawRealType);
		
		if(realType != predType) {
			if(predType.standardize() == realType.standardize()) {
				loss = 0.2;
			} else if (predType.clazz == realType.clazz) {
				loss = 0.4;
			} else {
				loss = 0.6;
			}
		}
		
		if(rawPred2nd != rawReal2nd) {
			loss += arrayContainsBoth(rawPred2nd, rawReal2nd) ? 0.2 : 0.4;
		}
		
		return loss;
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static final boolean arrayContainsBoth(char a, char b) {
		switch(a) {
		case 'E':		a='B';			break;
		case 'S':		a='C';			break;
		case 'T':		a='C';			break;
		case 'G':		a='H';			break;
		case 'I':		a='H';			break;	
		case 'P':		a='H';			break;
		case 'Z':		return false;
		}
		
		switch(b) {
		case 'E':		b='B';			break;
		case 'S':		b='C';			break;
		case 'T':		b='C';			break;
		case 'G':		b='H';			break;
		case 'I':		b='H';			break;	
		case 'P':		b='H';			break;
		case 'Z':		return false;
		}
		
		return a == b;
	}
	
	
	/*
	 * ALA_B, ALA_C, ALA_E, ALA_G, ALA_H, ALA_I, ALA_P, ALA_S, ALA_T, ALA_Z,  //A
	LIG_B, LIG_C, LIG_E, LIG_G, LIG_H, LIG_I, LIG_P, LIG_S, LIG_T, LIG_Z,  //B = Non-Amino Acid
	CYS_B, CYS_C, CYS_E, CYS_G, CYS_H, CYS_I, CYS_P, CYS_S, CYS_T, CYS_Z,  //C
	ASP_B, ASP_C, ASP_E, ASP_G, ASP_H, ASP_I, ASP_P, ASP_S, ASP_T, ASP_Z,  //D
	GLU_B, GLU_C, GLU_E, GLU_G, GLU_H, GLU_I, GLU_P, GLU_S, GLU_T, GLU_Z,  //E
	PHE_B, PHE_C, PHE_E, PHE_G, PHE_H, PHE_I, PHE_P, PHE_S, PHE_T, PHE_Z,  //F
	GLY_B, GLY_C, GLY_E, GLY_G, GLY_H, GLY_I, GLY_P, GLY_S, GLY_T, GLY_Z,  //G
	HIS_B, HIS_C, HIS_E, HIS_G, HIS_H, HIS_I, HIS_P, HIS_S, HIS_T, HIS_Z,  //H
	ILE_B, ILE_C, ILE_E, ILE_G, ILE_H, ILE_I, ILE_P, ILE_S, ILE_T, ILE_Z,  //I
	ORN_B, ORN_C, ORN_E, ORN_G, ORN_H, ORN_I, ORN_P, ORN_S, ORN_T, ORN_Z,  //J = Ornithine
	LYS_B, LYS_C, LYS_E, LYS_G, LYS_H, LYS_I, LYS_P, LYS_S, LYS_T, LYS_Z,  //K
	LEU_B, LEU_C, LEU_E, LEU_G, LEU_H, LEU_I, LEU_P, LEU_S, LEU_T, LEU_Z,  //L
	MET_B, MET_C, MET_E, MET_G, MET_H, MET_I, MET_P, MET_S, MET_T, MET_Z,  //M
	ASN_B, ASN_C, ASN_E, ASN_G, ASN_H, ASN_I, ASN_P, ASN_S, ASN_T, ASN_Z,  //N
	PYL_B, PYL_C, PYL_E, PYL_G, PYL_H, PYL_I, PYL_P, PYL_S, PYL_T, PYL_Z,  //O
	PRO_B, PRO_C, PRO_E, PRO_G, PRO_H, PRO_I, PRO_P, PRO_S, PRO_T, PRO_Z,  //P
	GLN_B, GLN_C, GLN_E, GLN_G, GLN_H, GLN_I, GLN_P, GLN_S, GLN_T, GLN_Z,  //Q
	ARG_B, ARG_C, ARG_E, ARG_G, ARG_H, ARG_I, ARG_P, ARG_S, ARG_T, ARG_Z,  //R
	SER_B, SER_C, SER_E, SER_G, SER_H, SER_I, SER_P, SER_S, SER_T, SER_Z,  //S
	THR_B, THR_C, THR_E, THR_G, THR_H, THR_I, THR_P, THR_S, THR_T, THR_Z,  //T
	SEC_B, SEC_C, SEC_E, SEC_G, SEC_H, SEC_I, SEC_P, SEC_S, SEC_T, SEC_Z,  //U
	VAL_B, VAL_C, VAL_E, VAL_G, VAL_H, VAL_I, VAL_P, VAL_S, VAL_T, VAL_Z,  //V
	TRP_B, TRP_C, TRP_E, TRP_G, TRP_H, TRP_I, TRP_P, TRP_S, TRP_T, TRP_Z,  //W
	UNK_B, UNK_C, UNK_E, UNK_G, UNK_H, UNK_I, UNK_P, UNK_S, UNK_T, UNK_Z,  //X = Other/Undefined Amino Acid
	TYR_B, TYR_C, TYR_E, TYR_G, TYR_H, TYR_I, TYR_P, TYR_S, TYR_T, TYR_Z,  //Y
	SME_B, SME_C, SME_E, SME_G, SME_H, SME_I, SME_P, SME_S, SME_T, SME_Z,  //Z = Selenomethionine
	START, END, PAD, MISSING;
	 */
	
	public static void main(String[] args) {
		qp(AminoToken.LIG_Z);

	}

}
