package dev;

import assist.util.LabeledSet;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class NoPDB_Tracker extends LocalToolBase {
	private static final String NO_DPB = "No PDB file exists for ";
	
	public static void main(String[] args) {
		LabeledSet<String> lineSet = new LabeledSet<String>();
		
		String[] lines = getFileLines("output.txt");
		for(String line: lines) {
			if(line.startsWith(NO_DPB)) {
				lineSet.add(line.substring(NO_DPB.length(), line.indexOf(":")));
			}
		}
		
		for(String line: lineSet) {
			qp(line);
		}
		qp(lineSet.size());
	}
}
