package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * KEGG-type GO xref
 * @author Benjamin Strauss
 *
 */

public class Kegg extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: KEGG:";
	
	public final String data;
	
	public Kegg(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected Kegg(Kegg mc) {
		this.data = mc.data;
	}
	
	public Kegg clone() { return new Kegg(this); }
	
	public boolean equals(Object other) {
		if(other instanceof Kegg) {
			Kegg km = (Kegg) other;
			if(!km.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
