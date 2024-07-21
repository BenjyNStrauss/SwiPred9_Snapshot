package modules.descriptor.vkbat.sspro5_2;

import assist.translation.perl.PerlExitException;
import utilities.exceptions.FromSwiPredException;

/**
 * Thrown when SSpro would call exit() in the original Perl code
 * @author Benjy Strauss
 *
 */

public class SSproExitException extends PerlExitException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;
	
	public SSproExitException() { super(1); }
	
	public SSproExitException(String msg) { super(msg, 1); }
	
	public SSproExitException(String msg, int exitVal) { super(msg, exitVal); }
	
	public SSproExitException(String msg, Exception e) { super(msg, e); }
	
	public SSproExitException(Exception e) { super(e.getMessage(), 1); }
	
	public SSproExitException(int exitVal) { super(exitVal); }
}
