package neuralNet;

import java.util.Objects;

import neuralNet.errorFunction.NNErrorFunction;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class AbstractNeuralNet extends LocalToolBase {
	private static final String NULL_ARRAY_ERROR = "Cannot convert null array!";
	
	/**
	 * Processes a single item in a data set
	 * @param data: array containing the descriptor data
	 * @param trueValues: the actual values for the network to learn from
	 * @return: results as determined by the neural network
	 */
	public double[] process(Number[] data, Number[] trueValues) {
		return process(convertToDoubleArray(data), convertToDoubleArray(trueValues));
	}
	
	/**
	 * Processes a single item in a data set
	 * @param data: array containing the descriptor data
	 * @param trueValues: the actual values for the network to learn from
	 * @return: results as determined by the neural network
	 */
	public abstract double[] process(double[] data, double[] trueValues);
	
	/**
	 * Processes a single item in a data set
	 * @param data: array containing the descriptor data
	 * @param trueValues: the actual values for the network to learn from
	 * @return: results as determined by the neural network
	 */
	public double[][] processBatch(Number[][] data, Number[][] trueValues) {
		Objects.requireNonNull(data, "Error: no data to process!");
		Objects.requireNonNull(trueValues, "Error: no true values to process!");
		return processBatch(convertToDoubleArray(data), convertToDoubleArray(trueValues));
	}
	
	/**
	 * Processes a single item in a data set
	 * @param data: array containing the descriptor data
	 * @param trueValues: the actual values for the network to learn from
	 * @return: results as determined by the neural network
	 */
	public double[][] processBatch(double[][] data, double[][] trueValues) {
		double[][] output = new double[data.length][];
		for(int index = 0; index < data.length; ++index) {
			output[index] = process(data[index], trueValues[index]);
		}
		return output;
	}
	
	public abstract void setLearningRate(double learningRate);
	
	public abstract double[] predict(double[] descriptors);
	
	abstract NNErrorFunction getErrorFunc();
	
	/**
	 * Converts an array of Numbers to an array of doubles
	 * @param array: the array of Numbers to convert
	 * @return: the converted array of doubles
	 */
	public static final double[] convertToDoubleArray(Number[] array) {
		Objects.requireNonNull(array, NULL_ARRAY_ERROR);
		double[] retval = new double[array.length];
		for(int index = 0; index < array.length; ++index) {
			retval[index] = array[index].doubleValue();
		}
		return retval;
	}
	
	/**
	 * Converts an array of Numbers to an array of doubles
	 * @param array: the array of Numbers to convert
	 * @return: the converted array of doubles
	 */
	public static final double[][] convertToDoubleArray(Number[][] array) {
		Objects.requireNonNull(array, NULL_ARRAY_ERROR);
		double[][] retval = new double[array.length][];
		for(int index = 0; index < array.length; ++index) {
			retval[index] = convertToDoubleArray(array[index]);
		}
		return retval;
	}
	
	/**
	 * Converts an array of doubles to an array of Doubles
	 * @param array: the array of doubles to convert
	 * @return: the converted array of Doubles
	 */
	public static final Double[] convertToNumberArray(double[] array) {
		Objects.requireNonNull(array, NULL_ARRAY_ERROR);
		Double[] retval = new Double[array.length];
		for(int index = 0; index < array.length; ++index) {
			retval[index] = array[index];
		}
		return retval;
	}
	
	/**
	 * Converts a 2D array of doubles to a 2D array of Doubles
	 * @param array: the 2D array of doubles to convert
	 * @return: the converted 2D array of Doubles
	 */
	public static final Double[][] convertToNumberArray(double[][] array) {
		Objects.requireNonNull(array, NULL_ARRAY_ERROR);
		Double[][] retval = new Double[array.length][];
		for(int index = 0; index < array.length; ++index) {
			retval[index] = convertToNumberArray(array[index]);
		}
		return retval;
	}
	
	public static final double random() { return Math.random()*2-1; }
}
