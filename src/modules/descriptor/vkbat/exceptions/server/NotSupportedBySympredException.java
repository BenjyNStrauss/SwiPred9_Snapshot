package modules.descriptor.vkbat.exceptions.server;

/**
 * Thrown when a vkabat algorithm is not supported by Sympred server
 * @author Benjy Strauss
 *
 */

public class NotSupportedBySympredException extends NotSupportedByServerException {
	private static final long serialVersionUID = 1L;
	public String problematic_data;

	public NotSupportedBySympredException() { }

	public NotSupportedBySympredException(String message) { super(message); }

	public NotSupportedBySympredException(Throwable cause) { super(cause); }
	
	public NotSupportedBySympredException(String message, Throwable cause) { super(message, cause); }

	public NotSupportedBySympredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
