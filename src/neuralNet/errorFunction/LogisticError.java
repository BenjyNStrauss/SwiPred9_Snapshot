package neuralNet.errorFunction;

/**
 * actual value = y
 * predicted value = ŷ
 * 
 * f(y,ŷ) = y_i•ln(ŷ) + (1-y)•ln(1-ŷ)
 * 
 * @author Benjamin Strauss
 * 
 */

public class LogisticError extends LogRegErrorFunction {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Applies the function to the input
	 * @param input
	 * @return
	 */
	public double apply(double predicted, double actual) {
		double term1 = actual*Math.log(predicted);
		double term2 = (1-actual)*Math.log(1-predicted);
		return -(term1 + term2);
	}
}
