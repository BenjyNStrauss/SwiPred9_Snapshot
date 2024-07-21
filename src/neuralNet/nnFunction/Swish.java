package neuralNet.nnFunction;

/**
 * Under development
 * Covers Swish Function for SwiPred Neural Networks
 * @author bns
 *
 */

public class Swish extends NNFunction {
	public Swish() { }
	
	@Override
	public double apply(double input) {
		double val = Math.exp(input)+1;
		return input/val;
	}
	
	@Override
	public double derivative(double value) {
		return value/(Math.exp(value)+1);
	}
	
	public boolean equals(Object other) {
		return (other instanceof Swish);
	}
	
	public String toString() {
		return "SwiPred NeuralNet Swish function.";
	}
}
