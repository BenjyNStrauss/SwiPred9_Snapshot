package modules.descriptor.vkbat.psipred;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class StringTokenNotFoundException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public StringTokenNotFoundException() { }
	
	public StringTokenNotFoundException(String msg) { super(msg); }
	
	public StringTokenNotFoundException(Throwable cause) { super(cause); }
	
	public StringTokenNotFoundException(String message, Throwable cause) { super(message, cause); }
	
	public StringTokenNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
