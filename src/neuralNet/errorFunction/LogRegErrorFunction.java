package neuralNet.errorFunction;

import java.io.Serializable;

import neuralNet.NNTools;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 * 
 * TODO
 * predicted = p
 * actual = a
 * 
 * too-simple: 0.5*(p-a)^2
 * logistic:	-(a*log(p)+(1-a)*log(1-p))
 * 
 */

public abstract class LogRegErrorFunction extends LocalToolBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Applies the function to the input
	 * @param input
	 * @return
	 */
	public abstract double apply(double predicted, double actual);
	
	/**
	 * 
	 * @param allPredicted: all of the predicted values by the logisitic regression
	 * @param allActual: all of the actual values of the predictions
	 * @return
	 */
	public double cost(double[] allPredicted, double[] allActual) {
		NNTools.verifyArraySize(allPredicted, allActual);
		double sum = 0;
		for(int index = 0; index < allPredicted.length; ++index) {
			sum += apply(allPredicted[index], allActual[index]);
		}
		sum /= allPredicted.length;
		return sum;
	}
}
