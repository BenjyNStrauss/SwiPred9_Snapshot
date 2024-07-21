package neuralNet.errorFunction;

import java.io.Serializable;

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

public abstract class NNErrorFunction extends LocalToolBase implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Applies the function to the input
	 * @param input
	 * @return
	 */
	public abstract double apply(double[] predicted, double[] actual);
	
}
