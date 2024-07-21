package neuralNet.nnFunction;

import assist.exceptions.NotYetImplementedError;

/**
 * Gaussian Error Linear Unit Function for SwiPred Neural Networks
 * @author bns
 *
 */

class GELU extends NNFunction {
	private static final double TAN_CONST = 0.044715;
	
	public GELU() { }
	
	@Override
	public double apply(double input) {
		double val = input + TAN_CONST * Math.pow(input, 3);
		val *= Math.sqrt(Math.PI / 2);
		val = Math.tanh(val)+1;
		val = val*input/2;
		return val;
	}
	
	@Override
	public double derivative(double value) {
		throw new NotYetImplementedError();
	}
	
	public boolean equals(Object other) {
		return (other instanceof GELU);
	}
	
	public String toString() {
		return "SwiPred NeuralNet GELU function.";
	}
}
