package utilities.exceptions;

/**
 * Thrown when data cannot be looked up
 * @author Benjamin Strauss
 *
 */

public class LookupException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public LookupException() { }
	
	public LookupException(String message) { super(message); }

	public LookupException(Throwable cause) { super(cause); }
	
	public LookupException(String message, Throwable cause) { super(message, cause); }
	
	public LookupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
