package analysis;

import assist.util.LabeledHash;
import biology.amino.AminoPosition;
import biology.amino.BioMolecule;
import biology.amino.ResidueConfig;
import biology.molecule.types.AminoType;
import biology.protein.MultiChain;
import biology.protein.ProteinChain;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class AminoCounter extends LabeledHash<AminoType, Integer> {
	private static final long serialVersionUID = 1L;
	
	public AminoCounter() { }
	
	public void record(ProteinChain chain) {
		for(BioMolecule bMol: chain) {
			if(bMol != null && bMol.moleculeType != null) {
				if(bMol.moleculeType instanceof AminoType) {
					if(keySet().contains(bMol.moleculeType)) {
						int val = get(bMol.moleculeType);
						put((AminoType) bMol.moleculeType, val+1);
					} else {
						put((AminoType) bMol.moleculeType, 1);
					}
				}
			}
		}
	}
	
	public void record(MultiChain chain) {
		for(AminoPosition ap: chain) {
			if(ap != null) {
				for(ResidueConfig rc: ap) {
					//always true, check is for potential future compatibility
					if(rc.primary() instanceof AminoType) { 
						if(keySet().contains(rc.primary())) {
							int val = get(rc.primary());
							put(rc.primary(), val+rc.occurrences());
						} else {
							put(rc.primary(), rc.occurrences());
						}
					}
				}
			}
		}
	}
}
