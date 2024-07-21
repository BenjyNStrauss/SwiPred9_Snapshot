package tools.reader.fasta.exceptions;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class BrokenDSSPFileException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public BrokenDSSPFileException() { }

	public BrokenDSSPFileException(String message) { super(message); }

	public BrokenDSSPFileException(Throwable cause) { super(cause); }
	
	public BrokenDSSPFileException(String message, Throwable cause) { super(message, cause); }

	public BrokenDSSPFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
