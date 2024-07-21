package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * UniPathway-type GO xref
 * @author Benjamin Strauss
 *
 */

public class UniPathway extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected final String data;
	
	public UniPathway(String xref) {
		if(!xref.startsWith("xref: UniPathway:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		if(parts.length > 2) {
			String tmp = parts[2];
			for(int ii = 3; ii < parts.length; ++ii) {
				tmp += ":" + parts[ii];
			}
			
			this.data = tmp;
		} else {
			this.data = parts[2];
		}
	}
	
	protected UniPathway(UniPathway tc) {
		this.data = tc.data;
	}
	
	public UniPathway clone() { return new UniPathway(this); }
	
	public boolean equals(Object other) {
		if(other instanceof UniPathway) {
			UniPathway tc = (UniPathway) other;
			if(!tc.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: UniPathway:"+data; }
}
