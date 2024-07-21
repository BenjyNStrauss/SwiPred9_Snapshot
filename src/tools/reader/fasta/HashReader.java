package tools.reader.fasta;

import assist.util.LabeledHash;
import biology.amino.AminoAcid;
import biology.amino.InsertCode;
import biology.protein.ChainID;
import biology.protein.ProteinChain;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class HashReader extends LabeledHash<InsertCode, AminoAcid> {
	private static final long serialVersionUID = 1L;
	
	public final ChainID id;
	protected boolean isLoaded = false;
	protected boolean atom_reorder_flag = false;
	
	protected HashReader(String label, ChainID id) {
		super(label);
		this.id = id;
	}
	
	public abstract ProteinChain toChain();
	
	public boolean atom_reorder_flag() { return atom_reorder_flag; }
}
