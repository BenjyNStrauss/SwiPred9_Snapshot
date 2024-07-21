package neuralNet.internal;

/**
 * 
 * @author bns
 *
 */

public class InputNode implements Computable {
	private double value;
	
	public InputNode() { }
	
	public void setValue(double value) { this.value = value; }
	
	@Override
	public double value() { return value; }
	
}