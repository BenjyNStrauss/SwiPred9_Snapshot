package utilities.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

import utilities.SwiPredObject;

/**
 * Used to allow one to catch any exception thrown by JBio
 * @author Benjy Strauss
 *
 */

public interface FromSwiPredException extends SwiPredObject {
	public String getLocalizedMessage();
	public String getMessage();
	public Throwable getCause();
	public void printStackTrace();
	public void printStackTrace(PrintStream s);
	public void printStackTrace(PrintWriter s);
}
