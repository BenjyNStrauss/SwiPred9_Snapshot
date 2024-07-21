package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * PMID-type GO xref
 * @author Benjamin Strauss
 *
 */

public class PMID extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: PMID:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("00000000");
	
	protected final int data;
	
	public PMID(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected PMID(PMID mc) {
		this.data = mc.data;
	}
	
	public PMID clone() { return new PMID(this); }
	
	public boolean equals(Object other) {
		if(other instanceof PMID) {
			PMID ns = (PMID) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
