package biology.exceptions;

import biology.protein.AminoChain;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when dealing an empty chain (that shouldn't be empty), usually during alignment
 * @author Benjy Strauss
 *
 */

public class EmptyChainException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public EmptyChainException() { }
	
	public EmptyChainException(String message) { super(message); }

	public EmptyChainException(Throwable cause) { super(cause); }
	
	public EmptyChainException(AminoChain<?> chain) { super(chain.toString()); }
	
	public EmptyChainException(String message, Throwable cause) { super(message, cause); }
	
	public EmptyChainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
