package modules.descriptor.vkbat.exceptions.server;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class NotSupportedByServerException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	public String problematic_data;

	public NotSupportedByServerException() { }

	public NotSupportedByServerException(String message) { super(message); }

	public NotSupportedByServerException(Throwable cause) { super(cause); }
	
	public NotSupportedByServerException(String message, Throwable cause) { super(message, cause); }

	public NotSupportedByServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
