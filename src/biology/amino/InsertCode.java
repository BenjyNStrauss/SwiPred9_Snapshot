package biology.amino;

import assist.ActuallyCloneable;
import assist.Deconstructable;
import biology.BioObject;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class InsertCode extends BioObject implements ActuallyCloneable, Comparable<InsertCode>, Deconstructable {
	private static final long serialVersionUID = 1L;
	
	public static final char NO_CODE = '*';
	
	public final int index;
	public final char code;
	
	//public char test;
	
	public InsertCode(String parseMe) {
		char ch = parseMe.charAt(parseMe.length()-1);
		
		if(Character.isAlphabetic(ch)) {
			index = Integer.parseInt(parseMe.substring(0, parseMe.length()-1));
			code = ch;
		} else {
			index = Integer.parseInt(parseMe);
			code = NO_CODE;
		}
	}
	
	public InsertCode(int index) {
		this(index, NO_CODE);
	}
	
	public InsertCode(int index, char code) {
		this.index = index;
		this.code = code;
	}
	
	public InsertCode(String index, String code) {
		this.index = Integer.parseInt(index);
		this.code = (code.length() == 0) ? NO_CODE : code.charAt(0);
	}
	
	private InsertCode(InsertCode cloneFrom) {
		this.index = cloneFrom.index;
		this.code = cloneFrom.code;
	}

	@Override
	public int compareTo(InsertCode other) {
		if(index == other.index) {
			return code - other.code;
		} else {
			return index - other.index;
		}
	}
	
	public int hashCode() { return toString().hashCode(); }
	
	public boolean equals(Object other) {
		if(other instanceof InsertCode) {
			InsertCode _other_ = (InsertCode) other;
			return (index == _other_.index) && (code == _other_.code);
		} else {
			return false;
		}
	}
	
	public boolean lesserThan(InsertCode other) { return compareTo(other) < 0; }
	public boolean lesserEqualThan(InsertCode other) { return compareTo(other) <= 0; }
	public boolean greaterThan(InsertCode other) { return compareTo(other) > 0; }
	public boolean greaterEqualThan(InsertCode other) { return compareTo(other) >= 0; }
	
	public InsertCode clone() { return new InsertCode(this); }
	
	public String toString() { return (code == NO_CODE) ? ""+index : ""+index+code; }

	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
}
