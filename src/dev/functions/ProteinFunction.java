package dev.functions;

import java.io.File;
import java.text.DecimalFormat;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ProteinFunction extends LocalToolBase {

	public static void main(String[] args) {
		//qp("    VIRAL PROTEIN                           ".length());
		//System.exit(0);
		
		File files_fasta_pdb = new File("files/fasta/pdb");
		LabeledHash<String, Integer> headers = new LabeledHash<String, Integer>();
		
		String[] files = files_fasta_pdb.list();
		for(String filename: files) {
			if(!filename.endsWith(".pdb")) { continue; }
			
			String[] lines = getFileLines(files_fasta_pdb.getPath() + "/" + filename);
			for(String str: lines) {
				if(str.startsWith("HEADER")) {
					str = str.substring(6, 50).trim();
					if(!headers.containsKey(str)) {
						qp(filename + "::" + str);
						headers.put(str, 1);
					} else {
						headers.put(str, headers.get(str)+1);
					}
				} else {
					break;
				}
			}
		}
		
		DecimalFormat df = new DecimalFormat("00000");
		LabeledList<String> outLines = new LabeledList<String>();
		for(String key: headers.keySet()) {
			outLines.add(df.format(headers.get(key)) + " :: " + key);
		}
		
		writeFileLines("functions-list.txt", outLines);
		qp(headers.size() + " unique out of " + files.length);
	}

}
