package tools.reader;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjy Strauss
 * 
 */

public class EmptyDescriptorListException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public EmptyDescriptorListException() { }
	
	public EmptyDescriptorListException(String message) { super(message); }

	public EmptyDescriptorListException(Throwable cause) { super(cause); }
	
	public EmptyDescriptorListException(String message, Throwable cause) { super(message, cause); }
	
	public EmptyDescriptorListException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
