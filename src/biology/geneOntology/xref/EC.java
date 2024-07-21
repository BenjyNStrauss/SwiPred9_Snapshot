package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * EC-type GO xref
 * @author Benjamin Strauss
 *
 */

public class EC extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	private static final String PREFIX = "xref: EC:";
	
	protected final int part1, part2, part3;
	protected final String part4;
	protected final String subref;
	
	public EC(int part1, int part2, int part3, String part4) {
		this(part1, part2, part3, part4, null);
	}
	
	public EC(int part1, int part2, int part3, String part4, String comment) {
		this.part1 = part1;
		this.part2 = part2;
		this.part3 = part3;
		this.part4 = part4;
		this.subref = comment;
	}
	
	public EC(String xref) {
		if(!xref.startsWith(PREFIX)) { throw new GOParsingException(); } 
		
		String[] parts = compactArray(xref.split(":"), 2);
		
		parts = parts[2].split("\\.");
		part1 = Integer.parseInt(parts[0]);
		part2 = (!parts[1].equals("-")) ? Integer.parseInt(parts[1]) : -1;
		part3 = (!parts[2].equals("-")) ? Integer.parseInt(parts[2]) : -1;
		
		if(parts[3].contains("{xref=\"")) {
			String[] tmp = parts[3].split("\\{xref=\"");
			part4 = tmp[0].trim();
			subref = tmp[1].substring(0, tmp[1].length()-2).trim();
		} else {
			part4 = parts[3];
			subref = null;
		}
	}
	
	protected EC(EC ec) {
		this.part1 = ec.part1;
		this.part2 = ec.part2;
		this.part3 = ec.part3;
		this.part4 = ec.part4;
		this.subref = ec.subref;
	}
	
	public EC clone() { return new EC(this); }
	
	public boolean equals(Object other) {
		if(other instanceof EC) {
			EC ec = (EC) other;
			if(ec.part1 != part1) { return false; }
			if(ec.part2 != part2) { return false; }
			if(ec.part3 != part3) { return false; }
			if(!ec.part4.equals(part4)) { return false; }
			if(!ec.subref.equals(subref)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { 
		String pt2 = (part2 < 0) ? "-" : ""+part2;
		String pt3 = (part3 < 0) ? "-" : ""+part3;
		String comment = (subref != null) ? " {xref=\""+subref+"\"}" : "";
		
		return PREFIX+part1+"."+pt2+"."+pt3+"."+part4+comment;
	}
}
