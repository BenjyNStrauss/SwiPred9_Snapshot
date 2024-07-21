package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;
import biology.geneOntology.GOParsingTools;

/**
 * DOI-type GO xref
 * @author Benjamin Strauss
 *
 */

public class DOI extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: DOI:";
	
	protected final double number;
	protected final String data;
	
	public DOI(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		parts = GOParsingTools.compactArray(parts[2].split("/"), 1, "/");
		
		number = Double.parseDouble(parts[0].trim());
		data = parts[1].trim();
	}
	
	protected DOI(DOI kr) {
		this.number = kr.number;
		this.data = kr.data;
	}
	
	public DOI clone() { return new DOI(this); }
	
	public boolean equals(Object other) {
		if(other instanceof DOI) {
			DOI doi = (DOI) other;
			if(doi.number != number) { return false; }
			if(data == null) {
				if(doi.data != null) { return false; }
			} else {
				if(!doi.data.equals(data)) { return false; }
			}
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		return PREFIX+number + "/"+data;
	}
}
