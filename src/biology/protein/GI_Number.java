package biology.protein;

import assist.ActuallyCloneable;
import utilities.DataObject;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class GI_Number extends DataObject implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	public final String prefix;
	public final double gi;
	
	public GI_Number(String raw) {
		double tmp;
		String pref_tmp;
		
		try {
			tmp = Integer.parseInt(raw);
			pref_tmp = "";
		} catch (NumberFormatException NFE) {
			int prefLen = 0;
			for(; prefLen < raw.length(); ++prefLen) {
				if(Character.isDigit(raw.charAt(prefLen))) {
					break;
				}
			}
			tmp = Double.parseDouble(raw.substring(prefLen));
			pref_tmp = raw.substring(0, prefLen);
			
		}
		this.gi = tmp;
		this.prefix = pref_tmp;
	}
	
	public GI_Number(double gi) { this("", gi); }
	
	public GI_Number(String prefix, double gi) {
		this.prefix = prefix;
		this.gi = gi;
	}
	
	public int hashCode() { return toString().hashCode(); }
	
	public GI_Number clone() { return new GI_Number(prefix, gi); }
	
	public boolean equals(Object other) {
		if(other instanceof GI_Number) {
			return toString().equals(other.toString());
		} else {
			return false;
		}
	}
	
	public String toString() {
		if(gi%0==0) {
			String tmp = prefix+gi;
			return tmp.substring(0, tmp.length()-2);
		} else {
			return prefix+gi;
		}
	}
}
