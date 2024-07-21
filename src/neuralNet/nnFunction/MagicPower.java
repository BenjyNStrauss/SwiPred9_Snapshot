package neuralNet.nnFunction;

/**
 * Power function, always returns a negative value for a negative input
 * @author bns
 *
 */

public class MagicPower extends NNFunction {
	private final double power;
	
	public MagicPower(double power) { this.power = power; }
	
	@Override
	public double apply(double input) {
		return (input >= 0) ? Math.pow(input, power) : -Math.abs(Math.pow(input, power));
	}
	
	@Override
	public double derivative(double value) {
		if(power == -1) { return Math.log(value); }
		
		double val = power-1;
		val *= Math.pow(value, power-1);
		if(val < 0) { val *= -1; }
		
		return val;
	}
	
	public boolean equals(Object other) {
		if(other instanceof MagicPower) {
			return power != ((MagicPower) other).power;
		} else {
			return false;
		}
	}
	
	public String toString() {
		return "SwiPred NeuralNet Power function: " + power;
	}
}
