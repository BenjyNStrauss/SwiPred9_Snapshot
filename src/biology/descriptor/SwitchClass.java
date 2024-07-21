package biology.descriptor;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;
import biology.amino.Aminoid;
import biology.amino.SecondaryStructure;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum SwitchClass implements Metric {
	CLASSIC_ASSIGNED, CLASSIC_UNASSIGNED, CLASSIC_ANY, MULTI_CLASS;

	String name;
	
	@Override
	public void setName(String arg) {
		name = arg;
	}
	
	public static SwitchClass parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		arg = arg.replaceAll("switch", "");
		arg = arg.replaceAll("ed", "");
		arg = arg.replaceAll("classic", "");
		arg = arg.replaceAll("class", "");
		arg = arg.replaceAll("-", "");
		arg = arg.replaceAll("unassign", "unasg");
		arg = arg.replaceAll("assign", "asg");
		
		switch(arg) {
		case "a":
		case "asg":			return CLASSIC_ASSIGNED;
		case "d":
		case "u":
		case "unasg":		return CLASSIC_UNASSIGNED;
		case "any":
		case "all":			return CLASSIC_ANY;
		case "multi":		return MULTI_CLASS;
		default:			throw new UnmappedEnumValueException(arg);
		}
	}
	
	public String toString() {
		return (name != null) ? name : super.toString();
	}
	
	public char getRegressionValue(Aminoid aa) {
		switch(this) {
		case CLASSIC_ANY:			return (getSwitchCharOLD(aa) == '|' || getSwitchCharOLD(aa) == '?') ? '1' : '0';
		case CLASSIC_ASSIGNED:		return (getSwitchCharOLD(aa) == '|') ? '1' : '0';
		case CLASSIC_UNASSIGNED:	return (getSwitchCharOLD(aa) == '?') ? '1' : '0';
		case MULTI_CLASS:			return getSwitchLetter(aa);
		default:					return '?';
		}
	}
	
	/**
	 * 
	 * @param aa
	 * @return
	 */
	private static char getSwitchCharOLD(Aminoid aa) {
		if(aa == null) {
			return '#';
		}
		
		int configs = 0;
		if(aa.hasHomologue(SecondaryStructure.SOME_HELIX)) { ++configs; }
		if(aa.hasHomologue(SecondaryStructure.SOME_SHEET)) { ++configs; }
		if(aa.hasHomologue(SecondaryStructure.SOME_OTHER)) { ++configs; }
		
		if(aa.hasHomologue(SecondaryStructure.DISORDERED)) {
			return (configs > 1) ? '|' : ((configs > 0) ? '?' : '-') ;
		} else {
			return (configs > 1) ? '|' : '-';
		}
	}
	
	/**
	 * 
	 * @param aa
	 * @return
	 */
	public static char getSwitchCharNEW(Aminoid aa) {
		if(aa == null) { return '#'; }
		
		int configs = 0;
		if(aa.hasHomologue(SecondaryStructure.SOME_HELIX)) { ++configs; }
		if(aa.hasHomologue(SecondaryStructure.SOME_SHEET)) { ++configs; }
		if(aa.hasHomologue(SecondaryStructure.SOME_OTHER)) { ++configs; }
		
		if(aa.hasHomologue(SecondaryStructure.DISORDERED)) {
			return (configs > 1) ? '|' : '?';
		} else {
			return (configs > 1) ? '|' : '-';
		}
	}
	
	/**
	 * 
	 * @param aa
	 * @return
	 */
	public static char getSwitchLetter(Aminoid aa) {
		char ch = getSwitchCharNEW(aa);
		switch(ch) {
		case '-':	return 'N';
		case '^':
		case '?':	return 'U';
		case '|':	return 'A';
		default:	return 'X';
			
		}
	}
}
