package dev.legacy.SwiPredBERT;

import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * R for inclusion of redundancy
 * @author Benjamin Strauss
 * 
 * stalled @ 738000
 *
 */

public class ProtIDFilter_Stage2R extends LocalToolBase {
	public static final int INTERVAL = 50;
	
	public static final String IN_FILE = "input/pipeline-temp/full-rcsb-go.txt";
	public static final String OUT_FILE = "input/full-rcsb-go-mapping-ok.txt";
	
	public static void main(String[] args) {
		qp("Running…");
		String[] lines = getFileLines(IN_FILE);
		
		LabeledList<String> ids = new LabeledList<String>();
		
		for(String line: lines) {
			if(!line.contains("<NULL>")) {
				ids.add(line);
			}
		}
		
		writeFileLines(OUT_FILE, ids);
		qp("Dataset size: " + ids.size());
	}
	
}
