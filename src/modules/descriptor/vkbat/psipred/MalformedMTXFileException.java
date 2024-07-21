package modules.descriptor.vkbat.psipred;

import utilities.exceptions.MalformedFileException;;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class MalformedMTXFileException extends MalformedFileException {
	private static final long serialVersionUID = 1L;
	
	public MalformedMTXFileException() { }
	
	public MalformedMTXFileException(String message) { super(message); }

	public MalformedMTXFileException(Throwable cause) { super(cause); }
	
	public MalformedMTXFileException(String message, Throwable cause) { super(message, cause); }
	
	public MalformedMTXFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
