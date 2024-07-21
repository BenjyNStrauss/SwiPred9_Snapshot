package utilities.exceptions.parsing;

/**
 * Thrown when an enum value is unsupported.  If one of these is thrown,
 * 	 it means that the code has been modified in a way that it was not intended to be.
 * 
 * @author Benjy Strauss
 *
 */

public class UnsupportedEnumValueException extends UnparsableDataException {
	private static final long serialVersionUID = 1L;
	
	public UnsupportedEnumValueException() { }
	
	public UnsupportedEnumValueException(String message) { super(message); }

	public UnsupportedEnumValueException(Throwable cause) { super(cause); }
	
	public UnsupportedEnumValueException(String message, Throwable cause) { super(message, cause); }
	
	public UnsupportedEnumValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
