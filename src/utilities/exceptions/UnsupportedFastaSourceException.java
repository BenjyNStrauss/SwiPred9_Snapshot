package utilities.exceptions;

/**
 * Thrown if a FASTA source is selected that is not yet supported by the current version of SwiPred
 * @author Benjy Strauss
 *
 */

public class UnsupportedFastaSourceException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UnsupportedFastaSourceException() { }
	
	public UnsupportedFastaSourceException(String message) { super(message); }

	public UnsupportedFastaSourceException(Throwable cause) { super(cause); }
	
	public UnsupportedFastaSourceException(String message, Throwable cause) { super(message, cause); }
	
	public UnsupportedFastaSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}