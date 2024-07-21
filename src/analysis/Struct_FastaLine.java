package analysis;

import assist.Deconstructable;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class Struct_FastaLine extends LocalToolBase implements Deconstructable {
	public final String header;
	public final String sequence;
	
	public final boolean isProtein;
	public final int length;
	public final String desc;
	public final String id;
	
	public boolean hasURL;
	
	public Struct_FastaLine(String header, String seq) {
		//qp(header);
		this.header = header;
		this.sequence = seq;
		this.isProtein = header.substring(header.indexOf(":")+1).startsWith("protein");
		
		String tmp = header.substring(header.indexOf(":")+1);
		tmp = tmp.substring(tmp.indexOf(":")+1);
		length = Integer.parseInt(tmp.substring(0, tmp.indexOf(" ")).trim());
		
		desc = tmp.substring(tmp.indexOf(" ")).trim();
		
		id = header.substring(1,header.indexOf(" ")+1).trim();
	}
	
	public boolean equals(Object other) {
		if(other instanceof Struct_FastaLine) {
			Struct_FastaLine osfl = (Struct_FastaLine) other;
			return (osfl.desc.equals(desc) && length == osfl.length) || sequence.equals(osfl.sequence);
		} else {
			return false;
		}
	}
	
	public String toString() {
		return id;
	}

	@SuppressWarnings("deprecation")
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}	
	}
}
