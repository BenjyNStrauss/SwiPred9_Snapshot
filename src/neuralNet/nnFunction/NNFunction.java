package neuralNet.nnFunction;

import assist.numerical.AssistFunction;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class NNFunction extends AssistFunction {
	
	/**
	 * Applies the function to the input
	 * @param input
	 * @return
	 */
	public abstract double apply(double input);
	
	public double apply(Number input) { return apply(input.doubleValue()); }
	
	public abstract double derivative(double value);
	
	public double derivative(Number input) { return derivative(input.doubleValue()); }
}
