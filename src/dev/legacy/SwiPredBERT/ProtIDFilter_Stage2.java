package dev.legacy.SwiPredBERT;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

//import assist.base.Assist;
//import assist.exceptions.IORuntimeException;
//import assist.util.LabeledSet;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ProtIDFilter_Stage2 extends LocalToolBase {
	public static final int INTERVAL = 100;
	
	public static void main(String[] args) {
		//non-redundant proteins
		String[] lines = getFileLines("input/full_pdb_nr_ids.txt");
		
		qp("Read in full_pdb_nr_fasta.txt: " + lines.length);
		
		Arrays.sort(lines);
		
		long time = System.currentTimeMillis();
		
		boolean displayed = false;
		int successes = 0, total = 0, skipped = 0;
		for(int index = 0; index < lines.length; ++index) {
			if(lines[index].isEmpty()) { continue; }
			if(lines[index].contains("*")) { ++ skipped; continue; }
			
			if(!displayed) { displayed = true; qp("Skipped: "+ skipped); }
			
			String pdb_url = "https://files.rcsb.org/view/" + lines[index].substring(0,lines[index].indexOf("_")) + ".pdb";
			++total;
			URL url;
			
			while(true) {
				try {
					url = new URL(pdb_url);
					HttpURLConnection huc = (HttpURLConnection) url.openConnection();
					int responseCode = huc.getResponseCode();
					
					if(responseCode == HttpURLConnection.HTTP_OK) { 
						lines[index] += "*OK";
						++successes;
					} else {
						lines[index] += "*MISSNING";
					}
					
					break;
				} catch (IOException e) {
					qp("pausing for: " + lines[index]);
					pause(2000);
				}
			}
			
			if(total % INTERVAL == 0) { 
				System.out.print(successes+"/"+total + " ("+(successes*100/total) + "%) [Last "+INTERVAL + ": " + ((System.currentTimeMillis()-time)/1000) + " seconds] GC Start...");
				time = System.currentTimeMillis();
				System.gc();
				System.out.println("GC End");
				writeFileLines("input/full_pdb_nr_ids.txt", lines);
			}
		}
		
		writeFileLines("input/full_pdb_nr_ids.txt", lines);
	}
	
}
