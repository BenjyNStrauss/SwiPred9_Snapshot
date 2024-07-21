package biology.amino;

import biology.protein.AminoChain;
import utilities.exceptions.MissingDataException;

/**
 * Used to signify when something is missing secondary structure
 * @author Benjy Strauss
 *
 */

public class SecStrNotSetException extends MissingDataException {
	private static final long serialVersionUID = 1L;
	
	public SecStrNotSetException(AminoChain<?> chain) {
		super(chain, "Missing data for: " + chain, null);
	}
	
	public SecStrNotSetException(ChainObject residue) {
		super(residue, "Missing data for: " + residue, null);
	}
	
	public SecStrNotSetException(AminoChain<?> chain, String message) { 
		super(chain, message, null);
	}

	public SecStrNotSetException(ChainObject residue, String message) { 
		super(residue, message, null);
	}
	
	public SecStrNotSetException(AminoChain<?> chain, Throwable cause) {
		super(chain, null, cause);
	}

	public SecStrNotSetException(ChainObject residue, Throwable cause) {
		super(residue, null, cause);
	}
	
	public SecStrNotSetException(AminoChain<?> chain, String message, Throwable cause) { 
		super(chain, message, cause);
	}

	public SecStrNotSetException(ChainObject residue, String message, Throwable cause) { 
		super(residue, message, cause);
	}
	
	public SecStrNotSetException(AminoChain<?> chain, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(chain, message, cause, enableSuppression, writableStackTrace);
	}
	
	public SecStrNotSetException(ChainObject residue, String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(residue, message, cause, enableSuppression, writableStackTrace);
	}
}
