package dev.legacy.SwiPredBERT;

import java.util.ArrayList;

import assist.util.LabeledHash;
import biology.protein.ChainID;
import utilities.LocalToolBase;

/**
 * R for inclusion of redundancy
 * @author Benjamin Strauss
 * 
 * stalled @ 738000
 *
 */

public class ProtIDFilter_Stage1RC extends LocalToolBase {
	public static final int INTERVAL = 50;
	
	public static final String APPEND_FILE = "full_pdb_ok_nr_fasta.txt";
	public static final String TMP_FILE = "input/pipeline-temp/tmp_pdb_w_ukb.txt";
	
	public static void main(String[] args) {
		
		String[] lines = getFileLines(TMP_FILE);
		LabeledHash<String, ChainID> whiteList = new LabeledHash<String, ChainID>();
		
		for(int index = 0; index < lines.length; ++index) {
			String line = lines[index];
			if(line.startsWith("*")) { continue; }
			
			ChainID id = new ChainID();
			String[] fields = line.split(":");
			
			id.setProtein(fields[0]);
			id.setChain(fields[1]);
			id.setUniprot(fields[2]);
			
			whiteList.put(id.standard(), id);
		}
		
		qp("Finished Pt1: " + whiteList.size());
		
		ChainID[] array = new ChainID[lines.length];
		whiteList.toValuesArray(array);
		
		int tracker = 1;
		lines = getFileLines("@dev/train_taxonomy.tsv");
		for(String line: lines) {
			String[] fields = line.split("\t");
			
			for(ChainID id: array) {
				if(id != null) {
					if(id.uniprot().equalsIgnoreCase(fields[0])) {
						id.setGO(Integer.parseInt(fields[1]));
					}
				}
			}
			if(tracker % 10000 == 0) { qp("At: " + tracker); }
		}
		
		qp("Finished Pt2");
		
		ArrayList<String> outLines = new ArrayList<String>();
		for(ChainID id: array) {
			if(id != null) {
				if(id.goID() != ChainID.NOT_SET) {
					outLines.add(id.protein()+":"+id.chain()+":"+id.uniprot()+":"+id.goID());
				} else {
					//outLines.add(id.protein()+":"+id.chain()+":<NULL>");
				}
			}
		}
		
		writeFileLines("input/full-rcsb-go-mapping.txt", outLines);
		qp("Dataset size: " + outLines.size());
	}
	
}
