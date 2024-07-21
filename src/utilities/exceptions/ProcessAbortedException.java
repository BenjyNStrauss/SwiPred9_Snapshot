package utilities.exceptions;

/**
 * Thrown to abort a procedure by user choice
 * @author Benjy Strauss
 *
 */

public class ProcessAbortedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ProcessAbortedException() {
		super("Procedure aborted by user.");
	}
	
	public ProcessAbortedException(String msg) {
		super(msg);
	}
}
