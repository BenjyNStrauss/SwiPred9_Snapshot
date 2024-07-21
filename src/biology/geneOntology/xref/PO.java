package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * PO-type GO xref
 * @author Benjamin Strauss
 *
 */

public class PO extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: PO:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public PO(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected PO(PO mc) {
		this.data = mc.data;
	}
	
	public PO clone() { return new PO(this); }
	
	public boolean equals(Object other) {
		if(other instanceof PO) {
			PO ns = (PO) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
