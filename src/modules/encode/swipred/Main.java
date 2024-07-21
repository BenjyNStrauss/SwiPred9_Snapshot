package modules.encode.swipred;

import assist.numerical.Calculator;
import assist.numerical.matrix.QuickMatrix;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class Main extends LocalToolBase {
	
	/**
	 * 62934
77865
42335
76A2A
	 * @param args
	 */
	public static void main(String[] args) {
		final double[][] test0 = new double[][] {
			{ 3, 9, 9, 5 },
			{ 2, 8, 8, 7 },
			{ 4, 2, 4, 1 },
			{ 5, 8, 3, 0xA },
			{ 0xA, 9, 2, 3},
		};
		
		final double[][] test1 = new double[][] {
			{ 6, 2, 9, 3, 4 },
			{ 7, 7, 8, 6, 5 },
			{ 4, 2, 3, 3, 5 },
			{ 7, 6, 0xA, 2, 0xA },
		};
		
		QuickMatrix qm0 = new QuickMatrix(test0);
		QuickMatrix qm1 = new QuickMatrix(test1);
		
		qp(qm0);
		qp(qm1);
		
		QuickMatrix qmp = Calculator.mult(qm0, qm1);
		
		qp(qmp);
	}

}
