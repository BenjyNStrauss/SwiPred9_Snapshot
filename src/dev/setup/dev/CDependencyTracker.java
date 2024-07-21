package dev.setup.dev;

import java.io.File;
import java.util.HashSet;

import assist.base.Assist;
import utilities.LocalToolBase;
//import java.util.Set;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class CDependencyTracker extends LocalToolBase {
	private static final String FILE_PATH = "dev/blastpgp-src";
	
	public static void main(String[] args) {
		File blastpgp_remake = new File(FILE_PATH);

		String[] files = blastpgp_remake.list();
		HashSet<String> dependencies = new HashSet<String>();
		//HashSet<String> filenames = new HashSet<String>();
		
		for(String str: files) {
			File file = new File(FILE_PATH + "/" + str);
			
			//qp("flag0 " + str);
			if(!file.isDirectory()) {
				String[] fileLines = getFileLines(file);
				
				for(String line: fileLines) {
					line = line.trim();
					
					//if(str.equals("seqmgr.c")) { qp(line); }
					
					if(line.startsWith("#include")) {
						line = line.substring(8);
						line = line.trim();
						//if(str.equals("seqmgr.c")) { qp("*1"); }
						line = Assist.removeCharsBetweenDelimiters(line, "/*", "*/");
						//if(str.equals("seqmgr.c")) { qp("*2"); }
						line = line.trim();
						line = line.replaceAll("<", "");
						line = line.replaceAll(">", "");
						//qp("adding: " + line);
						dependencies.add(line);
					}
					//if(str.equals("seqmgr.c")) { qp("done"); }
					
				}
			}
		}
		
		for(String str: files) {
			//qp("flag1");
			dependencies.remove(str);
		}
		
		for(String str: dependencies) {
			//qp("flag2");
			qp(str);
		}
	}
}
