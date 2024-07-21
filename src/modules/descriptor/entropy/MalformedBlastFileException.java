package modules.descriptor.entropy;

import utilities.exceptions.MalformedFileException;

/**
 * Thrown when reading a malformed blast file
 * @author Benjy Strauss
 *
 */

public class MalformedBlastFileException extends MalformedFileException {
	private static final long serialVersionUID = 1L;
	
	public MalformedBlastFileException() { }
	
	public MalformedBlastFileException(String message) { super(message); }

	public MalformedBlastFileException(Throwable cause) { super(cause); }
	
	public MalformedBlastFileException(String message, Throwable cause) { super(message, cause); }
	
	public MalformedBlastFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
