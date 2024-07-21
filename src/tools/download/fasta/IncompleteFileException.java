package tools.download.fasta;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class IncompleteFileException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public IncompleteFileException() { }
	
	public IncompleteFileException(String message) { super(message); }

	public IncompleteFileException(Throwable cause) { super(cause); }
	
	public IncompleteFileException(String message, Throwable cause) { super(message, cause); }
	
	public IncompleteFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
