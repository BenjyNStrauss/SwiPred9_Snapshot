package biology.exceptions.alignment;

import biology.protein.AminoChain;
import modules.descriptor.entropy.ShannonList;

/**
 * Something went wrong in assigning entropy!
 * Should have been handled by the system but wasn't
 * 
 * @author Benjy Strauss
 *
 */

public class EntropyAssignmentAlignmentError extends ResidueAlignmentError {
	private static final long serialVersionUID = 1L;
	private AminoChain<?> chain;
	private ShannonList vectors;
	
	public EntropyAssignmentAlignmentError() { }
	
	public EntropyAssignmentAlignmentError(String message) { super(message); }

	public EntropyAssignmentAlignmentError(Throwable cause) { super(cause); }
	
	public EntropyAssignmentAlignmentError(String message, Throwable cause) { super(message, cause); }

	public EntropyAssignmentAlignmentError(AminoChain<?> chain, ShannonList vectors) {
		super(chain.id().standard());
		this.chain = chain;
		this.vectors = vectors;
	}
	
	public AminoChain<?> getChain() { return chain; }
	public ShannonList getVectors() { return vectors; }
}
