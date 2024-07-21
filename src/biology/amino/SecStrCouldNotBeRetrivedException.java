package biology.amino;

import system.SwiPred;
import tools.DataSource;
import utilities.exceptions.MissingDataException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class SecStrCouldNotBeRetrivedException extends MissingDataException {
	private static final long serialVersionUID = 1L;
	
	public SecStrCouldNotBeRetrivedException(DataSource source) {
		super(source);
	}
	
	public SecStrCouldNotBeRetrivedException() {
		super(SwiPred.getShell().fastaSrc());
	}
	
	public SecStrCouldNotBeRetrivedException(String message) { 
		super(SwiPred.getShell().fastaSrc(), message);
	}
	
	public SecStrCouldNotBeRetrivedException(String message, DataSource source) { 
		super(source, message);
	}

	public SecStrCouldNotBeRetrivedException(Throwable cause) { 
		super(SwiPred.getShell().fastaSrc(), cause);
	}
	
	public SecStrCouldNotBeRetrivedException(Throwable cause, DataSource source) { 
		super(source, cause);
	}
	
	public SecStrCouldNotBeRetrivedException(String message, Throwable cause) { 
		super(SwiPred.getShell().fastaSrc(), message, cause);
	}
	
	public SecStrCouldNotBeRetrivedException(String message, Throwable cause, DataSource source) { 
		super(source, message, cause);
	}
	
	public SecStrCouldNotBeRetrivedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(SwiPred.getShell().fastaSrc(), message, cause, enableSuppression, writableStackTrace);
	}
}
