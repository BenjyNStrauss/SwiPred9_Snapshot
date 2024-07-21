package modules.descriptor.vkbat.psipred;

/**
 * Thrown when PsiPred's input is too long
 * @author Benjy Strauss
 *
 */

public class PsiPredInputTooLongException extends PsiPredException {
	private static final long serialVersionUID = 1L;

	public PsiPredInputTooLongException() { }
	
	public PsiPredInputTooLongException(String message) { super(message); }

	public PsiPredInputTooLongException(Throwable cause) { super(cause); }
	
	public PsiPredInputTooLongException(String message, Throwable cause) { super(message, cause); }
	
	public PsiPredInputTooLongException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
