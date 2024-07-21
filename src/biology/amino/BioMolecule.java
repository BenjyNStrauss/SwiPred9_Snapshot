package biology.amino;

import java.util.Objects;
import java.util.Set;

import assist.MetaBoolean;
import assist.util.LabeledList;
import biology.amino.atoms.AtomTable;
import biology.molecule.types.Ligand;
import biology.molecule.types.MoleculeType;
import chem.AminoAtom;

/**
 * Represents a BioMolecule (or more accurately, a part thereof)
 * @author Benjamin Strauss
 *
 */

public class BioMolecule extends ChainObject {
	private static final long serialVersionUID = 1L;
	
	public final MoleculeType moleculeType;
	//type of secondary structure associated with this amino acid
	protected SecondaryStructure secondary;
	
	//contains data about the secondary structure of homologues
	protected final LabeledList<SecondaryStructure> homologueStructures = new LabeledList<SecondaryStructure>();
	//contains data about the atoms in the residue
	protected final AtomTable atoms;
	
	public BioMolecule(MoleculeType type) {
		Objects.requireNonNull(type, "Amino Acid Type Cannot Be Null");
		this.moleculeType = type;
		atoms = new AtomTable();	
	}
	
	protected BioMolecule(BioMolecule bMol) {
		moleculeType = bMol.moleculeType;
		atoms = bMol.atoms.clone();
		
		for(SecondaryStructure secStr: homologueStructures) {
			homologueStructures.add(secStr);
		}
		
		Set<String> keys2 = bMol.descriptors.keySet();
		for(String key: keys2) {
			setDescriptor(key, bMol.descriptors.get(key));
		}
	}
	
	public Set<String> atomKeys() { return atoms.keySet(); }
	
	public void setAtom(AminoAtom atom) { atoms.put(atom.label(), atom); }
	
	/**
	 * Gets an atom assigned to the object
	 * @param key: The name of the atom to get
	 * @return: the atom with the name specified, or null if such an atom cannot be found.
	 */
	public AminoAtom getAtom(String key) { return atoms.get(key); }
	
	/**
	 * Removes (and returns) the atom with the specified name
	 * @param name: the name of the atom to return
	 * @return: the atom removed, or null if an atom with the specified name was not found
	 */
	public AminoAtom removeAtom(String name) { return atoms.remove(name); }
	
	public void recordHomologue(SecondaryStructure secStr) {
		homologueStructures.add(secStr);
	}
	
	/** @return: abbreviated representation of residue(s)*/
	public char toChar() { return moleculeType.toChar(); }
	
	/** @return: abbreviated representation of residue(s)*/
	public String toCode() { return moleculeType.toCode(); }
	
	public BioMolecule clone() { return new BioMolecule(this); }
	
	/** @return: type of amino acid secondary structure */
	public SecondaryStructure secondary() { return secondary; }
	
	/** Sets the secondary structure of the amino acid */
	public void setSecondaryStructure(SecondaryStructure type) {
		if(type != null) { homologueStructures.add(type); }
		secondary = type;
	}
	
	@Override
	public MetaBoolean disordered() { return MetaBoolean.UNKNOWN; }
	
	public String toString() {
		if(moleculeType == Ligand.UNKNOWN) {
			return "Unknown Ligand";
		} else {
			return moleculeType.toString();
		}
	}
}
