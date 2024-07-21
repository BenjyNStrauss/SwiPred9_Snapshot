package modules.compare;

import java.util.ArrayList;

import biology.descriptor.EncodingType;
import biology.protein.AminoChain;
import utilities.LocalToolBase;

/**
 * Contains helper methods for ProteinVectorizer
 * 
 * @author Benjamin Strauss
 *
 */

abstract class VectorizeHelper extends LocalToolBase {
	/**
	 * Helper Method
	 * @param chain: the chain to make the matrix embedding for
	 * @param type: The type of encoding to use
	 * @return
	 */
	protected static double[][] getMatrix(AminoChain<?> chain, EncodingType type, StringBuilder relevantSeq) {
		ArrayList<double[]> matrixList = new ArrayList<double[]>();
		
		for(int index = chain.startsAt(); index < chain.size(); ++index) {
			if(chain.get(index) != null && chain.get(index).getEncoding(type) != null) {
				matrixList.add(chain.get(index).getEncoding(type));
				if(relevantSeq != null) {
					relevantSeq.append(chain.get(index).toChar());
				}
			}
		}
		
		double[][] matrix = new double[matrixList.size()][];
		matrixList.toArray(matrix);
		return matrix;
	}
	
	/**
	 * Helper Method
	 * @param matrix
	 * @param featureNo
	 * @return
	 */
	protected static double[] getFeatureVector(double[][] matrix, int featureNo) {
		double[] featureVect = new double[matrix.length];
		
		for(int index = 0; index < matrix.length; ++index) {
			featureVect[index] = matrix[index][featureNo];
		}
		
		return featureVect;
	}
	
	/**
	 * Calculates the distance between two points represented as arrays
	 * @param point1
	 * @param point2
	 * @return
	 */
	protected static double distance(double[] point1, double[] point2) {
		double sum = 0;
		for(int index = 0; index < point1.length; ++index) {
			sum += Math.pow(point1[index] - point2[index], 2);
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	 * Calculates the magnitude of a vector as described by an n-dimensional point
	 * @param point1
	 * @param point2
	 * @return
	 */
	protected static double magnitude(double[] point) {
		double sum = 0;
		for(int index = 0; index < point.length; ++index) {
			sum += Math.pow(point[index], 2);
		}
		
		return Math.sqrt(sum);
	}
	
	/**
	 * 
	 * @param point1
	 * @param point2
	 * @return
	 */
	protected static double cosine(double[] point1, double[] point2) {
		double sum = 0;
		for(int index = 0; index < point1.length; ++index) {
			sum += point1[index] * point2[index];
		}
		sum /= (magnitude(point1) * magnitude(point2));
		return sum;
	}
}
