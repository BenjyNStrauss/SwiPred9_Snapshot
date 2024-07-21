package chem;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class AtomicSymbolNotYetParseableException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public AtomicSymbolNotYetParseableException() { }
	
	public AtomicSymbolNotYetParseableException(String message) { super(message); }

	public AtomicSymbolNotYetParseableException(Throwable cause) { super(cause); }
	
	public AtomicSymbolNotYetParseableException(String message, Throwable cause) { super(message, cause); }
	
	public AtomicSymbolNotYetParseableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
