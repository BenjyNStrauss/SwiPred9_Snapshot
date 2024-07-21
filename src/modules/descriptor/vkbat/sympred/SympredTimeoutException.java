package modules.descriptor.vkbat.sympred;

import utilities.exceptions.SwiPredException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class SympredTimeoutException extends SwiPredException {
	private static final long serialVersionUID = 1L;
	
	public SympredTimeoutException() { }
	
	public SympredTimeoutException(String message) { super(message); }

	public SympredTimeoutException(Throwable cause) { super(cause); }
	
	public SympredTimeoutException(String message, Throwable cause) { super(message, cause); }
	
	public SympredTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
