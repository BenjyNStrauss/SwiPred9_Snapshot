package model;

import java.util.Objects;

import assist.ActuallyCloneable;
import utilities.DataObject;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class ResKey extends DataObject implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	public final String protein;
	public final char chain;
	
	public ResKey(String protein, char chain) {
		Objects.requireNonNull(protein);
		this.protein = protein;
		this.chain = chain;
	}
	
	public int hashCode() { return protein.hashCode() + chain; }
	
	public ResKey clone() { return new ResKey(protein, chain); }
	
	public boolean equals(Object other) {
		if(other instanceof ResKey) {
			ResKey otherKey = (ResKey) other;
			return (protein.equals(otherKey.protein) && chain == otherKey.chain);
		} else {
			return false;
		}
	}
	
	public String toString() { return protein + ":" + chain; }
}
