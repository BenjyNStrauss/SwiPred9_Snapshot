package biology.exceptions;

/**
 * Thrown when an invalid RCSB-PDB ID is encountered
 * (Class is currently in use)
 * @author Benjy Strauss
 * 
 */

public class InvalidPDB_IDException extends InvalidProteinIDException {
	private static final long serialVersionUID = 1L;

	public InvalidPDB_IDException() { }
	
	public InvalidPDB_IDException(String message) { super(message); }

	public InvalidPDB_IDException(Throwable cause) { super(cause); }
	
	public InvalidPDB_IDException(String message, Throwable cause) { super(message, cause); }
	
	public InvalidPDB_IDException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
