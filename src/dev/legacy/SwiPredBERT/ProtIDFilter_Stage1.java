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

public class ProtIDFilter_Stage1 extends LocalToolBase {
	public static final int INTERVAL = 50;
	
	public static final String APPEND_FILE = "full_pdb_ok_nr_fasta.txt";
	
	public static void main(String[] args) {
		String[] lines = getFileLines("pdb_seqres.txt");
		qp("Read in: " + lines.length);
		
		LabeledSet<Struct_FastaLine> fastas = new LabeledSet<Struct_FastaLine>();
		
		long time = System.currentTimeMillis();
		
		for(int ii = 0; ii < lines.length; ii+=2) {
			Struct_FastaLine sfl = new Struct_FastaLine(lines[ii], lines[ii+1]);
			
			if(sfl.isProtein) {
				fastas.add(sfl);
			}			
		}
		qp("Finished initial sorting: " + fastas.size());
		
		
		qp("****"+fastas.size()+"****");
		StringBuilder sb = new StringBuilder();
		for(Struct_FastaLine sfl: fastas) {
			sb.append(sfl.id+"\n");
		}
		
		String[] out_lines = sb.toString().split("\n");
		
		writeFileLines("input/full_pdb_nr_fasta.txt", out_lines);
	}
	
}
