package tools.writer.csv;

import assist.exceptions.UnmappedEnumValueException;
import assist.exceptions.UserErrorException;
import assist.util.LabeledList;
import biology.descriptor.Metric;
import system.Instruction;
import utilities.LocalToolBase;

/**
 * 
 * @author bns
 *
 */

public final class DynamicWriterFactory extends LocalToolBase {
	private static final String[] FIELDS = { "-f", "-fields" };
	
	private DynamicWriterFactory() { }
	
	/**
	 * TODO: in progress
	 * @param instr
	 * @return
	 */
	public static AbstractDescriptorCSVWriter makeWriter(Instruction instr) {
		LabeledList<Metric> fields = new LabeledList<Metric>();
		if(!instr.hasArgumentNamed(true, FIELDS)) {
			throw new UserErrorException("Fields not specified by user.\n Use \"-f=[fields]\" to specify fields; separate with comma");
		}
		
		String fieldStr = instr.getFirstArgumentNamed(true, FIELDS);
		fieldStr = fieldStr.trim();
		if(fieldStr.startsWith("[")) { fieldStr = fieldStr.substring(1); }
		if(fieldStr.endsWith("]")) { fieldStr = fieldStr.substring(0, fieldStr.length()-1); }
		fieldStr = fieldStr.trim();
		String[] rawFields = fieldStr.split(",");
		for(String rawField: rawFields) {
			try {
				fields.add(Metric.parse(rawField));
			} catch (UnmappedEnumValueException UEVE) {
				error("Cannot parse string: +\""+rawField+"\"");
				error("If you think you are seeing this method in error, please contact: " + BMAIL);
			}
		}
		
		AbstractDescriptorCSVWriter writer = new DescriptorCSVWriter3();
		for(Metric metric: fields) {
			writer.add(metric);
		}
		
		return writer;
	}
}
