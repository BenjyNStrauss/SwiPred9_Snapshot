package tools.reader.fasta.pdb;

import java.util.Objects;

import biology.molecule.types.MoleculeType;
import biology.protein.ProteinChain;
import utilities.LocalToolBase;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class PDBChecksumException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	private ProteinChain chain;
	private PDB_Seqres checksum;
	
	public PDBChecksumException() { }

	public PDBChecksumException(String message) { super(message); }

	public PDBChecksumException(Throwable cause) { super(cause); }
	
	public PDBChecksumException(String message, Throwable cause) { super(message, cause); }

	public PDBChecksumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PDBChecksumException(String message, ProteinChain chain, PDB_Seqres checksum) {
		super(message);
		Objects.requireNonNull(chain);
		Objects.requireNonNull(checksum);
		
		this.chain = chain;
		this.checksum = checksum;
	}
	
	public void printDetails() {
		LocalToolBase.qp("   "+chain);
		System.out.print("checksum: ");
		
		for(MoleculeType m: checksum) {
			System.out.print(m.toChar());
		}
		LocalToolBase.qp("");
	}
	
	public PDB_Seqres checksum() { return checksum; }

	public ProteinChain chain() { return chain; }
}
