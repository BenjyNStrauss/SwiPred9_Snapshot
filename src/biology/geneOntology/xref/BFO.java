package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * BFO-type GO xref
 * @author Benjamin Strauss
 *
 */

public class BFO extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: BFO:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	public final int data;
	
	public BFO(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected BFO(BFO mc) {
		this.data = mc.data;
	}
	
	public BFO clone() { return new BFO(this); }
	
	public boolean equals(Object other) {
		if(other instanceof BFO) {
			BFO ns = (BFO) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
