package biology.molecule;

import biology.amino.BioMolecule;
import biology.molecule.types.NucleoType;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class Nucleotide extends BioMolecule {
	private static final long serialVersionUID = 1L;
	
	public Nucleotide(NucleoType type) {
		super(type);
	}	
}
