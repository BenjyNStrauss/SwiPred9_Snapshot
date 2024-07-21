package dev.setup.dev;

import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * Just used to make scripts
 * @author Benjy Strauss
 *
 */

//@SuppressWarnings("unused")
public class ScriptMaker extends LocalToolBase {
	
	public static void main(String[] args) {
		String[] lines = getFileLines("input/bon.tsv");
		
		for(int i = 0; i < 8; ++i) {
			LabeledList<String> outLines = new LabeledList<String>();
			outLines.add(lines[0]);
			int start = i * 800;
			for(int lineNo = 1; lineNo <= 800; ++lineNo) {
				if(start+lineNo == lines.length) { break; }
				outLines.add(lines[start+lineNo]);
			}
			
			writeFileLines("input/bon"+i+".tsv", outLines);
			makeVKScript(i);
		}
	}
	
	private static void makeVKScript(int i) {
		LabeledList<String> fileLines = new LabeledList<String>();
		fileLines.add("#!/bin/bash");
		fileLines.add("");
		fileLines.add("#SBATCH -p nodes");
		fileLines.add("#SBATCH --job-name=\"VK3-L"+i+"-BNS\"");
		fileLines.add("");
		fileLines.add("#SBATCH -o /home/bstrauss/vk-out3-"+i+".txt");
		fileLines.add("#SBATCH -e /home/bstrauss/vk-err3-"+i+".txt");
		fileLines.add("");
		fileLines.add("#SBATCH -c 4");
		fileLines.add("#SBATCH --mem=96gb");
		fileLines.add("");
		fileLines.add("#SBATCH -D /home/bstrauss/swipred");
		fileLines.add("#SBATCH --time=100000:59:59");
		fileLines.add("");
		fileLines.add("# #SBATCH --test-only");
		fileLines.add("");
		fileLines.add("module load java/11");
		fileLines.add("java -Xmx96g -Xms40g -jar SwiPredVKL.jar "+i);
		
		writeFileLines("scripts/vk3-local-"+i+".sh", fileLines);
	}
}
