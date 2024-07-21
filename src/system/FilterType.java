package system;

import assist.EnumParserHelper;

/**
 * 
 * @author Benjamin Strauss
 *
 */

enum FilterType {
	NO_FILTER, PROTEIN;

	public static FilterType parse(String arg) {
		if(arg == null) { return PROTEIN; }
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		
		switch(arg) {
		default:	return PROTEIN;
		}
	}
	
}
