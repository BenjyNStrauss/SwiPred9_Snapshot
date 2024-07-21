package neuralNet.nnFunction;

/**
 * Covers Sigmoid/Logistic Function for SwiPred Neural Networks
 * @author bns
 *
 */

public class Sigmoid extends NNFunction {
	public Sigmoid() { }
	
	@Override
	public double apply(double input) {
		double val = Math.exp(input)+1;
		return 1/val;
	}
	
	@Override
	public double derivative(double value) {
		return apply(value)*(1-apply(value));
	}
	
	public boolean equals(Object other) {
		return (other instanceof Sigmoid);
	}
	
	public String toString() {
		return "SwiPred NeuralNet Sigmoid/Logistic function.";
	}
}
