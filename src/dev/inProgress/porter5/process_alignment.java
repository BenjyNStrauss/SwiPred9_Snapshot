package dev.inProgress.porter5;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import assist.translation.python.PyFile;
import assist.translation.python.PythonTranslator;

//import os
//import sys
//import math
//from numpy import ravel as np

/**
 * 
 * 
 * @translator Benjamin Strauss
 *
 */

public class process_alignment extends PythonTranslator {
	
	/**
	 * Encode alignment file using a weighting scheme derived from Krogh et all (1995).
	 * Duplicates are filtered and external gaps are skipped.
	 * No gap is considered to calculate the weight of the sequence.
	 * Clipping of the AA in the query sequence is implemented as in https://doi.org/10.1101/289033.
	 * 
	 * Usage: python3 process-alignment.py alignment_file file_extension
	 * Expected input: 1 line header followed by primary sequences (and "." gaps) only.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String pattern = args[2]; // alignment extension
		String pattern1 = ".ann"; // encoded alignment extension
		String alignment = args[1]; // alignment path

		// create dictionary to represent the primary structure
		Hashtable<String, Integer> aa = new Hashtable<String, Integer>();
		
		aa.put("A", 0);
		aa.put("C", 1);
		aa.put("D", 2);
		aa.put("E", 3);
		aa.put("F", 4);
		aa.put("G", 5);
		aa.put("H", 6);
		aa.put("I", 7);
		aa.put("K", 8);
		aa.put("L", 9);
		aa.put("M", 10);
		aa.put("N", 11);
		aa.put("P", 12);
		aa.put("Q", 13);
		aa.put("R", 14);
		aa.put("S", 15);
		aa.put("T", 16);
		aa.put("V", 17);
		aa.put("W", 18);
		aa.put("Y", 19);
		aa.put("B", 20);
		aa.put("J", 20);
		aa.put("O", 20);
		aa.put("U", 20);
		aa.put("X", 20);
		aa.put("Z", 20);
		aa.put(".", 21);

		// open and read file to parse
		//f = open(alignment, "r");
		String[] lines_raw = getFileLines(alignment);

		// filter and delete duplicates
		ArrayList<String> lines = new ArrayList<String>();
		HashSet<String> lines_set = new HashSet<String>();
		for(String l : lines_raw) {
		    if(!lines_set.contains(l)) {
		        lines.add(l);
		    }
		    lines_set.add(l);  
		}
		// update number of sequences
		lines.set(0, ""+(len(lines) - 1));
		int sequences = len(lines) - 1;

		// delete pattern from the filename
		String pid = alignment.replace("."+pattern, "");
		// create file to fill up
		PyFile f = open(alignment + pattern1, "w");

		// write header and filename on the first 3 rows
		f.write("1\n22 3\n"+ pid + "\n");

		// check length and write it on the second row
		int length = len(lines.get(1).trim());
		f.write(str(length) + "\n");

		// create a list of lists to archive the frequencies,
		frequencies = [[0] * 22 for i in range(length)];
		// a list of lists to archive the extremities,
		extremities = [[0] * 2 for i in range(sequences)];
		// a list of list to archive the profiles,
		result = [[0] * 22 for i in range(length)];
		// and a list for the weighting scheme
		weights = [0] * int(lines[0]);


		if(sequences > 1) {
		    // check each column/AA to find the right and left extremity per sequence
		    for(int i = 0; i < sequences; ++i) {

		        for(int j = 0; j < length; ++j) {
		            if(lines[i+1][j] != ".") {
		                extremities[i][0] = j;
		                break;
		            }
		        }

		        for(int j = 0; j < length; ++j) {
		            if(lines[i+1][length-j-1] != ".") {
		                extremities[i][1] = length-j-1;
		                break;
		            }
		        }
		    }
		    
		    // check each column/AA to calculate its percentages
		    for(int j = 0; j < length; ++j) {
		        gaps = 0;

		        // how many gaps and invalid sequences in the column?
		        for(int i = 0; i < sequences; ++i) {
		            if(lines[i+1][j] == ".") {
		                gaps += 1;
		            }
		        }

		        // calculate the relative frequencies per column
		        add = 1 / (sequences - gaps);
		        for(int i = 0; i < sequences; ++i) {
		            if(lines[i+1][j] != ".") { // No gap is considered to calculate the weight of the sequence
		                frequencies[j][aa[lines[i+1][j]]] += add;
		            }
		        }

		        // calculate weights
		        for(int i = 0; i < sequences; ++i) {
		            if(lines[i+1][j] != ".") { // No gap is considered to calculate the weight of the sequence
		                weights[i] = weights[i] - math.log(frequencies[j][aa[lines[i+1][j]]]);
		            }
		        }
		    }
		    if(weights[0] > 0) {
		        // use the weights to create the profile
		        for(int j = 0; j < length; ++j) {
		            weightsSum = 0;

		            // calculate weights sum and entropy
		            for(int i = 0; i < sequences; ++i) {
		                if((! j < extremities[i][0]) && ! (j > extremities[i][1])) {
		                    result[j][aa[lines[i+1][j]]] += weights[i];
		                }
		            }
		            for(int i = 0; i < 21; ++i) { // Gaps are normalised separately
		                weightsSum += result[j][i];
		            }

		            // normalize result
		            for(int i = 0; i < 21; ++i) {
		                if(result[j][i] != 0) {
		                    result[j][i] = result[j][i] / weightsSum;
		                }
		            }
		            if(result[j][21] != 0) {
		                result[j][21] = result[j][21] / (weightsSum + result[j][21]);
		            }
		        }
		    } else if(sequences > 1) {
		        // 1 aligned sequence diverge only with gaps so no weights/entropy but gaps found
		        result = frequencies;
		    }
		}
		// clip to 1 the frequence of the AA presents in the profiled protein
		for(int j = 0; j < length; ++j) {
		    result[j][aa[lines[1][j]]] = 1.0;
		}

		// convert result (a list of lists) in a string and write out the result
		f.write(" ".join(map(str, np(result))) + "\n");
		f.close();
	}
}
