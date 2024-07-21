package project;

import utilities.exceptions.SwiPredException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class UnsupportedFileTypeException extends SwiPredException {
	private static final long serialVersionUID = 1L;
	
	public UnsupportedFileTypeException() { }
	
	public UnsupportedFileTypeException(String message) { super(message); }

	public UnsupportedFileTypeException(Throwable cause) { super(cause); }
	
	public UnsupportedFileTypeException(String message, Throwable cause) { super(message, cause); }
	
	public UnsupportedFileTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
