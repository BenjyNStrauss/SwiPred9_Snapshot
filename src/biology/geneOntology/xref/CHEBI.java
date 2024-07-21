package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * CHEBI-type GO xref
 * @author Benjamin Strauss
 *
 */

public class CHEBI extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: CHEBI:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("00000");
	
	protected final int data;
	
	public CHEBI(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected CHEBI(CHEBI mc) {
		this.data = mc.data;
	}
	
	public CHEBI clone() { return new CHEBI(this); }
	
	public boolean equals(Object other) {
		if(other instanceof CHEBI) {
			CHEBI ns = (CHEBI) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
