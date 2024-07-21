package modules.encode.swipred;

import org.tensorflow.ndarray.DoubleNdArray;
import org.tensorflow.ndarray.StdArrays;

import assist.numerical.matrix.QuickMatrix;
import utilities.LocalToolBase;

/**
 * Tools for Transformer Encoders
 * @author Benjamin Strauss
 *
 */

public class EncoderTools extends LocalToolBase {
	
	/**
	 * Stretches a matrix to match the given dimensions
	 * Repeats and/or truncates values as necessary
	 * Note: Tested and working
	 * 
	 * @param matrix
	 * @param targetRows
	 * @param targetCols
	 * @return
	 */
	public static QuickMatrix stretchMatrix(QuickMatrix matrix, int targetRows, int targetCols) {
		if(matrix.rows == targetRows && matrix.cols == targetCols) { return matrix; }
		
		QuickMatrix retval = new QuickMatrix(targetRows, targetCols);
		
		for(int row = 0, fromRow = 0; row < retval.rows; ++row, ++fromRow) {
			if(fromRow == matrix.rows) { fromRow = 0; }
			for(int col = 0, fromCol = 0; col < retval.cols; ++col, ++fromCol) {
				if(fromCol == matrix.cols) { fromCol = 0; }
				retval.set(row, col, matrix.get(fromRow, fromCol));
			}
		}
		
		return retval;
	}
	
	public static DoubleNdArray getRandomMatrix(int rows, int cols, int minVal, int maxVal) {
		return StdArrays.ndCopyOf(QuickMatrix.getRandomMatrix("", rows, cols, minVal, maxVal).doubleValue());
	}
}
