package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * MA-type GO xref
 * @author Benjamin Strauss
 *
 */

public class MA extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: MA:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public MA(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected MA(MA mc) {
		this.data = mc.data;
	}
	
	public MA clone() { return new MA(this); }
	
	public boolean equals(Object other) {
		if(other instanceof MA) {
			MA ns = (MA) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
