package modules.descriptor.vkbat.jpred;

import utilities.exceptions.SwiPredException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class JPredException extends SwiPredException {
	private static final long serialVersionUID = 1L;

	public JPredException() { }
	
	public JPredException(String message) { super(message); }

	public JPredException(Throwable cause) { super(cause); }
	
	public JPredException(String message, Throwable cause) { super(message, cause); }
	
	public JPredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
