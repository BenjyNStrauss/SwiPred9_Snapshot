package biology.amino;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjy Strauss
 * 
 */

public class InconsistentUnitException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public InconsistentUnitException() { }
	
	public InconsistentUnitException(String message) { super(message); }

	public InconsistentUnitException(Throwable cause) { super(cause); }
	
	public InconsistentUnitException(String message, Throwable cause) { super(message, cause); }
	
	public InconsistentUnitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}