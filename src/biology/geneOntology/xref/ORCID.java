package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * ORCID-type GO xref
 * @author Benjamin Strauss
 *
 */

public class ORCID extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: ORCID:";
	
	protected final int part1, part2, part3, part4;
	
	public ORCID(int part1, int part2, int part3, int part4) {
		this.part1 = part1;
		this.part2 = part2;
		this.part3 = part3;
		this.part4 = part4;
	}
	
	public ORCID(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = compactArray(xref.split(":"), 2);
		
		parts = parts[2].split("-");
		part1 = Integer.parseInt(parts[0]);
		part2 = Integer.parseInt(parts[1]);
		part3 = Integer.parseInt(parts[2]);
		part4 = Integer.parseInt(parts[3]);
	}
	
	protected ORCID(ORCID ec) {
		this.part1 = ec.part1;
		this.part2 = ec.part2;
		this.part3 = ec.part3;
		this.part4 = ec.part4;
	}
	
	public ORCID clone() { return new ORCID(this); }
	
	public boolean equals(Object other) {
		if(other instanceof ORCID) {
			ORCID orcid = (ORCID) other;
			if(orcid.part1 != part1) { return false; }
			if(orcid.part2 != part2) { return false; }
			if(orcid.part3 != part3) { return false; }
			if(orcid.part4 != part4) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		return PREFIX+part1+"-"+part2+"-"+part3+"-"+part4;
	}
}
