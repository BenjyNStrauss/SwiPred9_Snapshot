package dev.inProgress.sable.dev;

import assist.util.LabeledList;
import dev.inProgress.sable.NetworkBase;
import install.DirectoryManager;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class SableNNExtractor extends LocalToolBase {
	private static final String FILE_META = "/Approx_el_1";
	private static final String FILE_IN = "dev/sable_v4_distr/complexSA"+FILE_META+".c";
	private static final String FILE_OUT = DirectoryManager.FILES_PREDICT_SABLE + FILE_META+".txt";

	public static void main(String[] args) {
		LabeledList<String> units = new LabeledList<String>();
		LabeledList<Float> weights = new LabeledList<Float>();
		LabeledList<String> structs = new LabeledList<String>();
		structs.add("0.0,0.0,0,null,null");
		
		String[] lines = getFileLines(FILE_IN);
		//the stage of the parsing...
		int stage = 0;
		boolean been3 = false;
		
		for(int index = 0; index < lines.length; ++index) {
			String line = lines[index];
			if(line.trim().length() == 0) { continue; }
			boolean changedState = false;
			
			switch(line.trim()) {
			case "static pUnit Sources[] =  {":
				stage = 1;
				changedState = true;
				break;
			case "};":
				stage = 0;
				break;
			case "static float Weights[] =  {":
				stage = 1;
				changedState = true;
				break;
			case "{":
				if(!been3) {
					been3 = true;
					stage = 3;
					++index;
					changedState = true;
				}
				break;
			}
			
			if(changedState) { continue; }
			
			switch(stage) {
			case 1:
				line = line.replaceAll("\\s+", "");
				line = line.replaceAll("Units", "");
				line = line.replaceAll("\\+", "");
				units.add(line.substring(0,line.length()-1));
				break;
			case 2:
				line = line.replaceAll("\\s+", "");
				String[] values = line.split(",");
				for(String value: values) {
					weights.add(Float.parseFloat(value));
				}
				break;
			case 3:
				StringBuilder builder = new StringBuilder();
				builder.append(lines[index+1].replaceAll("\\s+", ""));
				//qp("^"+builder.toString());
				//System.exit(3);
				String source = lines[index+2].replaceAll("\\s+", "");
				source = source.replaceAll("&Sources\\[", "");
				source = source.replaceAll("\\],", "");
				builder.append(source+",");
				String weight = lines[index+3].replaceAll("\\s+", "");
				weight = weight.replaceAll("&Weights\\[", "");
				weight = weight.replaceAll("\\],", "");
				builder.append(weight+",");
				trimLastChar(builder);
				structs.add(builder.toString());
				index += 4;
				break;
			}
		}
		
		appendFileLines(FILE_OUT, NetworkBase.UNITS);
		appendFileLines(FILE_OUT, units);
		appendFileLines(FILE_OUT, NetworkBase.WEIGHTS);
		appendFileLines(FILE_OUT, weights);
		appendFileLines(FILE_OUT, NetworkBase.STRUCTS);
		appendFileLines(FILE_OUT, structs);
		System.exit(0);
	}

}
