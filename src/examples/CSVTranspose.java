package examples;

import utilities.LocalToolBase;

/**
 * Use to Transpose a CSV file
 * @author Benjamin Strauss
 *
 */

public class CSVTranspose extends LocalToolBase {
	
	public static void main(String... args) {
		String filename = "output/aucs.csv";
		String[] lines = getFileLines(filename);
		String[][] values = new String[lines.length][], transpose;
		
		for(int ii = 0; ii < lines.length; ++ii) {
			values[ii] = lines[ii].split(",");
		}
		
		transpose = new String[values[0].length][lines.length];
		
		for(int ii = 0; ii < lines.length; ++ii) {
			for(int jj = 0; jj < values[0].length; ++jj) {
				transpose[jj][ii] = values[ii][jj];
			}
		}
		
		String[] outLines = new String[transpose.length];
		for(int ii = 0; ii < transpose.length; ++ii) {
			outLines[ii] = combineStrings(transpose[ii]);
		}
		
		writeFileLines("output/aucs-filt0.3-vk-local.csv", outLines);
		
	}

	private static String combineStrings(String[] array) {
		StringBuilder builder = new StringBuilder();
		for(String str: array) {
			builder.append(str + ",");
		}
		
		trimLastChar(builder);
		return builder.toString();
	}
	
}
