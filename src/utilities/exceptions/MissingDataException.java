package utilities.exceptions;

import utilities.SwiPredObject;

/**
 * Used to signify when something is missing data
 * 
 * @author Benjy Strauss
 *
 */

public class MissingDataException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	protected final SwiPredObject object;
	
	public MissingDataException(SwiPredObject object) {
		this(object, "Missing data for: " + object, null);
	}
	
	public MissingDataException(SwiPredObject object, String message) { 
		this(object, message, null);
	}

	public MissingDataException(SwiPredObject object, Throwable cause) {
		this(object, null, cause);
	}
	
	public MissingDataException(SwiPredObject object, String message, Throwable cause) { 
		super(message, cause);
		this.object = object;
	}
	
	public MissingDataException(SwiPredObject object, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.object = object;
	}
}
