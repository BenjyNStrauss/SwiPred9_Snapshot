package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * FBbt-type GO xref
 * @author Benjamin Strauss
 *
 */

public class FBbt extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: FBbt:";
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("00000000");
	
	protected final int data;
	
	public FBbt(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected FBbt(FBbt mc) {
		this.data = mc.data;
	}
	
	public FBbt clone() { return new FBbt(this); }
	
	public boolean equals(Object other) {
		if(other instanceof FBbt) {
			FBbt ns = (FBbt) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return PREFIX+FORMATTER.format(data); }
}
