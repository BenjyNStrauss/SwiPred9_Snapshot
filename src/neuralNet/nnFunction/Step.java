package neuralNet.nnFunction;

/**
 * Covers Binary Step Function
 * @author bns
 *
 */

public class Step extends NNFunction {
	public Step() { }
	
	@Override
	public double apply(double input) {
		return (input > 0) ? 1 : 0;
	}
	
	@Override
	public double derivative(double input) {
		if(input == 0) { return Double.NaN; }
		return 0;
	}
	
	public boolean equals(Object other) {
		return (other instanceof Step);
	}
	
	public String toString() {
		return "SwiPred NeuralNet Step function.";
	}
}
