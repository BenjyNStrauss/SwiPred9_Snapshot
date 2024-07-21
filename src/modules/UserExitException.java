package modules;

import utilities.exceptions.SwiPredException;


/**
 * Thrown when the user wants to exit something.
 * Used to abort processes
 * 
 * @author Benjy Strauss
 *
 */

public class UserExitException extends SwiPredException {
	private static final long serialVersionUID = 1L;

	public UserExitException() { }

	public UserExitException(String message) { super(message); }

	public UserExitException(Throwable cause) { super(cause); }
	
	public UserExitException(String message, Throwable cause) { super(message, cause); }

	public UserExitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
