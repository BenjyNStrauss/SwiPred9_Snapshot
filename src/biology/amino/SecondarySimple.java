package biology.amino;

import assist.EnumParserHelper;
import assist.base.ToolBelt;
import assist.exceptions.UnmappedEnumValueException;
//import utilities.LocalToolBase;
import utilities.SwiPredObject;

/**
 * 
 * @author Benjy Strauss
 *
 */

public enum SecondarySimple implements ToolBelt, SwiPredObject {
	Helix, Sheet, Other, Disordered;
	
	public static final int NUMBER_OF_TYPES = 4;
	
	public static SecondarySimple parse(char ch) {
		return parse(""+ch);
	}
	
	/**
	 * Designed for vkabat algorithm output parsing
	 * Do NOT use this to parse characters from SecondaryStructures.toChar()!
	 * @param ch: the char to parse
	 * @return
	 */
	public static SecondarySimple parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		
		switch(arg) {

		case "h":
		case "helix":		return Helix;
		case "s":
		case "e":
		case "sheet":		return Sheet;
		case "_":
		case "-":
		case "c":
		case "t":			//assuming this means "turn"
		case " ":
		case "o":
		case "other":		return Other;
		default:			throw new UnmappedEnumValueException(arg);
		}
	}
	
	public static SecondarySimple parseDSSP(char ch) {
		ch = Character.toUpperCase(ch);
		
		switch(ch) {
		case 'B':		return Sheet;
		case 'C':		return Other;
		case 'E':		return Sheet;
		case 'G':		return Helix;
		case 'H':		return Helix;
		case 'I':		return Helix;
		case 'S':		return Other;
		case 'T':		return Other;
		case ' ':		return Other;
		default:		return null;
		}
	}
	
	public static SecondarySimple parse(SecondaryStructure secStr) {
		switch(secStr) {
		case ALPHA_HELIX:
		case THREE_HELIX:
		case FIVE_HELIX:
		case SOME_HELIX:		return Helix;
		
		case BETA_BRIDGE:
		case EXTENDED_STRAND:
		case SOME_SHEET:		return Sheet;
		
		case BEND:
		case COIL:
		case TURN:
		case UNKNOWN:
		case SOME_OTHER:		return Other;
		
		case DISORDERED:
		default:				return Disordered;
		}
	}
	
	public char toChar() {
		switch(this) {
		case Helix:			return 'H';
		case Other:			return 'O';
		case Sheet:			return 'S';
		case Disordered:
		default:				return 'D';
			
		}
	}
	
	public int index() {
		switch(this) {
		case Helix:			return 1;
		case Other:			return 3;
		case Sheet:			return 2;
		case Disordered:
		default:				return 0;
		}
	}
	
	public static int index(SecondarySimple sec) { return sec.index(); }
}
