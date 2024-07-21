package dev.legacy.SwiPredBERT;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import analysis.Struct_FastaLine;
import assist.base.Assist;
//import assist.exceptions.IORuntimeException;
import assist.util.LabeledSet;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ProtIDFilter extends LocalToolBase {
	public static final int INTERVAL = 50;
	
	public static final String APPEND_FILE = "full_pdb_ok_nr_fasta.txt";
	
	public static void main(String[] args) {
		String[] lines = getFileLines("pdb_seqres.txt");
		qp("Read in: " + lines.length);
		
		String[] lines2 = fileExists(APPEND_FILE) ? getFileLines(APPEND_FILE) : new String[0];
		
		LabeledSet<Struct_FastaLine> fastas = new LabeledSet<Struct_FastaLine>();
		
		long time = System.currentTimeMillis();
		
		for(int ii = 0; ii < lines.length; ii+=2) {
			Struct_FastaLine sfl = new Struct_FastaLine(lines[ii], lines[ii+1]);
			
			if(sfl.isProtein && !Assist.stringArrayContains(lines2, sfl.id)) {
				fastas.add(sfl);
			}			
		}
		qp("Finished initial sorting: " + fastas.size());
		
		int successes = 0, total = 0;
		for(Struct_FastaLine sfl: fastas) {
			String pdb_url = "https://files.rcsb.org/view/" + sfl.id.substring(0,sfl.id.indexOf("_")) + ".pdb";
			++total;
			URL url;
			
			//qp(sfl);
			
			//boolean ok = false;
			
			while(true) {
				try {
					url = new URL(pdb_url);
					HttpURLConnection huc = (HttpURLConnection) url.openConnection();
					int responseCode = huc.getResponseCode();
					sfl.hasURL = responseCode == HttpURLConnection.HTTP_OK;
					if(sfl.hasURL) { 
						appendFileLines(APPEND_FILE, sfl.id);
						++successes;
					}
					
					break;
				} catch (IOException e) {
					qp("pausing for: " + sfl);
					pause(50);
				}
			}
			
			if(total % INTERVAL == 0) { 
				qp(successes+"/"+total + " ("+(successes*100/total) + "%) [Last "+INTERVAL + ": " + ((System.currentTimeMillis()-time)/1000) + " seconds ]");
				time = System.currentTimeMillis();
				System.gc();
			}
		}
		
		qp("****"+fastas.size()+"****");
		StringBuilder sb = new StringBuilder();
		for(Struct_FastaLine sfl: fastas) {
			sb.append(sfl.id+"\n");
		}
		
		writeFileLines("full_pdb_nr_fasta.txt", sb.toString());
	}
	
}
