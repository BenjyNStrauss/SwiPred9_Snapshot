package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * IAO-type GO xref
 * @author Benjamin Strauss
 *
 */

public class IAO extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public IAO(String xref) {
		if(!xref.startsWith("xref: IAO:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected IAO(IAO mc) {
		this.data = mc.data;
	}
	
	public IAO clone() { return new IAO(this); }
	
	public boolean equals(Object other) {
		if(other instanceof IAO) {
			IAO ns = (IAO) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: IAO:"+FORMATTER.format(data); }
}
