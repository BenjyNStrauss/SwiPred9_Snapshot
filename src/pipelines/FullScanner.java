package pipelines;

import java.io.File;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class FullScanner extends LocalToolBase {
	
	public static void main(String[] args) {
		File pdbs = new File("files/fasta/pdb");
		String[] filenames = pdbs.list();
		for(String name: filenames) {
			String[] lines = getFileLines("files/fasta/pdb/"+name);
			for(String line: lines) {
				if(line.contains("UNK ")) {
					qp(name+ "::"+ line);
				}
			}
		}
	}
}
