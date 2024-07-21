package tools.reader.csv;

import assist.exceptions.IORuntimeException;
import utilities.exceptions.FromSwiPredException;;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class CSVReadingException extends IORuntimeException implements FromSwiPredException {
	private static final long serialVersionUID = 1L;
	
	public CSVReadingException() { }
	
	public CSVReadingException(String message) { super(message); }

	public CSVReadingException(Throwable cause) { super(cause); }
	
	public CSVReadingException(String message, Throwable cause) { super(message, cause); }
	
	public CSVReadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
