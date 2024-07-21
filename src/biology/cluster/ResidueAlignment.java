package biology.cluster;

import java.util.Objects;

import assist.util.LabeledHash;
import biology.amino.BioMolecule;
import biology.amino.InsertCode;
import biology.protein.ChainID;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ResidueAlignment extends LabeledHash<ChainID, BioMolecule> {
	private static final long serialVersionUID = 1L;
	
	public final InsertCode code;
	
	public ResidueAlignment(InsertCode code) {
		super(code.toString());
		this.code = code;
	}
	
	public ResidueAlignment(InsertCode code, int initialCapacity) {
		super(code.toString(), initialCapacity);
		this.code = code;
	}
	public ResidueAlignment(InsertCode code, int initialCapacity, float loadFactor) { 
		super(code.toString(), initialCapacity, loadFactor);
		this.code = code;
	}
	
	public ResidueAlignment(InsertCode code, String label) { 
		super(label);
		Objects.requireNonNull(code);
		this.code = code;
	}
	
	public ResidueAlignment(InsertCode code, String label, int initialCapacity) { 
		super(label, initialCapacity);
		Objects.requireNonNull(code);
		this.code = code;
	}
	
	public ResidueAlignment(InsertCode code, String label, int initialCapacity, float loadFactor) {
		super(label, initialCapacity, loadFactor);
		Objects.requireNonNull(code);
		this.code = code;
	}
}
