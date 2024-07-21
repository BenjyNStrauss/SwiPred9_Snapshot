package dev.inProgress.entropy;

import assist.translation.python.PyDict;
import assist.translation.python.PythonTranslator;

/*from functools import partial
import math
import sys

import pandas as pd
from scipy.stats import entropy*/

public class entropy extends PythonTranslator {

//from lib_entropy.AminoAcidClasses import E6, E20
//from bin.lib_entropy.AminoAcidClasses import E6, E20

	//################################################################################
	//# Define necessary functions
	//################################################################################
	public static String _map_to_classes(String seq, PyDict encodeMap) {
		return _map_to_classes(seq, encodeMap, "remove", "remove");
	}
	
	public static String _map_to_classes(String seq, PyDict encodeMap, String gaps) {
		return _map_to_classes(seq, encodeMap, gaps, "remove");
	}
	
	/*public static String _map_to_classes(String seq, PyDict encodeMap, String X) {
		return _map_to_classes(seq, encodeMap, "remove", X);
	}*/
	
	public static String _map_to_classes(String seq, PyDict encodeMap, String gaps, String X) {
	    /* Remap characters in a sequence to classes
	
	    Used for entropy amino acid classes
	
	    params:
	        gaps: one of 'remove', 'keep', 'convert'
	
	    Raises KeyError if character has no mapping defined
	    */
	    // float "Nan" refers to cases with no alignments at the position
	    if(seq instanceof Float) {
	        return seq;
	    } else {
	        String newSeq = "";
	        char[] chars = seq.toCharArray();
	        for(char _char: chars) {
	            // deal with gap characters
	            if (_char == '-') {
	                // remove gaps by default
	                if (gaps.equals("remove")) {
	                    continue;
	                } else if (gaps.equals("keep")) {
	                    newSeq += _char;
	                // requires encoding defined for gap character
	                } else if(gaps == "convert") {
	                    newSeq += encodeMap[_char];
	                } else {
	                    throw new ValueError("gaps argument option "+gaps+" not implemented");
	                }
	            }
	
	            // deal with gap characters
	            if(_char == 'X') {
	                // remove gaps by default
	                if(X.equals("remove")) {
	                    continue;
	                } else if(X.equals("keep")) {
	                    newSeq += charl;
	                // requires encoding defined for gap character
	                } else if(X == "convert") {
	                    newSeq += encodeMap[_char];
	                } else {
	                    throw new ValueError("gaps argument option "+gaps+" not implemented");
	                }
	            } else {
	                newSeq += encodeMap[_char];
	            }
	        }
	    }
	
	    return newSeq;
	}


	Object _entropy(Object seq) {
	    /* wrapper around scipy shannon entropy function
	
	    passes Nan back if encountered
	
	    */
	    // float "Nan" refers to cases with no alignments at the position
	    if(seq instanceof Float) {
	        return seq;
	    } else {
	        seq = pd.Series(list(seq));
	        seq = seq.value_counts();
	        return entropy(seq, base=LOG_BASE);
	    }
    }

	public static void main(String[] args) {
		//################################################################################
		//# Input/Output and parameters set
		//################################################################################
		String i_csv = args[1];
		//i_csv = "/home/joribello/Documents/OneDrive/Research/Spring_2020_Research/dataset_prep_nf/work/8a/f227932e0adadd501f906e6a7fa34d/1o51_A.csv"

		// logic here allows "e"
		Object LOG_BASE = args[2];
		if(LOG_BASE == "e") {
		    LOG_BASE = Math.E;
		} else {
		    LOG_BASE = (float) LOG_BASE;
		}

		String o_csv = args[3];
		
		//################################################################################
		//# Read in input and Compute entropies
		//################################################################################
		df = pd.read_csv(i_csv);
		
		//# E6 calculations
		e6_encoder = partial(_map_to_classes, encodeMap=E6);
		df["E6_alignments"] = df["alignments"].apply(e6_encoder);
		df["E6"] = df["E6_alignments"].apply(_entropy);
		
		//# E20 calculations
		E20_encoder = partial(_map_to_classes, encodeMap=E20);
		df["E20_alignments"] = df["alignments"].apply(E20_encoder);
		df["E20"] = df["E20_alignments"].apply(_entropy);
		
		//################################################################################
		//# Write to file
		//################################################################################
		df = df.set_index("query_index");
		df.to_csv(o_csv);
    }

}
