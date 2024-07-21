package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * TC-type GO xref
 * @author Benjamin Strauss
 *
 */

public class TC extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected final String data;
	
	public TC(String xref) {
		if(!xref.startsWith("xref: TC:")) { throw new GOParsingException(); } 
		
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
	
	protected TC(TC tc) {
		this.data = tc.data;
	}
	
	public TC clone() { return new TC(this); }
	
	public boolean equals(Object other) {
		if(other instanceof TC) {
			TC tc = (TC) other;
			if(!tc.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: TC:"+data; }
}
