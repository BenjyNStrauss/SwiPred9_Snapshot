package biology.protein;

import biology.amino.InsertCode;

/**
 * Thrown when a NullPointerException occurs from a null mapping
 * @author Benjamin Strauss
 *
 */

public class MappingNotFoundException extends NullPointerException {
	private static final long serialVersionUID = 1L;
	
	public final ChainID id;
	public final InsertCode code;
	
	public MappingNotFoundException() { this(null, null, null);	}
	
	public MappingNotFoundException(String message) { this(message, null, null); }
	
	public MappingNotFoundException(ChainID id, InsertCode code) { 
		this("Insertion Code " + code + " not found in mapping for "+id, id, code);
	}
	
	public MappingNotFoundException(String message, ChainID id, InsertCode code) { 
		super(message);
		this.id = id;
		this.code = code;
	}
	
	//"Insertion Code " + code + " not found in mapping for "+id
}
