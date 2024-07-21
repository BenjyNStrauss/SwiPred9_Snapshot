package biology.descriptor;

import java.util.Objects;

import assist.exceptions.NotYetImplementedError;

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

public enum Switch implements Metric {
	ASSIGNED_SWITCH, UNASSIGNED_SWITCH, ANY_SWITCH,
	UNASSIGNED_HOMOLOGUES,
	
	IS_H, IS_O, IS_S, IS_U, IS_HO, IS_HS, IS_HU, IS_OS, IS_OU, IS_SU, IS_HOS, IS_HOU, IS_HSU, IS_OSU,
	IS_HOSU,
	
	HAS_H, HAS_O, HAS_S, HAS_U, HAS_HO, HAS_HS, HAS_HU, HAS_OS, HAS_OU, HAS_SU, HAS_HOS, HAS_HOU,
	HAS_HSU, HAS_OSU;
	
	private String name;
	
	public void setName(String arg) { name = arg; }
	
	public String toString() {
		if(name != null) { return name; }
		
		switch(this) {
		case ANY_SWITCH:					return "Assigned+Unassigned Switch";
		case ASSIGNED_SWITCH:				return "Assigned Switch";
		case UNASSIGNED_HOMOLOGUES:			return "Unassigned-Homologues";
		case UNASSIGNED_SWITCH:				return "Unassigned-Switch";
		//case VK_OBS:						return "Vkbat-Observed";
		default:							return super.toString();
		}
	}
	
	/**
	 * TODO method stub
	 * @param arg0
	 * @return
	 */
	public static Switch parse(String arg0) {
		throw new NotYetImplementedError();
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
	public static String label(Switch[] descriptors) {
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
	public static boolean arrayEquals(Switch[] array1, Switch[] array2) {
		for(Switch desc1: array1) {
			boolean matchFound = false;
			for(Switch desc2: array2) {
				if(desc1 == desc2) {
					matchFound = true; break;
				}
			}
			if(!matchFound) { return false; }
		}
		
		return true;
	}
}
