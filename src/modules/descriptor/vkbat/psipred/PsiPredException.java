package modules.descriptor.vkbat.psipred;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown if a PsiPred prediction cannot be completed
 * @author Benjy Strauss
 *
 */

public class PsiPredException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	public PsiPredException() { }
	
	public PsiPredException(String message) { super(message); }

	public PsiPredException(Throwable cause) { super(cause); }
	
	public PsiPredException(String message, Throwable cause) { super(message, cause); }
	
	public PsiPredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
