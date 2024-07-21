package neuralNet.errorFunction;

import java.math.BigDecimal;

import assist.base.Assist;
import assist.numerical.matrix.MatrixSizeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class BinaryCrossEntropy extends NNErrorFunction {
	private static final long serialVersionUID = 1L;
	
	@Override
	public double apply(double[] predicted, double[] actual) {
		//Assist.qpln(predicted);
		//Assist.qpln(actual);
		
		if(predicted.length != actual.length) {
			throw new MatrixSizeException(predicted.length, actual.length);
		}
		
		//BigDecimal sum = BigDecimal.ZERO;
		double sum = 0;
		for(int ii = 0; ii < predicted.length; ++ii) {
			qp("**" + predicted[ii]);
			qp("**" +  Math.log(predicted[ii]));
			
			sum += actual[ii] * Math.log(predicted[ii]) + (1-actual[ii])*Math.log(1-predicted[ii]);
			qp("*" + actual[ii] * Math.log(predicted[ii]));
			qp("*" + (1-actual[ii])*Math.log(1-predicted[ii]));
			qp(sum);
		}
		
		
		sum *= -1 / predicted.length;
		return sum;
	}
	
	/*public static void main(String[] args) {
		
	}*/
}
