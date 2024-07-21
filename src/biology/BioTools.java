package biology;

import assist.util.LabeledList;
import biology.exceptions.InvalidPDB_IDException;
import biology.protein.AminoChain;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjy Strauss
 *
 */

public abstract class BioTools extends LocalToolBase {
	
	//single, assigned
	protected static final int SINGLE_ASSIGNED = 0;
	//single, both (assigned + unassigned)
	protected static final int SINGLE_BOTH = 1;
	//single, unassigned
	protected static final int SINGLE_UNASSIGNED = 2;
	//single, weighted (unassigned have a weight between 0 and 1)
	protected static final int SINGLE_WIEGHTED = 3;
	//triplet, assigned
	protected static final int TRIPLE_ASSIGNED = 4;
	//triplet, both (assigned + unassigned)
	protected static final int TRIPLE_BOTH = 5;
	//triplet, unassigned
	protected static final int TRIPLE_UNASSIGNED = 6;
	//triplet, weighted (unassigned have a weight between 0 and 1)
	protected static final int TRIPLE_WIEGHTED = 7;
	
	/**
	 * Gets the protein ID from a String containing both the protein's name and chain
	 * @param namePlusChain
	 * @return
	 */
	public static final String getProteinNameRCSB(String namePlusChain) {
		if(namePlusChain.contains(":")) {
			return namePlusChain.substring(0, namePlusChain.indexOf(":"));
		} else if(namePlusChain.contains("_")) {
			return namePlusChain.substring(0, namePlusChain.indexOf("_"));
		} else {
			return namePlusChain.substring(0,4);
		}
	}
	
	/**
	 * Gets the chain ID from a String containing both the protein's name and chain
	 * @param namePlusChain:
	 * @return: protein's chainID
	 */
	public static final String getChainID_RCSB(String namePlusChain) {
		if(namePlusChain.contains(":")) {
			return namePlusChain.substring(namePlusChain.indexOf(":")+1);
		} else if(namePlusChain.contains("_")) {
			return namePlusChain.substring(namePlusChain.indexOf("_")+1);
		} else {
			return namePlusChain.substring(4);
		}
	}
	
	/**
	 * Mac OS X file system is not case-sensitive, but RCSB Protein Chain ids are
	 * This method takes a string and modifies it so that a lower-case letter becomes
	 * a hash and the upper-case letter:
	 * Example: "x" -> "#X"
	 * 
	 * @param chain: the chain to modify
	 * @return: how the chain's file (ex: FASTA) should be saved in the file system
	 */
	public static final String modifyChainForFileSystemSyntax(String chain) {
		StringBuilder builder = new StringBuilder();
		char[] chainArray = chain.toCharArray();
		for(char ch: chainArray) {
			if(Character.isLowerCase(ch)) {
				builder.append("#" + Character.toUpperCase(ch));
			} else {
				builder.append(ch);
			}
		}
		return builder.toString().replaceAll(":", "_");
	}
	
	/**
	 * RCSB-PDB protein IDs are NOT case-sensitive, but RCSB-PDB chain IDs are
	 * This method converts JUST the protein part to uppercase
	 * @param pdbID: the id
	 * @return: id with capital protein id
	 */
	public static final String convertProteinToUpperCase(String pdbID) {
		String[] meta = pdbID.split("[:_]");
		if(meta.length != 2) {
			throw new InvalidPDB_IDException(pdbID);
		}
		meta[0] = meta[0].toUpperCase();
		return meta[0] + ":" + meta[1];
	}
	
	/**
	 * 
	 * @param chain
	 * @return
	 */
	public static final String getIDForUser(AminoChain<?> chain) {
		if(chain.id().protein() == null && chain.id().uniprot() != null) {
			return chain.id().uniprot();
		} else {
			return chain.id().standard();
		}
	}
	
	/**
	 * 
	 * @param lines
	 * @return
	 */
	public static final String[] splitFasta(String[] lines) {
		StringBuilder sequenceBuilder = new StringBuilder();
		
		LabeledList<String> list = new LabeledList<String>();
		
		for(String line: lines) {
			line = line.trim();
			
			if(line.startsWith(">")) {
				if(sequenceBuilder.length() != 0) {
					list.add(sequenceBuilder.toString());
					sequenceBuilder.setLength(0);
				}
				sequenceBuilder.append(line + "\n");
			} else {
				sequenceBuilder.append(line);
			}
		}
		list.add(sequenceBuilder.toString());
		
		String[] outList = new String[list.size()];
		list.toArray(outList);
		return outList;
	}
}
