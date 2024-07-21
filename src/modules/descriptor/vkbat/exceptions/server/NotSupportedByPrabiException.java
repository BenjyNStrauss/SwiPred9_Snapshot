package modules.descriptor.vkbat.exceptions.server;

/**
 * Thrown when a vkabat algorithm is not supported by PRABI server
 * @author Benjy Strauss
 *
 */

public class NotSupportedByPrabiException extends NotSupportedByServerException {
	private static final long serialVersionUID = 1L;
	public String problematic_data;

	public NotSupportedByPrabiException() { }

	public NotSupportedByPrabiException(String message) { super(message); }

	public NotSupportedByPrabiException(Throwable cause) { super(cause); }
	
	public NotSupportedByPrabiException(String message, Throwable cause) { super(message, cause); }

	public NotSupportedByPrabiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
