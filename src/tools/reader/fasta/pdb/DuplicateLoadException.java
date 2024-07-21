package tools.reader.fasta.pdb;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class DuplicateLoadException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateLoadException() { }

	public DuplicateLoadException(String message) { super(message); }

	public DuplicateLoadException(Throwable cause) { super(cause); }
	
	public DuplicateLoadException(String message, Throwable cause) { super(message, cause); }

	public DuplicateLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
