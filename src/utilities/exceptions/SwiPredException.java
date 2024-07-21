package utilities.exceptions;

import assist.base.ToolBelt;

/**
 * An exception thrown by the SWItch-PREDict program
 * @author Benjy Strauss
 *
 */

public class SwiPredException extends Exception implements FromSwiPredException, ToolBelt {
	private static final long serialVersionUID = 1L;
	
	public SwiPredException() { }
	
	public SwiPredException(String message) { super(message); }

	public SwiPredException(Throwable cause) { super(cause); }
	
	public SwiPredException(String message, Throwable cause) { super(message, cause); }
	
	public SwiPredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public String toString() {
		String aboutMe = "("+ super.getClass().toString().substring(6) + ") " +getMessage();
		return aboutMe;
	}
}
