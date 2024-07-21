package neuralNet.nnFunction;

/**
 * Covers TanH
 * @author bns
 *
 */

public class TanH extends NNFunction {
	
	public TanH() { }
	
	@Override
	public double apply(double input) {
		return Math.tanh(input);
	}
	
	@Override
	public double derivative(double value) {
		double meta = Math.pow(Math.cosh(value),2);
		return 1/meta;
	}
	
	public boolean equals(Object other) {
		return(other instanceof TanH);
	}
	
	public String toString() {
		return "SwiPred NeuralNet TanH function.";
	}
}
