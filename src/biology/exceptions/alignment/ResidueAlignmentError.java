package biology.exceptions.alignment;

import utilities.exceptions.SystemError;

/**
 * Thrown if and only if something goes wrong in the residue alignment algorithm
 * @author Benjy Strauss
 *
 */

public class ResidueAlignmentError extends SystemError {
	private static final long serialVersionUID = 1L;
	
	public ResidueAlignmentError() { }
	
	public ResidueAlignmentError(String message) { super(message); }

	public ResidueAlignmentError(Throwable cause) { super(cause); }
	
	public ResidueAlignmentError(String message, Throwable cause) { super(message, cause); }
}
