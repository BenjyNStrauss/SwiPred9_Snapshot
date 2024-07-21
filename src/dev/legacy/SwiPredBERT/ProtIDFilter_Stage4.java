package dev.legacy.SwiPredBERT;

import java.util.ArrayList;

import assist.util.LabeledHash;
import biology.protein.ChainID;
import tools.Lookup;
import utilities.LocalToolBase;
import utilities.exceptions.LookupException;

/**
 * Final size = 613433 chains
 * 613433/788316 == 77.8%
 * Last = P03368
 * 
 * @author Benjamin Strauss
 *
 */

public class ProtIDFilter_Stage4 extends LocalToolBase {
	public static final int INTERVAL = 100;
	
	public static final String IN_FILE = "input/full_pdb_nr_usable_ids.txt";
	public static final String OUT_FILE = "input/full_pdb_nr_usable_go_ids.txt";
	
	public static void main(String[] args) {
		//non-redundant proteins
		String[] lines = getFileLines(IN_FILE);
		
		LabeledHash<String, ChainID> whiteList = new LabeledHash<String, ChainID>();
		
		for(String line: lines) {
			ChainID id = new ChainID();
			id.setProtein(line.substring(0,4));
			id.setChain(line.substring(5));
			
			try {
				Lookup.getUniprotFromRCSB(id);
			} catch (LookupException le) {
				continue;
			} catch (NumberFormatException nfe) {
				continue;
			}
			
			if(id.uniprot() != null) {
				whiteList.put(id.uniprot(), id);
			}
		}
		
		lines = getFileLines("@dev/train_taxonomy.tsv");
		for(String line: lines) {
			String[] fields = line.split("\t");
			if(whiteList.containsKey(fields[0])) {
				whiteList.get(fields[0]).setGO(Integer.parseInt(fields[1]));
			}
		}
		
		ArrayList<String> outLines = new ArrayList<String>();
		for(String key: whiteList.keySet()) {
			ChainID id = whiteList.get(key);
			if(id.goID() != ChainID.NOT_SET) {
				outLines.add(id.protein()+":"+id.chain()+" "+id.goID());
			}
		}
		
		writeFileLines(OUT_FILE, outLines);
		qp("Dataset size: " + outLines.size());
	}
	
}
