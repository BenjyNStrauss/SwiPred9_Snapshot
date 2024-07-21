package biology.amino;

import java.util.Objects;

import assist.Deconstructable;
import assist.MetaBoolean;
import biology.BioObject;
import biology.molecule.types.AminoType;

/**
 * Represents a Residue's Primary-Secondary Structure configuration
 * Intended for use with AminoPosition
 * @author Benjy Strauss
 *
 */

public class ResidueConfig extends BioObject implements Deconstructable, Cloneable {
	private static final long serialVersionUID = 1L;
	//the type of primary structure (R-group)
	private AminoType primary;
	//the type of secondary structure
	private SecondaryStructure secondary;
	//how often the combination of primary and secondary structure occurs
	private int occurrences;
	
	private MetaBoolean disordered = MetaBoolean.UNKNOWN;
	
	public void setDisordered(MetaBoolean disordered) { this.disordered = disordered; }
	public MetaBoolean disordered() { return disordered; }
	
	/**
	 * Construct a new configuration.  Occurrences is set to 1 by default
	 * @param primary
	 * @param secondary
	 */
	public ResidueConfig(AminoType primary, SecondaryStructure secondary) { 
		this(primary, secondary, 1);
	}
	
	/**
	 * Construct a new configuration.  Occurrences is set to 1 by default
	 * @param primary
	 * @param secondary
	 */
	public ResidueConfig(AminoType primary, SecondaryStructure secondary, int occurrences) { 
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
	public SecondaryStructure secondary() { return secondary; }
	
	/**
	 * 
	 * @return the number of occurrences
	 */
	public int occurrences() { return occurrences; }
	
	
	
	/**
	 * 
	 * @param other
	 */
	public void add(ResidueConfig other) { add(other, false); }
	
	/**
	 * 
	 * @param other
	 * @param allowEquivalency
	 */
	public void add(ResidueConfig other, boolean allowEquivalency) {
		if(allowEquivalency) {
			if(equivalent(other)) {
				occurrences += other.occurrences;
			} else {
				throw new InconsistentUnitException();
			}
		} else {
			if(equals(other)) {
				occurrences += other.occurrences;
			} else {
				qp(this);
				qp(other);
				throw new InconsistentUnitException();
			}
		}
	}
	
	/**
	 * 
	 * @param other
	 */
	public void subtract(ResidueConfig other) { subtract(other, false); }
	
	public void subtract(ResidueConfig other, boolean allowEquivalency) {
		if(allowEquivalency) {
			if(equivalent(other)) {
				occurrences -= other.occurrences;
			} else {
				throw new InconsistentUnitException();
			}
		} else {
			if(equals(other)) {
				occurrences -= other.occurrences;
			} else {
				throw new InconsistentUnitException();
			}
		}
	}
	
	public String toCode() { return primary.toCode(); }
	
	public boolean equals(ResidueConfig other) {
		return (primary.equals(other.primary) && secondary.equals(other.secondary));
	}
	
	public boolean equivalent(ResidueConfig other) {
		return (primary.couldBe(other.primary) && secondary.simpleClassify().equals(other.secondary.simpleClassify()));
	}
	
	public static boolean equals(ResidueConfig a, ResidueConfig b) {
		return (a.primary.equals(b.primary) && a.secondary.equals(b.secondary));
	}
	
	public static boolean equivalent(ResidueConfig a, ResidueConfig b) {
		return (a.primary.couldBe(b.primary) && a.secondary.simpleClassify().equals(b.secondary.simpleClassify()));
	}
	
	public ResidueConfig clone() {
		return new ResidueConfig(primary, secondary, occurrences);
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
