package modules.encode.tokens;

/**
 * Thrown when a sequence cannot be accurately represented due to either START or END token having to be missing
 * 		but with no other residues missing…
 * @author Benjamin Strauss
 *
 */

public class MandatorilyInaccurateRepresentationException extends Exception {
	private static final long serialVersionUID = 1L;
	public String problematic_data;

	public MandatorilyInaccurateRepresentationException() { }

	public MandatorilyInaccurateRepresentationException(String message) { super(message); }

	public MandatorilyInaccurateRepresentationException(Throwable cause) { super(cause); }
	
	public MandatorilyInaccurateRepresentationException(String message, Throwable cause) { super(message, cause); }

	public MandatorilyInaccurateRepresentationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
