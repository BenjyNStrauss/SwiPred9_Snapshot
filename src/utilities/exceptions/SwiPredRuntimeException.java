package utilities.exceptions;

/**
 * An exception thrown by the SWItch-PREDict program
 * @author Benjy Strauss
 *
 */

public class SwiPredRuntimeException extends RuntimeException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;
	
	public SwiPredRuntimeException() { }
	
	public SwiPredRuntimeException(String message) { super(message); }

	public SwiPredRuntimeException(Throwable cause) { super(cause); }
	
	public SwiPredRuntimeException(String message, Throwable cause) { super(message, cause); }
	
	public SwiPredRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public String toString() {
		String aboutMe = "("+ super.getClass().toString().substring(7) + ") " +getMessage();
		return aboutMe;
	}
}
