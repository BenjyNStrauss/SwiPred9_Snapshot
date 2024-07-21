package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * WBbt-type GO xref
 * @author Benjamin Strauss
 *
 */

public class WBbt extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public WBbt(String xref) {
		if(!xref.startsWith("xref: WBbt:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected WBbt(WBbt mc) {
		this.data = mc.data;
	}
	
	public WBbt clone() { return new WBbt(this); }
	
	public boolean equals(Object other) {
		if(other instanceof WBbt) {
			WBbt ns = (WBbt) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: WBbt:"+FORMATTER.format(data); }
}
