package modules.encode.tokens;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.amino.ChainObject;
import biology.amino.ResidueConfig;
import biology.amino.SecondaryStructure;
import biology.molecule.types.AminoType;
import biology.protein.AminoChain;
import utilities.LocalToolBase;
/**
 * A method of encoding Amino Acids based on primary AND secondary structure, 264 (26*10+4) tokens in all
 * 
 * H = α-helix
 * B = residue in isolated β-bridge
 * E = extended strand, participates in β ladder
 * G = 3-helix (3/10 helix)
 * I = 5 helix (π-helix)
 * P = Poly-Proline Helix
 * T = hydrogen bonded turn
 * S = bend
 * C = Coil (blank in DSSP)
 * Z = Missing from experiment
 * 
 * @author Benjamin Strauss
 *
 */

public enum AminoToken implements ResidueToken {
	
	ALA_B, ALA_C, ALA_E, ALA_G, ALA_H, ALA_I, ALA_P, ALA_S, ALA_T, ALA_Z,  //A
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
	
	@SuppressWarnings("unused")
	private String name;
	
	public static boolean displayCondensed = false;
	
	static final LabeledHash<String, AminoToken> STR_PARSE = new LabeledHash<String, AminoToken>() {
		private static final long serialVersionUID = 1L;
		{
			for(AminoToken token: AminoToken.values()) {
				put(token.toString().replaceAll("_", "-"), token);
			}
			
			for(AminoToken token: AminoToken.values()) {
				put(token.condensed(), token);
			}
		}
	};
	
	private static final LabeledHash<Integer, AminoToken> INT_PARSE = new LabeledHash<Integer, AminoToken>() {
		private static final long serialVersionUID = 1L;
		{
			for(AminoToken token: values()) {
				put(token.ordinal(), token);
			}
		}
	};
	
	public void setName(String arg) { name = arg; }
	
	public static AminoToken[] parse(AminoChain<?> chain) {
		List<AminoToken> tokens = new LabeledList<AminoToken>(chain.id().toString());
		tokens.add(START);
		
		for(ChainObject amino: chain) {
			if(amino == null) {
				tokens.add(MISSING);
			} else {
				tokens.add(parse(amino.residueType(), amino.secondary()));
			}
		}
		
		tokens.add(END);
		AminoToken[] array = new AminoToken[tokens.size()];
		tokens.toArray(array);
		return array;
	}
	
	public static AminoToken parse(ResidueConfig config) {
		return parse(config.primary(), config.secondary());
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static AminoToken parse(String value) { return STR_PARSE.get(value); }
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static AminoToken parse(int value) { return INT_PARSE.get(value); }
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static AminoToken[] parse(String[] value) { 
		AminoToken[] array = new AminoToken[value.length];
		for(int index = 0; index < value.length; ++index) {
			array[index] = parse(value[index]);
		}
		return array;
	}
	
	/**
	 * 
	 * @param at
	 * @param ss
	 * @return
	 */
	public static AminoToken parse(AminoType at, SecondaryStructure ss) {
		Objects.requireNonNull(ss, "Error: no secondary structure assigned.");
		while(!TokenUtils.isStandardized(at)) { at = at.standardize(); }
		
		int val = 0;
		for(int index = 0; index < TokenUtils.BASE_TYPES.length; ++index) {
			if(at == TokenUtils.BASE_TYPES[index]) {
				val = index*10;
				break;
			}
		}
		
		/*
		 * H = α-helix
		 * B = residue in isolated β-bridge
		 * E = extended strand, participates in β ladder
		 * G = 3-helix (3/10 helix)
		 * I = 5 helix (π-helix)
		 * T = hydrogen bonded turn
		 * S = bend
		 * C = Coil (blank in DSSP)
		 */
		switch(ss) {
		case TURN:				++val;	//T
		case BEND:				++val;	//S
		case POLY_PROLINE_HELIX:++val;	//P
		case FIVE_HELIX:		++val;	//I
		case ALPHA_HELIX:		++val;	//H
		case THREE_HELIX:		++val;	//G
		case EXTENDED_STRAND:	++val;	//E
		case COIL:
		case UNKNOWN:			++val;	//C
		case BETA_BRIDGE:				//B
			break;
		default:
			val+=9;
		}
		
		for(AminoToken value: AminoToken.values()) {
			if(val == value.ordinal()) {
				return value;
			}
		}
		
		throw new InvalidSwipredTokenException();
	}
	
	public AminoType primary() {
		if(this == START || this == END || this == PAD || this == MISSING) {
			return null;
		}
		return AminoType.parse(toString().substring(0,3));
	}
	
	public SecondaryStructure secondary() {
		if(this == START || this == END || this == PAD || this == MISSING) {
			return null;
		}
		return SecondaryStructure.parse(toString().charAt(4));
	}
	
	public String condensed() {
		String raw = super.toString();
		
		if(raw.length() == 3) { return raw; }
		if(raw.equals("START")) { return "STA"; }
		if(raw.equals("MISSING")) { return "NUL"; }
		
		raw = raw.replaceAll("_B", "5");
		raw = raw.replaceAll("_C", "9");
		raw = raw.replaceAll("_E", "6");
		raw = raw.replaceAll("_G", "2");
		raw = raw.replaceAll("_H", "1");
		raw = raw.replaceAll("_I", "3");
		raw = raw.replaceAll("_P", "4");
		raw = raw.replaceAll("_S", "7");
		raw = raw.replaceAll("_T", "8");
		raw = raw.replaceAll("_Z", "0");
		
		raw = raw.replaceAll("ALA", "A");
		raw = raw.replaceAll("LIG", "!");
		raw = raw.replaceAll("CYS", "C");
		raw = raw.replaceAll("ASP", "D");
		raw = raw.replaceAll("GLU", "E");
		raw = raw.replaceAll("PHE", "F");
		raw = raw.replaceAll("GLY", "G");
		raw = raw.replaceAll("HIS", "H");
		raw = raw.replaceAll("ILE", "I");
		raw = raw.replaceAll("ORN", ""+AminoType.Ornithine.utf16_letter);
		raw = raw.replaceAll("LYS", "K");
		raw = raw.replaceAll("LEU", "L");
		raw = raw.replaceAll("MET", "M");
		raw = raw.replaceAll("ASN", "N");
		raw = raw.replaceAll("PYL", "O");
		raw = raw.replaceAll("PRO", "P");
		raw = raw.replaceAll("GLN", "Q");
		raw = raw.replaceAll("ARG", "R");
		raw = raw.replaceAll("SER", "S");
		raw = raw.replaceAll("THR", "T");
		raw = raw.replaceAll("SEC", "U");
		raw = raw.replaceAll("VAL", "V");
		raw = raw.replaceAll("TRP", "W");
		raw = raw.replaceAll("UNK", "X");
		raw = raw.replaceAll("TYR", "Y");
		raw = raw.replaceAll("SME", ""+AminoType.Selenomethionine.utf16_letter);
		
		return raw;
	}
	
	public String toString() {
		return (displayCondensed) ? condensed() : super.toString().replaceAll("_", "-");
	}
	
	public static void printTokenList() {
		StringBuffer buffer = new StringBuffer();
		for(AminoToken at: AminoToken.values()) {
			buffer.append("\""+at.condensed()+"\",");
		}
		
		String[] array = buffer.toString().split(",");
		Arrays.sort(array);
		LocalToolBase.qp(array);
		System.exit(0);
	}
}
