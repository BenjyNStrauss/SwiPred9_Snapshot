package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * GO-type GO xref
 * @author Benjamin Strauss
 *
 */


public class GO extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: GO:";
	
	protected final String data;
	
	public GO(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected GO(GO mc) {
		this.data = mc.data;
	}
	
	public GO clone() { return new GO(this); }
	
	public boolean equals(Object other) {
		if(other instanceof GO) {
			GO ns = (GO) other;
			if(!ns.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
