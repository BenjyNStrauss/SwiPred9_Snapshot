package regression;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown if all of the arrays in a 2d-array (which represents a matrix) are not the same length
 * @author Benjy Strauss
 *
 */

public class InconsistentMatrixSizeException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public InconsistentMatrixSizeException() { }
	
	public InconsistentMatrixSizeException(String message) { super(message); }

	public InconsistentMatrixSizeException(Throwable cause) { super(cause); }
	
	public InconsistentMatrixSizeException(String message, Throwable cause) { super(message, cause); }
	
	public InconsistentMatrixSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
