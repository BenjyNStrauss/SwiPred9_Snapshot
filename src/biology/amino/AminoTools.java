package biology.amino;

import java.util.Set;

import assist.exceptions.NotYetImplementedError;
import biology.descriptor.DescriptorType;
import biology.descriptor.EncodingType;
import biology.descriptor.SecStructConfig;
import biology.descriptor.VKPred;
import chem.AminoAtom;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public final class AminoTools extends LocalToolBase {

	private AminoTools() { }
	
	/**
	 * 
	 * @param src
	 * @param dest
	 * @param desc
	 */
	public static final void copyDescriptor(Aminoid src, Aminoid dest, DescriptorType desc) {
		copyDescriptor(src, dest, desc.toString());
	}
	
	/**
	 * 
	 * @param src
	 * @param dest
	 * @param desc
	 */
	public static final void copyDescriptor(Aminoid src, Aminoid dest, String desc) {
		dest.setDescriptor(desc, src.getDescriptor(desc));
	}
	
	
	
	/**
	 * Copies all of the atoms from the source to the target
	 * @param target
	 * @param source
	 */
	public static void copyAtoms(Aminoid dest, Aminoid source) {
		Set<String> keys = source.atomKeys();
		for(String key: keys) {
			AminoAtom atom = source.getAtom(key);
			dest.setAtom(atom);
		}
	}
	
	/**
	 * 
	 * @param entropy: entropy value
	 * @param entropyType: number of terms in entropy
	 * @param flip: whether to flip the entropy value
	 * @param normalize: whether to normalize the entropy value
	 * @return
	 */
	public static final String processEntropy(double entropy, int entropyType, boolean flip, boolean normalize) {
		double retVal;
		double maxVal = Math.log(entropyType)/Math.log(2);
		if(flip) { retVal = maxVal - entropy; } else { retVal = entropy; }
		if(normalize) { retVal /= maxVal; }
		return retVal + "";
	}
	
	public static int aminoHasHomologue(Aminoid amino, SecStructConfig descriptor) {
		switch(descriptor) {
		case HAS_H:
			return(amino.hasHomologue(SecondaryStructure.SOME_HELIX) ? 1 : 0);
		case HAS_HO:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER)) ? 1 : 0);
		case HAS_HOS:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) ) ? 1 : 0);
		case HAS_HOU:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case HAS_HS:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET)) ? 1 : 0);
		case HAS_HSU:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case HAS_HU:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED)) ? 1 : 0);
		case HAS_O:
			return(amino.hasHomologue(SecondaryStructure.SOME_OTHER) ? 1 : 0);
		case HAS_OS:
			return((amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET)) ? 1 : 0);
		case HAS_OSU:
			return((amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case HAS_OU:
			return((amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED)) ? 1 : 0);
		case HAS_S:
			return(amino.hasHomologue(SecondaryStructure.SOME_SHEET) ? 1 : 0);
		case HAS_SU:
			return((amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED)) ? 1 : 0);
		case HAS_U:
			return(amino.hasHomologue(SecondaryStructure.DISORDERED) ? 1 : 0);
		case IS_H:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					!amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					!amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_HO:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					!amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					!amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_HOS:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					!amino.hasHomologue(SecondaryStructure.DISORDERED)) ? 1 : 0);
		case IS_HOSU:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_HOU:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_HS:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					!amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_HSU:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_HU:
			return((amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					!amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_O:
			return((!amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					!amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					!amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_OS:
			return((!amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					!amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_OSU:
			return((!amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_OU:
			return((!amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					!amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_S:
			return((!amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					!amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_SU:
			return((!amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case IS_U:
			return((!amino.hasHomologue(SecondaryStructure.SOME_HELIX) &&
					!amino.hasHomologue(SecondaryStructure.SOME_OTHER) &&
					!amino.hasHomologue(SecondaryStructure.SOME_SHEET) &&
					amino.hasHomologue(SecondaryStructure.DISORDERED) ) ? 1 : 0);
		case NUM_H:	return amino.numHomologues(SecondaryStructure.SOME_HELIX);
		case NUM_O:	return amino.numHomologues(SecondaryStructure.SOME_OTHER);
		case NUM_S:	return amino.numHomologues(SecondaryStructure.SOME_SHEET);
		case NUM_U:	return amino.numHomologues(SecondaryStructure.DISORDERED);
		case VK_OBS_3:
		case VK_OBS_4:
			return getObservedVariability(amino, descriptor);
		default:
			throw new NotYetImplementedError("Parsing Error: Unrecognized secondary structure type.");
		}
	}
	
	private static int getObservedVariability(Aminoid amino, SecStructConfig descriptor) {
		int k;
		int n;
		int n1;
		
		int helix = amino.numHomologues(SecondaryStructure.SOME_HELIX);
		int sheet = amino.numHomologues(SecondaryStructure.SOME_SHEET);
		int other = amino.numHomologues(SecondaryStructure.SOME_OTHER);
		
		switch(descriptor) {
		case VK_OBS_3:
			k = 3;
			n = helix+sheet+other;
			n1 = max(helix, sheet, other);
			return k*n/n1;
		case VK_OBS_4:
			int unassigned = amino.numHomologues(SecondaryStructure.SOME_OTHER);
			k = 4;
			n = helix+sheet+other+unassigned;
			n1 = max(helix, sheet, other, unassigned);
			return k*n/n1;
		default:
			return -1;
		}
	}
	
	public static void copyFields(AminoAcid src, AminoAcid dst) {
		for(VKPred key: src.vkKeys()) {
			dst.setVkbat(key, src.getVKPrediction(key));
		}
		
		for(String key: src.allDescriptors()) {
			dst.setDescriptor(key, src.getDescriptor(key));
		}
		
		for(SecondaryStructure key: src.homologueStructures) {
			dst.homologueStructures.add(key);
		}
		
		for(EncodingType key: src.allEncodings()) {
			dst.setEncoding(key, src.getEncoding(key));
		}
		
		copyAtoms(dst, src);
		
		dst.secondary = src.secondary();
	}
}
