package modules.descriptor.entropy;

/**
 * Thrown when a negative value is generated for 6-term entropy
 * @author Benjy Strauss
 *
 */

public class NegativeE6Exception extends NegativeEntropyException {
	private static final long serialVersionUID = 1L;

	public NegativeE6Exception() { }
	
	public NegativeE6Exception(String message) { super(message); }

	public NegativeE6Exception(Throwable cause) { super(cause); }
	
	public NegativeE6Exception(String message, Throwable cause) { super(message, cause); }
	
	public NegativeE6Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
