package dev.legacy.SwiPredBERT;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import assist.util.LabeledList;
import assist.util.LabeledSet;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class FileHeaderScanner extends LocalToolBase {

	public static void main(String[] args) throws IOException {
		
		final String lines[] = getFileLines("input/full_pdb_r_usable_go_ids.txt");
		final LabeledSet<String> ukbIDs = new LabeledSet<String>();
		
		final LabeledList<String> mappingLines = new LabeledList<String>();
		
		for(String line: lines) {
			ukbIDs.add(line.split(":")[2]);
		}
		
		final String filePath = "@dev/goa_uniprot_all.gaf";
		
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		for(int ii = 1; reader.ready(); ++ii) {
			String line = reader.readLine();
			
			String[] fields = line.split("\t");
			if(fields.length < 3) { continue; }
			
			if(ukbIDs.contains(fields[2])) {
				mappingLines.add(line);
			}
			
			if(ii % 1000000 == 0) {
				qp("@line: "+(ii/1000000) +"m");
				System.gc();
			}
		}
		reader.close();
		
		writeFileLines("input/relevant-pdb-mappings.txt", mappingLines);
	}

}
