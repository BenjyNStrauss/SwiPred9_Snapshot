package dev.functions;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ProteinFunction2 extends LocalToolBase {

	public static void main(String[] args) {
		String[] lines = getFileLines("functions-list.txt");
		
		for(String line: lines) {
			String[] parts = line.split("::");
			parts[0] = parts[0].trim();
			parts[1] = parts[1].trim();
			int occ = Integer.parseInt(parts[0]);
			if(occ > 16) {
				qp(parts[1]);
			}
		}
	}

}
