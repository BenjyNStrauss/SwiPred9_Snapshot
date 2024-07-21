package utilities.exceptions;

import assist.exceptions.IORuntimeException;

/**
 * Thrown when reading a malformed file
 * @author Benjy Strauss
 *
 */

public class MalformedFileException extends IORuntimeException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;
	
	public MalformedFileException() { }
	
	public MalformedFileException(String message) { super(message); }

	public MalformedFileException(Throwable cause) { super(cause); }
	
	public MalformedFileException(String message, Throwable cause) { super(message, cause); }
	
	public MalformedFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
