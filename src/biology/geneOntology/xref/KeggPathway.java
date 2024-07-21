package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * KEGG_PATHWAY-type GO xref
 * @author Benjamin Strauss
 *
 */

public class KeggPathway extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected final String data;
	
	public KeggPathway(String xref) {
		if(!xref.startsWith("xref: KEGG_PATHWAY:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected KeggPathway(KeggPathway mc) {
		this.data = mc.data;
	}
	
	public KeggPathway clone() { return new KeggPathway(this); }
	
	public boolean equals(Object other) {
		if(other instanceof KeggPathway) {
			KeggPathway kr = (KeggPathway) other;
			if(!kr.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: KEGG_PATHWAY:"+data; }
}
