package model;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class DSSPBorderException extends DSSPException {
	private static final long serialVersionUID = 1L;
	
	public DSSPBorderException() { }
	
	public DSSPBorderException(String message) { super(message); }

	public DSSPBorderException(Throwable cause) { super(cause); }
	
	public DSSPBorderException(String message, Throwable cause) { super(message, cause); }
	
	public DSSPBorderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
