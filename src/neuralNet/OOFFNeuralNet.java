package neuralNet;

import java.util.Objects;

import assist.base.MathBase;
import neuralNet.errorFunction.NNErrorFunction;
import neuralNet.internal.AbstractNeuralLayer;
import neuralNet.internal.InputLayer;
import neuralNet.internal.NeuralLayer;
import neuralNet.nnFunction.NNFunction;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * A Feed-Forwards Java Neural Network
 * @author Benjamin Strauss
 * 
 * TODO: add gradient descent: cannot yet update weights
 * • NeuralNode.updateWeights() >> does this work
 * • Where do the true values come for a neural network hidden layer
 * • loss()
 * • accuracy()
 * 
 */

public class OOFFNeuralNet extends OONeuralNet {
	//learning rate of the neural network
	private double learningRate;
	private final NNErrorFunction errFunction;
	
	/**
	 * Create a new Feed-Forwards Neural Network
	 * @param error 
	 * @param layerSizes: array of the sizes of each layer
	 * @param function: function for each layer of the neural network to apply to the linear input
	 * 		This function will be assigned to all layers of the neural network by default
	 */
	public OOFFNeuralNet(int[] layerSizes, NNFunction function, NNErrorFunction error) {
		super(makeLayers(layerSizes, function, error));
		errFunction = error;
	}

	/** @return Learning rate of the Neural Network */
	public double learningRate() { return learningRate; }
	/** @param learningRate: Learning rate of the Neural Network  */
	public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
	
	@Override
	NNErrorFunction getErrorFunc() { return errFunction; }
	
	/**
	 * Processes a single item in a data set
	 * @param data: array containing the descriptor data
	 * @return: results as determined by the neural network
	 */
	public double[] process(double[] data, double[] trueValues) {
		Objects.requireNonNull(data, "Descriptor Data is null.");
		Objects.requireNonNull(trueValues, "True values are null.");
		
		((InputLayer) input).loadValues(data);
		for(int index = 1; index < layers.length; ++index) {
			layers[index].process();
		}
		
		double[] propensities = new double[output.size()];
		for(int index = 0; index < propensities.length; ++index) {
			propensities[index] = output.get(index).value();
		}
		
		double[] normOutput = new double[propensities.length];
		propensities = MathBase.softmax(propensities);
		return normOutput;
	}
	
	/**
	 * first index is the item in the batch, second index is the value of the item
	 * @param data
	 * @return
	 */
	public double[][] processBatch(double[][] data, double[][] trueValues) {
		Objects.requireNonNull(data, "Descriptor Data is null.");
		Objects.requireNonNull(trueValues, "True values are null.");
		double[][] predicted = new double[data.length][];
		
		for(int index = 0; index < data.length; ++index) {
			predicted[index] = process(data[index], trueValues[index]);
		}
		
		//do back-propagation…  somewhere
		backPropagateBatch(trueValues);
		
		//double lossVal = loss(predicted, trueValues);
		//double accVal = accuracy(predicted, trueValues);
		
		return predicted;
	}
	
	@Override
	public double[] predict(double[] descriptors) {
		Objects.requireNonNull(descriptors, "Descriptor Data is null.");
		
		((InputLayer) input).loadValues(descriptors);
		for(int index = 1; index < layers.length; ++index) {
			layers[index].process();
		}
		
		double[] propensities = new double[output.size()];
		for(int index = 0; index < propensities.length; ++index) {
			propensities[index] = output.get(index).value();
		}
		
		return propensities;
	}
	
	/**
	 * 
	 * @param trueVals
	 */
	private void backPropagate(double[] trueVals) {
		Objects.requireNonNull(trueVals, "True values are null.");
		double[] hiddenTrueVals = trueVals;
		for(int index = layers.length-1; index >= 1; --index) {
			hiddenTrueVals = ((NeuralLayer) layers[index]).backPropagate(hiddenTrueVals, learningRate);
		}
	}
	
	/**
	 * 
	 * @param trueValues
	 */
	private void backPropagateBatch(double[][] trueValues) {
		Objects.requireNonNull(trueValues, "True values are null.");
		for(double[] trueVals: trueValues) {
			backPropagate(trueVals);
		}
	}
	
	/**
	 * 
	 * @params (unknown at this time)
	 * @return
	 */
	private double cost() {
		// TODO Auto-generated method stub
		return 0.0;
	}
	
	/*
	 * part of cost 
	 */
	private double loss(double[][] predicted, double[][] trueValues) {
		// TODO Auto-generated method stub
		return 0.0;
	}
	
	private double accuracy(double[][] predicted, double[][] trueValues) {
		// TODO Auto-generated method stub
		return 0.0;
	}
	
	/**
	 * 
	 * @param layerSizes
	 * @param function
	 * @return
	 */
	private static AbstractNeuralLayer<?>[] makeLayers(int[] layerSizes, NNFunction function,
			NNErrorFunction error) {
		Objects.requireNonNull(layerSizes, "Layer sizes must be specified!");
		if(layerSizes.length <= 1) {
			throw new SwiPredRuntimeException("Neural Network needs at least 2 layers!");
		}
		
		AbstractNeuralLayer<?>[] layers = new AbstractNeuralLayer[layerSizes.length];
		layers[0] = new InputLayer(layerSizes[0]);
		
		for(int index = 1; index < layerSizes.length; ++index) {
			layers[index] = new NeuralLayer(layerSizes[index], layers[index-1], function, error);
		}
		return layers;
	}
}
