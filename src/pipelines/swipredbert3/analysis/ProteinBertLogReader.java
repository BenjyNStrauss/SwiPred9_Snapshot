package pipelines.swipredbert3.analysis;

import pipelines.swipredbert3.CommonTools;

/**
 * (Target) Output Format: 
 * Token,Predictor (Train,Test,Valid)
 * 
 * 
 * @author Benjamin Strauss
 * 
 */

public class ProteinBertLogReader extends CommonTools {
	
	public static void main(String[] args) {
		final String[] lines = getFileLines("output/pbert-log-nr-196e-4ft.txt");
		
		final double[] data = new double[3];
		String tokenType = "", metric = "", set = "";
		
		qp("Token,Predictor,Train,Test,Valid");
		
		for(String line: lines) {
			line = cleanLine(line);
			
			if(line.startsWith("==========")) {
				String[] fields = line.split("\\s+");
				if(fields[1].endsWith("-")) { fields[1] = fields[1].substring(0, fields[1].length()-1); }
				tokenType = fields[1];
				metric = fields[3];
				continue;
			} else if(line.startsWith("*** ")) {
				set = line.split("\\s+")[1];
				set = set.substring(0, set.length()-4);
			} else if(line.toLowerCase().startsWith("all")) {
				switch(set.toLowerCase()) {
				case "training":
					data[0] = Double.parseDouble(line.split("\\s+")[2]);
					break;
				case "validation":
					data[2] = Double.parseDouble(line.split("\\s+")[2]);
					break;
				case "test":
					data[1] = Double.parseDouble(line.split("\\s+")[2]);
					qp(tokenType + "," + metric + "," + data[0] + "," + data[1] + "," + data[2]);
					break;
				}
			}
		}
	}
	
	private static String cleanLine(String line) {
		if(line.startsWith("[")) {
			line = line.substring(line.indexOf("]")+2);
		}
		return line;
	}

}
