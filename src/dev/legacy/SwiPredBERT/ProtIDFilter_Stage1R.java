package dev.legacy.SwiPredBERT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.protein.ChainID;
import tools.Lookup;
import utilities.LocalToolBase;
import utilities.exceptions.LookupException;

/**
 * R for inclusion of redundancy
 * @author Benjamin Strauss
 * 
 * stalled @ 738000
 *
 */

public class ProtIDFilter_Stage1R extends LocalToolBase {
	public static final int INTERVAL = 50;
	
	public static final String APPEND_FILE = "full_pdb_ok_nr_fasta.txt";
	public static final String TMP_FILE = "tmp_pdb_w_ukb.txt";
	
	public static void main(String[] args) {
		String[] lines = getFileLines("input/pdb_seqres.txt");
		
		LabeledList<String> ids = new LabeledList<String>();
		for(String line: lines) {
			if(line.startsWith(">")) {
				ids.add(line.substring(1, line.indexOf(" ")));
			}
		}
		
		File tracker = new File(TMP_FILE);
		try {
			tracker.createNewFile();
		} catch (IOException e) { }
		
		qp("Finished Pt1: " + ids.size());
		LabeledHash<String, ChainID> whiteList = new LabeledHash<String, ChainID>();
		
		for(int index = 0; index < lines.length; ++index) {
			String line = ids.get(index);
			
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
				appendFileLines(tracker, id.protein() + ":" + id.chain() + ":" + id.uniprot());
			}
			
			//do garbage collection every 200
			if((index + 1)%10000 == 0) {
				qp("@Index: "+(index+1));
				System.gc();
			}
		}
		
		qp("Finished Pt2: " + whiteList.size());
		
		lines = getFileLines("@dev/uniprot-id-to-go-annotation.tsv");
		for(String line: lines) {
			String[] fields = line.split("\t");
			if(whiteList.containsKey(fields[0])) {
				whiteList.get(fields[0]).setGO(Integer.parseInt(fields[1]));
			}
		}
		
		qp("Finished Pt3");
		
		ArrayList<String> outLines = new ArrayList<String>();
		for(String key: whiteList.keySet()) {
			ChainID id = whiteList.get(key);
			if(id.goID() != ChainID.NOT_SET) {
				outLines.add(id.protein()+":"+id.chain()+" "+id.goID());
			}
		}
		
		writeFileLines("input/full_pdb_wr_usable_go_ids.txt", outLines);
		qp("Dataset size: " + outLines.size());
	}
	
}
