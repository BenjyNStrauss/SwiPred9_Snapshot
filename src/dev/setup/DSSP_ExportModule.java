package dev.setup;

import java.io.File;

import assist.script.Script;
import utilities.LocalToolBase;

/**
 * Exports a subset of DSSP Files
 * @author Benjy Strauss
 *
 */

public class DSSP_ExportModule extends LocalToolBase {
	
	public static void main(String[] args) {
		exportDSSP("input/learn-4.txt");
	}
	
	public static void exportDSSP(String dataSetFilePath) {
		String[] filelines = getFileLines(dataSetFilePath);
		File dssp_export_dir = new File("output/dssp-export");
		if(!dssp_export_dir.exists()) { dssp_export_dir.mkdir(); }
		
		for(String line: filelines) {
			if(line.length() > 4) {
				line = line.substring(0, 4) + ".dssp";
				String[] cp_params = {
						"cp",
						"files/dssp/"+line,
						dssp_export_dir.getPath()+"/"+line
				};
				Script.runScript(cp_params);
			}
		}
		
	}
}
