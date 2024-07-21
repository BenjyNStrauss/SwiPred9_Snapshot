package modules.descriptor.charge;

import java.util.Objects;

import biology.BioObject;
import biology.amino.InconsistentUnitException;
import biology.amino.SecondarySimple;
import biology.molecule.types.AminoType;
import assist.Deconstructable;

/**
 * Represents a Residue's Primary-Secondary Structure configuration
 * Intended for use with AminoPosition
 * @author Benjy Strauss
 *
 */

class SimpleResConfig extends BioObject implements Deconstructable, Cloneable {
	private static final long serialVersionUID = 1L;
	//the type of primary structure (R-group)
	private AminoType primary;
	//the type of secondary structure
	private SecondarySimple secondary;
	//how often the combination of primary and secondary structure occurs
	private int occurrences;
	
	/**
	 * Construct a new configuration.  Occurrences is set to 1 by default
	 * @param primary
	 * @param secondary
	 */
	public SimpleResConfig(AminoType primary, SecondarySimple secondary) { 
		this(primary, secondary, 0);
	}
	
	/**
	 * Construct a new configuration.  Occurrences is set to 1 by default
	 * @param primary
	 * @param secondary
	 */
	public SimpleResConfig(AminoType primary, SecondarySimple secondary, int occurrences) { 
		Objects.requireNonNull(primary, "No primary structure specified!");
		Objects.requireNonNull(secondary, "No secondary structure specified!");
		
		this.primary = primary;
		this.secondary = secondary;
		this.occurrences = occurrences;
	}
	
	/**
	 * Not the occurrence of this configuration
	 */
	public void noteOccurence() { ++occurrences; }
	
	/**
	 * The configuration's primary structure
	 * @return
	 */
	public AminoType primary() { return primary; }
	
	/**
	 * The configuration's seconday structure
	 * @return
	 */
	public SecondarySimple secondary() { return secondary; }
	
	/**
	 * 
	 * @return the number of occurrences
	 */
	public int occurrences() { return occurrences; }
	
	/**
	 * 
	 * @param other
	 */
	public void add(SimpleResConfig other) { add(other, false); }
	
	/**
	 * 
	 * @param other
	 * @param allowEquivalency
	 */
	public void add(SimpleResConfig other, boolean allowEquivalency) {
		if(equals(other)) {
			occurrences += other.occurrences;
		} else {
			qp(this);
			qp(other);
			throw new InconsistentUnitException();
		}
	}
	
	/**
	 * 
	 * @param other
	 */
	public void subtract(SimpleResConfig other) { subtract(other, false); }
	
	public void subtract(SimpleResConfig other, boolean allowEquivalency) {
		if(equals(other)) {
			occurrences -= other.occurrences;
		} else {
			throw new InconsistentUnitException();
		}
	}
	
	public String toCode() { return primary.toCode(); }
	
	public boolean equals(SimpleResConfig other) {
		return (primary.equals(other.primary) && secondary.equals(other.secondary));
	}
	
	public static boolean equals(SimpleResConfig a, SimpleResConfig b) {
		return (a.primary.equals(b.primary) && a.secondary.equals(b.secondary));
	}
	
	public SimpleResConfig clone() {
		return new SimpleResConfig(primary, secondary, occurrences);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			qpl("Could not finalize " + this);
		}
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(" + primary + "," + secondary + "," + occurrences + ")");
		return builder.toString();
	}
}
