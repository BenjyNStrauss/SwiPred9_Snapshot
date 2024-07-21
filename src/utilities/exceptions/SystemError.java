package utilities.exceptions;

/**
 * Thrown when there is a bug in the code!
 * @author Benjy Strauss
 *
 */

public class SystemError extends Error {
	private static final long serialVersionUID = 1L;

	public SystemError() { }
	
	public SystemError(String message) { super(message); }

	public SystemError(Throwable cause) { super(cause); }
	
	public SystemError(String message, Throwable cause) { super(message, cause); }
}
