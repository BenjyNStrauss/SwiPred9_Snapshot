package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * ISBN-type GO xref
 * @author Benjamin Strauss
 *
 */

public class ISBN extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: ISBN:";
	
	//private static final DecimalFormat FORMATTER = new DecimalFormat("0000000000");
	
	protected final String data;
	
	public ISBN(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected ISBN(ISBN mc) {
		this.data = mc.data;
	}
	
	public ISBN clone() { return new ISBN(this); }
	
	public boolean equals(Object other) {
		if(other instanceof ISBN) {
			ISBN ns = (ISBN) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
