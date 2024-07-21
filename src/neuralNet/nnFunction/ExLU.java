package neuralNet.nnFunction;

/**
 * Exponential Linear Unit Function
 * @author bns
 *
 */

public class ExLU extends NNFunction {
	private final double alpha;
	
	public ExLU(double alpha) { this.alpha = alpha; }
	
	@Override
	public double apply(double input) {
		return (input >= 0) ? input : (Math.exp(input)-1)*alpha;
	}
	
	@Override
	public double derivative(double value) {
		return (value >= 0) ? 1 : apply(value)+alpha;
	}
	
	public boolean equals(Object other) {
		if(other instanceof ExLU) {
			return alpha != ((ExLU) other).alpha;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "SwiPred NeuralNet ExLU function with alpha = " + alpha;
	}
}
