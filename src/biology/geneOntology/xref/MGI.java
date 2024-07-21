package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * MGI-type GO xref
 * @author Benjamin Strauss
 *
 */


public class MGI extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: MGI:";
	
	public final String data;
	
	public MGI(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected MGI(MGI mc) {
		this.data = mc.data;
	}
	
	public MGI clone() { return new MGI(this); }
	
	public boolean equals(Object other) {
		if(other instanceof MGI) {
			MGI ns = (MGI) other;
			if(!ns.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
