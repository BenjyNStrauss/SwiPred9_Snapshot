package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * RESID-type GO xref
 * @author Benjamin Strauss
 *
 */


public class RESID extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: RESID:";
	
	public final String data;
	
	public RESID(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected RESID(RESID mc) {
		this.data = mc.data;
	}
	
	public RESID clone() { return new RESID(this); }
	
	public boolean equals(Object other) {
		if(other instanceof RESID) {
			RESID ns = (RESID) other;
			if(!ns.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
