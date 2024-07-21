package dev.setup.dev;

import install.Gor4FileManager;
import utilities.LocalToolBase;

/**
 * Used to deal with files that are too long to put in a single String
 * @author Benjamin Strauss
 *
 */

public class FileSplitter extends LocalToolBase {

	public static void main(String[] args) {
		String[] fileLines = getFileLines(Gor4FileManager.OBS_FILENAME);
		
		for(int index = 0; index < fileLines.length; ++index) {
			//fileLines[index] = fileLines[index].replaceAll("\\", "\\\\");
			qp("\"" + fileLines[index] + "\",");
		}
		
		/*fileLines = getFileLines(Gor4FileManager.OBS_FILENAME);
		
		for(int index = 0; index < fileLines.length; ++index) {
			qp("\"" + fileLines[index] + "\",");
		}*/
	}

}
