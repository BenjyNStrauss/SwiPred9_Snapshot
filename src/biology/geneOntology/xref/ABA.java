package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * ABA-type GO xref
 * @author Benjamin Strauss
 *
 */


public class ABA extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: ABA:";
	
	protected final String data;
	
	public ABA(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected ABA(ABA mc) {
		this.data = mc.data;
	}
	
	public ABA clone() { return new ABA(this); }
	
	public boolean equals(Object other) {
		if(other instanceof ABA) {
			ABA ns = (ABA) other;
			if(!ns.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
