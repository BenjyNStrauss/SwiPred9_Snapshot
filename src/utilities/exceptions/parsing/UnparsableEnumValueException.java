package utilities.exceptions.parsing;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class UnparsableEnumValueException extends UnparsableDataException {
	private static final long serialVersionUID = 1L;
	
	public UnparsableEnumValueException() { }

	public UnparsableEnumValueException(String message) { super(message); }

	public UnparsableEnumValueException(Throwable cause) { super(cause); }
	
	public UnparsableEnumValueException(String message, Throwable cause) { super(message, cause); }
	
	public UnparsableEnumValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
