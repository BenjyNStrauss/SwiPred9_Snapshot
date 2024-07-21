package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * KEGG_MODULE-type GO xref
 * @author Benjamin Strauss
 *
 */

public class KeggModule extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: kegg.module:";
	
	protected final String data;
	
	public KeggModule(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected KeggModule(KeggModule mc) {
		this.data = mc.data;
	}
	
	public KeggModule clone() { return new KeggModule(this); }
	
	public boolean equals(Object other) {
		if(other instanceof KeggModule) {
			KeggModule km = (KeggModule) other;
			if(!km.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
