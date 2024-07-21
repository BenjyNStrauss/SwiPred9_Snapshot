package modules.descriptor.entropy;

import biology.protein.AminoChain;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class EntropyRetrievalException extends DataRetrievalException {
	private static final long serialVersionUID = 1L;
	
	public EntropyRetrievalException() { }
	
	public EntropyRetrievalException(String message) { super(message); }
	
	public EntropyRetrievalException(AminoChain<?> chain) {
		super("NCBI Blast failed for chain: " + chain.id());
	}

	public EntropyRetrievalException(Throwable cause) { super(cause); }
	
	public EntropyRetrievalException(String message, Throwable cause) { super(message, cause); }
	
	public EntropyRetrievalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
