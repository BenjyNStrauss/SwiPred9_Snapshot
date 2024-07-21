package biology.amino;

import java.util.Set;

import assist.ActuallyCloneable;
import assist.MetaBoolean;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.descriptor.EncodingType;
import biology.descriptor.VKPred;
import biology.molecule.types.AminoType;
import chem.AminoAtom;

/**
 * Amino is an abstract class designed to represent 1 or a number of amino acids residues 
 * @author Benjy Strauss
 *
 */

public class AminoAcid extends BioMolecule implements Aminoid, ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	//for use in calculating vkabat
	protected final LabeledHash<VKPred, SecondarySimple> predictedStructures;
	
	//set this if only residue with descriptors should be written
	public static boolean onlyWriteWithDescriptors = false;
	
	//type of amino acid residue
	protected AminoType type;
	
	protected MetaBoolean disordered = MetaBoolean.UNKNOWN;
	public boolean truly_unknown = false;
	
	/**
	 * Creates a new AminoAcid Object
	 * @param residueType: the type of residue (single letter)
	 */
	public AminoAcid(char residueType) {
		this(AminoType.parse(residueType));
	}
	
	/**
	 * Creates a new AminoAcid Object
	 * @param residueType: the type of residue (multi-letter sequence)
	 * @throws UnknownCodeException 
	 */
	public AminoAcid(String residueType) throws UnknownResidueException {
		this(AminoType.parse(residueType));
	}
	
	/**
	 * Creates a new AminoAcid Object
	 * @param type: the type of residue (enumeration)
	 */
	public AminoAcid(AminoType type) {
		super(type);
		
		predictedStructures = new LabeledHash<VKPred, SecondarySimple>();
		this.type = type;
	}
	
	private AminoAcid(AminoAcid cloneFrom) {
		super(cloneFrom);
		type = cloneFrom.type;
		
		Set<VKPred> keys = cloneFrom.predictedStructures.keySet();
		for(VKPred key: keys) {
			setVkbat(key, cloneFrom.predictedStructures.get(key));
		}
		
		predictedStructures = new LabeledHash<VKPred, SecondarySimple>();
		
		for(SecondaryStructure key: cloneFrom.homologueStructures) {
			homologueStructures.add(key);
		}
		
		for(EncodingType key: cloneFrom.encodings.keySet()) {
			setEncoding(key, cloneFrom.encodings.get(key));
		}
		
		secondary = cloneFrom.secondary;
	}
	
	public void setDisordered(MetaBoolean disordered) {
		if(disordered == null) {
			this.disordered = MetaBoolean.UNKNOWN;
		} else {
			this.disordered = disordered;
		}
		
		if(disordered == MetaBoolean.TRUE) {
			secondary = SecondaryStructure.DISORDERED;
		}
	}
	
	/**
	 * 
	 * @param predictor: what method was used
	 * @param prediction: what secondary simple was predicted
	 */
	public void setVkbat(VKPred predictor, SecondarySimple prediction) { 
		predictedStructures.put(predictor, prediction);
	}
	
	public SecondarySimple getVKPrediction(VKPred method) {
		return predictedStructures.get(method);
	}
	
	public Set<VKPred> vkKeys() { return predictedStructures.keySet(); }
	
	/**
	 * Calculate the Vkabat value for a single amino acid residue from an input string
	 * @return vkabat value
	 */
	public double vkbat() {
		int H = 0, E = 0, O = 0;
		Set<VKPred> keys = predictedStructures.keySet();
		
		for(VKPred key: keys) {
			switch(predictedStructures.get(key)) {
			case Helix:		++H;		break;
			case Sheet:		++E;		break;
			case Other:		++O;		break;
			default:	
			}
		}
		
		double k = 0; //max = 3
		
		if(H > 0) { ++k; }
		if(E > 0) { ++k; }
		if(O > 0) { ++k; }
		
		int n1 = max(H, E, O); // min = 5
		int N = keys.size(); //max = 15
		
		double result = ( k / n1) * N; //max = 3/5*15 = 9
		
		return result;
	}
	
	public String vkPredData() {
		StringBuilder dataBuilder = new StringBuilder();
		Set<VKPred> keys = predictedStructures.keySet();
		
		for(VKPred key: keys) {
			dataBuilder.append(predictedStructures.get(key).toChar());
		}
		
		return dataBuilder.toString();
	}
	
	public MetaBoolean disordered() { return disordered; }
	
	/**
	 * Copy vkabat data from another Amino object
	 * @param src: the Amino to copy data from
	 */
	public void	copyVKDataFrom(AminoAcid src) {
		predictedStructures.clear();
		
		Set<VKPred> keys = src.predictedStructures.keySet();
		for(VKPred key: keys) {
			setVkbat(key, predictedStructures.get(key));
		}
	}
	
	/** @param res: new type of sidechain */
	public void setResidueType(AminoType res) { type = res; }
	
	/** @return: type of amino acid side chain */
	public AminoType residueType() { return type; }
	
	/** @return: the type of simplified secondary structure */
	public SecondarySimple secSimple() { 
		return (secondary() != null) ? secondary().simpleClassify() : null;
	}
	
	/**
	 * Count the number of homologues with a given type of secondary structure
	 * @param secStr
	 * @return
	 */
	public int numHomologues(SecondaryStructure secStr) {
		if(secStr == null) { return homologueStructures.size(); }
		
		int total = 0;
		for(SecondaryStructure homologue: homologueStructures) {
			//if it's an exact match
			if(homologue == secStr) {
				++total;
				continue;
			}
			
			//else if its a category of multiple secondary structure types
			switch(secStr) {
			case SOME_HELIX:
				if(homologue == SecondaryStructure.THREE_HELIX ||
						homologue == SecondaryStructure.ALPHA_HELIX ||
						homologue == SecondaryStructure.FIVE_HELIX ) {
					++total;
				} break;
			case SOME_OTHER:
				if(homologue == SecondaryStructure.BEND ||
						homologue == SecondaryStructure.COIL ||
						homologue == SecondaryStructure.TURN ||
						homologue == SecondaryStructure.UNKNOWN ) {
					++total;
				} break;
			case SOME_SHEET:
				if(homologue == SecondaryStructure.BETA_BRIDGE ||
						homologue == SecondaryStructure.EXTENDED_STRAND ) {
					++total;
				} break;
			default:
			}
		}
		return total;
	}
	
	/** @return: abbreviated representation of residue(s)*/
	//public char toChar() { return residueType().toChar(); }
	
	/** @return: abbreviated representation of residue(s)*/
	//public String toCode() { return residueType().toCode(); }
	
	/**
	 * Returns the average charge for all of the atoms with known charges: 
	 * A value will be skipped over if it cannot be applied.
	 * @return Average charge of the known atoms of the amino acid
	 */
	public double averageCharge()  {
		double retVal = 0;
		int numberOfCharges = 0;
		
		Set<String> keys = atoms.keySet();
		for(String key: keys) {
			AminoAtom atom = atoms.get(key);
			if(atom.charge() != Double.NaN) {
				retVal += atom.charge();
				++numberOfCharges;
			}
		}
		
		retVal /= numberOfCharges;
		
		return retVal;
	}
	
	public ResidueConfig getConfig() { return new ResidueConfig(type, secondary); }
	
	public Set<String> listAtoms() { return atoms.keySet(); }
	
	/** @return: comma-separated value string of the standard charges */
	public String getStandardCharges() {
		StringBuilder dataBuilder = new StringBuilder();
		for(String s: STANDARD_ATOM_KEYS) {
			AminoAtom atom = getAtom(s);
			if(atom != null) {
				dataBuilder.append(atom.charge() + ",");
			} else {
				dataBuilder.append(Double.NaN + ",");
			}
		}
		return dataBuilder.toString();
	}
	
	/** @return: Overrides clone() in Object*/
	/**
	 * Make a deep copy of the AminoAcid Object
	 */
	public AminoAcid clone() { return new AminoAcid(this); }
	
	public LabeledList<SecondaryStructure> homologueStructures() {
		return homologueStructures;
	}
	
	public void clearVK() { predictedStructures.clear(); }
	
	public boolean equals(Object other) {
		if(other instanceof AminoAcid) {
			return getConfig().equals(((AminoAcid) other).getConfig());
		} else {
			return false;
		}
	}
	
	public String toString() { return type.toString(); }
}
