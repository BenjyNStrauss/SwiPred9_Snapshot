package examples.bondugula;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class BonSplitter extends LocalToolBase {
	public static final int CLUSTERS_PER_FILE = 64;

	public static void main(String[] args) {
		String[] lines = getFileLines("input/bon.tsv");
		String header = lines[0];
		int num_files = (lines.length-1)/CLUSTERS_PER_FILE + 1;
		
		String[][] newFileLines = new String[num_files][CLUSTERS_PER_FILE+1];
		
		for(int index = 0; index < lines.length-1; ++index) {
			int fileNo = index/CLUSTERS_PER_FILE;
			int linesNo = index%CLUSTERS_PER_FILE;
			newFileLines[fileNo][linesNo+1] = lines[index+1];
		}
		
		for(int index = 0; index < newFileLines.length; ++index) {
			newFileLines[index][0] = header;
			writeFileLines("input/bondugula_"+index+".tsv", newFileLines[index]);
		}
	}
}
