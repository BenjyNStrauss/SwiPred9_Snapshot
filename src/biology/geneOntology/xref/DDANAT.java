package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * DDANAT-type GO xref
 * @author Benjamin Strauss
 *
 */

public class DDANAT extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: DDANAT:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public DDANAT(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected DDANAT(DDANAT mc) {
		this.data = mc.data;
	}
	
	public DDANAT clone() { return new DDANAT(this); }
	
	public boolean equals(Object other) {
		if(other instanceof DDANAT) {
			DDANAT ns = (DDANAT) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
