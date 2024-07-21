package utilities.exceptions;

/**
 * 
 * @author Benjy Strauss
 * 
 */

public class MessedUpSystemFileException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public MessedUpSystemFileException() { }
	
	public MessedUpSystemFileException(String message) { super(message); }

	public MessedUpSystemFileException(Throwable cause) { super(cause); }
	
	public MessedUpSystemFileException(String message, Throwable cause) { super(message, cause); }
	
	public MessedUpSystemFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
