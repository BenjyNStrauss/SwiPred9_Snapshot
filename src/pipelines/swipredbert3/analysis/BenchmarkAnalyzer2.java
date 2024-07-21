package pipelines.swipredbert3.analysis;

import pipelines.swipredbert3.CommonTools;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class BenchmarkAnalyzer2 extends CommonTools {
	
	/*private static final int P   = 0;
	private static final int PS3 = 1;
	private static final int PS8 = 2;
	private static final int  S3 = 3;
	private static final int  S8 = 4;
	private static final int PBERT = 5;
	
	private static final int TRAINING = 0;
	private static final int VALIDATION = 0;
	private static final int TEST = 0;*/
	
	public static void main(String[] args) {
		final String[] lines = getFileLines("output/pbert-log-combined.txt");
		
		//final String[][] data = new String[6][3];
		String token = "", metric = "", set = "";
		
		for(String line: lines) {
			line = cleanLine(line);
			
			//
			if(line.startsWith("==========")) {
				String[] fields = line.split("\\s+");
				if(fields[1].endsWith("-")) { fields[1] = fields[1].substring(0, fields[1].length()-1); }
				token = fields[1];
				metric = fields[3];
				continue;
			} else if(line.startsWith("*** ")) {
				set = line.split("\\s+")[1];
				set = set.substring(0, set.length()-4);
			} else if(line.toLowerCase().startsWith("all")) {
				double accuracy = Double.parseDouble(line.split("\\s+")[2]);
				qp(token + " : " + metric + " : " + set + " : " + accuracy);
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
