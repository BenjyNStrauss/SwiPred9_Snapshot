package neuralNet.nnFunction;

import assist.exceptions.NotYetImplementedError;

/**
 * Under development
 * Covers ReLU, Parametric ReLu, and LeakyReLU Functions for SwiPred Neural Networks
 * @author bns
 *
 */

class SELU extends NNFunction {
	private final double lambda;
	private final double alpha;
	
	public SELU() { this(0, 0); }
	
	/**
	 * 
	 * @param alpha
	 */
	public SELU(double alpha) { this(1, alpha); }
	
	public SELU(double lambda, double alpha) { 
		this.lambda = lambda;
		this.alpha = alpha;
	}
	
	@Override
	public double apply(double input) {
		return (input > 0) ? input*lambda : alpha*(Math.exp(input)-1)*lambda;
	}
	
	@Override
	public double derivative(double value) {
		throw new NotYetImplementedError();
	}
	
	public boolean equals(Object other) {
		if(other instanceof SELU) {
			return (lambda != ((SELU) other).lambda) && 
					(alpha != ((SELU) other).alpha);
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "SwiPred NeuralNet SELU function with alpha = " + alpha + " and lambda = " + lambda + ".";
	}
}
