package dev.setup.dev;

import assist.base.Assist;

/**
 * A simple program to remove the line numbers from a source code file
 * @author Benjy Strauss
 *
 */

public class LineNumberRemover extends Assist {
	
	public static void main(String[] args) {
		String[] lines = getFileLines("for-hpc-only/blastpgp.c");
		for(int index = 0; index < lines.length; ++index) {
			lines[index] = lines[index].substring(4);
		}
		writeFileLines("for-hpc-only/blastpgp-custom.c", (Object[]) lines);
	}
	
}
