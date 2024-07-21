package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * KeggReaction-type GO xref
 * @author Benjamin Strauss
 *
 */

public class KeggReaction extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected final String data;
	protected final String comment;
	
	public KeggReaction(String xref) {
		if(!xref.startsWith("xref: KEGG_REACTION:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split("[:\"]");
		
		data = parts[2].trim();
		if(parts.length == 3) {
			comment = null;
		} else {
			comment = compactArray(parts, 3)[3].trim();
		}
	}
	
	protected KeggReaction(KeggReaction kr) {
		this.data = kr.data;
		this.comment = kr.comment;
	}
	
	public KeggReaction clone() { return new KeggReaction(this); }
	
	public boolean equals(Object other) {
		if(other instanceof KeggReaction) {
			KeggReaction kr = (KeggReaction) other;
			if(!kr.data.equals(data)) { return false; }
			if(comment == null) {
				if(kr.comment != null) { return false; }
			} else {
				if(!kr.comment.equals(comment)) { return false; }
			}
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		if(comment == null) {
			return "xref: KEGG_REACTION:"+data;
		} else {
			return "xref: KEGG_REACTION:"+data + " \""+comment+"\"";
		}
	}
}
