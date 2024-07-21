package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * GOC-type GO xref
 * @author Benjamin Strauss
 *
 */

public class GOC extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: GOC";
	private static final String PREFIX2 = "xref: GGOC";
	
	protected final String data;
	
	public GOC(String xref) {
		if(!xref.startsWith(PREFIX) && !xref.startsWith(PREFIX2)) { 
			throw new GOParsingException(xref);
		} 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected GOC(GOC mc) {
		this.data = mc.data;
	}
	
	public GOC clone() { return new GOC(this); }
	
	public boolean equals(Object other) {
		if(other instanceof GOC) {
			GOC km = (GOC) other;
			if(!km.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
