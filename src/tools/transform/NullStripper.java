package tools.transform;

import java.util.List;

import assist.util.LabeledList;
import tools.reader.csv.CSVReaderTools;

/**
 * Designed to remove null residues from lists of descriptor CSVs
 * @author Benjamin Strauss
 *
 */

public class NullStripper extends CSVReaderTools {
	public static void main(String[] args) {
		if(args.length == 0) { qp("Error: no files specified"); }
		for(String file: args) {
			cleanFile(file);
		}
	}
	
	public static void cleanFile(String filename) {
		cleanFile(filename, filename);
	}
	
	public static void cleanFile(String infile, String outfile) {
		String[] lines = getFileLines(infile);
		int resCol = detectResCol(lines[0].split(","));
		List<String> outList = new LabeledList<String>();
		//add the header
		outList.add(lines[0]);
		for(int index = 1; index < lines.length; ++index) {
			String[] tokens = lines[index].split(",");
			if(!tokens[resCol].equals("null") && !tokens[resCol].equals("_")) {
				outList.add(lines[index]);
			}
		}
		writeFileLines(outfile, outList);
	}
	
	/**
	 * Cleans null residues out of a list of strings
	 * @param residueCSV
	 * @param resCol
	 */
	public static void cleanList(List<String> residueCSV, int resCol) {
		for(int index = 1; index < residueCSV.size(); ++index) {
			String[] tokens = residueCSV.get(index).split(",");
			if(tokens[resCol].equals("null") && tokens[resCol].equals("_")) {
				residueCSV.remove(index);
				--index;
			}
		}
	}
}
