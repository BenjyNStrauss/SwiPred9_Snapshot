package dev.chemParser;

import utilities.LocalToolBase;

/**
 * Designed to test Operations for the megaparser…
 * @author Benjamin Strauss
 *
 */

public class OperationTest extends LocalToolBase {

	public static void main(String[] args) {
		String[] lines = getFileLines("@dev/Glycine.html");
		for(String line: lines) {
			qp(line);
		}
	}

}
