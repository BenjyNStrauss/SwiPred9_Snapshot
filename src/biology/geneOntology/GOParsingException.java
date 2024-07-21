package biology.geneOntology;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when parsing GO notation
 * @author Benjamin Strauss
 *
 */

public class GOParsingException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public GOParsingException() { }
	
	public GOParsingException(String message) { super(message); }

	public GOParsingException(Throwable cause) { super(cause); }
	
	public GOParsingException(String message, Throwable cause) { super(message, cause); }
	
	public GOParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
