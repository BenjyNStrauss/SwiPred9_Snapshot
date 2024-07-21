package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * UniProtKB-KW-type GO xref
 * @author Benjamin Strauss
 *
 */

public class UniProtKB_KW extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: UniProtKB-KW:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000");
	
	public final String letters;
	public final int number;
	
	public UniProtKB_KW(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split("[:\\-\"]");
		letters = parts[3];
		number = Integer.parseInt(parts[4].trim());
	}
	
	protected UniProtKB_KW(UniProtKB_KW upkw) {
		this.letters = upkw.letters;
		this.number = upkw.number;	
	}
	
	public UniProtKB_KW clone() { return new UniProtKB_KW(this); }
	
	public boolean equals(Object other) {
		if(other instanceof UniProtKB_KW) {
			UniProtKB_KW upkw = (UniProtKB_KW) other;
			if(upkw.number != number) { return false; }
			if(!upkw.letters.equals(letters)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		return PREFIX+letters+"-"+FORMATTER.format(number);
	}
}
