package tools.reader.fasta.exceptions;

import utilities.exceptions.parsing.UnexpectedDataFormatException;

/**
 * Thrown when a FASTA file is in an unexpected format
 * @author Benjy Strauss
 *
 */

public class UnexpectedFastaFormatException extends UnexpectedDataFormatException {
	private static final long serialVersionUID = 1L;
	
	public UnexpectedFastaFormatException() { }
	
	public UnexpectedFastaFormatException(String message) { super(message); }

	public UnexpectedFastaFormatException(Throwable cause) { super(cause); }
	
	public UnexpectedFastaFormatException(String message, Throwable cause) { super(message, cause); }
	
	public UnexpectedFastaFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
