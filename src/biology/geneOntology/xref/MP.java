package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * MP-type GO xref
 * @author Benjamin Strauss
 *
 */

public class MP extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: MP:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public MP(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected MP(MP mc) {
		this.data = mc.data;
	}
	
	public MP clone() { return new MP(this); }
	
	public boolean equals(Object other) {
		if(other instanceof MP) {
			MP ns = (MP) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
