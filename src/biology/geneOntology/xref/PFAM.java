package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * PFAM-type GO xref
 * @author Benjamin Strauss
 *
 */

public class PFAM extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: Pfam:PF";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("00000");
	
	protected final int data;
	
	public PFAM(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2].substring(2));
	}
	
	protected PFAM(PFAM mc) {
		this.data = mc.data;
	}
	
	public PFAM clone() { return new PFAM(this); }
	
	public boolean equals(Object other) {
		if(other instanceof PFAM) {
			PFAM ns = (PFAM) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
