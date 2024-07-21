package neuralNet.internal;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author bns
 *
 */

public class NNInvalidSizeException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public NNInvalidSizeException() { }

	public NNInvalidSizeException(String message) { super(message); }

	public NNInvalidSizeException(Throwable cause) { super(cause); }
	
	public NNInvalidSizeException(String message, Throwable cause) { super(message, cause); }

	public NNInvalidSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public NNInvalidSizeException(int length, int expected) {
		super("Neural network layer size mismatch: expected " + expected + "; got " + length + ".");
	}

}
