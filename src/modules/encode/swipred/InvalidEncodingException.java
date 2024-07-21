package modules.encode.swipred;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class InvalidEncodingException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidEncodingException() { }
	
	public InvalidEncodingException(String message) { super(message); }

	public InvalidEncodingException(Throwable cause) { super(cause); }
	
	public InvalidEncodingException(String message, Throwable cause) { super(message, cause); }
	
	public InvalidEncodingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
