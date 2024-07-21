package modules.descriptor.vkbat.sspro5_2;

import assist.translation.perl.PerlDyingException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class SSproDyingException extends PerlDyingException {
	private static final long serialVersionUID = 1L;

	public SSproDyingException() { }
	
	public SSproDyingException(String msg) { super(msg); }
	
	public SSproDyingException(Throwable cause) { super(cause); }
	
	public SSproDyingException(String msg, Throwable cause) { super(msg, cause); }
	
	public SSproDyingException(String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(msg, cause, enableSuppression, writableStackTrace);
	}
}
