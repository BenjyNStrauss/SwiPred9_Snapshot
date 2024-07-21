package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * SABIO-RK-type GO xref
 * @author Benjamin Strauss
 *
 */

public class SabioRK extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: SABIO-RK:";

	private static final DecimalFormat FORMATTER = new DecimalFormat("0000");
	
	protected final int data;
	
	public SabioRK(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected SabioRK(SabioRK mc) {
		this.data = mc.data;
	}
	
	public SabioRK clone() { return new SabioRK(this); }
	
	public boolean equals(Object other) {
		if(other instanceof SabioRK) {
			SabioRK ns = (SabioRK) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
