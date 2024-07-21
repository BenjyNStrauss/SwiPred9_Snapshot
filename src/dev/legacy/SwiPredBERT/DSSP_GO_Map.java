package dev.legacy.SwiPredBERT;

import java.io.IOException;

import assist.util.LabeledList;
import assist.util.LabeledSet;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class DSSP_GO_Map extends LocalToolBase {

	public static void main(String[] args) throws IOException {
		/*
		 * Contains ALL GO ids for every uniprot ID that maps to a DSSP ID
		 */
		final String map_lines[] = getFileLines("input/re-bert/relevant-pdb-mappings.txt");
		
		final String  r_lines[] = getFileLines("input/re-bert/full_pdb_r_usable_go_ids.txt");
		final String nr_lines[] = getFileLines("input/re-bert/full_pdb_nr_usable_go_ids.txt");
		
		
		
		
		
		
	}

}
