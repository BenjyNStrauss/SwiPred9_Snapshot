package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * UM-BBD_pathwayID
 * @author Benjamin Strauss
 *
 */

public class UM_BBD_pathwayID extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: UM-BBD_pathwayID:";
	
	protected final String data;
	
	public UM_BBD_pathwayID(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected UM_BBD_pathwayID(UM_BBD_pathwayID mc) {
		this.data = mc.data;
	}
	
	public UM_BBD_pathwayID clone() { return new UM_BBD_pathwayID(this); }
	
	public boolean equals(Object other) {
		if(other instanceof UM_BBD_pathwayID) {
			UM_BBD_pathwayID kr = (UM_BBD_pathwayID) other;
			if(!kr.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}

