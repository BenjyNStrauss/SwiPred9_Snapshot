package regression;

import regression.vectors.BoolVector;
import regression.vectors.DataVector;
import utilities.MathTools;
import utilities.exceptions.BadArraySizeException;

/**
 * 
 * @author Benjy Strauss
 *
 */

//@SuppressWarnings("unused")
public class RegressionModule extends MathTools {
	
	public static void main(String[] args) {
		codeTest();
	}
	
	/**
	 * TODO
	 * @param descriptors
	 * @return
	 */
	public static double[] findConstants(DataMatrix descriptors) {
		notCodedYet();
		
		return null;
	}
	
	/**
	 * Try to maximize this to get the regression to work
	 * @param descriptors
	 * @param responses: response variable (0 and 1 from output) aka 'y'
	 * @param β
	 * @return
	 */
	public static double tryMaximize(DataMatrix descriptors, BoolVector responses, double[] β) {
		double result = 0;
		double[][] x = descriptors.matrix;
		double[] η = get_η(descriptors, β);
		
		if(descriptors.matrix.length != β.length) {
			throw new BadArraySizeException(descriptors.matrix.length, β.length,
					"Descriptors don't match matrix size");
		}
		
		//i = column index
		for(int i = 0; i < descriptors.matrix[0].length; ++i) {
			//j = descriptor/covariate
			for(int j = 0; j < descriptors.matrix.length; ++j) {
				if(responses.get(i)) {
					//x[j][i] flipped from the book because of how indexes work in java
					result += x[j][i] * β[j];
				}
			}
		}
		
		double value = 0;
		//i = column index
		for(int i = 0; i < descriptors.matrix[0].length; ++i) {
			double j_sum = 0;
			//j = descriptor/covariate
			for(int j = 0; j < descriptors.matrix.length; ++j) {
				j_sum += x[j][i] * β[j];
			}
			
			value += η[i] * Math.log(1 + Math.exp(j_sum));
		}
		
		result -= value;
		return result;
	}
	
	/**
	 * TODO
	 * @param descriptors
	 * @return
	 */
	public static double[] get_η(DataMatrix x, double[] β) {
		double result[] = new double[x.matrix[0].length];
		
		for(int i = 0; i < result.length; ++i) {
			result[i] = 0;
			for(int j = 0; j < x.matrix.length; ++j) {
				result[i] += x.matrix[j][i] * β[j];
			}
		}
		
		return result;
	}
	
	/**
	 * logit = g1(∏) = η = ln[ ∏/(1-∏) ]
	 * >> e^η = ∏/(1-∏)
	 * >> (1-∏) e^η = ∏
	 * >> e^η - ∏e^η = ∏
	 * >> e^η/∏ - eη = 1
	 * >> ∏ - 1 = 1 / e^η
	 * >> ∏ = e^η / ( 1 + e^η )
	 *
	 * @param η
	 * @return
	 */
	public static double get_PI(double η) {
		return Math.exp(η) / (1 - Math.exp(η));
	}
	
	private static void codeTest() {
		Double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
		DataVector<Double> x1 = new DataVector<Double>(null, "x");
		x1.addAll(x);
		
		Double[] y = {-2.0, -1.0, 0.0, 1.0, 2.0};
		DataVector<Double> y1 = new DataVector<Double>(null, "x");
		y1.addAll(y);
		
		double[][] test = getCovarianceMatrix(x1, x1);
		for(double[] row: test){
			qp(row);
		}
	}
}
