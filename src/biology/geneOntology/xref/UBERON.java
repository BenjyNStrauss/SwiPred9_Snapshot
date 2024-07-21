package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * UBERON-type GO xref
 * @author Benjamin Strauss
 *
 */

public class UBERON extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: UBERON:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public UBERON(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected UBERON(UBERON mc) {
		this.data = mc.data;
	}
	
	public UBERON clone() { return new UBERON(this); }
	
	public boolean equals(Object other) {
		if(other instanceof UBERON) {
			UBERON ns = (UBERON) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
