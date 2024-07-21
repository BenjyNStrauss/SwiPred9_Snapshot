package dev.legacy.SwiPredBERT;

import java.util.ArrayList;

//import assist.base.Assist;
//import assist.exceptions.IORuntimeException;
//import assist.util.LabeledSet;
import utilities.LocalToolBase;

/**
 * Final size = 613433 chains
 * 613433/788316 == 77.8%
 * 
 * @author Benjamin Strauss
 *
 */

public class ProtIDFilter_Stage3 extends LocalToolBase {
	public static final int INTERVAL = 100;
	
	public static final String APPEND_FILE = "input/full_pdb_nr_ids.txt";
	
	public static void main(String[] args) {
		//non-redundant proteins
		String[] lines = getFileLines("input/full_pdb_nr_ids.txt");
		
		ArrayList<String> whiteList = new ArrayList<String>();
		
		for(String line: lines) {
			if(line.endsWith("*OK")) {
				whiteList.add(line.substring(0,line.length()-3));
			}
		}
		
		qp(whiteList.size());
		writeFileLines("input/full_pdb_nr_usable_ids.txt", whiteList);
	}
	
}
