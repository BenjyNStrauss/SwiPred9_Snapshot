package tools.reader.fasta.pdb;

import biology.amino.InsertCode;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class LigandException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	private InsertCode insCode = null;
	private String aminoCode = null;
	
	public LigandException() { }

	public LigandException(String message) { super(message); }

	public LigandException(Throwable cause) { super(cause); }
	
	public LigandException(String message, Throwable cause) { super(message, cause); }

	public LigandException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LigandException(InsertCode insCode, String aminoCode) {
		this.insCode = insCode;
		this.aminoCode = aminoCode;
	}
	
	public InsertCode insCode() { return insCode; }
	public String aminoCode() { return aminoCode; }
}
