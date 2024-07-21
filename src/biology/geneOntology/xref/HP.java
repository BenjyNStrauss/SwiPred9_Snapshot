package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * HP-type GO xref
 * @author Benjamin Strauss
 *
 */

public class HP extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: HP:";
	
	public static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public HP(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException("(HP)"+xref); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected HP(HP mc) {
		this.data = mc.data;
	}
	
	public HP clone() { return new HP(this); }
	
	public boolean equals(Object other) {
		if(other instanceof HP) {
			HP ns = (HP) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
