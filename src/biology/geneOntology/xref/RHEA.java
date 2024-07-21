package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * RHEA-type GO xref
 * @author Benjamin Strauss
 *
 */

public class RHEA extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX_IN = "xref: rhea:";
	private static final String PREFIX_OUT = "xref: RHEA:";
	
	protected final int number;
	
	public RHEA(int number) {
		this.number = number;
	}
	
	public RHEA(String xref) {
		if(!xref.toLowerCase().startsWith(PREFIX_IN)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		number = Integer.parseInt(parts[2]);
	}
	
	protected RHEA(RHEA rhea) {
		this.number = rhea.number;
	}
	
	public RHEA clone() { return new RHEA(this); }
	
	public boolean equals(Object other) {
		if(other instanceof RHEA) {
			RHEA rhea = (RHEA) other;
			if(rhea.number != number) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX_OUT+number; }
}
