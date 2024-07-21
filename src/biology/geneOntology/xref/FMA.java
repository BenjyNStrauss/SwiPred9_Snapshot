package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * FMA-type GO xref
 * @author Benjamin Strauss
 *
 */

public class FMA extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: FMA:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("00000");
	
	public final int data;
	
	public FMA(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected FMA(FMA mc) {
		this.data = mc.data;
	}
	
	public FMA clone() { return new FMA(this); }
	
	public boolean equals(Object other) {
		if(other instanceof FMA) {
			FMA ns = (FMA) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
