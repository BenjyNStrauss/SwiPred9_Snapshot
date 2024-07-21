package biology.exceptions;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class NotAnAminoAcidException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotAnAminoAcidException() { }
	
	public NotAnAminoAcidException(String message) { super(message); }

	public NotAnAminoAcidException(Throwable cause) { super(cause); }
	
	public NotAnAminoAcidException(String message, Throwable cause) { super(message, cause); }
	
	public NotAnAminoAcidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
