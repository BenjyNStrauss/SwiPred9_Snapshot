package biology.geneOntology;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public final class GOParsingTools extends LocalToolBase {
	private GOParsingTools() { }
	
	public static String[] compactArray(String[] array, int lastValidIndex, String delimiter) {
		if(array.length > lastValidIndex) {
			for(int ii = lastValidIndex+1; ii < array.length; ++ii) {
				array[lastValidIndex] += delimiter+array[ii];
			}
		}
		return array;
	}
}
