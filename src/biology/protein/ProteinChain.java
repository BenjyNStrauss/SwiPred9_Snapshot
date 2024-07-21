package biology.protein;

import java.util.Objects;

import biology.amino.AminoAcid;
import biology.amino.BioMolecule;
import biology.amino.ChainObject;
import biology.descriptor.DescriptorType;
import biology.exceptions.NotAnAminoAcidException;
import biology.molecule.types.AminoType;

/**
 * Represents a protein molecule as a named list of amino acids
 * Note that get...(int index) methods use the physical index in the internal array;
 * To use the PDB index, use get...At(int index) methods.
 *  (This assumes that the ProteinChain has been constructed correctly)
 * 
 * @author Benjy Strauss
 *
 */

public class ProteinChain extends AminoChain<BioMolecule> {
	private static final long serialVersionUID = 1L;
	
	public ProteinChain() { }
	
	public ProteinChain(ChainID id) { this(id, ""); }
	
	public ProteinChain(ChainID id, String sequence) { 
		super(id, new ChainFlags());
		for(char ch: sequence.toCharArray()) {
			add(ch);
		}
	}
	
	private ProteinChain(ProteinChain clonefrom) {
		super(clonefrom);
		for(BioMolecule bMol: clonefrom) {
			if(bMol == null) {
				pad();
			} else if (bMol instanceof AminoAcid) {
				add(((AminoAcid) bMol).clone());
			} else {
				add(bMol.clone());
			}
		}
	}
	
	/**
	 * Sets the flexibility value at a specified index
	 * @param index: the index into the internal array at which to set the flexibility value
	 * @param flexVal: the value to set the flexibility to
	 */
	public void setFlexibility(int index, double flexibility) { 
		Objects.requireNonNull(get(index), "Residue at index " + index + " is null!");
		if(get(index) instanceof AminoAcid) {
			((AminoAcid) get(index)).setDescriptor(DescriptorType.FLEXIBILITY, flexibility);
		} else {
			throw new NotAnAminoAcidException();
		}
	}
	
	public void add(char amino) { add(new AminoAcid(amino)); }
	public void add(int index, char amino) { add(index, new AminoAcid(amino)); }
	
	/**
	 * Get the protein's amino acid sequence
	 * @return: the protein's amino acid sequence as an array of AminoAcid objects
	 */
	public BioMolecule[] toArray() { 
		ChainObject[] array = super.toArray();
		AminoAcid[] newArray = new AminoAcid[array.length];
		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}
	
	/**
	 * Changes the type of an Amino Acid at a given index
	 * @param res: the new Amino Acid Residue Type
	 * @param index: the arrayIndex of the residue to replace
	 */
	public void updateAminoResidue(AminoType res, int index) {
		if(get(index) instanceof AminoAcid) {
			((AminoAcid) get(index)).setResidueType(res);
		} else {
			set(index, new AminoAcid(res));
		}
	}
	
	/**
	 * Returns how many of the Vkabat algorithms succeeded for the protein chain
	 * If not all residues have Vkabat data, this algorithm returns the number of algorithms that succeeded for
	 * the residue(s) that had the most succeed.  This is because sometimes there is a residue on the end of a
	 * chain that does not get Vkabat data for some reason.  These residues are not included in the output.
	 * 
	 * @return how many of the Vkabat algorithms succeeded for the protein chain
	 */
	public int maxVkabatCompletion() {
		int maxCompletion = 0;
		
		for(BioMolecule amino: this) {
			if(amino != null && amino instanceof AminoAcid) {
				AminoAcid aa = (AminoAcid) amino;
				maxCompletion = max(aa.vkKeys().size(), maxCompletion);
			}
		}
		
		return maxCompletion;
	}
	
	/**
	 * Two ProteinChains are considered to be equal if they have the same Amino Acid residue sequence
	 * NOTE: this method only takes sequence into consideration
	 * NOTE: use a sequence aligner before using this method!
	 * @param other: the Sequence of which to compare this
	 * @return true if the sequences are the same (or could be the same), otherwise false
	 */
	public boolean equals(Object other) {
		if(other instanceof ProteinChain) {
			return equals((ProteinChain) other, 0);
		} else {
			return false;
		}
	}
	
	/**
	 * Two ProteinChains are considered to be equal if they have the same Amino Acid residue sequence
	 * NOTE: this method only takes sequence into consideration
	 * NOTE: use a sequence aligner before using this method!
	 * @param other: the Sequence of which to compare this
	 * @param allowedErrors: number of errors allowed
	 * @return true if the sequences are the same (or could be the same), otherwise false
	 */
	public boolean equals(ProteinChain other, int allowedErrors) {
		if(size() != other.size()) { return false; }
		
		int errors = 0;
		
		for(int index = 0; index < size(); ++index) {
			if(get(index) == null && other.get(index) == null) {
				
			} else if(get(index) == null && other.get(index) != null) {
				++errors;
			} else if(get(index) != null && other.get(index) == null) {
				++errors;
			} else if(!get(index).residueType().couldBe(other.get(index).residueType())) {
				++errors;
			}
		}
		
		if(errors < allowedErrors) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Fills all nulls with BadAminos
	 */
	public void fillBlanks() {
		for(int index = 0; index < size(); ++index) {
			if(get(index) == null) {
				set(index, new AminoAcid(AminoType.INVALID));
			}
		}
	}
	
	/**
	 * TODO: contains debug code
	 * Trims trailing null residues from the ProteinChain
	 * @param trim
	 */
	public void trimTrailingBlanks(int trimSize) {
		String meta = toSequence();
		
		while(size() > 0 && actualSize() > 0 && get(size()-1) == null) {
			try {
				remove(size()-1);
			} catch (IndexOutOfBoundsException IOOBE) {
				qerr(">>Fatal Error when trimming " + label);
				qerr(">>Original Chain \"" + meta + "\"");
				throw IOOBE;
			}
		}
	}
	
	/**
	 * Cleans Bad Amino acids out of the sequence, deleting them for good
	 */
	public void clean() {
		for(int ii = size()-1; ii >= 0; --ii) {
			if(get(ii) == null) {
				remove(ii);
			} else if(get(ii).residueType() == AminoType.INVALID) {
				remove(ii);
			}
		}
	}
	
	/**
	 * Make a deep copy of the protein chain
	 */
	public ProteinChain clone() { return new ProteinChain(this); }
	
	@Override
	public ProteinChain subSequence(int start, int end) {
		ProteinChain myClone = new ProteinChain(this);
		myClone.description = description;
		myClone.modCount = modCount;
		
		for(int index = start; index < end; ++index) {
			if(index < start) {
				myClone.pad();
			} else if(get(index) == null) {
				pad();
			} else if (get(index) instanceof AminoAcid) {
				myClone.add(((AminoAcid) get(index)).clone());
			} else {
				myClone.add(get(index).clone());
			}
		}
		
		return myClone;
	}
	
	public int getLastArrayIndexNonNull() {
		for(int index = size()-1; index >= 0; --index) {
			if(get(index) != null) { return index; }
		}
		return 0;
	}
	
	/** @return: true if the DSSP SS data OR RCSB SS data is missing */
	public boolean missingSecStr() { return metaData.missing_dssp || metaData.missing_rcsb_ss_data; }
}
