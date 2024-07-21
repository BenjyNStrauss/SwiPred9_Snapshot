package neuralNet.nnFunction;

/**
 * Covers ReLU, Parametric ReLu, and LeakyReLU Functions for SwiPred Neural Networks
 * @author bns
 *
 */

public class ReLU extends NNFunction {
	private final double subzeroSlope;
	private final double param;
	
	public ReLU() { this(0, 0); }
	
	public ReLU(double subzeroSlope) { this(subzeroSlope, 0); }
	
	public ReLU(double subzeroSlope, double param) { 
		this.subzeroSlope = subzeroSlope;
		this.param = param;
	}
	
	@Override
	public double apply(double input) {
		return (input > 0) ? input+param : input*subzeroSlope+param;
	}
	
	@Override
	public double derivative(double value) {
		if(value == 0) { return 0; }
		return (value > 0) ? 1 : subzeroSlope;
	}
	
	public boolean equals(Object other) {
		if(other instanceof ReLU) {
			return (subzeroSlope != ((ReLU) other).subzeroSlope) && 
					(param != ((ReLU) other).param);
		} else {
			return false;
		}
	}
	
	public String toString() {
		String val = "SwiPred NeuralNet ReLU function";
		if(subzeroSlope != 0) {
			val += " with leak of " + subzeroSlope;
		}
		if(param != 0) {
			if(subzeroSlope != 0) {
				val += " and";
			}
			val += " with param of " + param;
		}
		
		return val+".";
	}
}
