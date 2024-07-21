package biology.amino;

import java.util.Set;

import biology.descriptor.DescriptorType;
import biology.descriptor.EncodingType;
import biology.descriptor.ResAnnotation;
import biology.descriptor.VKPred;
import biology.molecule.types.AminoType;
import chem.AminoAtom;

/**
 * 
 * @author Benjamin strauss
 *
 */

public interface Aminoid {
	
	public AminoType residueType();
	public SecondaryStructure secondary();
	public SecondarySimple secSimple();
	
	public default char toChar() { 
		return (residueType() != null) ? residueType().toChar() : '_';
	}
	
	public default String toCode() { 
		return (residueType() != null) ? residueType().toCode() : null;
	}
	
	public void setDescriptor(String string, double value);
	public double getDescriptor(String key);
	
	public Set<String> atomKeys();
	public AminoAtom getAtom(String atomKey);
	
	public Set<VKPred> vkKeys();
	
	public void setVkbat(VKPred predictor, SecondarySimple pred);
	public default void setVkbat(VKPred predictor, char pred) {
		setVkbat(predictor, SecondarySimple.parse(pred));
	}
	
	public SecondarySimple getVKPrediction(VKPred key);
	public void clearVK();
	public double vkbat();
	
	public void setAtom(AminoAtom aatom);
	public double averageCharge();
	public String getStandardCharges();
	
	public void setEncoding(EncodingType type, double[] ds);
	public double[] getEncoding(EncodingType model);
	
	public int numHomologues(SecondaryStructure type);
	public default boolean hasHomologue(SecondaryStructure type) {
		return numHomologues(type) > 0;
	}
	
	public boolean hasAnnotation(ResAnnotation ra);
	public boolean addAnnotation(ResAnnotation ra);

	
	public default void setDescriptor(DescriptorType descType, double value) {
		setDescriptor(descType.toString(), value);
	}
	
	public default double getDescriptor(DescriptorType descType) {
		return getDescriptor(descType.toString());
	}
}
