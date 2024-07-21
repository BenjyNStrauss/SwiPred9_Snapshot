package tools.reader.fasta.exceptions;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

@Deprecated
public class BadPDBAtomLineException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public BadPDBAtomLineException() { }
	
	public BadPDBAtomLineException(String message) { super(message); }

	public BadPDBAtomLineException(Throwable cause) { super(cause); }
	
	public BadPDBAtomLineException(String message, Throwable cause) { super(message, cause); }
	
	public BadPDBAtomLineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
