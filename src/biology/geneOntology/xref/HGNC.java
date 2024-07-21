package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * HGNC-type GO xref
 * @author Benjamin Strauss
 *
 */

public class HGNC extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: HGNC:";
	
	protected final int data;
	
	public HGNC(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected HGNC(HGNC mc) {
		this.data = mc.data;
	}
	
	public HGNC clone() { return new HGNC(this); }
	
	public boolean equals(Object other) {
		if(other instanceof HGNC) {
			HGNC ns = (HGNC) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
