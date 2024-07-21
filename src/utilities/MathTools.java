package utilities;

import regression.vectors.DataVector;

/**
 * Contains Statistical Tools
 * @author Benjy Strauss
 *
 */

public class MathTools extends LocalToolBase {
	
	protected static double getExpectedValue(Iterable<? extends Number> vals) {
		double expected = 0;
		int count = 0;
		for(Number n: vals) {
			expected += n.doubleValue();
			++count;
		}
		expected /= count;
		return expected;
	}
	
	/**
	 * TODO method under construction--be careful, is co-variance calculated correctly?
	 * @param matrix
	 * @return
	 */
	protected static double[][] getCovarianceMatrix(DataVector<?> vector1, DataVector<?> vector2) {
		double cov[][] = new double[vector1.size()][vector2.size()];
		
		double expected1 = getExpectedValue(vector1);
		double expected2 = getExpectedValue(vector2);
		
		//double expectedTotal = 0;
		
		for(int aa = 0; aa < vector1.size(); ++aa) {
			for(int bb = 0; bb < vector2.size(); ++bb) {
				//calculate the covariance
				double term1 = vector1.get(aa).doubleValue() - expected1;
				double term2 = vector2.get(bb).doubleValue() - expected2;
				//qp(term1 + "," + term2);
				cov[aa][bb] = term1*term2;
				
				//calculate the connected expected value
				//expectedTotal += term1*term2;
			}
		}
		
		//multiply by the expectedTotal, divide by total number
		/*for(int aa = 0; aa < vector1.length(); ++aa) {
			for(int bb = 0; bb < vector2.length(); ++bb) {
				cov[aa][bb] *= expectedTotal / (vector1.length()-1);
			}
		}*/
		
		return cov;
	}
	
	protected static double covariance(double aa, double bb) {
		return 0;
	}
}
