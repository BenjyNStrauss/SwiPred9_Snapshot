package biology.exceptions;

/**
 * Thrown when an invalid Protein ID is encountered
 * @author Benjy Strauss
 *
 */

public class InvalidProteinIDException extends InvalidIDException {
	private static final long serialVersionUID = 1L;

	public InvalidProteinIDException() { }
	
	public InvalidProteinIDException(String message) { super(message); }

	public InvalidProteinIDException(Throwable cause) { super(cause); }
	
	public InvalidProteinIDException(String message, Throwable cause) { super(message, cause); }
	
	public InvalidProteinIDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
