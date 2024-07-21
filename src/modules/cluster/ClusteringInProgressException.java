package modules.cluster;

import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ClusteringInProgressException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ClusteringInProgressException() { }
	
	public ClusteringInProgressException(String message) { super(message); }

	public ClusteringInProgressException(Throwable cause) { super(cause); }
	
	public ClusteringInProgressException(String message, Throwable cause) { super(message, cause); }
	
	public ClusteringInProgressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
