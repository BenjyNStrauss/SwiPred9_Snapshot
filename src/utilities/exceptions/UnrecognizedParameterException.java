package utilities.exceptions;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class UnrecognizedParameterException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UnrecognizedParameterException() { }
	
	public UnrecognizedParameterException(String message) { super(message); }

	public UnrecognizedParameterException(Throwable cause) { super(cause); }
	
	public UnrecognizedParameterException(String message, Throwable cause) { super(message, cause); }
	
	public UnrecognizedParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public UnrecognizedParameterException(char msg) {
		super("Unrecognized char: " + msg);
	}
	
	public UnrecognizedParameterException(int msg) {
		super("Unrecognized method: " + msg);
	}
	
}
