package neuralNet;

import java.util.Objects;

import neuralNet.internal.AbstractNeuralLayer;

/**
 * Abstract Object-Oriented Neural Network for SwiPred
 * @author Benjamin Strauss
 *
 */

abstract class OONeuralNet extends AbstractNeuralNet {
	
	protected final AbstractNeuralLayer<?> input;
	protected final AbstractNeuralLayer<?>[] layers;
	protected final AbstractNeuralLayer<?> output;
	
	protected final boolean inputIncluded;
	protected final boolean outputIncluded;
	
	/**
	 * Creates a new Object-Orient Neural Network (OONeuralNet)
	 * @param layers: layers of the Neural Network
	 */
	protected OONeuralNet(AbstractNeuralLayer<?>[] layers) {
		this(null, layers, null);
	}
	
	/**
	 * Creates a new Object-Orient Neural Network (OONeuralNet)
	 * @param input: Specified input layer, if null, the first hidden layer will be used
	 * @param layers: hidden layers
	 * @param output: Specified output layer, if null, the last hidden layer will be used
	 */
	protected OONeuralNet(AbstractNeuralLayer<?> input, AbstractNeuralLayer<?>[] layers, 
			AbstractNeuralLayer<?> output) {
		Objects.requireNonNull(layers, "No Layers Specified!");
		for(int index = 0; index < layers.length; ++index) {
			Objects.requireNonNull(layers[index], "Layer #" + index +" is null!");
		}
		this.layers = layers;
		inputIncluded = (input != null);
		this.input = (input != null) ? input : layers[0];
		outputIncluded = (output != null);
		this.output = (output != null) ? output : layers[layers.length-1];
	}
}
