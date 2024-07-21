package modules.encode.tokens;

import java.util.Objects;

import assist.exceptions.UnmappedEnumValueException;
import biology.amino.SecondarySimple;
import biology.amino.SecondaryStructure;
import biology.protein.AminoChain;

/**
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
 * 
 * DISORDERED(Z) = Missing from DSSP
 * MISSING = Missing from PDB
 * 
 * @author Benjamin Strauss
 *
 */

public enum SecondarySimpleToken implements ResidueToken {
	HELIX('H'), SHEET('E'), OTHER('O'), DISORDERED('D'),
	START('<'), END('>'), PAD('='), MISSING('-');
	
	private SecondarySimpleToken(char ch) {
		condensed = ch;
	}
	
	public static boolean displayCondensed = false;
	
	@SuppressWarnings("unused")
	private String name;
	
	public final char condensed;
	
	@Override
	public void setName(String name) { this.name = name; }
	
	public static SecondarySimpleToken parse(char ch) {
		ch = Character.toUpperCase(ch);
		switch(ch) {
		case 'G':
		case 'I':
		case 'P':
		case 'H':					return HELIX;
		case 'B':
		case 'E':					return SHEET;
		case ' ':
		case 'S':
		case 'T':
		case 'C':					return OTHER;
		case 'D':
		case 'Z':
		case '*':					return MISSING;
		default:					throw new InvalidSwipredTokenException();
		}
	}
	
	public static SecondarySimpleToken parse(AminoToken token) {
		return parse(SecondaryToken.parse(token));
	}
	
	public static SecondarySimpleToken parse(SecondaryToken s_token) {
		Objects.requireNonNull(s_token, "Error: no secondary structure assigned.");
		switch(s_token) {
		case B:				return SHEET;
		case C:				return OTHER;
		case E:				return SHEET;
		case END:			return END;
		case G:
		case H:
		case I:				return HELIX;
		case MISSING:		return MISSING;
		case P:				return HELIX;
		case PAD:			return PAD;
		case S:				return OTHER;
		case START:			return START;
		case T:				return OTHER;
		case Z:				return DISORDERED;
		default:			throw new UnmappedEnumValueException(""+s_token);
		}
	}
	
	public static SecondarySimpleToken parse(SecondaryStructure ss) {
		Objects.requireNonNull(ss, "Error: no secondary structure assigned.");
		
		switch(ss) {
		case SOME_HELIX:	return HELIX;
		case SOME_SHEET:	return SHEET;
		case SOME_OTHER:	return OTHER;
		default:			return parse(SecondaryToken.parse(ss));
		}
	}
	
	public static SecondarySimpleToken parse(SecondarySimple ss) {
		Objects.requireNonNull(ss, "Error: no secondary structure assigned.");
		
		switch(ss) {
		case Helix:			return HELIX;
		case Sheet:			return SHEET;
		case Other:			return OTHER;
		case Disordered:	return DISORDERED;
		default:			throw new UnmappedEnumValueException(""+ss);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static SecondarySimpleToken parse(String value) { 
		return parse(SecondaryToken.parse(value));
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static SecondarySimpleToken[] parse(String[] value) { 
		SecondarySimpleToken[] array = new SecondarySimpleToken[value.length];
		for(int index = 0; index < value.length; ++index) {
			array[index] = parse(value[index]);
		}
		return array;
	}
	
	public static SecondarySimpleToken[] parse(AminoChain<?> chain) {
		SecondaryToken[] ss_tokens = SecondaryToken.parse(chain);
		SecondarySimpleToken[] converted = new SecondarySimpleToken[ss_tokens.length];
		
		for(int index = 0; index < ss_tokens.length; ++index) {
			converted[index] = parse(ss_tokens[index]);
		}
		
		return converted;
	}
	
	public String toString() {
		return (displayCondensed) ? ""+condensed : super.toString();
	}
}
