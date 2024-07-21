package tools.reader.fasta.exceptions;

import biology.amino.InsertCode;
import biology.protein.AminoChain;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * Thrown when an RCSB-PDB Mapping doesn't make sense to the reader.
 * Specifically, when the size of the source region doesn't match the
 * 			size of the target region
 * This is NOT an internal exception and means something has gone wrong with
 * 		a .pdb file.
 * 
 * @author Benjamin Strauss
 *
 */

public class IncomprehensibleMappingException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	//public final String[] fields'
	public AminoChain<?> chain = null;
	public InsertCode mapStart;
	public InsertCode mapEnd;
	public int trueStart;
	public int trueEnd;

	public IncomprehensibleMappingException() { }
	
	public IncomprehensibleMappingException(String message) { super(message); }

	public IncomprehensibleMappingException(Throwable cause) { 
		super(cause);
		if(cause instanceof IncomprehensibleMappingException) {
			IncomprehensibleMappingException imeCause = (IncomprehensibleMappingException) cause;
			chain = imeCause.chain;
			mapStart = imeCause.mapStart;
			mapEnd = imeCause.mapEnd;
			trueStart = imeCause.trueStart;
			trueEnd = imeCause.trueEnd;
		}
	}
	
	public IncomprehensibleMappingException(String message, Throwable cause) { 
		super(message, cause);
		if(cause instanceof IncomprehensibleMappingException) {
			IncomprehensibleMappingException imeCause = (IncomprehensibleMappingException) cause;
			chain = imeCause.chain;
			mapStart = imeCause.mapStart;
			mapEnd = imeCause.mapEnd;
			trueStart = imeCause.trueStart;
			trueEnd = imeCause.trueEnd;
		}
	}

	public IncomprehensibleMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		if(cause instanceof IncomprehensibleMappingException) {
			IncomprehensibleMappingException imeCause = (IncomprehensibleMappingException) cause;
			chain = imeCause.chain;
			mapStart = imeCause.mapStart;
			mapEnd = imeCause.mapEnd;
			trueStart = imeCause.trueStart;
			trueEnd = imeCause.trueEnd;
		}
	}

	public IncomprehensibleMappingException(String message, String[] fields) {
		super(message);
	}
	
	public IncomprehensibleMappingException(AminoChain<?> chain, InsertCode mapStart, InsertCode mapEnd,
			int trueStart, int trueEnd) {
		super("Range mismatch for chain: " + chain.id().standard());
		this.chain = chain;
		this.mapStart = mapStart;
		this.mapEnd = mapEnd;
		this.trueStart = trueStart;
		this.trueEnd = trueEnd;
	}
}
