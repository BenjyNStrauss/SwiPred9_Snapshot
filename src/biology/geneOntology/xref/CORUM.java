package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * CORUM-type GO xref
 * @author Benjamin Strauss
 *
 */

public class CORUM extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: CORUM:";
	
	protected final int data;
	
	public CORUM(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected CORUM(CORUM mc) {
		this.data = mc.data;
	}
	
	public CORUM clone() { return new CORUM(this); }
	
	public boolean equals(Object other) {
		if(other instanceof CORUM) {
			CORUM ns = (CORUM) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
