package biology.amino;

import java.util.Objects;
import java.util.Set;

import assist.MetaBoolean;
import assist.util.LabeledHash;
import assist.util.LabeledSet;
import biology.BioObject;
import biology.descriptor.EncodingType;
import biology.descriptor.Metric;
import biology.descriptor.ResAnnotation;
import biology.molecule.types.AminoType;

/**
 * An object that can go in a Chain
 * @author Benjamin Strauss
 *
 */

public abstract class ChainObject extends BioObject {
	private static final long serialVersionUID = 1L;
	private InsertCode postition;
	public boolean conflict;
	
	//holds all the descriptors with a string as the key
	protected final LabeledHash<String, Double> descriptors;
	//contains encoding data
	protected final LabeledHash<EncodingType, double[]> encodings;
	
	protected final LabeledSet<ResAnnotation> annotations = new LabeledSet<ResAnnotation>();
	
	public ChainObject() { 
		descriptors = new LabeledHash<String, Double>();
		encodings = new LabeledHash<EncodingType, double[]>();
	}

	public abstract char toChar();
	
	public abstract String toCode();
	
	public abstract SecondaryStructure secondary();
	
	public abstract MetaBoolean disordered();
	
	public InsertCode postition() { return postition; }

	public void setPostition(InsertCode postition) { this.postition = postition; }
	
	/** @return SideChain if one exists */
	public AminoType residueType() { return AminoType.INVALID; }
	
	public void setDescriptor(String descriptor, double value) {
		Objects.requireNonNull(descriptor, "Null descriptors cannot be set.");
		descriptors.put(descriptor, value);
	}
	
	public double getDescriptor(String descriptor) { 
		Objects.requireNonNull(descriptor);
		Double value = descriptors.get(descriptor);
		return (value == null) ? Double.NaN : value;
	}
	
	public double getDescriptor(Metric descriptor) { 
		Objects.requireNonNull(descriptor);
		Double value = descriptors.get(descriptor.toString());
		return (value == null) ? Double.NaN : value;
	}
	
	/**
	 * 
	 * @param model
	 * @param ds
	 */
	public void setEncoding(EncodingType type, double[] value) {
		Objects.requireNonNull(value, "Null encodings are prohibited.");
		encodings.put(type, value);
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public double[] getEncoding(EncodingType type) {
		Objects.requireNonNull(type, "Null encodings are prohibited.");
		return encodings.get(type);
	}
	
	public Set<EncodingType> allEncodings() { return encodings.keySet(); }
	
	/**
	 * 
	 * @param annotation
	 * @return
	 */
	public boolean hasAnnotation(ResAnnotation annotation) {
		return annotations.contains(annotation);
	}
	
	/**
	 * 
	 * @param annotation
	 * @return
	 */
	public boolean addAnnotation(ResAnnotation annotation) {
		return annotations.add(annotation);
	}
	
	/**
	 * 
	 * @param annotation
	 * @return
	 */
	public boolean removeAnnotation(ResAnnotation annotation) {
		return annotations.remove(annotation);
	}
	
	public Set<String> allDescriptors() { return descriptors.keySet(); }
}
