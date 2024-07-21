package utilities.exceptions;

/**
 * 
 * @author Benjy
 *
 */

public class UnknownEnumValueException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UnknownEnumValueException() { }
	
	public UnknownEnumValueException(String message) { super(message); }

	public UnknownEnumValueException(Throwable cause) { super(cause); }
	
	public UnknownEnumValueException(String message, Throwable cause) { super(message, cause); }
	
	public UnknownEnumValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
