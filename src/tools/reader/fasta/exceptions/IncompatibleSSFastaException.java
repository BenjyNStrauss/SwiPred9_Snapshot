package tools.reader.fasta.exceptions;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when 2 FASTAs are incompatible (when assigning secondary structure)
 * @author Benjamin Strauss
 *
 */

public class IncompatibleSSFastaException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	//public final String[] fields'

	public IncompatibleSSFastaException() { }

	public IncompatibleSSFastaException(String message) { super(message); }

	public IncompatibleSSFastaException(Throwable cause) { super(cause); }
	
	public IncompatibleSSFastaException(String message, Throwable cause) { super(message, cause); }

	public IncompatibleSSFastaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
