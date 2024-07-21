package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * NIF_Subcellular-type GO xref
 * @author Benjamin Strauss
 *
 */


public class NIF_Subcellular extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected final String data;
	
	public NIF_Subcellular(String xref) {
		if(!xref.startsWith("xref: NIF_Subcellular:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected NIF_Subcellular(NIF_Subcellular mc) {
		this.data = mc.data;
	}
	
	public NIF_Subcellular clone() { return new NIF_Subcellular(this); }
	
	public boolean equals(Object other) {
		if(other instanceof NIF_Subcellular) {
			NIF_Subcellular ns = (NIF_Subcellular) other;
			if(!ns.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: NIF_Subcellular:"+data; }
}
