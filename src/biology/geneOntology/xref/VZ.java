package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * VZ-type GO xref
 * @author Benjamin Strauss
 *
 */

public class VZ extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: VZ:";
	
	protected final int number;
	protected final String comment;
	
	public VZ(String xref) {
		if(!xref.toLowerCase().startsWith(PREFIX.toLowerCase())) { 
			throw new GOParsingException(xref);
		} 
		
		String[] parts = xref.split("[:\"]");
		number = Integer.parseInt(parts[2].trim());
		if(parts.length == 3) {
			comment = null;
		} else {
			comment = compactArray(parts, 3)[3];
		}
	}
	
	protected VZ(VZ kr) {
		this.number = kr.number;
		this.comment = kr.comment;
	}
	
	public VZ clone() { return new VZ(this); }
	
	public boolean equals(Object other) {
		if(other instanceof VZ) {
			VZ vz = (VZ) other;
			if(vz.number != number) { return false; }
			if(comment == null) {
				if(vz.comment != null) { return false; }
			} else {
				if(!vz.comment.equals(comment)) { return false; }
			}
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		if(comment == null) {
			return PREFIX+number;
		} else {
			return PREFIX+number + " \""+comment+"\"";
		}
	}
}
