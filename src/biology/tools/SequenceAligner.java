package biology.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.template.Profile;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;

import assist.base.Assist;
import assist.script.Script;
import assist.util.BitList;
import assist.util.LabeledList;
import assist.util.Pair;
import biology.amino.ChainObject;
import biology.cluster.ChainCluster;
import biology.exceptions.EmptyChainException;
import biology.exceptions.alignment.EntropyAssignmentAlignmentError;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.molecule.MoleculeLookup;
import biology.molecule.types.AminoType;
import biology.protein.AminoChain;
import biology.protein.ChainID;
import biology.protein.MultiChain;
import biology.protein.ProteinChain;
import modules.UserInputModule;
import modules.descriptor.entropy.ShannonList;
import system.SwiPred;
import utilities.LocalToolBase;
import utilities.exceptions.ValueOutOfRangeException;

/**
 * Contains static method for protein sequence alignment, both pairwise and multiple sequence
 * @author Benjy Strauss
 *
 */

public final class SequenceAligner extends LocalToolBase {
	private static final char NULL_RES = AminoChain.NULL_RESIDUE;
	private static final boolean DEBUG_MODULE = false;
	
	private SequenceAligner() { }
	
	/**
	 * Aligns multiple protein chains
	 * @param chains
	 * @return
	 * @throws ResidueAlignmentException
	 */
	public static LabeledList<ProteinChain> align(ProteinChain... chains) throws ResidueAlignmentException {
		LabeledList<ProteinChain> chainList = new LabeledList<ProteinChain>("aligned-chains");
		for(ProteinChain chain: chains) {
			Objects.requireNonNull(chain, "Can't align a null chain!");
			chainList.add(chain);
		}
		
		LabeledList<ProteinChain> aligned = (LabeledList<ProteinChain>) align(chainList);
		
		trimCommonBlanks(aligned);
		return aligned;
	}
	
	/**
	 * 
	 * @param seqs
	 * @return
	 * @throws ResidueAlignmentException
	 */
	public static List<String> align(String... seqs) throws ResidueAlignmentException {
		LabeledList<String> chainList = new LabeledList<String>("Chain Sequences");
		for(String chain: seqs) {
			Objects.requireNonNull(chain, "Can't align a null sequence!");
			chainList.add(chain);
		}
		
		chainList = (LabeledList<String>) alignStringSeq(chainList);
		trimCommonBlanksString(chainList);
		return chainList;
	}
	
	/**
	 * TODO: needs DEBUG! - chain getting erased 12/11/21 -- seems moot now?
	 * TODO: add logging > what goes in vs what goes out.
	 * @param cluster
	 * @throws ResidueAlignmentException
	 */
	public static void align(ChainCluster cluster) throws ResidueAlignmentException {
		if(cluster.size() < 2) { return; }
		
		if(DEBUG_MODULE) {
			Assist.qpf("debug.txt", "\nflag0");
			for(Pair<ChainID, ProteinChain> chain: cluster) {
				Assist.qpf("debug.txt", ""+chain.y);
			}
		}
		
		log("\nInput into aligner:");
		cluster.log();
		
		LabeledList<ProteinSequence> seqList = new LabeledList<ProteinSequence>("unaligned-sequence-list");
		for(Pair<ChainID, ProteinChain> chain: cluster) {
			Objects.requireNonNull(chain.y, "Can't align a null chain!");
			if(chain.y.size() == 0)  { throw new EmptyChainException(chain.y.name()); }
			
			try {
				seqList.add(toProteinSequence(chain.y));
			} catch (CompoundNotFoundException e) {
				throw new ResidueAlignmentException(e);
			}
		}
		
		if(DEBUG_MODULE) {
			Assist.qpf("debug.txt", "\nflag1");
			Assist.qpf("debug.txt", seqList);
		}
		
		LabeledList<String> sequenceList = new LabeledList<String>("Sequence list");
		Profile<ProteinSequence, ?> result = null;
		
		try {
			result = Alignments.getMultipleSequenceAlignment(seqList);
		} catch (ArrayIndexOutOfBoundsException AIOOBE) {
			return;
		}
		
		if(result == null) {
			error("result is null!");
			throw new ResidueAlignmentException("Alignment failed, something weird in BioJava!");
		}
		
		for(Integer index = 1; index <= result.getSize(); ++index) {
			String str = result.getAlignedSequence(index).toString();
			sequenceList.add(str.replaceAll("-", ""+NULL_RES));
		}
		
		if(DEBUG_MODULE) {
			Assist.qpf("debug.txt", "\nflag3");
			Assist.qpf("debug.txt", sequenceList);
		}
		
		Set<ChainID> keys = cluster.keySet();
		
		for(ChainID key: keys) {
			ProteinChain ptr = cluster.get(key);
			//qp("Aligning [" + ii + "] " + ptr);
			for(int jj = 0; jj < sequenceList.size(); ++jj) {
				
				if(alignmentIsValidForChain(ptr, sequenceList.get(jj))) {
					//qp("applying...");
					modifyToMatchSequence(ptr, sequenceList.get(jj));
					sequenceList.remove(jj);
					break;
				}
			}
		}
		
		if(DEBUG_MODULE) {
			Assist.qpf("debug.txt", "\nflag4");
			Assist.qpf("debug.txt", cluster);
		}
		
		trimCommonBlanks(cluster.toChainArray());
		
		//TODO: debug code: comment
		if(DEBUG_MODULE) {
			Assist.qpf("debug.txt", "\nflag5");
			for(Pair<ChainID, ProteinChain> chain: cluster) {
				Assist.qpf("debug.txt", ""+chain.y);
			}
		}
		
		log("\nOutput from aligner:");
		cluster.log();
	}
	
	/**
	 * 
	 * @param chains
	 * @return
	 * @throws ResidueAlignmentException
	 */
	public static List<ProteinChain> align(List<ProteinChain> chains) throws ResidueAlignmentException {
		if(chains.size() < 2) { return chains; }
			
		if(DEBUG_MODULE) { qp("\npre-aligned:"); }
		if(DEBUG_MODULE) { qp(chains.toArray()); }
		if(DEBUG_MODULE) { qp(chains.size()); }
		
		LabeledList<ProteinSequence> seqList = new LabeledList<ProteinSequence>("unaligned-sequence-list");
		for(ProteinChain chain: chains) {
			Objects.requireNonNull(chain, "Can't align a null chain!");
			if(chain.size() == 0)  { throw new EmptyChainException(chain.name()); }
			
			try {
				seqList.add(toProteinSequence(chain));
			} catch (CompoundNotFoundException e) {
				throw new ResidueAlignmentException(e);
			}
		}
		
		LabeledList<String> sequenceList = new LabeledList<String>("Sequence list");
		Profile<ProteinSequence, ?> result = null;
		
		try {
			result = Alignments.getMultipleSequenceAlignment(seqList);
		} catch (ArrayIndexOutOfBoundsException AIOOBE) {
			return chains;
		}
		
		if(result == null) {
			error("result is null!");
			throw new ResidueAlignmentException("Alignment failed, something weird in BioJava!");
		}
		
		for(Integer index = 1; index <= result.getSize(); ++index) {
			String str = result.getAlignedSequence(index).toString();
			sequenceList.add(str.replaceAll("-", ""+NULL_RES));
		}
		
		if(DEBUG_MODULE) { qp("\npost-aligned:"); }
		if(DEBUG_MODULE) { qp(sequenceList.toArray()); }
		
		for(int ii = 0; ii < chains.size(); ++ii) {
			ProteinChain ptr = chains.get(ii);
			for(int jj = 0; jj < sequenceList.size(); ++jj) {
				
				if(alignmentIsValidForChain(ptr, sequenceList.get(jj))) {
					//qp("applying...");
					modifyToMatchSequence(ptr, sequenceList.get(jj));
					sequenceList.remove(jj);
					break;
				}
			}
		}
		
		if(DEBUG_MODULE) { qp("\nchains-aligned:"); }
		if(DEBUG_MODULE) { qp(chains.toArray()); }
		
		//BUG HERE
		trimCommonBlanks(chains);
		if(DEBUG_MODULE) { qp("\nchains-aligned2:"); }
		if(DEBUG_MODULE) { qp(chains.toArray()); }
		
		return chains;
	}
	
	/**
	 * 
	 * @param chains
	 * @return
	 * @throws ResidueAlignmentException
	 */
	public static List<String> alignStringSeq(List<String> chains) throws ResidueAlignmentException {
		if(chains.size() < 2) { return chains; }
		
		//set up the list of ProteinSequences for alignment
		LabeledList<ProteinSequence> seqList = new LabeledList<ProteinSequence>("unaligned-sequence-list");
		for(String chain: chains) {
			try {
				seqList.add(new ProteinSequence(chain.replaceAll("[^a-zA-Z]", "X").toUpperCase().trim()));
			} catch (CompoundNotFoundException e) {
				throw new ResidueAlignmentException(e);
			}
		}
		
		LabeledList<String> sequenceList = new LabeledList<String>("Sequence list");
		Profile<ProteinSequence, ?> result;
		
		try {
			result = Alignments.getMultipleSequenceAlignment(seqList);
		} catch (ArrayIndexOutOfBoundsException AIOOBE) {
			return chains;
		}
		
		//perform the alignment
		if(result == null) { return chains; }
		
		//put the aligned chains in 'sequenceList'
		for(Integer index = 1; index <= result.getSize(); ++index) {
			String str = result.getAlignedSequence(index).toString();
			//qp("putting [" + index + "]" + str);
			sequenceList.add(str.replaceAll("-", ""+NULL_RES));
		}
		
		LabeledList<String> outChains = new LabeledList<String>("String Alignement Output");
		
		for(int ii = 0; ii < chains.size(); ++ii) {
			String ptr = chains.get(ii);
			//qp("Aligning [" + ii + "] " + ptr);
			for(int jj = 0; jj < sequenceList.size(); ++jj) {
				if(alignmentIsValidForChain(ptr, sequenceList.get(jj))) {
					outChains.add(sequenceList.get(jj));
					sequenceList.remove(jj);
				}
			}
		}
		
		trimCommonBlanksString(outChains);
		return outChains;
	}
	
	/**
	 * @param chain
	 * @param sequence
	 * @return
	 */
	public static AminoChain<?> modifyToMatchSequence(AminoChain<?> chain, String sequence) {
		sequence = sequence.replaceAll("[-–—]", "_");
		if(DEBUG_MODULE) {
			qerr("\nRunning modifyToMatchSequence() on: " + chain.id().standard());
			qerr("orig target:    " + sequence);
			qerr("orig sequence:  " + chain.toSequence());
		}
		
		char[] charArr = sequence.toCharArray();
		
		//qp(">" + new String(charArr));
		//qp(">" + chain);
		//qp(">" + chain.startsAt());
		
		//remove nulls as needed
		for(int index = 0; index < charArr.length; ++index) {
			if(charArr[index] != NULL_RES) {
				int chain_index = index+chain.startsAt();
				
				//we're done!
				if(chain_index >= chain.size()) { break; }
				if(chain.get(chain_index) == null || chain.get(chain_index).residueType() == null) {
					chain.remove(chain_index);
					--index;
				}
			}
		}
		if(DEBUG_MODULE) {
			qerr("minus sequence: " + chain.toSequence());
		}
		
		//add nulls as needed
		for(int index = 0; index < charArr.length; ++index) {
			int chain_index = index+chain.startsAt();
			if(chain_index >= chain.size()) {
				chain.add(chain_index, null);
			} else if(charArr[index] == NULL_RES && chain.get(chain_index) != null) {
				if(chain.get(chain_index).residueType() != null) { chain.add(chain_index, null); }
			}
		}
		
		if(DEBUG_MODULE) {
			qerr("plus sequence:  " + chain.toSequence());
		}
		return chain;
	}
	
	/**
	 * TODO
	 * Modifies a ShannonList by adding/removing null residues to make it match the given sequence
	 * @param vectors: the ShannonList to modify
	 * @param sequence: the sequence to match
	 * @return
	 */
	private static ShannonList modifyToMatchSequence(ShannonList vectors, String sequence) {
		sequence = sequence.replaceAll("\\.", "_");
		//qp("modifytoMatchSequence:vec: " + vectors);
		//qp("modifytoMatchSequence:seq: " + sequence);
		char[] charArr = sequence.toCharArray();
		
		for(int index = 0; index < charArr.length; ++index) {
			if(charArr[index] == NULL_RES) {
				if(index >= vectors.size()) {
					vectors.add(index, null);
				} else if(vectors.get(index) != null) {
					//qp("added");
					vectors.add(index, null);
				}
			}
		}
		
		//qp("plus seq: " + vectors);
		
		//remove nulls as needed
		for(int index = 0; index < charArr.length; ++index) {
			if(charArr[index] != NULL_RES) {
				if(index >= vectors.size()) { break; }
				
				if(vectors.get(index) == null && sequence.charAt(index) != '_') {
					//qp(">" + sequence.charAt(index));
					//qp(">" + index);
					vectors.remove(index);
					--index;
				}
			}
		}
		//qp("minus seq: " + vectors);
		return vectors;
	}
	
	public static boolean alignmentIsValidForChain(ProteinChain chain, String sequence) {
		if(chain == null || sequence == null) { dqp("f*"); return false; }
		return alignmentIsValidForChain(chain.toSequence(), sequence);
	}
	
	/**
	 * 3 checks:
	 * (1) if the non-null letters are in the same order
	 * (2) if the number of non-null letters are the same
	 * (3) if the chain (including nulls) fits within the sequence
	 * 
	 * @param chain
	 * @param sequence
	 * @return
	 */
	public static boolean alignmentIsValidForChain(String chain, String sequence) {
		sequence = sequence.replaceAll("-", ""+NULL_RES);
		//dqp("<< chain: " + chain);
		//dqp("<< seq: " + sequence);
		
		if(chain == null || sequence == null) { return false; }
		String nullRes = ""+NULL_RES;
		
		if(chain.length() > sequence.length()) { return false; }
		
		String nonNullChain = chain.replaceAll(nullRes, "");
		String nonNullSeq = sequence.replaceAll(nullRes, "");
		
		//qp("nonNullChain: " + nonNullChain);
		//qp("nonNullSeq: " + nonNullSeq);
		
		//fails condition #2--not the same chain
		if(nonNullChain.length() != nonNullSeq.length()) { return false; }
		
		for(int index = 0; index < nonNullChain.length(); ++index) {
			if(nonNullChain.charAt(index) != nonNullSeq.charAt(index)) {
				//fails condition #1
				return false;
			}
		}
		
		//both conditions passed
		return true;
	}
	
	public static ProteinSequence toProteinSequence(ProteinChain chain) throws CompoundNotFoundException {
		return new ProteinSequence(MoleculeLookup.proteinogenify(chain.toSequence()));
	}
	
	/**
	 * Aligns a ShannonList to an AminoChain by adding nulls
	 * TODO: what happens with the right number of nulls, but in the wrong places?
	 * 
	 * @param vectors: the list of ShannonVectors which contain the entropy data for the chain
	 * @param chain: the chain which we are assigning entropy data too
	 * @param ignoreVectAtPos
	 * @return: request a redo entropy download
	 */
	public static boolean align(ShannonList vectors, AminoChain<?> chain, BitList ignoreVectAtPos) {
		Objects.requireNonNull(chain);
		Objects.requireNonNull(vectors);
		Objects.requireNonNull(ignoreVectAtPos);
		//qp("Aligning ShannonList to chain: " + chain);
		
		//make sure sizes match
		if(chain.size() != vectors.size()) {
			String[] chainPlusVect = new String[2];
			chainPlusVect[0] = chain.toSequence();
			chainPlusVect[1] = vectors.toString();
			
			LabeledList<String> aligned = new LabeledList<String>();
			
			try {
				aligned = (LabeledList<String>) align(chainPlusVect);
			} catch (ResidueAlignmentException e) {
				e.printStackTrace();
			}
			
			modifyToMatchSequence(chain, aligned.get(0));
			modifyToMatchSequence(vectors, aligned.get(1));
		}
		
		//if there's a multichain, adjust the vectors if need be...
		if(chain instanceof MultiChain && chain.size() != vectors.size()) {
			MultiChain mChain = (MultiChain) chain;
			for(int index = 0; index < mChain.size(); ++index) {
				if(mChain.get(index) != null) {
					if(mChain.get(index).toChar() == '_') {
						vectors.add(index-chain.startsAt(), null);
					}
				} else {
					//this is unexpected; an error occurred somewhere along the process
					qerr("Warning: null AminoPosition in MultiChain: " + mChain.id().standard());
					qerr("\tSequence: " + mChain.toSequence());
					qerr("\tindex:    " + index);
					vectors.add(index, null);
				}
			}
		}
		
		//Something unexpected happened...
		if(chain.actualSize() != vectors.size()) {
			error("SequenceAligner Error: Unexpected event happened.");
			error("A bug has likely occurred; please contact " + BMAIL);
			
			error(chain.toString());
			error(vectors.toString());
			throw new EntropyAssignmentAlignmentError(chain, vectors);
		}
		
		//proofreading: lengths are guaranteed to be the same at this point!
		for(int index = chain.startsAt(); index < chain.size(); ++index) {
			if(!effectivelyNull(chain.get(index)) && vectors.get(index-chain.startsAt()) != null) {
				//qp(index);
				//qp("["+index+"] "+vectors.get(index - chain.startsAt()).queryLetter+" : "+chain.get(index).toChar());
				
				//try to see if we can shift things?
				if(vectors.get(index-chain.startsAt()).queryLetter != chain.get(index).toChar()) {
					
					if(SwiPred.askUserForHelp) {
						//Patch 1-26-2021 as MultiChains may not align perfectly...
						if(chain instanceof MultiChain) {
							AminoType sc = AminoType.parse(vectors.get(index-chain.startsAt()).queryLetter);
							if(!((MultiChain) chain).get(index).contains(sc)) {
								ignoreVectAtPos.set(index-chain.startsAt());
							}
						}
					} else {
						//ask user for help
						error("EntropyAssignmentAlignmentError ["+vectors.get(index-chain.startsAt()).queryLetter + " vs " + chain.get(index).toChar()+"]");
						error("chain/vector index: " + (index-chain.startsAt()));
						if(chain instanceof MultiChain) {
							AminoType sc = AminoType.parse(vectors.get(index-chain.startsAt()).queryLetter);
							if(!((MultiChain) chain).get(index).contains(sc)) {
								error("Amino position does not contains queryLetter");
							} else {
								error("Amino position contains queryLetter");
							}
						}
						error("chain: " + chain);
						error("ShannonList: " + vectors);
						
						String prompt = "Please enter an option: \n\t0: ignore issue\n\t1: abort process"
								+ "\n\t2: redownload entropy\n\t3: skip this residue";
						
						int value = UserInputModule.getOptionFromUser(prompt, 3);
						switch(value) {
						case 0:	continue;
						case 1:	throw new EntropyAssignmentAlignmentError(chain.name());
						case 2: return true;
						case 3: ignoreVectAtPos.set(index);	break;
						}
						
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Culls similar AminoChain from an array based on the presence of an identical section
	 * 
	 * @param chains: the list to cull from
	 * @param length: the minimum length of the identical section
	 * @return: culled array of AminoChains
	 */
	public static AminoChain<?>[] cullBySequenceMatchOfLength(AminoChain<?>[] chains, int length) {
		ArrayList<AminoChain<?>> output = new ArrayList<AminoChain<?>>();
		ArrayList<String> sequences = new ArrayList<String>();
		
		for(int index = 0; index < chains.length; ++index) {
			String seq = chains[index].toSequence();
			boolean soFarUnique = true;
			
			for(int ii = 0; ii < sequences.size(); ++ii) {
				try {
					align(seq, sequences.get(ii));
					//if we can align the sequence, break
					soFarUnique = false;
					break;
				} catch (ResidueAlignmentException RAE) { }
			}
			
			if(soFarUnique) {
				sequences.add(seq);
				output.add(chains[index]);
			}
		}
		
		AminoChain<?>[] retVal = new AminoChain[output.size()];
		output.toArray(retVal);
		
		return retVal;
	}
	
	/**
	 * Culls similar sequences from an array based on the presence of an identical section
	 * 
	 * @param chains: the list to cull from
	 * @param length: the minimum length of the identical section
	 * @return: culled array of sequences
	 */
	public static String[] cullBySequenceMatchOfLength(String[] chains, int length) {
		ArrayList<String> sequences = new ArrayList<String>();
		
		for(int index = 0; index < chains.length; ++index) {
			String seq = chains[index];
			boolean soFarUnique = true;
			
			for(int ii = 0; ii < sequences.size(); ++ii) {
				try {
					align(seq, sequences.get(ii));
					soFarUnique = false;
					//if we can align the sequence, break
					break;
				} catch (ResidueAlignmentException RAE) { }
			}
			
			if(soFarUnique) {
				sequences.add(seq);
			}
		}
		
		String[] retVal = new String[sequences.size()];
		sequences.toArray(retVal);
		//qp(retVal);
		
		return retVal;
	}
	
	/**
	 * TODO DOES NOT WORK AT THIS TIME: PISCES DOES NOT RUN
	 * Runs PISCES on a file
	 * @param filename: file of PDB chain identifiers, each line has only one PDB chain identifier (1TNFA).
	 * @param identityThreshold: percent sequence identity threshold, the valid range is 5-100
	 */
	public static void piscesCull(String filename, int identityThreshold) {
		piscesCull(filename, identityThreshold, 0.0, 2.5, 20, 10000, 0.3, false, false);
	}
	
	/**
	 * TODO DOES NOT WORK AT THIS TIME: PISCES DOES NOT RUN
	 * Runs PISCES on a file
	 * @param filename: file of PDB chain identifiers, each line has only one PDB chain identifier (1TNFA).
	 * @param identityThreshold: percent sequence identity threshold, the valid range is 5-100.
	 * @param minRes: minimum resolution
	 * @param maxRes: maximum resolution
	 * @param minLen: minimum sequence length
	 * @param maxLen: maximum sequence length
	 * @param rFactor: 
	 * @param includeNonXRay: include chains from NMR
	 * @param cAlphaOnly: Only include chains with coordinates for the alpha carbon
	 */
	public static void piscesCull(String filename, int identityThreshold, double minRes, double maxRes,
			int minLen, int maxLen, double rFactor, boolean includeNonXRay, boolean cAlphaOnly) {
		if(identityThreshold < 5 || identityThreshold > 100) {
			throw new ValueOutOfRangeException("identityThreshold must be between 5 and 100.");
		}
		
		if(rFactor < 0 || rFactor > 1) {
			throw new ValueOutOfRangeException("rFactor must be between 0.0 and 1.0.");
		}
		
		String scriptArgs[] = new String[15];
		scriptArgs[0] = "scripts/PISCES/bin/Cull_for_UserPDB.pl";
		scriptArgs[1] = "-i";
		scriptArgs[2] = filename;
		scriptArgs[3] = "-p";
		scriptArgs[4] = "" + identityThreshold;
		scriptArgs[5] = "-l";
		scriptArgs[6] = minLen + "-" + maxLen;
		scriptArgs[7] = "-r";
		scriptArgs[8] = minRes + "-" + maxRes;
		scriptArgs[9] = "-f";
		scriptArgs[10] = "" +rFactor;
		scriptArgs[11] = "-x";
		scriptArgs[12] = "" + (includeNonXRay ? 'T' : 'F');
		scriptArgs[13] = "-a";
		scriptArgs[14] = "" + (cAlphaOnly ? 'T' : 'F');
		
		qp(scriptArgs);
		
		Script.runScript(scriptArgs);
	}

	/**
	 * Trims common blanks
	 * @param chains
	 * @return
	 */
	private static List<ProteinChain> trimCommonBlanks(List<ProteinChain> chains) {
		trimming:
		for(int index = 0; true; ++index) {
			boolean allBlanks = true;
			for(ProteinChain chain: chains) {
				if(index >= chain.size()) {
					break trimming;
				} else if(chain.get(index) == null) {
					//nothing to do here
				} else {
					allBlanks = false;
				}
			}
			
			if(allBlanks) {
				for(ProteinChain chain: chains) {
					/* this check is to ensure that we don't try to remove something that
					 * is out of range for a given chain
					 */
					if(index >= chain.startsAt()) {
						chain.remove(index);
					}
				}
				--index;
			}
		}
		
		return chains;
	}
	
	/**
	 * Trims common blanks
	 * @param chains
	 * @return
	 */
	private static ProteinChain[] trimCommonBlanks(ProteinChain[] chains) {
		
		trimming:
		for(int index = 0; true; ++index) {
			boolean allBlanks = true;
			for(ProteinChain chain: chains) {
				
				if(index+chain.startsAt() >= chain.size()) {
					break trimming;
				} else if(chain.get(index+chain.startsAt()) == null) {
					//nothing to do here
				} else {
					allBlanks = false;
					break;
				}
			}
			
			if(allBlanks) {
				for(ProteinChain chain: chains) {
					try {
						chain.remove(index+chain.startsAt());
					} catch (IndexOutOfBoundsException IOOBE) {
						qp("index = " + index);
						qp("" + chain);
						qp("\tstartsAt() = " + chain.startsAt());
						qp("\tsize() = " + chain.size());
						
						pause(2);
						IOOBE.printStackTrace();
						
						System.exit(3);
					}
					
				}
				--index;
			}
		}
		
		return chains;
	}
	
	public static final boolean effectivelyNull(ChainObject amino) {
		return (amino == null || amino.toChar() == '_');
	}
	
	private static List<String> trimCommonBlanksString(List<String> chains) {
		trimming:
		for(int index = 0; true; ++index) {
			boolean allBlanks = true;
			for(String chain: chains) {
				if(index >= chain.length()) {
					break trimming;
				} else if(chain.charAt(index) == NULL_RES) {
					//nothing to do here
				} else {
					allBlanks = false;
				}
			}
			
			if(allBlanks) {
				for(int ii = 0; ii < chains.size(); ++ii) {
					chains.set(ii, chains.get(ii).substring(0, index) + chains.get(ii).substring(index+1));
				}
				--index;
			}
		}
		
		return chains;
	}
	
	public static String[] trimCommonBlanks(String... chains) {
		char[][] chain_chars = new char[chains.length][];
		int minLen = Integer.MAX_VALUE;
		
		for(int index = 0; index < chains.length; ++index) {
			chain_chars[index] = chains[index].toCharArray();
			minLen = min(minLen, chain_chars[index].length);
		}
		
		for(int index = 0; index < minLen; ++index){
			boolean mark = true;
			for(char[] ch_arr: chain_chars) {
				if(ch_arr[index] != '_') { 
					mark = false;
					break;
				}
			}
			if(mark) {
				for(char[] ch_arr: chain_chars) {
					ch_arr[index] = ' ';
				}
			}
		}
		
		for(int index = 0; index < chains.length; ++index) {
			chains[index] = new String(chain_chars[index]);
		}
		
		return chains;
	}
	
	public static double getPercentIdentity(String seq1, String seq2) throws ResidueAlignmentException {
		List<String> aligned = align(seq1, seq2);
		//order doesn't matter…
		char[] seq_1 = aligned.get(0).toCharArray();
		char[] seq_2 = aligned.get(1).toCharArray();
		
		if(seq_1.length != seq_2.length) {
			throw new ResidueAlignmentException("Aligned sequence length didn't match!");
		}
		
		double matches = 0;
		for(int ii = 0; ii < seq_1.length; ++ii) {
			if(seq_1[ii] == seq_2[ii]) {
				++matches;
			}
		}
		
		double denominator = seq_1.length;
		return matches/denominator;
	}
}