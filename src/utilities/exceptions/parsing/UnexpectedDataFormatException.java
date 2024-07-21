package utilities.exceptions.parsing;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class UnexpectedDataFormatException extends UnparsableDataException {
	private static final long serialVersionUID = 1L;
	
	public UnexpectedDataFormatException() { }
	
	public UnexpectedDataFormatException(String message) { super(message); }

	public UnexpectedDataFormatException(Throwable cause) { super(cause); }
	
	public UnexpectedDataFormatException(String message, Throwable cause) { super(message, cause); }
	
	public UnexpectedDataFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
