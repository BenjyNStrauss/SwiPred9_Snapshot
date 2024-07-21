package utilities.exceptions;

/**
 * Typically thrown if the wrong size array is passed into a method
 * These should probably never be seen.  If one is, then something has likely
 * (but not necessarily) gone wrong internally
 * 
 * @author Benjamin Strauss
 *
 */

public class BadArraySizeException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	/** What size of array was required */
	public final int expected;
	/** What size of array was given */
	public final int given;
	
	public BadArraySizeException(int expected, int given) { this(expected, given, null, null); }
	
	public BadArraySizeException(int expected, int given, String message) { 
		this(expected, given, message, null);
	}

	public BadArraySizeException(int expected, int given, Throwable cause) { 
		this(expected, given, null, cause);
	}
	
	public BadArraySizeException(int expected, int given, String message, Throwable cause) { 
		super(message, cause);
		this.expected = expected;
		this.given = given;
	}
	
	public BadArraySizeException(int expected, int given, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.expected = expected;
		this.given = given;
	}
}
