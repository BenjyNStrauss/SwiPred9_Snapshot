package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * Reactome-type GO xref
 * @author Benjamin Strauss
 *
 */

public class Reactome extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: Reactome:";
	
	public final char ch;
	public final String code;
	public final double number;
	public final String comment;
	
	/**
	 * 
	 * @param ch
	 * @param code
	 * @param number
	 * @param comment
	 */
	public Reactome(char ch, String code, double number, String comment) {
		this.ch = ch;
		this.code = code;
		this.number = number;
		if(comment.startsWith("\"")) { comment = comment.substring(1); }
		if(comment.endsWith("\"")) { comment = comment.substring(0, comment.length()-1); }
		this.comment = comment;
	}
	
	/**
	 * 
	 * @param xref
	 */
	public Reactome(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split("[:\"]");
		
		if(parts.length > 4) { compactArray(parts, 4); }
		
		this.comment = (parts.length >= 4) ? parts[3] : null;
		
		parts = parts[2].trim().split("\\-");
		
		switch(parts.length) {
		case 3:
			this.ch = parts[0].charAt(0);
			this.code = parts[1];
			this.number = Double.parseDouble(parts[2]);
			break;
		case 1:
			this.ch = '-';
			this.code = null;
			this.number = Double.parseDouble(parts[0]);
			break;
		default:
			throw new GOParsingException("(unexpected format) "+xref);
		}
	}
	
	protected Reactome(Reactome re) {
		this.ch = re.ch;
		this.code = re.code;
		this.number = re.number;
		this.comment = re.comment;
	}
	
	public Reactome clone() { return new Reactome(this); }
	
	public boolean equals(Object other) {
		if(other instanceof Reactome) {
			Reactome re = (Reactome) other;
			if(re.ch != ch) { return false; }
			if(re.code != code) { return false; }
			if(re.number != number) { return false; }
			if(re.comment != comment) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		String tmp = (ch == '-') ? PREFIX+ch+"-"+code+"-"+number : PREFIX+number;
		if(tmp.endsWith(".0")) { tmp = tmp.substring(0, tmp.length()-2); }
		if(comment != null) { return tmp += " \""+comment+"\""; } 
		return tmp;
	}
}
