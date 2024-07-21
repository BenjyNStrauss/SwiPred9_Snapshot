package utilities.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class InvalidResolutionException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public InvalidResolutionException() { }
	
	public InvalidResolutionException(String message) { super(message); }

	public InvalidResolutionException(Throwable cause) { super(cause); }
	
	public InvalidResolutionException(String message, Throwable cause) { super(message, cause); }
	
	public InvalidResolutionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
