package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * MetaCyc-type GO xref
 * @author Benjamin Strauss
 *
 */

public class MetaCyc extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected final String data;
	
	public MetaCyc(String xref) {
		if(!xref.startsWith("xref: MetaCyc:")) { throw new GOParsingException(); } 
		
		this.data = compactArray(xref.split(":"), 2)[2];
	}
	
	protected MetaCyc(MetaCyc mc) {
		this.data = mc.data;
	}
	
	public MetaCyc clone() { return new MetaCyc(this); }
	
	public boolean equals(Object other) {
		if(other instanceof MetaCyc) {
			MetaCyc mc = (MetaCyc) other;
			if(!mc.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: MetaCyc:"+data; }
}
