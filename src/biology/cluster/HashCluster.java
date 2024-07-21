package biology.cluster;

import java.util.Map;
import java.util.Objects;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.LabeledSet;
import biology.amino.AminoAcid;
import biology.amino.BioMolecule;
import biology.amino.InsertCode;
import biology.protein.AminoChain;
import biology.protein.ChainID;
import tools.reader.fasta.HashReader;

/**
 * Unlike a normal ChainCluster, a HashCluster uses InsertCodes
 * 
 * 
 * @author Benjamin Strauss
 *
 */

public class HashCluster extends LabeledHash<InsertCode, ResidueAlignment> {
	private static final long serialVersionUID = 1L;
	private final LabeledSet<ChainID> chains = new LabeledSet<ChainID>();
	
	public HashCluster() { }
	public HashCluster(int initialCapacity) { super(initialCapacity); }
	public HashCluster(int initialCapacity, float loadFactor) { super(initialCapacity, loadFactor); }
	public HashCluster(Map<? extends InsertCode, ? extends ResidueAlignment> t) { super(t); }
	
	public HashCluster(String label) { super(label); }
	public HashCluster(String label, int initialCapacity) {  super(label, initialCapacity); }
	public HashCluster(String label, int initialCapacity, float loadFactor) { super(label, initialCapacity, loadFactor); }
	public HashCluster(String label, Map<? extends InsertCode, ? extends ResidueAlignment> t) { super(label, t); }
	
	public ResidueAlignment put(InsertCode key, ResidueAlignment alignment) {
		Objects.requireNonNull(key);
		for(ChainID id: alignment.keySet()) {
			chains.add(id);
		}
		
		return super.put(key, alignment);
	}
	
	public LabeledList<BioMolecule> put(HashReader reader) {
		LabeledList<BioMolecule> receipt = new LabeledList<BioMolecule>();
		chains.add(reader.id);
		for(InsertCode key: reader.keySet()) {
			if(get(key) == null) { put(key, new ResidueAlignment(key)); }
			receipt.add(get(key).put(reader.id, reader.get(key)));
		}
		return receipt;
	}
	
	public BioMolecule put(InsertCode key, ChainID key2, BioMolecule value) {
		chains.add(key2);
		if(get(key) == null) { put(key, new ResidueAlignment(key)); }
		BioMolecule oldValue = get(key).get(key2);
		get(key).put(key2, value);
		return oldValue;
	}
	
	public LabeledList<BioMolecule> put(AminoChain<AminoAcid> chain) {
		LabeledList<BioMolecule> receipt = new LabeledList<BioMolecule>();
		chains.add(chain.id());
		for(AminoAcid aa: chain) {
			if(aa.postition() != null) {
				receipt.add(put(aa.postition(), chain.id(), aa));
			}
		}
		
		return receipt;
	}
	
	public BioMolecule get(InsertCode key, ChainID key2) {
		return (get(key) == null) ? null : get(key).get(key2) ;
	}
	
	public AminoAlignment toAlignment() {
		AminoAlignment alignment = new AminoAlignment(this);
		return alignment;
	}
	
	public int noChains() { return chains.size(); }
	
	public ChainID[] chainSet() {
		ChainID[] ids = new ChainID[chains.size()];
		chains.toArray(ids);
		return ids;
	}
	
	public void clear() {
		super.clear();
		chains.clear();
	}
}
