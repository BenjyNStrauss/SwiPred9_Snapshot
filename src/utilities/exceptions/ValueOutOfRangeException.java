package utilities.exceptions;

/**
 * Thrown when a nonsensical data value is encountered
 * 		Example: negative probabilities
 * @author Benjy Strauss
 *
 */

public class ValueOutOfRangeException extends SwiPredRuntimeException {
	private static final long serialVersionUID = 1L;

	protected final boolean outOfRangeValueSpecified;
	protected final double outOfRangeValue;
	
	public ValueOutOfRangeException() { this("Not Specified."); }
	
	public ValueOutOfRangeException(String msg) {
		super(msg);
		outOfRangeValue = Double.NaN;
		outOfRangeValueSpecified = false;
	}
	
	public ValueOutOfRangeException(String msg, double value) {
		outOfRangeValue = value;
		outOfRangeValueSpecified = true;
	}
	
	public ValueOutOfRangeException(Throwable cause) { 
		super(cause); 
		outOfRangeValue = Double.NaN;
		outOfRangeValueSpecified = false;
	}
	
	public ValueOutOfRangeException(String message, Throwable cause) { 
		super(message, cause); 
		outOfRangeValue = Double.NaN;
		outOfRangeValueSpecified = false;
	}
	
	public ValueOutOfRangeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		outOfRangeValue = Double.NaN;
		outOfRangeValueSpecified = false;
	}

	public double outOfRangeValue() { return outOfRangeValue; }
	
	public boolean outOfRangeValueSpecified() { return outOfRangeValueSpecified; }
}
