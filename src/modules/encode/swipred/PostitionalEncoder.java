package modules.encode.swipred;

import assist.numerical.matrix.QuickMatrix;
import utilities.LocalToolBase;

/**
 * Performs positional encodings
 * @author Benjamin Strauss
 *
 */

public class PostitionalEncoder extends LocalToolBase {
	
	private PostitionalEncoder() { }
	
	/**
	 * Performs a positional encoding based on Sine and Cosine (Vaswani et al., 2017):
	 * 
	 * x_matrix ∈ R ^ (n * d)
	 * x_matrix[n=#rows][d=#columns]
	 * 
	 * Vaswani, A., Shazeer, N., Parmar, N., Uszkoreit, J., Jones, L., Gomez, A. N., …
	 *  Polosukhin, I. (2017). Attention is all you need. Advances in Neural Information
	 *   Processing Systems (pp. 5998–6008).
	 * 
	 * @param x_matrix: base matrix: X
	 * @return: positionally encoded matrix X+P
	 */
	public static double[][] encodePos_Vaswani(double[][] x_matrix) {
		QuickMatrix xx = new QuickMatrix("X", x_matrix);
		QuickMatrix pp = new QuickMatrix("P", xx.rows, xx.cols);
		int dd = xx.cols;
		
		for(int ii = 0; ii < pp.rows; ++ii) {
			for(int jj = 0; jj < pp.cols; jj +=2) {
				pp.set(ii, jj, Math.sin(ii / Math.pow(10000, 2*jj/dd)));
				if(jj+1 >= pp.rows) { break; }
				pp.set(ii, jj+1, Math.cos(ii / Math.pow(10000, 2*jj/dd)));
			}
		}
		
		QuickMatrix xp = xx.clone();
		
		xp.add(pp);
		return xp.matrix;
	}
	
}
