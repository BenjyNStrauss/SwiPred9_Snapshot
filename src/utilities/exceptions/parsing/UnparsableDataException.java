package utilities.exceptions.parsing;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when data cannot be parsed
 * @author Benjy Strauss
 *
 */

public class UnparsableDataException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public String unparseable_data;
	
	public UnparsableDataException() { }
	
	public UnparsableDataException(String message) { super(message); }

	public UnparsableDataException(Throwable cause) { super(cause); }
	
	public UnparsableDataException(String message, Throwable cause) { super(message, cause); }
	
	public UnparsableDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
