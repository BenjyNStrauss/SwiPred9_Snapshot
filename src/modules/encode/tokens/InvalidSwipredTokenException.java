package modules.encode.tokens;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class InvalidSwipredTokenException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	public String problematic_data;

	public InvalidSwipredTokenException() { }

	public InvalidSwipredTokenException(String message) { super(message); }

	public InvalidSwipredTokenException(Throwable cause) { super(cause); }
	
	public InvalidSwipredTokenException(String message, Throwable cause) { super(message, cause); }

	public InvalidSwipredTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
