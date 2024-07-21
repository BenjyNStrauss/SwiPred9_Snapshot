package modules.descriptor.vkbat.choufasman;

import java.util.Objects;

import assist.translation.python.PythonTranslator;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.Pair;

/**
 * Summary:
 *    An implementation of the Chou-Fasman algorithm
 * Authors:
 * 		Samuel A. Rebelsky (layout of the program see:
 * 			http://www.cs.grinnell.edu/~rebelsky/ExBioPy/Projects/project-7.5.html)
 *		Nicolas Girault
 * @translator: Benjamin Strauss
 * URL to original: https://github.com/ravihansa3000/ChouFasman/blob/master/ChouFasman.py
 */

public class ChouFasman extends PythonTranslator {
	private static final boolean DEBUG = false;
	
	static String protein1 = "GSRDNLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVPRCPRCPADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV";
	static String protein2 = "MRRYEVNIVLNPNLDQSQLALEKEIIQRALENYGARVEKVAILGLRRLAYPIAKDPQGYFLWYQVEMPEDRVNDLARELRIRDNVRRVMVVKSQEPFLANA";
	static String protein3 = "MVGLTTLFWLGAIGMLVGTLAFAWAGRDAGSGERRYYVTLVGISGIAAVAYVVMALGVGWVPVAERTVFAPRYIDWILTTPLIVYFLGLLAGLDSREFGIVITLNTVVMLAGFAGAMVPGIERYALFGMGAVAFLGLVYYLVGPMTESASQRSSGIKSLYVRLRNLTVILWAIYPFIWLLGPPGVALLTPTVDVALIVYLDLVTKVGFGFIALDAAATLRAEHGESLAGVDTDAPAVAD";
	static String protein4 = "MKIDAIVGRNSAKDIRTEERARVQLGNVVTAAALHGGIRISDQTTNSVETVVGKGESRVLIGNEYGGKGFWDNHHHHHH";
	
	// The Chou-Fasman table, with rows of the table indexed by amino acid name.
	//   Data copied, pasted, and reformatted from 
	//     http://prowl.rockefeller.edu/aainfo/chou.htm
	// Columns are          SYM,P(a), P(b),P(turn), f(i),   f(i+1), f(i+2), f(i+3)

	private final LabeledHash<Double, Double> Pa = new LabeledHash<Double, Double>();
	private final LabeledHash<Double, Double> Pb = new LabeledHash<Double, Double>();
	private final LabeledHash<Double, Double> Pturn = new LabeledHash<Double, Double>();
	private final LabeledHash<Double, Double> F0 = new LabeledHash<Double, Double>();
	private final LabeledHash<Double, Double> F1 = new LabeledHash<Double, Double>();
	private final LabeledHash<Double, Double> F2 = new LabeledHash<Double, Double>();
	private final LabeledHash<Double, Double> F3 = new LabeledHash<Double, Double>();
	
	/*private static final String[] aa_names = {"Alanine", "Arginine", "Asparagine", "Aspartic Acid",
	            "Cysteine", "Glutamic Acid", "Glutamine", "Glycine",
	            "Histidine", "Isoleucine", "Leucine", "Lysine",
	            "Methionine", "Phenylalanine", "Proline", "Serine",
	            "Threonine", "Tryptophan", "Tyrosine", "Valine"};*/
	
	/**
	 * Constructor
	 * 
	 * Convert the Chou-Fasman table above to more convenient formats
	 *     Note that for any amino acid, aa CF[aa][0] gives the abbreviation
	 *     of the amino acid.
	 */
	public ChouFasman() {
		for(String aa: CFStruct.CF.keySet()){
			Pa.put(CFStruct.CF.get(aa).data[0], CFStruct.CF.get(aa).data[1]);
			Pb.put(CFStruct.CF.get(aa).data[0], CFStruct.CF.get(aa).data[2]);
			Pturn.put(CFStruct.CF.get(aa).data[0], CFStruct.CF.get(aa).data[3]);
			F0.put(CFStruct.CF.get(aa).data[0], CFStruct.CF.get(aa).data[4]);
			F1.put(CFStruct.CF.get(aa).data[0], CFStruct.CF.get(aa).data[5]);
			F2.put(CFStruct.CF.get(aa).data[0], CFStruct.CF.get(aa).data[6]);
			F3.put(CFStruct.CF.get(aa).data[0], CFStruct.CF.get(aa).data[7]);
		}
	}

	/**
	 * Find all likely alpha helices in sequence.
	 * Returns a list of [start,end] pairs for the alpha helices.
	 * 
	 * Seems to run in O(n^2)
	 * 
	 * @param seq
	 * @return
	 */
	LabeledList<Pair<Integer,Integer>> CF_find_alpha(String seq) {
	    int start = 0;
	    LabeledList<Pair<Integer,Integer>> results = new LabeledList<Pair<Integer,Integer>>();
	    // Try each window
	    while (start + 6 < len(seq)) {
	        // Count the number of "good" amino acids (those likely to be
	        // in an alpha helix).
	        int numgood = 0;
	        for (int i = start; i < start+6; ++i) {
	            if (Pa.get((double) seq.charAt(i)) > 100) {
	                numgood = numgood + 1;
	            }
	        }
	        //qp(start + "," + numgood);
	        if (numgood >= 4) {
	        	Pair<Integer,Integer> estart_end = CF_extend_alpha(seq, start, start+6);
	            //print("Exploring potential alpha " + str(estart_end.x) + ":" + str(estart_end.y));
	            //if (CF_good_alpha(seq[estart:end])):
	            if(!results.contains(estart_end)) {
	                results.add(estart_end);
	            }
	        }
	        // Go on to the next frame
	        start = start + 1;
	    }
	    // That's it, we're done
	    return results;
	}
	
	/**
	 * Extend a potential alpha helix sequence.  Return the endpoints of the extended sequence.
	 * 
	 * Seems to run in O(n)
	 * 
	 * We extend the region in both directions until the average propensity for a set of four 
	 * contiguous residues has Pa( ) < 100, which means we assume the helix ends there
	 * @param seq:   sequence of the protein
	 * @param start: start index
	 * @param end:   end index
	 * @return
	 */
	Pair<Integer,Integer> CF_extend_alpha(String seq, int start, int end) {
	    // seq[end-3:end+1] is: x | x | x | END
		//orig:  while ( float(sum([Pa[x] for x in seq[end-3:end+1]])) / float(4) ) > 100 and end < len(seq)-1:
		
	    while ( (sum(Pa, seq, end-3, end+1) / (float) 4) > 100 && end < len(seq)-1) {
	    	end += 1;
	    }
	    
	    while ( (sum(Pa, seq, start, start+4) / (float) 4 ) > 100 && start > 0) {
	        start -= 1;
	    }

	    return new Pair<Integer,Integer>(start,end);
	}

	/**
	 * 
	 * @param seq
	 * @return
	 */
	LabeledList<Pair<Integer,Integer>> CF_find_beta(String seq) {
	    /*Find all likely beta strands in seq.  Returns a list
	       of [start,end] pairs for the beta strands.*/
	    int start = 0;
	    LabeledList<Pair<Integer,Integer>> results = new LabeledList<Pair<Integer,Integer>>();
	    // Try each window
	    while (start + 5 < len(seq)) {
	        // Count the number of "good" amino acids (those likely to be
	        // in an beta sheet).
	    	int numgood = 0;
	        for(int i = start; i < start+5; ++i) {
	            if (Pb.get((double) seq.charAt(i)) > 100) {
	                numgood = numgood + 1; 
	            }
	        }
	        if (numgood >= 3) {
	        	Pair<Integer,Integer> estart_end = CF_extend_beta(seq, start, start+5);
	            //print("Exploring potential alpha " + str(estart_end.x) + ":" + str(estart_end.y));
	            //if (CF_good_alpha(seq[estart:end])):
	            if (!results.contains(estart_end)) {
	                results.add(estart_end);
	            }
	        }
	        // Go on to the next frame
	        start = start + 1;
	    }
	    // That's it, we're done
	    return results;
	}
	
	/**
	 * Extend a potential beta helix sequence.  Return the endpoints
	 * 		of the extended sequence.
	 * 
	 * We extend the region in both directions until the average propensity for a set of four
	 * contiguous residues has Pa( ) < 100, which means we assume the helix ends there
	 * @param seq:   sequence of the protein
	 * @param start: start index
	 * @param end:   end index
	 * @return
	 */
	Pair<Integer,Integer> CF_extend_beta(String seq, int start, int end) {	    
		while ( (sum(Pb, seq, end-3, end+1) / (float) 4) > 100 && end < len(seq)-1) {
	    	end += 1;
	    }
		
		while ( (sum(Pb, seq, start, start+4) / (float) 4 ) > 100 && start > 0) {
	        start -= 1;
	    }
	    
	    return new Pair<Integer,Integer>(start,end);
	}
	
	/**
	 * Find all likely beta turns in seq.  Returns a list of positions which are likely to be turns.
	 * @param seq
	 * @return
	 */
	LabeledList<Integer> CF_find_turns(String seq) {
	    LabeledList<Integer> result = new LabeledList<Integer>();
	    for(int i = 0; i <  len(seq)-3; ++i){
	    	// CONDITION 1
	    	//
	    	//F0[seq[i]]*F1[seq[i+1]]*F2[seq[i+2]]*F3[seq[i+3]]
	        boolean c1 = F0.get((double) seq.charAt(i))*F1.get((double) seq.charAt(i+1))*
	        		F2.get((double) seq.charAt(i+2))*F3.get((double) seq.charAt(i+3)) > 0.000075;
	        // CONDITION 2
	        //orig: c2 = ( (float)(sum([Pturn[x] for x in seq[i:i+4]])) / float(4) ) > 100;
	        float turnSum = 0;
	        for(int x = i; x < i+4; ++x) {
	        	//qp((double) x);
	        	turnSum += Pturn.get((double) seq.charAt(x));
	        }
	        		
	        boolean c2 = turnSum / (float) 4 > 100;
	        // CONDITION 3
	        //orig: c3 = sum([Pturn[x] for x in seq[i:i+4]]) > max(sum([Pa[x] for x in seq[i:i+4]]),sum([Pb[x] for x in seq[i:i+4]]))
	        
	        float paSum = 0, pbSum = 0;
	        for(int x = i; x < i+4; ++x) {
	        	paSum += Pa.get((double) seq.charAt(x));
	        	pbSum += Pb.get((double) seq.charAt(x));
	        }
	        
	        boolean c3 = turnSum > max(paSum ,pbSum);
	        if(c1 && c2 && c3){
	            result.add(i);
	        }
	    }
	    return result;
	}
	
	/**
	 * Given two regions, represented as two-element lists, determine
	 * if the two regions overlap.
	 * @return
	 */
	boolean region_overlap(Pair<Integer,Integer> region_a, Pair<Integer,Integer> region_b) {
	    return (region_a.x <= region_b.x && region_b.x <= region_a.y) || 
	           (region_b.x <= region_a.x && region_a.x <= region_b.y);
	}
	
	/**
	 * Given two regions, represented as two-element lists, return
	 * 		the minimum region that contains both regions.
	 * @return
	 */
	Pair<Integer,Integer> region_merge(Pair<Integer,Integer> region_a, Pair<Integer,Integer> region_b) {
	    return new Pair<Integer,Integer>(min(region_a.x, region_b.x), max(region_a.y, region_b.y));
	}
	
	/**
	 * Given two regions, represented as two-element lists, return
	 * the intersection of the two regions.
	 * @return
	 */
	Pair<Integer,Integer> region_intersect(Pair<Integer,Integer> region_a, Pair<Integer,Integer> region_b) {
	    return new Pair<Integer,Integer>(max(region_a.x, region_b.x), min(region_a.y, region_b.y));
	}
	
	/**
	 * Given two regions, represented as two-element lists, return
	 * 		the part of region_a which in not in region_b.
	 * 		It can be one or two regions depending on the position
	 * 		of region_b and its size.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	Pair<Integer,Integer>[] region_difference(Pair<Integer,Integer> region_a, Pair<Integer,Integer> region_b) {
		Pair<Integer,Integer>[] retval = null;
		
		// region_a start before region_b and stop before region_b
	    if(region_a.x < region_b.x && region_a.y <= region_b.y) {
	    	retval = new Pair[1];
	    	retval[0] = new Pair<Integer,Integer>(region_a.x, region_b.x-1);
	        return retval;
	    // region_a start after region_b and stop after region_b
	    } else if(region_a.x >= region_b.x && region_a.y > region_b.y) {
	    	retval = new Pair[1];
	    	retval[0] = new Pair<Integer,Integer>(region_b.y+1,region_a.y);
	        return retval;
	    // region_b is included in region_a => return 2 regions
	    } else if(region_a.x < region_b.x && region_a.y > region_b.y) {
	    	retval = new Pair[2];
	    	retval[0] = new Pair<Integer,Integer>(region_a.x, region_b.x-1);
	    	retval[1] = new Pair<Integer,Integer>(region_b.y+1,region_a.y);
	        return retval;
	    // region_a is included in region_b
	    } else {
	        return new Pair[0];
	    }
	}
	
	/**
	 * Analyze seq using the Chou-Fasman algorithm and display the results
	 * 		Only takes standard 20 amino acids!
	 * H = Helix, E = Sheet, T = Turn, C = Other
	 * 
	 * @param seq: protein's primary structure sequence
	 * @return: protein's predicted secondary structure sequence
	 * @throws ChouFasmanValuesMissingException: if the sequence has amino acids the algorithm cannot handle
	 */
	public String runChouFasman(String seq) throws ChouFasmanValuesMissingException {
		return runChouFasman(seq, false);
	}
	
	/**
	 * Analyze seq using the Chou-Fasman algorithm and display the results
	 * 		Only takes standard 20 amino acids!
	 * H = Helix, E = Sheet, T = Turn, C = Other
	 * 
	 * @param seq: protein's primary structure sequence
	 * @param preprocess: if true, apply pre-processing to the sequence to remove values the algorithm cannot handle
	 * 		(these become Alanine)
	 * @return: protein's predicted secondary structure sequence
	 * @throws ChouFasmanValuesMissingException: if the sequence has amino acids the algorithm cannot handle
	 */
	public String runChouFasman(String seq, boolean preprocess) throws ChouFasmanValuesMissingException {
		Objects.requireNonNull(seq, "Cannot run ChouFasman on null sequence!");
		
		//apply pre-processing if requested
		if(preprocess) {
			seq = ChouFasmanPreprocessor.process(seq);
		} else {
			if(!seq.matches(CFStruct.CHOU_FASMAN_VALUES)) {
				throw new ChouFasmanValuesMissingException();
			}
		}
		
	    // Find probable locations of alpha helices, beta strands,
	    // and beta turns.
		LabeledList<Pair<Integer,Integer>> alphas = CF_find_alpha(seq);
	    print("Alphas = " + str(alphas));
		LabeledList<Pair<Integer,Integer>> betas = CF_find_beta(seq);
	    print("Betas = " + str(betas));
		LabeledList<Integer> turns = CF_find_turns(seq);
	    print("Turns = " + str(turns));

	    // Handle overlapping regions between alpha helix and beta strands
	    // SEE COMMENT IN MY REPORT: WHY I DONT MERGE THE ALPHA AND BETA REGIONS TOGETHER
	    /* First we merge the alpha helix regions together
	    x = 0
	    while x < len(alphas)-1:
	        if region_overlap(alphas[x],alphas[x+1]):
	            alphas[x] = region_merge(alphas[x],alphas[x+1])
	            alphas.pop(x+1)
	        else:
	          x += 1
	    print "Potential alphas = " + str(alphas)
	    // The same for beta strand regions
	    x = 0
	    while x < len(betas)-1:
	        if region_overlap(betas[x],betas[x+1]):
	            betas[x] = region_merge(betas[x],betas[x+1])
	            betas.pop(x+1)
	        else:
	          x += 1
	    print "Ptential betas = " + str(betas)'''*/

	    // Then it's really messy!
	    LabeledList<Pair<Integer,Integer>> alphas2 = new LabeledList<Pair<Integer,Integer>>();
	    LabeledList<Pair<Integer,Integer>> alphas_to_test = alphas;
	    LabeledList<Pair<Integer,Integer>> betas_to_test = betas;
	    while(len(alphas_to_test) > 0) {
	    	Pair<Integer,Integer> alpha = alphas_to_test.pop();
	        // a_shorten record if the alpha helix region has been shorten
	        boolean a_shorten = false;
	        for(int index = 0; index < betas_to_test.size(); ++index) {
	        	Pair<Integer,Integer> beta = betas_to_test.get(index);
	        	
	            if(region_overlap(alpha,beta)) {
	            	//qp(alpha);
	            	//qp(beta);
	            	Pair<Integer,Integer> inter = region_intersect(alpha, beta);
	                print("Now studying overlap: "+str(inter));
	                
	                //orig: sum_Pa = sum([Pa[seq[i]] for i in range(inter[0],inter[1]+1)]);
	                double sum_Pa = 0;
	                for(int i = inter.x; i < inter.y+1; ++i) {
	                	sum_Pa += Pa.get((double) seq.charAt(i));
	                }
	                
	                //orig: sum_Pb = sum([Pb[seq[i]] for i in range(inter[0],inter[1]+1)]);
	                double sum_Pb = 0;
	                for(int i = inter.x; i < inter.y+1; ++i) {
	                	sum_Pb += Pb.get((double) seq.charAt(i));
	                }
	                
	                if(sum_Pa > sum_Pb) {
	                    // No more uncertainty on this overlap region: it will be a alpha helix
	                	Pair<Integer,Integer>[] diff = region_difference(beta,alpha);
	                    //print("\tAlpha helix WIN - beta sheet region becomes: "+str(diff));
	                    for(Pair<Integer,Integer> d: diff) {
	                        if(d.y-d.x > 4) {
	                            betas_to_test.add(d);
	                        }
	                    }
	                    betas_to_test.remove(beta);
	                } else {
	                    // No more uncertainty on this overlap region: it will be a beta strand
	                    a_shorten = true;
	                    Pair<Integer,Integer>[] diff = region_difference(alpha,beta);
	                    //print("\tBeta sheet WIN - alpha helix region becomes: "+str(diff));
	                    for(Pair<Integer,Integer> d: diff) {
	                        if(d.y-d.x > 4) {
	                            alphas_to_test.add(d);
	                        }
	                    }
	                }
	            }
	        }
	        if(!a_shorten) {
	            alphas2.add(alpha);
	        }
	    }
	    alphas = alphas2;
	    betas = betas_to_test;
	                    
	    print("final alphas: "+str(alphas));
	    print("final betas: "+str(betas));
	    // Build a sequence of spaces of the same length as seq.
	    StringBuilder analysis = new StringBuilder();
	    for(int i = 0; i < len(seq); ++i) {
	        analysis.append('C');
	    }

	    // Fill in the predicted alpha helices
	    for (Pair<Integer,Integer> alpha: alphas) {
	        for(int i = alpha.x; i < alpha.y; ++i) {
	            analysis.setCharAt(i, 'H');
	        }
	    }
	    // Fill in the predicted beta strands 
	    for(Pair<Integer,Integer> beta: betas) {
	        for(int i = beta.x; i < beta.y; ++i) {
	        	analysis.setCharAt(i, 'E');
	        }
	    }
	    // Fill in the predicted beta turns
	    for(Integer turn: turns) {
	        analysis.setCharAt(turn, 'T');
	    }

	    // Turn the analysis and the sequence into strings for ease
	    // of printing
	    String astr = analysis.toString();
	    String sstr = seq.toString();

	    print("astr: " + astr);
	    print("sstr: " + sstr);
	    return astr;
	}
	
	/**
	 * A main() for testing
	 * @param args
	 * @throws ChouFasmanValuesMissingException 
	 */
	public static void main(String[] args) throws ChouFasmanValuesMissingException {
		ChouFasman module = new ChouFasman();
		
		if(args.length == 0) {
			qp(module.runChouFasman(protein1));
			return;
		}
		
		String seq;// = protein1;
		//module.runChouFasman(seq);
		if(len(args) != 2) {
		    print("flag0");
		    print("Usage: %s [protein1, protein2, protein3]", args[0]);
		} else if(args[1].equals("-h") || args[1].equals("--help")) {
		    print("flag1");
		    print("Usage: %s [protein1, protein2, protein3]", args[0]);
		} else if(args[1].equals("protein1")) {
		    seq = protein1;
		    module.runChouFasman(seq);
		} else if(args[1].equals("protein2")) {
		    seq = protein2;
		    module.runChouFasman(seq);
		} else if(args[1].equals("protein3")) {
		    seq = protein3;
		    module.runChouFasman(seq);
		} else {
		    print("flag2");
		    print("Usage: %s [protein1, protein2, protein3]", args[0]);
		}
	}
	
	/**
	 * 
	 * @param hash
	 * @param seq
	 * @param start
	 * @param end
	 * @return
	 */
	private static float sum(LabeledHash<Double, Double> hash, String seq, int start, int end) {
		float sum = 0;
		for(int index = start; index < end; ++index) {
			sum += hash.get((double) seq.charAt(index));
		}
		
		return sum;
	}
	
	/**
	 * Used to help us turn on/off debug statements easily
	 * @param args
	 */
	private static void print(String args) {
		if(DEBUG) {
			PythonTranslator.print(args);
		}
	}
}
