package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * BioCyc-type GO xref
 * @author Benjamin Strauss
 *
 */

public class BioCyc extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: BioCyc:";
	
	//private static final DecimalFormat FORMATTER = new DecimalFormat("0000");
	
	public final String letters;
	public final int number;
	
	public BioCyc(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split("[:\\-\"]");
		letters = parts[2];
		number = Integer.parseInt(parts[3].trim());
	}
	
	protected BioCyc(BioCyc upkw) {
		this.letters = upkw.letters;
		this.number = upkw.number;	
	}
	
	public BioCyc clone() { return new BioCyc(this); }
	
	public boolean equals(Object other) {
		if(other instanceof BioCyc) {
			BioCyc upkw = (BioCyc) other;
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
