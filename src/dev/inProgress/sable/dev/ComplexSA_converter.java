package dev.inProgress.sable.dev;

import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ComplexSA_converter extends LocalToolBase {
	
	public static void main(String[] args) {
		String[] lines = getFileLines("src/inProgress/sable/complexSA/Approx_el_1.java");
		LabeledList<String> output = new LabeledList<String>();
		boolean flag_Units = false;
		boolean flag_net = false;
		
		for(int index = 0; index < lines.length; ++index) {
			String line = lines[index];
			if(line.contains("Units + ")) {
				qp(line);
				//System.exit(0);
				line = line.replaceAll(", Units \\+ ", ",");
				line = line.replaceAll("Units \\+ ", "");
				if(line.endsWith(",")) {
					line = line.substring(0, line.length()-1) + ",";
				}			
				//line = line.replaceAll("};", "\\]};");
			} else if(line.equals("	    { 0.0, 0.0, 0, null , null },") || line.equals("	    { 0.0, 0.0, 0, NULL , NULL },")) {
				line = "	    new pUnit( 0.0, 0.0, 0, null, null),";
				flag_Units = true;
			} else if(line.equals("	  };")) {
				flag_Units = false;
			} else if(line.equals("int net")) {
				flag_net = true; 
			} /*else if(line.equals("	  };") || line.contains("definition")) {
				flag_weights = false;
			}*/ else if (flag_net) {
				if(line.trim().length() > 1) {
					String[] values = line.split(",");
					StringBuilder lineBuilder = new StringBuilder("\t\t");
					for(String value: values) {
						lineBuilder.append("(float)" + value + ",");
					}
					
					line = lineBuilder.toString();
				}
			} else if(flag_Units) {
				StringBuilder lineBuilder = new StringBuilder();
				lineBuilder.append(line.replaceAll("	    \\{ ", "\t\t"));
				//qp(lineBuilder);
				//System.exit(0);
				lineBuilder.append("\n\t\tnew pUnit( ");
				lineBuilder.append(lines[index+1].trim());
				String meta = lines[index+2].trim();
				if(meta.length() == 0) {
					output.add("");
					continue;
				}
				
				lineBuilder.append(" "+meta.substring(1, meta.length()-2));
				lineBuilder.append(", "+lines[index+3].trim().substring(1, lines[index+3].trim().length()-2)+"),");
				index += 4;
				line = lineBuilder.toString();
			}
			line = line.replaceAll("UnitType", "pUnit");
			line = line.replaceAll("->", ".");
			output.add(line);
		}
		
		writeFileLines("Approx-el-1-out.txt", output);
		System.exit(0);
	}
	
}
