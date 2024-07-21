package utilities.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ProteinParsingException extends SwiPredException {
	private static final long serialVersionUID = 1L;

	public ProteinParsingException() { }
	
	public ProteinParsingException(String message) { super(message); }

	public ProteinParsingException(Throwable cause) { super(cause); }
	
	public ProteinParsingException(String message, Throwable cause) { super(message, cause); }
	
	public ProteinParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
