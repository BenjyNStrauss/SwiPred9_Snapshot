package tools.reader.fasta.exceptions;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when 2 FASTAs are incompatible
 * @author Benjamin Strauss
 *
 */

public class IncompatibleFastaException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	//public final String[] fields'

	public IncompatibleFastaException() { }

	public IncompatibleFastaException(String message) { super(message); }

	public IncompatibleFastaException(Throwable cause) { super(cause); }
	
	public IncompatibleFastaException(String message, Throwable cause) { super(message, cause); }

	public IncompatibleFastaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
