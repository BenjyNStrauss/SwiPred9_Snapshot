package tools.reader.fasta.pdb;

import java.util.Arrays;
import java.util.Set;

import assist.Deconstructable;
import assist.util.LabeledHash;
import biology.amino.AminoAcid;
import biology.amino.InsertCode;
import biology.protein.ProteinChain;
import tools.reader.fasta.SequenceReaderBase;
import tools.reader.mapping.AminoMapping;

/**
 * Helps with niche PDB cases, when something like: 0A,0C maps to 0,0
 * @author Benjamin Strauss
 *
 */

public class AddOn extends LabeledHash<InsertCode, AminoAcid> implements Comparable<AddOn>, Deconstructable {
	private static final long serialVersionUID = 1L;
	
	public final int index;
	public final InsertCode min;
	public final InsertCode max;
	
	public AddOn(int index, InsertCode min, InsertCode max) {
		this.index = index;
		this.min = min;
		this.max = max;
	}
	
	public boolean inRange(InsertCode code) {
		return min.lesserEqualThan(code) && max.greaterEqualThan(code);
	}
	
	public boolean add_if_in_range(InsertCode code, AminoAcid aa) {
		boolean result = inRange(code);
		if(result) { put(code, aa); }
		return result;
	}
	
	public void add_all_to_chain(ProteinChain chain) {
		Set<InsertCode> keys = keySet();
		InsertCode[] keyArray = new InsertCode[keys.size()];
		keys.toArray(keyArray);
		Arrays.sort(keyArray);
		
		int offset = 0;
		/* Do NOT insert the last Amino Acid key, or that Amino Acid will be duplicated 
		 * Remove this message when that bug is fixed, so this doesn't cause a bug
		 */
		for(; offset < keyArray.length-1; ++offset) {
			chain.add(index+offset, get(keyArray[offset]));
		}
		
		//adjust all of the mappings to account for inserted residues
		for(AminoMapping mapping: chain.mappings) {
			mapping.adjust(offset);
		}
		
		chain.setWarnings(SequenceReaderBase.CHAIN_SHIFTED);
	}
	
	@Override
	public int compareTo(AddOn other) { return index - other.index; }

	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
