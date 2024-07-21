package biology.amino;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import assist.MetaBoolean;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.amino.atoms.AtomTable;
import biology.descriptor.EncodingType;
import biology.descriptor.VKPred;
import biology.molecule.types.AminoType;
import chem.AminoAtom;

/**
 * Represents a residue position: 
 * That is: a position in one or more (mostly) homologous amino acid chains
 * Unlike AminoAcid, AminoPosition can hold multiple primary/secondary structure combinations
 * if the homologous chains differ at the specific position
 * 
 * @author Benjy Strauss
 *
 */

public class AminoPosition extends ChainObject implements Aminoid, Iterable<ResidueConfig> {
	private static final long serialVersionUID = 1L;
	
	// contains all of the primary/secondary structure combinations, as well as the number of occurences
	private final LabeledList<ResidueConfig> configs;
	
	//holds all the descriptors with a string as the key
	protected final LabeledHash<String, Double> descriptors;
	//for use in calculating vkabat
	protected final LabeledHash<VKPred, SecondarySimple> predictedStructures;

	//contains encoding data
	protected final LabeledHash<EncodingType, double[]> encodings;
	
	protected final AtomTable atoms = new AtomTable();
	
	//set this if only residue with descriptors should be written
	public static boolean onlyWriteWithDescriptors = false;
	
	//the sidechain of the dominant chain
	protected AminoType dom_primary;
	//type of secondary structure associated with this amino acid
	protected SecondaryStructure dom_secondary;
	
	/**
	 * Construct a new AminoPosition
	 */
	public AminoPosition() { 
		configs = new LabeledList<ResidueConfig>("residue-configs");
		descriptors = new LabeledHash<String, Double>();
		predictedStructures = new LabeledHash<VKPred, SecondarySimple>();
		encodings = new LabeledHash<EncodingType, double[]>();
	}
	
	/**
	 * 
	 * @param dom_primary
	 */
	public AminoPosition(AminoType dom_primary) { 
		this(dom_primary, SecondaryStructure.UNKNOWN);
	}
	
	/**
	 * Construct a new AminoPosition
	 * @param primary: a primary structure for this residue position
	 * @param secondary: a secondary structure for this residue position
	 */
	public AminoPosition(AminoType primary, SecondaryStructure secondary) {
		if(primary == null) { primary = AminoType.INVALID; }
		if(secondary == null) { secondary = SecondaryStructure.UNKNOWN; }
		
		configs = new LabeledList<ResidueConfig>("residue-configs");
		configs.add(new ResidueConfig(primary,secondary));
		
		this.dom_primary = primary;
		this.dom_secondary = secondary;
		
		descriptors = new LabeledHash<String, Double>();
		predictedStructures = new LabeledHash<VKPred, SecondarySimple>();
		encodings = new LabeledHash<EncodingType, double[]>();
	}
	
	private AminoPosition(AminoPosition cloneFrom) {
		configs = new LabeledList<ResidueConfig>("residue-configs");
		descriptors = new LabeledHash<String, Double>();
		predictedStructures = new LabeledHash<VKPred, SecondarySimple>();
		encodings = new LabeledHash<EncodingType, double[]>();
		
		for(VKPred key: cloneFrom.predictedStructures.keySet()) {
			setVkbat(key, cloneFrom.predictedStructures.get(key));
		}
		
		for(String key: cloneFrom.descriptors.keySet()) {
			setDescriptor(key, cloneFrom.descriptors.get(key));
		}
		
		AminoTools.copyAtoms(this, cloneFrom);
		
		for(EncodingType key: cloneFrom.encodings.keySet()) {
			setEncoding(key, cloneFrom.encodings.get(key));
		}
		
		for(ResidueConfig config: cloneFrom.configs) { addConfig(config.clone()); }
	}
	
	/**
	 * Add a primary/secondary structure configuration
	 * @param primary
	 * @param secondary
	 */
	public void removeConfig(AminoType primary, SecondaryStructure secondary) {
		removeConfig(new ResidueConfig(primary, secondary));
	}
	
	/**
	 * 
	 * @param primary
	 * @param secondary
	 * @param occurences
	 */
	public void removeConfig(AminoType primary, SecondaryStructure secondary, int occurences) {
		removeConfig(new ResidueConfig(primary, secondary, occurences));
	}
	
	/**
	 * 
	 * @param config
	 */
	public void removeConfig(ResidueConfig config) {
		removeConfig(config, true);
	}
	
	/**
	 * 
	 * @param config
	 * @param overrideEquivalency
	 */
	public void removeConfig(ResidueConfig config, boolean overrideEquivalency) {
		if(overrideEquivalency) {
			for(ResidueConfig resCon: configs) {
				if(ResidueConfig.equivalent(config, resCon)) {
					resCon.subtract(config);
					if(resCon.occurrences() <= 0) { configs.remove(resCon); }
					resCon.deconstruct();
					return;
				}
			}
		} else {
			for(ResidueConfig resCon: configs) {
				if(ResidueConfig.equals(config, resCon)) {
					resCon.subtract(config);
					if(resCon.occurrences() <= 0) { configs.remove(resCon); }
					resCon.deconstruct();
					return;
				}
			}
		}
	}
	
	/**
	 * 
	 * @param primary
	 * @param secondary
	 */
	public void removeAllConfig(AminoType primary, SecondaryStructure secondary) {
		removeAllConfig(new ResidueConfig(primary, secondary));
	}
	
	/**
	 * 
	 * @param config
	 */
	public void removeAllConfig(ResidueConfig config) {
		removeAllConfig(config, true);
	}
	
	/**
	 * 
	 * @param config
	 * @param overrideEquivalency
	 */
	public void removeAllConfig(ResidueConfig config, boolean overrideEquivalency) {
		if(overrideEquivalency) {
			for(ResidueConfig resCon: configs) {
				if(ResidueConfig.equivalent(config, resCon)) {
					configs.remove(resCon);	
					resCon.deconstruct();
					return;
				}
			}
		} else {
			for(ResidueConfig resCon: configs) {
				if(ResidueConfig.equals(config, resCon)) {
					resCon.deconstruct();
					return;
				}
			}
		}
	}
	
	/**
	 * Returns (one of) the most common residue types for this residue position
	 * @return most common residue type
	 */
	public AminoType residueType() { return residueType(false); }
	
	public AminoType residueType(boolean allowEquivalency) {
		Hashtable<AminoType, Integer> primaryTable = new Hashtable<AminoType, Integer>();
		
		AminoType mostCommon = null;
		int maxOccurences = 0;
		//primaryTable.put(dom_primary, 1);
		
		for(ResidueConfig config: configs) {
			Integer meta = primaryTable.get(config.primary());
			if(meta == null) {
				if(allowEquivalency) {
					primaryTable.put(config.primary().standardize(), config.occurrences());
				} else {
					primaryTable.put(config.primary(), config.occurrences());
				}
			} else {
				if(allowEquivalency) {
					primaryTable.put(config.primary().standardize(), config.occurrences() + meta);
				} else {
					primaryTable.put(config.primary(), config.occurrences() + meta);
				}
			}
		}
		
		Set<AminoType> keys = primaryTable.keySet();
		for(AminoType key: keys) {
			Integer occ = primaryTable.get(key);
			if(occ.intValue() > maxOccurences) {
				maxOccurences = occ.intValue();
				mostCommon = key;
			}
		}
		
		return mostCommon;
	}
	
	/**
	 * Gets a list of all the residueConfigs
	 * Note that this is a DEEP COPY
	 * @return
	 */
	public ResidueConfig[] getConfigs() {
		ResidueConfig[] retVal = new ResidueConfig[configs.size()];
		
		for(int index = 0; index < retVal.length; ++index) {
			retVal[index] = configs.get(index).clone();
		}
		
		return retVal;
	}
	
	/**
	 * Returns (one of) the most common residue types for this residue position
	 * @return most common residue type
	 */
	public SecondaryStructure secondary() { 
		Hashtable<SecondaryStructure, Integer> secondaryTable = new Hashtable<SecondaryStructure, Integer>();
		
		SecondaryStructure mostCommon = null;
		int maxOccurences = 0;
		
		for(ResidueConfig config: configs) {
			Integer meta = (Integer) secondaryTable.get(config.secondary());
			if(meta == null) {
				secondaryTable.put(config.secondary(), config.occurrences());
			} else {
				secondaryTable.put(config.secondary(), config.occurrences() + meta);
			}
		}
		
		Set<SecondaryStructure> keys = secondaryTable.keySet();
		for(SecondaryStructure key: keys) {
			Integer occ = (Integer) secondaryTable.get(key);
			if(occ.intValue() > maxOccurences) {
				maxOccurences = occ.intValue();
				mostCommon = key;
			}
		}
		
		return mostCommon;
	}
	
	/**
	 * Returns (one of) the most common secondary structure types for this residue position
	 * @return most common residue secondary structure
	 */
	public SecondarySimple secSimple() { 
		Hashtable<SecondarySimple, Integer> secondaryTable = new Hashtable<SecondarySimple, Integer>();
		
		SecondarySimple mostCommon = null;
		int maxOccurences = 0;
		
		for(ResidueConfig config: configs) {
			Integer meta = (Integer) secondaryTable.get(config.secondary().simpleClassify());
			if(meta == null) {
				secondaryTable.put(config.secondary().simpleClassify(), config.occurrences());
			} else {
				secondaryTable.put(config.secondary().simpleClassify(), config.occurrences() + meta);
			}
		}
		
		Set<SecondarySimple> keys = secondaryTable.keySet();
		for(SecondarySimple key: keys) {
			Integer occ = (Integer) secondaryTable.get(key);
			if(occ.intValue() > maxOccurences) {
				maxOccurences = occ.intValue();
				mostCommon = key;
			}
		}
		
		return mostCommon;
	}
	
	/**
	 * Determines if this AminoPosition contains a configuration(s) with the given type of SideChain
	 * Always returns 'true' if 'null' is passed in
	 * @param chainType: the type of side chain
	 * @param equivalent: false to treat modified residues as different
	 * @return if this AminoPosition contains a configuration(s) with the given type of SideChain
	 */
	public boolean containsSideChain(AminoType chainType, boolean equivalent) {
		if(chainType == null) { return true; }
		
		if(equivalent) {
			for(ResidueConfig config: configs) {
				if(config.primary().couldBe(chainType)) { return true; }
			}
		} else {
			for(ResidueConfig config: configs) {
				if(config.primary().equals(chainType)) { return true; }
			}
		}
		
		return false;
	}
	
	public boolean isSwitch() {
		Hashtable<SecondarySimple, Integer> secondaryTable = new Hashtable<SecondarySimple, Integer>();
		
		for(ResidueConfig config: configs) {
			Integer meta = (Integer) secondaryTable.get(config.secondary().simpleClassify());
			if(meta == null) {
				secondaryTable.put(config.secondary().simpleClassify(), config.occurrences());
			}
		}
		
		return (secondaryTable.size() > 1);
	}
	
	public boolean isUnwritable() { return (configs.size() == 0); }
	
	public boolean contains(AminoType primary) {
		for(ResidueConfig rConf: configs) {
			if(rConf.primary() == primary) { return true; }
		}
		return false;
	}
	
	/**
	 * Whether the residue position only contains secondary structures with the given
	 * simplified secondary structure
	 * @param secStr
	 * @return true, if the condition above holds, else false
	 */
	public boolean onlyContains(SecondarySimple... secStr) {
		for(SecondarySimple ssim: secStr) {
			if(!contains(ssim)) { return false; }
		}
		
		//test each config to make sure it doesn't have a secondary simple not in the list
		for(ResidueConfig rConf: configs) {
			boolean configPass = false;
			
			for(SecondarySimple ssim: secStr) {
				configPass = configPass || (rConf.secondary().simpleClassify() == ssim);
			}
			if(!configPass) { return false; }
		}
		return true;
	}
	
	/**
	 * Whether the residue position contains secondary structures with the given
	 * simplified secondary structure
	 * @param secStr
	 * @return true, if the condition above holds, else false
	 */
	public boolean contains(SecondarySimple secStr) {
		for(ResidueConfig rConf: configs) {
			if(rConf.secondary().simpleClassify() == secStr) { return true; }
		}
		return false;
	}
	
	/**
	 * Whether the residue position contains secondary structures with the given
	 * secondary structure
	 * @param secStr
	 * @return true, if the condition above holds, else false
	 */
	public boolean contains(SecondaryStructure secStr) {
		for(ResidueConfig rConf: configs) {
			if(rConf.secondary() == secStr) { return true; }
		}
		return false;
	}
	
	public char toChar() {
		if(configs.size() == 0) {
			return (dom_primary != null) ? dom_primary.toChar() : '_';
		} else {
			return residueType().toChar();
		}
	}
	
	public String toCode() { 
		if(configs.size() == 0) {
			return (dom_primary != null) ? dom_primary.toCode() : null;
		} else {
			return residueType().toCode();
		}
	}
	
	public MetaBoolean disordered() { 
		MetaBoolean isDisordered = MetaBoolean.UNKNOWN;
		
		for(ResidueConfig config: configs) {
			MetaBoolean configDisorder = config.disordered();
			switch(isDisordered) {
			case FALSE:
				if(configDisorder == MetaBoolean.TRUE) { return MetaBoolean.SUPERPOSTION; }
				break;
			case TRUE:
				if(configDisorder == MetaBoolean.FALSE) { return MetaBoolean.SUPERPOSTION; }
				break;
			case UNKNOWN:
				isDisordered = configDisorder;
				break;
			case SUPERPOSTION:
				return isDisordered;
			}
		}
		
		return isDisordered;
	}
	
	/*
	 * 
	private AtomTable atoms;(non-Javadoc)
	 * @see bio.Tablizable#clone()
	 */
	public AminoPosition clone() { return new AminoPosition(this); }
	
	public String secondaryString() {
		StringBuilder builder = new StringBuilder();
		boolean helix = false;
		boolean sheet = false;
		boolean other = false;
		boolean unassigned = false;
		
		for(ResidueConfig config: configs) { 
			if(config.secondary().simpleClassify() == SecondarySimple.Helix) { helix = true; }
			if(config.secondary().simpleClassify() == SecondarySimple.Sheet) { sheet = true; }
			if(config.secondary().simpleClassify() == SecondarySimple.Other) { other = true; }
			if(config.secondary().simpleClassify() == SecondarySimple.Disordered) { unassigned = true; }
		}
		
		if(helix) { builder.append("H"); }
		if(sheet) { builder.append("S"); }
		if(other) { builder.append("O"); }
		if(unassigned) { builder.append("U"); }
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public AminoType dominantPrimary() { return dom_primary; }
	
	public AminoAcid simplify() {
		if(residueType() == null) { return null; }
		
		AminoAcid generalConsensus = new AminoAcid(residueType());
		
		Set<VKPred> keys = predictedStructures.keySet();
		for(VKPred key: keys) {
			generalConsensus.setVkbat(key, predictedStructures.get(key));
		}
		
		Set<String> keys2 = descriptors.keySet();
		for(String key: keys2) {
			generalConsensus.setDescriptor(key, descriptors.get(key));
		}
		
		generalConsensus.setSecondaryStructure(secondary());
		
		for(ResidueConfig config: configs) {
			generalConsensus.homologueStructures().add(config.secondary());
		}
		
		Set<String> atomKeys = atoms.keySet();
		for(String key: atomKeys) {
			generalConsensus.setAtom(atoms.get(key));
		}
		
		return generalConsensus;
	}
	
	public boolean unassignedHomologues() {
		for(ResidueConfig config: configs) {
			if(config.secondary() == SecondaryStructure.DISORDERED) {
				return true;
			}
		}
		return false;
	}
	
	@Override
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

	@Override
	public Iterator<ResidueConfig> iterator() { return configs.iterator(); }

	@Override
	public void setVkbat(VKPred predictor, SecondarySimple pred) {
		predictedStructures.put(predictor, pred);	
	}

	@Override
	public void setDescriptor(String string, double value) {
		descriptors.put(string, value);
	}

	@Override
	public void clearVK() { predictedStructures.clear(); }

	@Override
	public double averageCharge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AminoAtom getAtom(String atomKey) { return atoms.get(atomKey); }

	@Override
	public double getDescriptor(String key) {
		if(descriptors.get(key) == null) { return Double.NaN; }
		return descriptors.get(key);
	}
	
	public boolean vk_assignable() {
		if(configs.size() == 0) { return false; }
		return residueType() != AminoType.INVALID;
	}
	
	@Override
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

	@Override
	public void setAtom(AminoAtom aatom) { atoms.put(aatom); }
	
	public int numHomologues(SecondaryStructure secStr) {
		int total = 0;
		if(secStr == null) { 
			for(ResidueConfig config: configs) {
				total += config.occurrences();
			}
			return total;
		}
		
		total = 0;
		for(ResidueConfig config: configs) {
			
			//if it's an exact match
			if(config.secondary() == secStr) {
				total += config.occurrences();
				continue;
			}
			
			//else if its a category of multiple secondary structure types
			switch(secStr) {
			case SOME_HELIX:
				if(config.secondary() == SecondaryStructure.THREE_HELIX ||
						config.secondary() == SecondaryStructure.ALPHA_HELIX ||
						config.secondary() == SecondaryStructure.FIVE_HELIX ) {
					total += config.occurrences();
				} break;
			case SOME_OTHER:
				if(config.secondary() == SecondaryStructure.BEND ||
						config.secondary() == SecondaryStructure.COIL ||
						config.secondary() == SecondaryStructure.TURN ||
						config.secondary() == SecondaryStructure.UNKNOWN ) {
					total += config.occurrences();
				} break;
			case SOME_SHEET:
				if(config.secondary() == SecondaryStructure.BETA_BRIDGE ||
						config.secondary() == SecondaryStructure.EXTENDED_STRAND ) {
					total += config.occurrences();
				} break;
			default:
			}
		}
		
		return total;
	}

	@Override
	public Set<VKPred> vkKeys() { return predictedStructures.keySet(); }

	@Override
	public SecondarySimple getVKPrediction(VKPred key) {
		return predictedStructures.get(key);
	}

	@Override
	public Set<String> atomKeys() { return atoms.keySet(); }

	@Override
	public void setEncoding(EncodingType type, double[] ds) {
		Objects.requireNonNull(type, "Type of encoding cannot be null.");
		encodings.put(type, ds);
	}
	
	@Override
	public double[] getEncoding(EncodingType type) {
		return encodings.get(type);
	}

	public void addConfig(AminoType residueType, SecondaryStructure secondary) {
		addConfig(new ResidueConfig(residueType, secondary));
	}
	
	public void addConfig(ResidueConfig newConfig) {
		for(ResidueConfig config: configs) {
			if(config.equals(newConfig)) {
				for(int ii = 0; ii < newConfig.occurrences(); ++ii) {
					config.noteOccurence();
				}
				return;
			}
		}
		configs.add(newConfig);
	}
}