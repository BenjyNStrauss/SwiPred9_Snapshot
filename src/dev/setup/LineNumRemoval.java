package dev.setup;

import utilities.LocalToolBase;

/**
 * Designed to remove line numbers from text files...
 * @author Benjy Strauss
 *
 */

public class LineNumRemoval extends LocalToolBase {
	public static void main(String[] args) {
		fixFile("logger-fix/logger.java");
	}
	
	private static void fixFile(String filename) {
		String[] lines = getFileLines(filename);
		for(int i = 0; i < lines.length; ++i) {
			lines[i] = lines[i].substring(3);
		}
		writeFileLines(filename, lines);
	}
}
