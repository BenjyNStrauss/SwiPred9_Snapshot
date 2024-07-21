package biology.cluster;

import java.util.Objects;
import java.util.Set;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.Pair;
import biology.amino.Aminoid;
import biology.amino.BioMolecule;
import biology.amino.SecondaryStructure;
import biology.exceptions.alignment.ResidueAlignmentError;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.protein.ChainID;
import biology.protein.MultiChain;
import biology.protein.ProteinChain;
import biology.tools.SequenceAligner;
import danger.Danger;
import utilities.LocalToolBase;
import utilities.SwiPredObject;

/**
 * Represents a cluster of ProteinChains, v3.0
 * Implemented with an HashTable (via LabeledHash)
 * 
 * In this new version, the dominant chain is also in the table!
 * 
 * @author Benjy Strauss
 *
 */

public class ChainCluster extends LabeledHash<ChainID, ProteinChain> implements Cloneable, Alignable, SwiPredObject {
	private static final long serialVersionUID = 1L;
	public static final int MIN_SIZE_ALLOWED = 4;
	
	public String clusterName;
	//dominant Protein Chain in the cluster
	protected ProteinChain dominant;
	//is the list guaranteed to be aligned?
	protected boolean isAligned;
	
	private ChainCluster() { }
	
	/**
	 * 
	 * @param dominant
	 */
	public ChainCluster(ProteinChain dominant) {
		this("Cluster around "+dominant.id(), dominant);
	}
	
	/**
	 * 
	 * @param label
	 * @param dominant
	 */
	public ChainCluster(String label, ProteinChain dominant) {
		super(label);
		Objects.requireNonNull(dominant);
		if(this.label == null) {
			this.label = toString();
		}
		
		this.dominant = dominant;
		add(dominant);
		isAligned = true;
	}
	
	/** @return Dominant Chain */
	public ProteinChain dominant() { return dominant; }
	
	public void add(ProteinChain... chains) {
		for(ProteinChain chain: chains) {
			if(chain != null) { 
				isAligned = false;
				super.put(chain.id(), chain);
			} 
		}
	}
	
	public ProteinChain add(ProteinChain chain) {
		if(chain != null) {
			isAligned = false;
			return super.put(chain.id(), chain);
		} 
		else { return null; }
	}
	
	public ProteinChain get(Object key) {
		if(key instanceof Number) {
			throw new Danger("Warning! get() called with number " + key + ".\nSomething is likely broken internally!  (Please use stack trace!)");
		} else {
			return super.get(key);
		}
	}
	
	/**
	 * Replaces the dominant chain with the chain at the specified index
	 * @param newDominant: chain with which to replace the dominant chain
	 * @return: true if anything was changed
	 */
	public boolean replaceDominant(ProteinChain newDominant) {
		if(newDominant == null) { return false; }
		if(contains(newDominant)) {
			dominant = newDominant;
			return true;
		} else {
			add(newDominant);
			dominant = newDominant;
			return true;
		}
	}
	
	public ProteinChain remove(ProteinChain chain) {
		if(chain.equals(dominant)) {
			ProteinChain temp = remove(dominant.id());
			dominant = toChainArray()[0];
			return temp;
		} else {
			return remove(chain.id());
		}
	}
	
	public ProteinChain[] toChainArray() {
		ProteinChain[] retval = new ProteinChain[size()];
		Set<ChainID> keys = keySet();
		int index = 0;
		
		for(ChainID key: keys) {
			retval[index] = get(key);
			++index;
		}
		
		return retval;
	}
	
	public ChainCluster clone() {
		ChainCluster myClone = new ChainCluster();
		Set<ChainID> keys = keySet();
		for(ChainID key: keys) {
			myClone.add(get(key).clone());
		}
		return myClone;
	}
	
	/**
	 * new alignment technique 6/15/19: more powerful 
	 */
	public void align() {
		//check for nulls
		int nullsIn = nullChains();
		
		try {
			SequenceAligner.align(this);
			int nullsOut = nullChains();
			if(nullsIn != nullsOut) {
				throw new ResidueAlignmentError("An error occured in alignment for " + this);
			}
			
			trimFix();
			isAligned = true;
		} catch (ResidueAlignmentException e) {
			throw new ResidueAlignmentError("An error occured in alignment for " + this, e);
		}
	}
	
	public String listAll() {
		StringBuilder builder = new StringBuilder("{ ");
		
		for(Pair<ChainID, ProteinChain> chain: this) {
			builder.append(chain.y.name() + ", ");
		}
		
		builder = trimLastChar(builder);
		builder = trimLastChar(builder);
		builder.append(" }");
		return builder.toString();
	}
	
	/**
	 * Prevents unnecessary trailing blanks
	 */
	protected final void trimFix() {
		ProteinChain chains[] = toChainArray();
		//qerr(">>>>");
		//qerr(chains);
		int trimTo = 0;
		
		for(ProteinChain chain: chains) {
			trimTo = LocalToolBase.max(trimTo, chain.getLastArrayIndexNonNull()+1);
		}
		
		for(ProteinChain ch: chains) {
			
			ch.trimTrailingBlanks(ch.size() - trimTo);
			if(ch.size() == 0) {
				//TODO comment debug code once bug resolved
				qerr("DEBUG: ChainCluster.trimFix() <ON> " + ch);
				throw new RuntimeException("Error!  Chain trimmed to nothing!");
			}
		}
	}
	
	/**
	 * Obtains a PositionChain object with the data of the cluster
	 * @return
	 */
	public MultiChain toPositionChain() {
		if(!isAligned) { align(); }
		MultiChain superChain = new MultiChain(dominant);
		
		/*
		 * because the superchain already has the dominant chain's data, 
		 * we create a second temporary cluster which we can remove the dominant chain's
		 * data from, so we don't assign it twice!
		 */
		ChainCluster noDom = new ChainCluster();
		for(Pair<ChainID, ProteinChain> chain: this) {
			noDom.add(chain.y);
		}
		noDom.remove(dominant);

		for(Pair<ChainID, ProteinChain> chain: noDom) {
			for(int index = chain.y.startsAt(); index < superChain.size(); ++index) {
				if(index >= chain.y.size()) { break; }
				
				if(chain.y.get(index) != null && chain.y.get(index).secondary() != null) {
					superChain.get(index).addConfig(chain.y.get(index).residueType(), chain.y.get(index).secondary());
				}
			}
		}
		
		return superChain;
	}
	
	/**
	 * 
	 * @return
	 * @throws ResidueAlignmentException
	 */
	public String[] makeSecStrCSV() throws ResidueAlignmentException {
		return makeSecStrCSV(true);
	}
	
	/**
	 * 
	 * @param simplify: use simplified secondary structure
	 * @return
	 * @throws ResidueAlignmentException
	 */
	public String[] makeSecStrCSV(boolean simplify) throws ResidueAlignmentException {
		int domLen = dominant.size();
		LabeledList<String> outLines = new LabeledList<String>("Secondary Structure Comparison for " + this);
		
		for(Pair<ChainID, ProteinChain> chain: this) {
			if(chain.y.size() != domLen) {
				qerr("Malfunction in Cluster Alignment!!!");
				for(Pair<ChainID, ProteinChain> viewMe: this) { qerr(viewMe.y); }
				throw new ResidueAlignmentException("Malfunction in Cluster Alignment!");
			}
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("Index,Residue,");
		for(Pair<ChainID, ProteinChain> chain: this) { builder.append(chain.y.name() + ","); }
		trimLastChar(builder);
		outLines.add(builder.toString());
		
		for(int index = 0; index < domLen; ++index) {
			builder.setLength(0);
			
			BioMolecule aa0 = dominant.get(index);
			//append the index and residue type
			builder.append(index + ",");
			if(aa0 != null) {
				builder.append(aa0.toChar() + ",");
			} else {
				builder.append("-,");
			}
			
			for(Pair<ChainID, ProteinChain> chain: this) {
				if(chain.y.equals(dominant)) { continue; }
				BioMolecule aa = chain.y.get(index);
				if(aa0 == null) {
					if(aa == null) {
						builder.append("-,");
					} else {
						getSecondary(aa, builder, simplify);
						builder.append("(" + aa.toChar() + "),");
					}
				} else {
					if(aa == null) {
						builder.append("-,");
					} else {
						if(aa0.toChar() != aa.toChar()) {
							getSecondary(aa, builder, simplify);
							builder.append("(" + aa.toChar() + "),");
						} else {
							getSecondary(aa, builder, simplify);
							builder.append(",");
						}
					}
				}
			}
			trimLastChar(builder);
			outLines.add(builder.toString());
		}
		
		String[] fileLines = new String[outLines.size()];
		outLines.toArray(fileLines);
		return fileLines;
	}
	
	/**
	 * WARNING: might have broken in refactor 
	 * @param config
	 */
	public void recordHomologueStructures() {
		if(!isAligned) { align(); }
		ProteinChain chainArray[] = toChainArray();
		
		//loop through all of the non-dominant chains
		for(ProteinChain chain: chainArray) {
			
			//loop through all of the residues in each non-dominant chain
			for(int index = chain.startsAt(); index < chain.size(); ++index) {
				if(dominant.startsAt() <= index && dominant.size() > index) {
					if(chain == dominant) { continue; }
					
					//check the bounds
					if(dominant.get(index) != null && chain.get(index) != null) {
						dominant.get(index).recordHomologue(chain.get(index).secondary());
					}
					
				}
			}	
		}
	}
	
	public void recordHomologuesInDominant() {
		for(int index = 0; index < dominant.size(); ++index) {
			if(dominant.get(index) == null) { continue; }
			for(Pair<ChainID, ProteinChain> chainPair: this) {
				if(chainPair.y == null) { continue; }
				BioMolecule bMol = chainPair.y.get(index);
				if(bMol != null) {
					SecondaryStructure ss = bMol.secondary();
					if(ss != null) {
						dominant.get(index).recordHomologue(ss);
					}
				}
			}
		}
	}
	
	public boolean isAligned() { return isAligned; }
	
	private int nullChains() {
		int nulls = 0;
		for(Pair<ChainID, ProteinChain> chain: this) {
			if(chain.y.size() == chain.y.nulls()) { ++nulls; }
		}
		return nulls;
	}
	
	public String toFasta() {
		StringBuilder fastaBuilder = new StringBuilder();
		for(Pair<ChainID, ProteinChain> chain: this) {
			fastaBuilder.append(chain.y.toFasta() + "\n");
		}
		return fastaBuilder.toString();
	}
	
	@Override
	public String toString() {
		if(clusterName != null) { return clusterName; }
		if(dominant == null) { return "Cluster around [[nothing]]"; }
		if(dominant.id() == null) {
			return "Cluster around: " + dominant;
		} else {
			return "Cluster around: " + dominant.id();
		}
	}

	public void log() {
		LocalToolBase.log(toString());
		for(ChainID id: this.keySet()) {
			LocalToolBase.log("\t"+get(id).toSequence());
		}
	}
	
	private static void getSecondary(BioMolecule bMol, StringBuilder builder, boolean simplify) {
		if(bMol instanceof Aminoid) {
			if(simplify) {
				builder.append(((Aminoid) bMol).secSimple().toChar());
			} else {
				builder.append(bMol.secondary().toChar());
			}
		} else {
			builder.append("*");
		}
	}
}
