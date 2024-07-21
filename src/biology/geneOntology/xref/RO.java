package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * RO-type GO xref
 * @author Benjamin Strauss
 *
 */

public class RO extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public RO(String xref) {
		if(!xref.startsWith("xref: RO:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected RO(RO mc) {
		this.data = mc.data;
	}
	
	public RO clone() { return new RO(this); }
	
	public boolean equals(Object other) {
		if(other instanceof RO) {
			RO ns = (RO) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: RO:"+FORMATTER.format(data); }
}
