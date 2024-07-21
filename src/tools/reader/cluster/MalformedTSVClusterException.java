package tools.reader.cluster;

import tools.reader.schema.FileSchema;
import utilities.exceptions.MalformedFileException;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class MalformedTSVClusterException extends MalformedFileException {
	private static final long serialVersionUID = 1L;
	private FileSchema<?> schema;
	
	public MalformedTSVClusterException() { }
	
	public MalformedTSVClusterException(String message) { super(message); }
	
	public MalformedTSVClusterException(String message, FileSchema<?> schema) { 
		super(message);
		this.schema = schema;
	}

	public MalformedTSVClusterException(Throwable cause) { super(cause); }
	
	public MalformedTSVClusterException(String message, Throwable cause) { super(message, cause); }
	
	public MalformedTSVClusterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public FileSchema<?> schema() { return schema; }
}
