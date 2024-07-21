package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * PubChem_Compound-type GO xref
 * @author Benjamin Strauss
 *
 */

public class PubChem_Compound extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: PubChem_Compound:";
	
	protected final int data;
	
	public PubChem_Compound(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected PubChem_Compound(PubChem_Compound mc) {
		this.data = mc.data;
	}
	
	public PubChem_Compound clone() { return new PubChem_Compound(this); }
	
	public boolean equals(Object other) {
		if(other instanceof PubChem_Compound) {
			PubChem_Compound ns = (PubChem_Compound) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
