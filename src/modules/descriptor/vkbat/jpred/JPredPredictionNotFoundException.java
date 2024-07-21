package modules.descriptor.vkbat.jpred;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when JPred Job Id is not found
 * @author Benjamin Strauss
 *
 */

public class JPredPredictionNotFoundException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public JPredPredictionNotFoundException() { }
	
	public JPredPredictionNotFoundException(String message) { super(message); }

	public JPredPredictionNotFoundException(Throwable cause) { super(cause); }
	
	public JPredPredictionNotFoundException(String message, Throwable cause) { super(message, cause); }
	
	public JPredPredictionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
