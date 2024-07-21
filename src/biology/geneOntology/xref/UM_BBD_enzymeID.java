package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * UM_BBD_enzymeID-type GO xref
 * @author Benjamin Strauss
 *
 */

public class UM_BBD_enzymeID extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: UM-BBD_enzymeID:";
	
	protected final String data;
	
	public UM_BBD_enzymeID(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected UM_BBD_enzymeID(UM_BBD_enzymeID mc) {
		this.data = mc.data;
	}
	
	public UM_BBD_enzymeID clone() { return new UM_BBD_enzymeID(this); }
	
	public boolean equals(Object other) {
		if(other instanceof UM_BBD_enzymeID) {
			UM_BBD_enzymeID kr = (UM_BBD_enzymeID) other;
			if(!kr.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}
