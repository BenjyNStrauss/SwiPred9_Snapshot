package pipelines.swipredbert3.analysis;

import pipelines.swipredbert3.CommonTools;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class BenchmarkAnalyzer3 extends CommonTools {
	
	private static final String[][][] TABLE = new String[4][10][7];
	private static int tableNo = 0;
	
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
				
				placeInTable(token, metric, set, accuracy);
				
				if(tableFull()) { ++tableNo; }
			}
		}
		
		for(String[][] table: TABLE) {
			for(String[] row: table) {
				qp(row);
			}
			qp("");
		}
		
		computeAverages();
	}

	private static void placeInTable(String token, String metric, String set, double accuracy) {
		int x = 0, y = 0;
		switch(token) {
		case "ptoken":		x = 1;	break;
		case "ps8token":	x = 2;	break;
		case "ps3token":	x = 3;	break;
		case "s8token":		x = 4;	break;
		case "s3token":		x = 5;	break;
		case "pberttoken":	x = 6;	break;
		}
		
		switch(metric) {
		case "family":		y = 1;	break;
		case "superfamily":	y = 4;	break;
		case "species":		y = 7;	break;
		}
		
		switch(set) {
		case "Training":	y += 0;	break;
		case "Test":		y += 1;	break;
		case "Validation":	y += 2;	break;
		}
		
		verifyToken(x, token);
		TABLE[tableNo][y][x] = ""+accuracy;
	}
	
	private static void verifyToken(int x, String token) {
		if(TABLE[tableNo][0][x] == null) {
			TABLE[tableNo][0][x] = token;
		} else if(!TABLE[tableNo][0][x].equals(token)) {
			throw new RuntimeException();
		}
	}
	
	private static boolean tableFull() {
		for(int ii = 1; ii < TABLE[tableNo].length; ++ii) {
			for(int jj = 1; jj < TABLE[tableNo][ii].length; ++jj) {
				if(TABLE[tableNo][ii][jj] == null) {
					return false;
				}
			}
		}
		
		return true;
	}

	private static String cleanLine(String line) {
		if(line.startsWith("[")) {
			line = line.substring(line.indexOf("]")+2);
		}
		return line;
	}
	
	private static void computeAverages() {
		qp("Averages: ");
		
		for(int col = 1; col < 7; ++col) {
			double dssp = 0, unp = 0;
			for(int table_no: new int[]{0, 1}) {
				for(int row = 1; row < 10; ++row) {
					dssp += Double.parseDouble(TABLE[table_no][row][col]);
				}
			}
			
			dssp /= 18;
			
			for(int table_no: new int[]{2, 3}) {
				for(int row = 1; row < 10; ++row) {
					unp += Double.parseDouble(TABLE[table_no][row][col]);
				}
			}
			
			unp /= 18;
			
			qp(dssp + " : " + unp);
		}
	}
}
