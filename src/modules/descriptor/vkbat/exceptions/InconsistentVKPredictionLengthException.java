package modules.descriptor.vkbat.exceptions;

import utilities.exceptions.BadArraySizeException;

/**
 * Thrown when assigning vkbat predictions of different lengths
 *  because it becomes unclear what was predicted of what
 * 
 * @author Benjy Strauss
 * 
 */

public class InconsistentVKPredictionLengthException extends BadArraySizeException {
	private static final long serialVersionUID = 1L;
	
	public InconsistentVKPredictionLengthException(int expected, int given) { super(expected, given); }
	
	public InconsistentVKPredictionLengthException(int expected, int given, String message) { 
		super(expected, given, message);
	}

	public InconsistentVKPredictionLengthException(int expected, int given, Throwable cause) { 
		super(expected, given, cause);
	}
	
	public InconsistentVKPredictionLengthException(int expected, int given, String message, Throwable cause) { 
		super(expected, given, message, cause);
	}
	
	public InconsistentVKPredictionLengthException(int expected, int given, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(expected, given, message, cause, enableSuppression, writableStackTrace);
	}
}
