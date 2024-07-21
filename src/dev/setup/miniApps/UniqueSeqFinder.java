package dev.setup.miniApps;

import java.io.File;

import assist.util.LabeledList;
import biology.BioTools;
import utilities.LocalToolBase;

/**
 * Finds unique sequences in a FASTA that contains multiple sequences
 * @author Benjy Strauss
 *
 */

public class UniqueSeqFinder extends LocalToolBase {
	private static final String FILE_NAME = "input/PF01474_full_length_sequences.fasta";
	private static final String OUT_FILE = "myobact-seqs.txt";
	
	public static void main(String[] args) {
		String filename;
		if(args.length > 0) { filename = args[0]; }
		else { filename = FILE_NAME; }
		
		File inFile = new File(filename);
		if(!inFile.exists()) {
			qerr("Infile does not exist!");
			return;
		} else if(inFile.isDirectory()) {
			qerr("Infile is directory!");
			return;
		}
		
		String[] filelines = getFileLines(inFile);
		String[] fastas = BioTools.splitFasta(filelines);
		LabeledList<String> uniqueSeqs = new LabeledList<String>();
		
		for(int index = 0; index < fastas.length; ++index) {
			String seq = fastas[index].substring(fastas[index].indexOf("\n")+1);
			if(!uniqueSeqs.contains(seq)) {
				uniqueSeqs.add(seq);
			}
		}
		
		writeFileLines(OUT_FILE, uniqueSeqs);
	}

}
