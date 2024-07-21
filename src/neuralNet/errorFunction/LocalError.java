package neuralNet.errorFunction;

/**
 * Error function to find the local, not global, minimum
 * >> 0.5*(p-a)^2
 * @author Benjamin Strauss
 *
 */

public class LocalError extends LogRegErrorFunction {
	private static final long serialVersionUID = 1L;

	@Override
	public double apply(double predicted, double actual) {
		return 0.5*(predicted-actual)*(predicted-actual);
	}

}
