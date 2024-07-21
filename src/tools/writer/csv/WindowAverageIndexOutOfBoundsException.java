package tools.writer.csv;

import utilities.exceptions.FromSwiPredException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class WindowAverageIndexOutOfBoundsException extends IndexOutOfBoundsException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;

	public WindowAverageIndexOutOfBoundsException(int badIndex) { 
		super("Bad Index: " + badIndex + "!");
	}
	
	public WindowAverageIndexOutOfBoundsException() { }
	
	public WindowAverageIndexOutOfBoundsException(String message) { super(message); }
}
