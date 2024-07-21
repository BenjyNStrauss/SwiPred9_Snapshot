package biology.descriptor;

import java.util.HashSet;
import java.util.Objects;

import assist.EnumParserHelper;
import assist.base.ToolBase;
import assist.exceptions.UnmappedEnumValueException;

/**
 * A Metric is something that can be written into a spreadsheet or csv.
 * @author Benjy Strauss
 *
 */
public interface Metric extends AbstractMetric {
	
	public void setName(String arg);
	
	public default Metric toMetric() { return this; }
	
	/**
	 * Guesses a column type based on it's name
	 * @param columnStr: the name of the column of which to guess the type of
	 * @return: the guessed column type
	 */
	public static Metric parse(String columnStr) {
		Objects.requireNonNull(columnStr, "Null Argument");
		columnStr = EnumParserHelper.parseStringForEnumConversion(columnStr);
		
		VKPred maybePred = VKPred.parse(columnStr);
		if(maybePred != VKPred.UNKNOWN) { return maybePred; }
		
		try {
			return SecStructConfig.parse(columnStr);
		} catch (UnmappedEnumValueException UEVE) { }
			
		
		if(columnStr.contains("switch")) {
			try {
				return SwitchClass.parse(columnStr);
			} catch (UnmappedEnumValueException UEVE) { }
		}
		
		try {
			return new Descriptor(DescriptorType.parse(columnStr));
		} catch (UnmappedEnumValueException UEVE) { }
		
		//Test for identifiers
		try {
			return Identifier.parse(columnStr);
		} catch (UnmappedEnumValueException UEVE) { }
		
		if(columnStr.contains("flex")) { return new Descriptor(DescriptorType.FLEXIBILITY); }
		
		if(columnStr.contains("e22")) { return new Descriptor(DescriptorType.E22); }
		if(columnStr.contains("e20")) { return new Descriptor(DescriptorType.E20); }
		if(columnStr.contains("e6")) { return new Descriptor(DescriptorType.E6); }
		if(columnStr.contains("entropy")) { 
			if(columnStr.contains("6")) { return new Descriptor(DescriptorType.E6); }
			if(columnStr.contains("20")) { return new Descriptor(DescriptorType.E20); }
			if(columnStr.contains("22")) { return new Descriptor(DescriptorType.E22); }
		}
		
		if(columnStr.startsWith("vk")) {
			String tempStr = columnStr.substring(2).strip();
			if(tempStr.length() == 0) { return new Descriptor(DescriptorType.VKBAT); }
			while(tempStr.startsWith("-")) {
				tempStr = tempStr.substring(1);
				if(tempStr.length() == 0) { return new Descriptor(DescriptorType.VKBAT); }
			}
			VKPred maybePred2 = VKPred.parse(tempStr);
			if(maybePred != VKPred.UNKNOWN) { return maybePred2; }
		}
		
		if(columnStr.contains("vk")) {
			if(columnStr.contains("comp")) { return new Descriptor(DescriptorType.VKBAT_COMPLETION); }
			else { return new Descriptor(DescriptorType.VKBAT); }
		}
		
		if(columnStr.contains("charge")) {
			if(columnStr.contains("α") || columnStr.contains("alpha")) {
				return new Descriptor(DescriptorType.CHARGE_Cα);
			} else if(columnStr.contains("β") || columnStr.contains("beta")) {
				return new Descriptor(DescriptorType.CHARGE_Cβ);
			} else if(columnStr.contains("'") || columnStr.contains("p")) {
				return new Descriptor(DescriptorType.CHARGE_CP);
			} else if(columnStr.contains("hn")||columnStr.contains("nh")) {
				return new Descriptor(DescriptorType.CHARGE_NH);
			} else if(columnStr.contains("n")) {
				return new Descriptor(DescriptorType.CHARGE_N);
			} else if(columnStr.contains("o")) {
				return new Descriptor(DescriptorType.CHARGE_O);
			} else if(ToolBase.containsOneOf(columnStr, "average", "avg", "mean")) {
				return new Descriptor(DescriptorType.AVERAGE_CHARGE);
			} else if(ToolBase.containsOneOf(columnStr, "net")) {
				return new Descriptor(DescriptorType.NET_CHARGE);
			} 
		}
		
		if(ToolBase.containsOneOf(columnStr, "isu", "is-u")) { return new Descriptor(DescriptorType.ISUNSTRUCT); }
		
		throw new UnmappedEnumValueException(columnStr);
	}
	
	/**
	 * Compares two arrays of descriptors
	 * @param array1
	 * @param array2
	 * @return: whether the arrays contain the same descriptors, order non-withstanding
	 */
	public static boolean arrayEquals(Descriptor[] array1, Descriptor[] array2) {
		HashSet<Descriptor> set1 = new HashSet<Descriptor>();
		for(Descriptor desc1: array1) { set1.add(desc1); }
		
		HashSet<Descriptor> set2 = new HashSet<Descriptor>();
		for(Descriptor desc2: array2) { set2.add(desc2); }
		
		return set1.equals(set2);
	}
}
