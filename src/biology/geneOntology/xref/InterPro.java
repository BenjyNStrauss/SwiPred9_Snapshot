package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * InterPro-type GO xref
 * @author Benjamin Strauss
 *
 */


public class InterPro extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: InterPro:";
	
	protected final String data;
	
	public InterPro(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected InterPro(InterPro mc) {
		this.data = mc.data;
	}
	
	public InterPro clone() { return new InterPro(this); }
	
	public boolean equals(Object other) {
		if(other instanceof InterPro) {
			InterPro ns = (InterPro) other;
			if(!ns.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
