package model;

import utilities.exceptions.SwiPredException;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class DSSPException extends SwiPredException {
	private static final long serialVersionUID = 1L;
	
	public DSSPException() { }
	
	public DSSPException(String message) { super(message); }

	public DSSPException(Throwable cause) { super(cause); }
	
	public DSSPException(String message, Throwable cause) { super(message, cause); }
	
	public DSSPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
