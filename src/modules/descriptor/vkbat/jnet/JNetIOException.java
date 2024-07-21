package modules.descriptor.vkbat.jnet;

import utilities.exceptions.FromSwiPredException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class JNetIOException extends RuntimeException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;

	public JNetIOException(String msg) { super(msg); }
	public JNetIOException() { }
}
