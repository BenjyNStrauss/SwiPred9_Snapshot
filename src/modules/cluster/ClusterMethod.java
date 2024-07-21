package modules.cluster;

import assist.EnumParserHelper;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum ClusterMethod {
	DEFAULT, MSA, INSERTCODE;
	
	public static ClusterMethod parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		switch(arg) {
		case "msa":				return MSA;
		case "insertcode":		return INSERTCODE;
		default:				return DEFAULT;
		}
	}
}
