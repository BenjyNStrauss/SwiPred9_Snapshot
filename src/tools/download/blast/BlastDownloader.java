package tools.download.blast;

import assist.exceptions.NotYetImplementedError;
import biology.protein.AminoChain;
import modules.descriptor.entropy.EntropyRetrievalException;
import utilities.LocalToolBase;

/**
 * This class downloads BLAST files from the internet
 * 
 * @author Benjamin Strauss
 *
 */

public class BlastDownloader extends LocalToolBase {
	
	/**
	 * 
	 * @param chain: chain to download blast for
	 * @param replaceExisting: if true, replace existing BLAST data
	 * @param usePython: use python subroutine?
	 * @return: path to the BLAST file
	 * @throws EntropyRetrievalException 
	 */
	@SuppressWarnings("unused")
	public static String downloadNCBI(String purpose, AminoChain<?> chain, boolean replaceExisting, boolean usePython) throws EntropyRetrievalException {
		if(usePython) {
			return BLAST_NCBI.downloadBlastNCBI_py(purpose, chain, replaceExisting);
		} else {
			if(true) { throw new NotYetImplementedError(); }
			return BLAST_NCBI.downloadBlastNCBI(purpose, chain, replaceExisting);
		}
	}
}
