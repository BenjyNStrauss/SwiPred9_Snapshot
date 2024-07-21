package biology.descriptor;

import java.util.ArrayList;
import java.util.Objects;

import utilities.DataObject;

/**
 * The different types of columns that CSVWriter2 can write to a CSV
 * @author Benjy Strauss
 *
 * 
 */

public class Descriptor extends DataObject implements Metric {
	private static final long serialVersionUID = 1L;
	
	public final DescriptorType type;
	//size of the window -- now many neighboring residues on each side are included 
	private int windowSize = 0;
	public boolean normalize;
	public boolean flip;
	private String name;
	
	/**
	 * 
	 * @param type
	 */
	public Descriptor(DescriptorType type) {
		Objects.requireNonNull(type, "Descriptor type cannot be null!");
		this.type = type;
	}
	
	public void setName(String arg) { name = arg; }
	
	public int getWindowSize() { return windowSize; }
	
	public void setWindowSize(int newSize) {
		if(newSize >= 0) { windowSize = newSize; }
	}
	
	public String toString() {
		if(name != null) { return name; }
		String retval = "";
		
		switch(type) {
		case AMBER_95:						retval = "Amber95";					break;
		case AVERAGE_CHARGE:				retval = "Average Charge";			break;
		case CHARGE_Cα:						retval = "Charge-Cα";				break;
		case CHARGE_Cβ:						retval = "Charge-Cβ";				break;
		case CHARGE_CP:						retval = "Charge-C'";				break;
		case CHARGE_N:						retval = "Charge-N";				break;
		case CHARGE_NH:						retval = "Charge-NH";				break;
		case CHARGE_O:						retval = "Charge-O";				break;
		case E20:							retval = "E20";						break;
		case E22:							retval = "E22";						break;
		case E6:							retval = "E6";						break;
		case FLEXIBILITY:					retval = "Flexibility";				break;
		case ISUNSTRUCT:					retval = "isUnstruct";				break;
		
		case VKBAT:							retval = "Vkbat";					break;
		case VKBAT_COMPLETION:				retval = "Vkbat-#-of-predictions";	break;
		case VK_PREDICTED_AMBER:			retval = "Amber95-Weighted(VK)";	break;

		case VK_PREDICTED_AVG_ABS_TMB:		retval = "VkPred Avg |Charge|";		break;
		case VK_PREDICTED_AVG_TMB:			retval = "VkPred Avg Charge";		break;
		case VK_PREDICTED_NET_ABS_TMB:		retval = "VkPred Net |Charge|";		break;
		case VK_PREDICTED_NET_TMB:			retval = "VkPred Net Charge";		break;
		default:							retval = "???";
		}
		
		if(flip) { retval += "(f)"; }
		if(normalize) { retval += "(n)"; }
		if(windowSize != 0) { retval += " [ " + (windowSize*2+1) + " ]"; }
		
		return retval;
	}
	
	/**
	 * TODO DOES NOT HANDLE CHARGE YET!
	 * @param str
	 * @return
	 */
	public static Metric[] parseDescriptors(String str) {
		ArrayList<Metric> descList = new ArrayList<Metric>();
		
		if(str.contains("C(Residue, levels=SideChainIDs)")) { descList.add(Identifier.RESIDUE_LETTER_CONSENSUS); }
		if(str.contains("E6")) { descList.add(new Descriptor(DescriptorType.E6)); }
		if(str.contains("E20")) { descList.add(new Descriptor(DescriptorType.E20)); }
		if(str.contains("isUnstruct")) { descList.add(new Descriptor(DescriptorType.ISUNSTRUCT)); }
		if(str.contains("Vkabat")) { descList.add(new Descriptor(DescriptorType.VKBAT)); }
		
		Descriptor[] retVal = new Descriptor[descList.size()];
		descList.toArray(retVal);
		
		return retVal;
	}
	
	/**
	 * Gets a 3 to 5 character abbreviation of the descriptor
	 * A95 = Amber95
	 * CRG = Charge
	 * E#  = #-term entropy
	 * FLX = Flexibility
	 * VK(c) = Vkabat (completion)
	 * 
	 * @return 3-5-character representation of the descriptor
	 */
	public String toAbbrev() {
		switch(type) {
		case AMBER_95:					return "A95";
		case AVERAGE_CHARGE:			return "CRG";
		case CHARGE_N:					return "CRGn";
		case CHARGE_NH:					return "CRGhn";
		case CHARGE_Cα:					return "CRGcα";
		case CHARGE_Cβ:					return "CRGcβ";
		case CHARGE_CP:					return "CRGc'";
		case CHARGE_O:					return "CRGo";
		case E22:						return "E22";
		case E20:						return "E20";
		case E6:						return "E6 ";
		case ISUNSTRUCT:				return "IsU";
		//case RESIDUE_CODE_CONSENSUS:	return "Res";
		//case RESIDUE_CODE_DOMINANT:		return "RES";
		//case RESIDUE_LETTER_CONSENSUS:	return "Res";
		//case RESIDUE_LETTER_DOMINANT:	return "RES";
		case VKBAT:						return "VK ";
		case VKBAT_COMPLETION:			return "VKc";
		case FLEXIBILITY:				return "FLX";
		default:						return "ERR";
		}
	}
	
	/**
	 * 
	 * @param descriptorList
	 * @return
	 */
	public static String label(Metric[] descriptorList) {
		Objects.requireNonNull(descriptorList, "No descriptors to construct label with!");
		
		if(descriptorList.length == 0) {
			throw new NullPointerException("No descriptors to construct label with!");
		} 
		
		StringBuilder retVal = new StringBuilder();
		
		if(descriptorList[0] instanceof Descriptor) {
			Descriptor d = (Descriptor) descriptorList[0];
			retVal.append(d.toAbbrev());
		} else {
			retVal.append(descriptorList[0]);
		}
		
		for(int i = 1; i < descriptorList.length; ++i) { 
			if(descriptorList[i] instanceof Descriptor) {
				Descriptor d = (Descriptor) descriptorList[i];
				retVal.append(" + " + d.toAbbrev()); 
			} else {
				retVal.append(" + " + descriptorList[i]); 
			}
		}
		return retVal.toString();
	}
}
