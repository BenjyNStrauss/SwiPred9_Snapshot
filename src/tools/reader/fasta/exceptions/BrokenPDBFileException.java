package tools.reader.fasta.exceptions;

import biology.amino.AminoAcid;
import biology.amino.InsertCode;
import biology.protein.ChainID;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class BrokenPDBFileException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	private ChainID id;
	private InsertCode conflictCode;
	private AminoAcid existing;
	private String newCode;
	
	public BrokenPDBFileException() { }

	public BrokenPDBFileException(String message) { super(message); }

	public BrokenPDBFileException(Throwable cause) { super(cause); }
	
	public BrokenPDBFileException(String message, Throwable cause) { super(message, cause); }

	public BrokenPDBFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BrokenPDBFileException(ChainID id, InsertCode conflictCode) {
		super("Conflict for "+id+" at position " +conflictCode);
		this.id = id;
		this.conflictCode = conflictCode;
	}
	
	public BrokenPDBFileException(ChainID id, InsertCode conflictCode, 
			AminoAcid existing, String aminoCode) {
		super("Conflict for "+id+" at position " +conflictCode);
		this.id = id;
		this.conflictCode = conflictCode;
		this.existing = existing;
		this.newCode = aminoCode;
	}

	public ChainID getID() { return id; }
	public InsertCode conflictCode() { return conflictCode; }
	public AminoAcid getExisting() { return existing; }
	public String getConflicting() { return newCode; }
}
