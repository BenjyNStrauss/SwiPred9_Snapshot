package modules.descriptor.vkbat.choufasman;

import utilities.LocalToolBase;

/**
 * The Chou-Fasman algorithm cannot process any residues other than:
 * 		• the standard 20 amino acids.
 * 
 * This class pre-processes a String of amino acids by
 * 		(1) capitalizing everything
 * 		(2) changing all non-standard values to 'A' for Alanine
 * 
 * @author Benjamin Strauss
 *
 */

final class ChouFasmanPreprocessor extends LocalToolBase {
	private ChouFasmanPreprocessor() { }
	
	/**
	 * Pre-processes an amino acid sequence so Chou-Fasman can be run on it
	 * Anything that is not a standard amino acid becomes "A" for Alanine
	 * @param seq
	 * @return
	 */
	public static String process(String seq) {
		char[] seqChars = seq.toUpperCase().toCharArray();
		for(int index = 0; index < seqChars.length; ++index) {
			if(!CFStruct.ACCEPTABLE_RESIDUES.contains(""+seqChars[index])) {
				seqChars[index] = 'A';
			}
		}
		
		return new String(seqChars);
	}
	
	
}
