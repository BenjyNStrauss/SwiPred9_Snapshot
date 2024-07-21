package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * PR-type GO xref
 * @author Benjamin Strauss
 *
 */

public class PR extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: PR:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("000000000");
	
	protected final int data;
	
	public PR(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected PR(PR mc) {
		this.data = mc.data;
	}
	
	public PR clone() { return new PR(this); }
	
	public boolean equals(Object other) {
		if(other instanceof PR) {
			PR ns = (PR) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
