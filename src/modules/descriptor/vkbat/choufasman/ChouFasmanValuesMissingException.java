package modules.descriptor.vkbat.choufasman;

import utilities.exceptions.SwiPredException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ChouFasmanValuesMissingException extends SwiPredException {
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_MSG = "Error: sequence contains 1+ amino acids for which Chou-Fasman values do not exist";
	
	public ChouFasmanValuesMissingException() { this(DEFAULT_MSG); }
	
	public ChouFasmanValuesMissingException(String message) { super(message); }

	public ChouFasmanValuesMissingException(Throwable cause) { super(cause); }
	
	public ChouFasmanValuesMissingException(String message, Throwable cause) { super(message, cause); }
	
	public ChouFasmanValuesMissingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
