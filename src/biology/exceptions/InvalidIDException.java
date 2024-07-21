package biology.exceptions;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when something has an invalid ID
 * @author Benjy Strauss
 *
 */

public class InvalidIDException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidIDException() { }
	
	public InvalidIDException(String message) { super(message); }

	public InvalidIDException(Throwable cause) { super(cause); }
	
	public InvalidIDException(String message, Throwable cause) { super(message, cause); }
	
	public InvalidIDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
