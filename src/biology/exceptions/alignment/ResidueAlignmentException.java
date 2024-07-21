package biology.exceptions.alignment;

import biology.amino.BioMolecule;
import biology.protein.ProteinChain;
import utilities.exceptions.SwiPredException;

/**
 * Thrown when two Amino Acid Sequences cannot be lined up
 * @author Benjy Strauss
 *
 */

public class ResidueAlignmentException extends SwiPredException {
	private static final long serialVersionUID = 1L;
	private static final String EMPTY_CHAIN = "Empty Chain!";
	private static final String ALL_X = "No residue types specified!  Chain was all 'X's!";
	
	private ProteinChain badChain;
	
	public ResidueAlignmentException() { }
	
	public ResidueAlignmentException(ProteinChain badChain) { 
		super(generateMessage(badChain));
		this.badChain = badChain;
	}
	
	public ResidueAlignmentException(String message) { super(message); }

	public ResidueAlignmentException(Throwable cause) { super(cause); }
	
	public ResidueAlignmentException(String message, Throwable cause) { super(message, cause); }
	
	public ResidueAlignmentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public ProteinChain badChain() { return badChain; }
	
	private static String generateMessage(ProteinChain chain) {
		if(chain == null) { return "Chain was null!"; }
		if(chain.size() == 0) { return EMPTY_CHAIN; }
		
		boolean lowercaseLetters = false;
		boolean allX = true;
		for(BioMolecule bMol: chain) {
			char letter = bMol.toChar();
			if(Character.isLowerCase(letter)) { lowercaseLetters = true; }
			if(letter != 'X') { allX = false; }
		}
		
		if(lowercaseLetters) { return "Invalid sequence (lowercase letters)!"; }
		if(allX) { return ALL_X; }
		return "Unknown error.  Maybe no suitable matching segment found?";
	}
	
	public boolean emptyChain() { return (getMessage().equals(EMPTY_CHAIN)); }
	public boolean allX() { return (getMessage().equals(ALL_X)); }
}
