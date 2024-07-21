package modules.descriptor.entropy;

/**
 * Thrown when a negative value is generated for 20-term entropy
 * @author Benjy Strauss
 *
 */

public class NegativeE20Exception extends NegativeEntropyException {
	private static final long serialVersionUID = 1L;

	public NegativeE20Exception() { }
	
	public NegativeE20Exception(String message) { super(message); }

	public NegativeE20Exception(Throwable cause) { super(cause); }
	
	public NegativeE20Exception(String message, Throwable cause) { super(message, cause); }
	
	public NegativeE20Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
