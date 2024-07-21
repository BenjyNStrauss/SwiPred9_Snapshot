package biology.amino;

import assist.exceptions.UnmappedEnumValueException;
import utilities.exceptions.FromSwiPredException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class UnknownResidueException extends UnmappedEnumValueException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;
	
	public UnknownResidueException() { }

	public UnknownResidueException(String code) { super("Unrecognized code: " + code); }

	public UnknownResidueException(Throwable cause) { super(cause); }
	
	public UnknownResidueException(String message, Throwable cause) { super(message, cause); }
	
	public UnknownResidueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
