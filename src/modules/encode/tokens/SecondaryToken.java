package modules.encode.tokens;

import java.util.List;
import java.util.Objects;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;
import assist.util.LabeledList;
import biology.amino.ChainObject;
import biology.amino.SecondaryStructure;
import biology.protein.AminoChain;

/**
 * 
 * @author Benjamin Strauss
 * 
 * Z = Missing from DSSP
 * MISSING = Missing from PDB
 *
 */

public enum SecondaryToken implements ResidueToken {
	B, C, E, G, H, I, P, S, T, Z,
	START, END, PAD, MISSING;

	public static boolean displayCondensed = false;
	
	@SuppressWarnings("unused")
	private String name;
	
	@Override
	public void setName(String name) { this.name = name; }
	
	public static SecondaryToken parse(char ch) {
		ch = Character.toUpperCase(ch);
		switch(ch) {
		case 'B':					return B;	//B = residue in isolated β-bridge
		case 'C':
		case ' ':					return C;	//C = Coil (blank in DSSP)
		case 'E':					return E;	//E = extended strand, participates in β ladder
		case 'G':					return G;	//G = 3-helix (3/10 helix)
		case 'H':					return H;	//
		case 'I':					return I;	//I = 5 helix (π-helix)
		case 'P':					return P;	//P
		case 'S':					return S;	//S = bend
		case 'T':					return T;	//T = hydrogen bonded turn
		case 'Z':
		case '*':					return Z;	//Residue entirely Missing from DSSP
		default:					throw new InvalidSwipredTokenException();
		}
	}
	
	public static SecondaryToken parse(SecondaryStructure ss) {
		Objects.requireNonNull(ss, "Error: no secondary structure assigned.");
		
		/**
		 * H = α-helix
		 * B = residue in isolated β-bridge
		 * E = extended strand, participates in β ladder
		 * 
		 * I = 5 helix (π-helix)
		 * T = hydrogen bonded turn
		 * S = bend
		 * C = Coil (blank in DSSP)
		 */
		switch(ss) {
		case BETA_BRIDGE:			return B;	//B = residue in isolated β-bridge
		case COIL:
		case UNKNOWN:				return C;	//C = Coil (blank in DSSP)
		case EXTENDED_STRAND:		return E;	//E = extended strand, participates in β ladder
		case THREE_HELIX:			return G;	//G = 3-helix (3/10 helix)
		case ALPHA_HELIX:			return H;	//
		case FIVE_HELIX:			return I;	//I = 5 helix (π-helix)
		case POLY_PROLINE_HELIX:	return P;	//P
		case BEND:					return S;	//S = bend
		case TURN:					return T;	//T = hydrogen bonded turn
		case DISORDERED:			return Z;	//Residue entirely Missing from DSSP
		
		default:					throw new InvalidSwipredTokenException();
		}
	}
	
	public static SecondaryToken parse(AminoToken token) {
		switch(token) {
		case START:			return START;
		case END:			return END;
		case PAD:			return PAD;
		case MISSING:		return MISSING;
		default:
			String str = token.toString();
			char parseMe = str.charAt(str.length()-1);
			return parse(parseMe);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static SecondaryToken parse(String value) { 
		switch(EnumParserHelper.parseStringForEnumConversion(value)) {
		case "sta":
		case "start":		return START;
		case "end":			return END;
		case "pad":			return PAD;
		case "mis":
		case "missing":		return MISSING;
		case "b":			return B;
		case "":
		case "c":			return C;
		case "e":			return E;
		case "g":			return G;
		case "h":			return H;
		case "i":			return I;
		case "p":			return P;
		case "s":			return S;
		case "t":			return T;
		case "disordered":
		case "z":			return Z;
		default:			throw new UnmappedEnumValueException(value);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static SecondaryToken[] parse(String[] value) { 
		SecondaryToken[] array = new SecondaryToken[value.length];
		for(int index = 0; index < value.length; ++index) {
			array[index] = parse(value[index]);
		}
		return array;
	}
	
	public static SecondaryToken[] parse(AminoChain<?> chain) {
		List<SecondaryToken> tokens = new LabeledList<SecondaryToken>(chain.id().toString());
		tokens.add(START);
		for(ChainObject amino: chain) {
			if(amino == null) {
				tokens.add(MISSING);
			} else {
				tokens.add(parse(amino.secondary()));
			}
		}
		
		tokens.add(END);
		SecondaryToken[] array = new SecondaryToken[tokens.size()];
		tokens.toArray(array);
		return array;
	}
	
	private String condensed() {
		switch(this) {
		case START:			return "<";
		case END:			return ">";
		case PAD:			return "=";
		case MISSING:		return "-";
		default:	 		return super.toString();
		}
	}
	
	public String toString() {
		return (displayCondensed) ? condensed() : super.toString();
	}
}
