package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * UM-BBD_reactionID
 * @author Benjamin Strauss
 *
 */

public class UM_BBD_reactionID extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: UM-BBD_reactionID:";
	
	protected final String data;
	
	public UM_BBD_reactionID(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = parts[2];
	}
	
	protected UM_BBD_reactionID(UM_BBD_reactionID mc) {
		this.data = mc.data;
	}
	
	public UM_BBD_reactionID clone() { return new UM_BBD_reactionID(this); }
	
	public boolean equals(Object other) {
		if(other instanceof UM_BBD_reactionID) {
			UM_BBD_reactionID kr = (UM_BBD_reactionID) other;
			if(!kr.data.equals(data)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+data; }
}

