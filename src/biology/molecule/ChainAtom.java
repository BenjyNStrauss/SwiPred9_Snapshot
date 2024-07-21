package biology.molecule;

import assist.sci.chem.Atom;
import assist.sci.chem.AtomTools;
import biology.amino.BioMolecule;
import biology.molecule.types.AtomType;


/**
 * This is a wrapper class for assist.sci.Atom because zarking Java doesn't allow
 * 	multiple inheritance!
 * @author Benjamin Strauss
 *
 */

public class ChainAtom extends BioMolecule {
	private static final long serialVersionUID = 1L;
	
	public final Atom atom;
	
	public ChainAtom(AtomType type) {
		super(type);
		atom = AtomTools.getAtom(type.elementID);
		atom.setCharge(type.charge);
	}
	
	public ChainAtom(AtomType type, int isotope) {
		super(type);
		atom = AtomTools.getAtom(type.elementID, isotope);
		atom.setCharge(type.charge);
	}
	
	public int hashCode() { return atom.hashCode(); }
	
	public boolean equals(Object other) {
		if(other instanceof ChainAtom) {
			return atom.equals(((ChainAtom) other).atom);
		} else if(other instanceof Atom) {
			return atom.equals(other);
		} else {
			return false;
		}
	}
	
	public String toString() { return atom.toString(); }
}
