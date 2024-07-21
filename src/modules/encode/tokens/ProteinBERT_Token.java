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

public enum ProteinBERT_Token implements ResidueToken {
	ALA, CYS, ASP, GLU, PHE,
	GLY, HIS, ILE, LYS, LEU,
	MET, ASN, PRO, GLN, ARG,
	SER, THR, SEC, VAL, TRP, OTHER,
	TYR, 
	
	UNK, START, END, PAD, MISSING;
	
	public static boolean displayCondensed = false;
	
	@SuppressWarnings("unused")
	private String name;
	
	@Override
	public void setName(String name) { this.name = name; }
	
	public static ProteinBERT_Token parse(AminoType at) {
		if(at == AminoType.Selenocysteine) { return SEC; }
		if(!TokenUtils.isStandardizedPB(at)) { return OTHER; }
		
		int val = 0;
		for(int index = 0; index < TokenUtils.PROTEINBERT_TYPES.length; ++index) {
			if(at == TokenUtils.PROTEINBERT_TYPES[index]) {
				val = index;
				break;
			}
		}
		
		for(ProteinBERT_Token value: ProteinBERT_Token.values()) {
			
			if(val == value.ordinal()) {
				return value;
			}
		}
		throw new InvalidSwipredTokenException();
	}
	
	public static ProteinBERT_Token[] parse(AminoChain<?> chain) {
		List<ProteinBERT_Token> tokens = new LabeledList<ProteinBERT_Token>(chain.id().toString());
		tokens.add(START);
		for(ChainObject amino: chain) {
			if(amino == null) {
				tokens.add(MISSING);
			} else {
				tokens.add(parse(amino.residueType()));
			}
		}
		
		tokens.add(END);
		ProteinBERT_Token[] array = new ProteinBERT_Token[tokens.size()];
		tokens.toArray(array);
		return array;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static ProteinBERT_Token parse(String value) { 
		value = value.toUpperCase().trim();
		for(ProteinBERT_Token token: values()) {
			if(token.superToString().equals(value) || token.condensed().equals(value)) {
				return token;
			}
		}
		throw new UnmappedEnumValueException(value);
	}
	
	public static ProteinBERT_Token parse(AminoToken token) {
		switch(token) {
		case MISSING:
		case PAD:			return PAD;
		case START:			return START;
		case END:			return END;
		default:
		}
		
		switch(token.toString().substring(0, 3)) {
		case "ALA":		return ALA;
		case "LIG":		return OTHER;
		case "CYS":		return CYS;
		case "ASP":		return ASP;
		case "GLU":		return GLU;
		case "PHE":		return PHE;
		case "GLY":		return GLY;
		case "HIS":		return HIS;
		case "ILE":		return ILE;
		case "ORN":		return OTHER;
		case "LYS":		return LYS;
		case "LEU":		return LEU;
		case "MET":		return MET;
		case "ASN":		return ASN;
		case "PYL":		return OTHER;
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
		case "SME":		return OTHER;
		default:		throw new UnmappedEnumValueException(""+token);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static ProteinBERT_Token[] parse(String[] value) { 
		ProteinBERT_Token[] array = new ProteinBERT_Token[value.length];
		for(int index = 0; index < value.length; ++index) {
			array[index] = parse(value[index]);
		}
		return array;
	}
	
	public String condensed() {
		switch(this) {
		case START:		return "<";
		case MISSING:	return "_";
		case ALA:		return "A";
		case CYS:		return "C";
		case ASP:		return "D";
		case GLU:		return "E";
		case PHE:		return "F";
		case GLY:		return "G";
		case HIS:		return "H";
		case ILE:		return "I";
		case LYS:		return "K";
		case LEU:		return "L";
		case MET:		return "M";
		case ASN:		return "N";
		case PRO:		return "P";
		case GLN:		return "Q";
		case ARG:		return "R";
		case SER:		return "S";
		case THR:		return "T";
		case SEC:		return "U";
		case VAL:		return "V";
		case TRP:		return "W";
		case UNK:		return "?";
		case TYR:		return "Y";
		case END:		return ">";
		case PAD:		return "=";
		case OTHER:		return "X";
		default: 		return super.toString();
		}
	}
	
	private String superToString() { return super.toString(); }
	
	public String toString() {
		//LocalToolBase.qp((displayCondensed) ? condensed() : super.toString().replaceAll("_", "-"));
		return (displayCondensed) ? condensed() : super.toString().replaceAll("_", "-");
	}
}
