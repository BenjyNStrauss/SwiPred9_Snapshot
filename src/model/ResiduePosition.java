package model;

import java.util.Objects;

import assist.ActuallyCloneable;
import assist.util.LabeledHash;
import biology.amino.InsertCode;

/**
 * All DSSP entries at a given residue position
 * @author Benjamin Strauss
 * 
 */

public class ResiduePosition extends LabeledHash<ResKey, DSSP_Entry> implements ActuallyCloneable, Comparable<ResiduePosition> {
	private static final long serialVersionUID = 1L;
	
	private final InsertCode code;
	
	/**
	 * 
	 * @param code
	 */
	public ResiduePosition(InsertCode code) {
		Objects.requireNonNull(code);
		this.code = code;
	}
	
	private ResiduePosition(ResiduePosition cloneFrom) {
		this.code = cloneFrom.code.clone();
		for(ResKey key: cloneFrom.keySet()) {
			put(key.clone(), cloneFrom.get(key).clone());
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public InsertCode positionCode() { return code.clone(); }
	
	public void merge(ResiduePosition element) {
		merge(element, false);
	}
	
	public void merge(ResiduePosition element, boolean override) {
		for(ResKey key: element.keySet()) {
			if(!containsKey(key) || override) {
				put(key.clone(), element.get(key).clone());
			}
		}
	}
	
	@Override
	public int compareTo(ResiduePosition other) { return code.compareTo(other.code); }
	
	public int hashCode() { return code.hashCode(); }
	
	public ResiduePosition clone() { return new ResiduePosition(this); }
	
	public boolean equals(Object other) {
		if(other instanceof ResiduePosition) {
			return ((ResiduePosition) other).code.equals(code); 
		} else {
			return false;
		}
	}
	
	public String toString() { return "ResPos "+code; }
}
