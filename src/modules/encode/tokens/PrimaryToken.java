package modules.encode.tokens;

import java.util.List;

import assist.exceptions.UnmappedEnumValueException;
import assist.util.LabeledList;
import biology.amino.ChainObject;
import biology.molecule.types.AminoType;
import biology.protein.AminoChain;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum PrimaryToken implements ResidueToken {
	ALA, LIG, CYS, ASP, GLU, PHE,
	GLY, HIS, ILE, ORN, LYS, LEU,
	MET, ASN, PYL, PRO, GLN, ARG,
	SER, THR, SEC, VAL, TRP, UNK,
	TYR, SME,
	
	START, END, PAD, MISSING;
	
	public static boolean displayCondensed = false;
	
	@SuppressWarnings("unused")
	private String name;
	
	@Override
	public void setName(String name) { this.name = name; }
	
	public static PrimaryToken parse(AminoType at) {
		while(!TokenUtils.isStandardized(at)) { at = at.standardize(); }
		
		int val = 0;
		for(int index = 0; index < TokenUtils.BASE_TYPES.length; ++index) {
			if(at == TokenUtils.BASE_TYPES[index]) {
				val = index;
				break;
			}
		}
		
		for(PrimaryToken value: PrimaryToken.values()) {
			if(val == value.ordinal()) {
				return value;
			}
		}
		throw new InvalidSwipredTokenException();
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static PrimaryToken[] parse(String[] value) { 
		PrimaryToken[] array = new PrimaryToken[value.length];
		for(int index = 0; index < value.length; ++index) {
			array[index] = parse(value[index]);
		}
		return array;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static PrimaryToken parse(String value) { 
		value = value.toUpperCase().trim();
		for(PrimaryToken token: values()) {
			if(token.superToString().equals(value) || token.condensed().equals(value)) {
				return token;
			}
		}
		
		//System.out.println("Uncaught: "+value + "::" +((int)value.charAt(0)));
		
		switch(value) {
		//This 'M' is actually an uppercase mu!
		case "SME":		case "Μ":		return SME;
		case "ORN":		
		
		default:	throw new UnmappedEnumValueException(value);
		}
		
		
	}
	
	public static PrimaryToken[] parse(AminoChain<?> chain) {
		List<PrimaryToken> tokens = new LabeledList<PrimaryToken>(chain.id().toString());
		tokens.add(START);
		for(ChainObject amino: chain) {
			if(amino == null) {
				tokens.add(MISSING);
			} else {
				tokens.add(parse(amino.residueType()));
			}
		}
		
		tokens.add(END);
		PrimaryToken[] array = new PrimaryToken[tokens.size()];
		tokens.toArray(array);
		return array;
	}
	
	public static PrimaryToken parse(AminoToken token) {
		switch(token) {
		case MISSING:		return MISSING;
		case PAD:			return PAD;
		case START:			return START;
		case END:			return END;
		default:
		}
		
		switch(token.toString().substring(0, 3)) {
		case "ALA":		return ALA;
		case "LIG":		return LIG;
		case "CYS":		return CYS;
		case "ASP":		return ASP;
		case "GLU":		return GLU;
		case "PHE":		return PHE;
		case "GLY":		return GLY;
		case "HIS":		return HIS;
		case "ILE":		return ILE;
		case "ORN":		return ORN;
		case "LYS":		return LYS;
		case "LEU":		return LEU;
		case "MET":		return MET;
		case "ASN":		return ASN;
		case "PYL":		return PYL;
		case "PRO":		return PRO;
		case "GLN":		return GLN;
		case "ARG":		return ARG;
		case "SER":		return SER;
		case "THR":		return THR;
		case "SEC":		return SEC;
		case "VAL":		return VAL;
		case "TRP":		return TRP;
		case "UNK":		return UNK;
		case "TYR":		return TYR;
		case "SME":		return SME;
		default:		throw new UnmappedEnumValueException(""+token);
		}
	}
	
	public String condensed() {
		switch(super.toString()) {
		case "START":	return "<";
		case "MISSING":	return "_";
		case "ALA":		return "A";
		case "LIG":		return "!";
		case "CYS":		return "C";
		case "ASP":		return "D";
		case "GLU":		return "E";
		case "PHE":		return "F";
		case "GLY":		return "G";
		case "HIS":		return "H";
		case "ILE":		return "I";
		case "ORN":		return ""+AminoType.Ornithine.utf16_letter;
		case "LYS":		return "K";
		case "LEU":		return "L";
		case "MET":		return "M";
		case "ASN":		return "N";
		case "PYL":		return "O";
		case "PRO":		return "P";
		case "GLN":		return "Q";
		case "ARG":		return "R";
		case "SER":		return "S";
		case "THR":		return "T";
		case "SEC":		return "U";
		case "VAL":		return "V";
		case "TRP":		return "W";
		case "UNK":		return "X";
		case "TYR":		return "Y";
		case "SME":		return ""+AminoType.Selenomethionine.utf16_letter;
		case "END":		return ">";
		case "PAD":		return "=";
		default: 		return super.toString();
		}
	}
	
	private String superToString() { return super.toString(); }
	
	public String toString() {
		return (displayCondensed) ? condensed() : super.toString();
	}
}
