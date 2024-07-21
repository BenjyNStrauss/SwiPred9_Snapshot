package biology.amino;

import assist.exceptions.UnmappedEnumValueException;
import utilities.exceptions.FromSwiPredException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class UnknownLigandException extends UnmappedEnumValueException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;
	
	public UnknownLigandException() { }

	public UnknownLigandException(String code) { super("Unrecognized code: " + code); }

	public UnknownLigandException(Throwable cause) { super(cause); }
	
	public UnknownLigandException(String message, Throwable cause) { super(message, cause); }
	
	public UnknownLigandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
