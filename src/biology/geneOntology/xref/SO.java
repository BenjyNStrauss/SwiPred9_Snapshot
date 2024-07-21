package biology.geneOntology.xref;

import java.text.DecimalFormat;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * SO-type GO xref
 * @author Benjamin Strauss
 *
 */

public class SO extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	private static final DecimalFormat FORMATTER = new DecimalFormat("0000000");
	
	protected final int data;
	
	public SO(String xref) {
		if(!xref.startsWith("xref: SO:")) { throw new GOParsingException(); } 
		
		String[] parts = xref.split(":");
		data = Integer.parseInt(parts[2]);
	}
	
	protected SO(SO mc) {
		this.data = mc.data;
	}
	
	public SO clone() { return new SO(this); }
	
	public boolean equals(Object other) {
		if(other instanceof SO) {
			SO ns = (SO) other;
			if(ns.data != data) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: SO:"+FORMATTER.format(data); }
}
