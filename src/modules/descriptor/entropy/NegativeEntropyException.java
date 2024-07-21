package modules.descriptor.entropy;

import utilities.exceptions.FromSwiPredException;

/**
 * Thrown when a negative entropy value is generated
 * @author Benjy Strauss
 *
 */

public abstract class NegativeEntropyException extends RuntimeException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;
	
	public NegativeEntropyException() { }
	
	public NegativeEntropyException(String message) { super(message); }

	public NegativeEntropyException(Throwable cause) { super(cause); }
	
	public NegativeEntropyException(String message, Throwable cause) { super(message, cause); }
	
	public NegativeEntropyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
