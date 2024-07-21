package biology.descriptor;

import java.util.Objects;

import assist.EnumParserHelper;
import assist.base.ToolBase;
import assist.exceptions.UnmappedEnumValueException;

/**
 * Represents a type of switch-like behavior from an amino acid residue.
 * HAS_X means that 1+ of residue and its homologues have secondary structure type X
 * HAS_XY means that 1+ of residue and its homologues have secondary structure type X and
 * 					1+ of residue and its homologues have secondary structure type Y
 * IS_X means the residue and homologues are all only of secondary structure type X
 * IS_XY means the residue and homologues are all only of secondary structure type X or secondary structure type Y
 * 
 * UNASSIGNED_HOMOLOGUES is true if the set of the residue and it's homologues contains:
 * 		unassigned
 * UNASSIGNED_SWITCH is true if the set of the residue and it's homologues contains:
 * 		unassigned and exactly one of { HELIX, SHEET, OTHER }
 * ASSIGNED_SWITCH is true if the set of the residue and it's homologues contains:
 * 		two or more of { HELIX, SHEET, OTHER }
 * ANY_SWITCH is UNASSIGNED_SWITCH or ASSIGNED_SWITCH
 * 
 * @author Benjy Strauss
 *
 */

public enum SecStructConfig implements Metric {
	IS_H, IS_O, IS_S, IS_U, IS_HO, IS_HS, IS_HU, IS_OS, IS_OU, IS_SU, IS_HOS, IS_HOU, IS_HSU, IS_OSU,
	IS_HOSU,
	
	HAS_H, HAS_O, HAS_S, HAS_U, HAS_HO, HAS_HS, HAS_HU, HAS_OS, HAS_OU, HAS_SU, HAS_HOS, HAS_HOU,
	HAS_HSU, HAS_OSU,
	
	NUM_H, NUM_O, NUM_S, NUM_U, 
	VK_OBS_3, VK_OBS_4;
	
	private String name;
	
	public void setName(String arg) { name = arg; }
	
	public String toString() {
		return (name != null) ? name : super.toString();
	}
	
	/**
	 * TODO needs more work...
	 * @param arg0
	 * @return
	 */
	public static SecStructConfig parse(String arg0) {
		arg0 = EnumParserHelper.parseStringForEnumConversion(arg0);
		arg0 = arg0.replaceAll("helix", "h");
		arg0 = arg0.replaceAll("sheet", "s");
		arg0 = arg0.replaceAll("other", "o");
		arg0 = arg0.replaceAll("unknown", "u");
		arg0 = arg0.replaceAll("unassigned", "u");
		arg0 = arg0.replaceAll("disordered", "u");
		
		/*switch(arg0) {
		case "is-h":		return IS_H;
		case "is-s":		return IS_S;
		case "is-o":		return IS_O;
		case "is-d":
		case "is-u":		return IS_U;
		case "is-oh":
		case "is-ho":		return IS_HO;
		case "is-hs":		return IS_HS;
		case "is-hu":		return IS_HU;
		case "is-os":		return IS_OS;
		case "is-ou":		return IS_OU;
		case "is-su":		return IS_SU;
		case "is-hos":		return IS_HOS;
		case "is-hou":		return IS_HOU;
		case "is-hsu":		return IS_HSU;
		case "is-osu":		return IS_OSU;
		case "has-hosu":
		case "is-hosu":		return IS_HOSU;
		case "has-h":		return HAS_H;
		case "has-s":		return HAS_S;
		case "has-o":		return HAS_O;
		case "has-u":		return HAS_U;
		case "has-ho":		return HAS_HO;
		case "has-hs":		return HAS_HS;
		case "has-hu":		return HAS_HU;
		case "has-os":		return HAS_OS;
		case "has-ou":		return HAS_OU;
		case "has-su":		return HAS_SU;
		case "has-hos":		return HAS_HOS;
		case "has-hou":		return HAS_HOU;
		case "has-hsu":		return HAS_HSU;
		case "has-osu":		return HAS_OSU;
		default:
		}*/
		if(ToolBase.containsOneOf(arg0, "percent", "fraction", "frac")) {
			if(arg0.endsWith("h")) {
				return SecStructConfig.NUM_H;
			} else if(arg0.endsWith("o")) {
				return SecStructConfig.NUM_O;
			} else if(arg0.endsWith("s")) {
				return SecStructConfig.NUM_S;
			} else if(arg0.endsWith("u")) {
				return SecStructConfig.NUM_U;
			} else {
				throw new UnmappedEnumValueException(arg0);
			}
		}
		
		if(ToolBase.containsOneOf(arg0, "hosu", "hous", "hsuo", "huso", "hsou", "huos",
				"shou", "shuo", "sohu", "souh", "suho", "suoh",
				"ohus", "ohsu", "osuh", "oshu", "oush", "ouhs",
				"uhso", "uhos", "uohs", "uosh", "usoh", "usho")) {
			return SecStructConfig.IS_HOSU;
		} else if(ToolBase.containsOneOf(arg0, "hso", "hos", "ohs", "osh", "soh", "sho")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_HOS;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_HOS;
			}
		} else if(ToolBase.containsOneOf(arg0, "hsu", "hus", "uhs", "ush", "suh", "shu")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_HSU;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_HSU;
			}
		} else if(ToolBase.containsOneOf(arg0, "hou", "huo", "uho", "uoh", "ouh", "ohu")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_HOU;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_HOU;
			}
		} else if(ToolBase.containsOneOf(arg0, "sou", "suo", "uso", "uos", "ous", "osu")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_OSU;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_OSU;
			}
		} else if(ToolBase.containsOneOf(arg0, "hs", "sh")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_HS;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_HS;
			}
		} else if(ToolBase.containsOneOf(arg0, "os", "so")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_OS;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_OS;
			}
		} else if(ToolBase.containsOneOf(arg0, "ho", "oh")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_HO;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_HO;
			}
		} else if(ToolBase.containsOneOf(arg0, "hu", "uh")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_HU;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_HU;
			}
		} else if(ToolBase.containsOneOf(arg0, "ou", "uo")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_OU;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_OU;
			}
		} else if(ToolBase.containsOneOf(arg0, "hu", "uh")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_HU;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_HU;
			}
		} else if(ToolBase.containsOneOf(arg0, "h")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_H;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_H;
			}
		} else if(ToolBase.containsOneOf(arg0, "s")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_S;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_S;
			}
		} else if(ToolBase.containsOneOf(arg0, "o")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_O;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_O;
			}
		} else if(ToolBase.containsOneOf(arg0, "u")) {
			if(arg0.contains("is")) {
				return SecStructConfig.IS_U;
			} else if(arg0.contains("has")){
				return SecStructConfig.HAS_U;
			}
		}
		
		throw new UnmappedEnumValueException(arg0);
	}
	
	/**
	 * TODO method stub
	 * @return 3-character representation of the descriptor
	 */
	public String toAbbrev() {
		switch(this) {
		default:						return "???";
		}
	}
	
	/**
	 * 
	 * @param descriptors
	 * @return
	 */
	public static String label(SecStructConfig[] descriptors) {
		Objects.requireNonNull(descriptors, "No descriptors to construct label with!");
		if(descriptors.length == 0) {
			throw new ArrayIndexOutOfBoundsException("No descriptors to construct label with!");
		} 
		
		StringBuilder retVal = new StringBuilder();
		retVal.append(descriptors[0].toAbbrev());
		
		for(int i = 1; i < descriptors.length; ++i) { retVal.append(" + " + descriptors[i].toAbbrev()); }
		return retVal.toString();
	}
	
	/**
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static boolean arrayEquals(SecStructConfig[] array1, SecStructConfig[] array2) {
		for(SecStructConfig desc1: array1) {
			boolean matchFound = false;
			for(SecStructConfig desc2: array2) {
				if(desc1 == desc2) {
					matchFound = true; break;
				}
			}
			if(!matchFound) { return false; }
		}
		
		return true;
	}
}
