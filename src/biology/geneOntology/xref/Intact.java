package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * Intact-type GO xref
 * @author Benjamin Strauss
 *
 */

public class Intact extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: Intact:";
	
	public final String letters;
	public final int number;
	
	public Intact(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split("[:\\-\"]");
		letters = parts[2];
		number = Integer.parseInt(parts[3].trim());
	}
	
	protected Intact(Intact upkw) {
		this.letters = upkw.letters;
		this.number = upkw.number;	
	}
	
	public Intact clone() { return new Intact(this); }
	
	public boolean equals(Object other) {
		if(other instanceof Intact) {
			Intact upkw = (Intact) other;
			if(upkw.number != number) { return false; }
			if(!upkw.letters.equals(letters)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		return PREFIX+letters+"-"+number;
	}
}
