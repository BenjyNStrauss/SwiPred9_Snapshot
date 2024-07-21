package modules.cluster;

import utilities.exceptions.SwiPredException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class HashAlignmentException extends SwiPredException {
	private static final long serialVersionUID = 1L;
	
	public HashAlignmentException() { }
	
	public HashAlignmentException(String message) { super(message); }

	public HashAlignmentException(Throwable cause) { super(cause); }
	
	public HashAlignmentException(String message, Throwable cause) { super(message, cause); }
	
	public HashAlignmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
