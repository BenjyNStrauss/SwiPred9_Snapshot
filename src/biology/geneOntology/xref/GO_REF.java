package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * GO_REF-type GO xref
 * @author Benjamin Strauss
 *
 */

public class GO_REF extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: GO_REF:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("00000000");
	
	protected final int data;
	
	public GO_REF(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected GO_REF(GO_REF mc) {
		this.data = mc.data;
	}
	
	public GO_REF clone() { return new GO_REF(this); }
	
	public boolean equals(Object other) {
		if(other instanceof GO_REF) {
			GO_REF ns = (GO_REF) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
