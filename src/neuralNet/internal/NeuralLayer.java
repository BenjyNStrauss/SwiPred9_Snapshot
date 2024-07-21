package neuralNet.internal;

import neuralNet.errorFunction.NNErrorFunction;
import neuralNet.nnFunction.NNFunction;

/**
 * A Layer of nodes for a Feed-Forwards Neural Network
 * Specifically, a hidden or output layer
 * @author Benjamin Strauss
 *
 */

public class NeuralLayer extends AbstractNeuralLayer<NeuralNode> {
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG_VIEW_INPUT = false;
	private static final String DEFAULT_NAME = "SwiPredNN Layer of size = ";
	
	//inputs of all nodes on this layer of the neural network
	private final AbstractNeuralLayer<?> inputs;
	
	private final NNErrorFunction eFunct;
	//bias of all nodes in this layer of the neural network
	private final double bias;
	
	private double[] allValues;
	
	/**
	 * Creates a new layer of the neural network with a bias of 0 and y=x activation function
	 * @param size  : Size of this layer of neurons -- can be modified later
	 * @param inputs: Inputs to the neurons on this layer of the neural network
	 */
	public NeuralLayer(int size, AbstractNeuralLayer<?> inputs, NNErrorFunction eFunct) { 
		this(DEFAULT_NAME + size + ")", size, inputs, null, eFunct, 0);
	}
	
	/**
	 * Creates a new layer of the neural network with a bias of 0
	 * @param size  : Size of this layer of neurons -- can be modified later
	 * @param inputs: Inputs to the neurons on this layer of the neural network
	 * @param funct : Default activation function of the neurons on this layer of the neural network
	 */
	public NeuralLayer(int size, AbstractNeuralLayer<?> inputs, NNFunction funct, NNErrorFunction eFunct) { 
		this(DEFAULT_NAME + size + ")", size, inputs, funct, eFunct, 0);
	}
	
	/**
	 * Creates a new layer of the neural network
	 * @param size  : Size of this layer of neurons -- can be modified later
	 * @param inputs: Inputs to the neurons on this layer of the neural network
	 * @param funct : Default activation function of the neurons on this layer of the neural network
	 * @param bias  : Bias for all of the neurons on this layer of the neural network
	 */
	public NeuralLayer(int size, AbstractNeuralLayer<?> inputs, NNFunction funct,
			NNErrorFunction eFunct, double bias) { 
		this(DEFAULT_NAME + size + ")", size, inputs, funct, eFunct, bias);
	}
	
	/**
	 * Creates a new layer of the neural network
	 * @param label : Label of this layer of neurons
	 * @param size  : Size of this layer of neurons -- can be modified later
	 * @param inputs: Inputs to the neurons on this layer of the neural network
	 * @param funct : Default activation function of the neurons on this layer of the neural network
	 * @param bias  : Bias for all of the neurons on this layer of the neural network
	 */
	public NeuralLayer(String label, int size, AbstractNeuralLayer<?> inputs, NNFunction funct,
			NNErrorFunction eFunct, double bias) {
		super(label, size);
		this.inputs = inputs;
		this.bias = bias;
		this.eFunct = eFunct;
		
		for(int index = 0; index < size; ++index) {
			super.add(new NeuralNode(this.inputs, funct, this.bias));
			super.get(index).setFunction(funct);
		}
	}
	
	/**
	 * Called to have each neuron in the hidden layer calculate its output
	 */
	public void process() {
		allValues = null;
		for(NeuralNode nn: this) {
			nn.compute();
		}
	}
	
	/**
	 * Updates the weights on each neuron in the layer
	 * @param trueValues: list of values that each neuron in the layer should have
	 * @param learningRate: learning rate for the neural network
	 * @return vector to correct the nodes on the next layer
	 */
	public double[] backPropagate(double[] trueValues, double learningRate) {
		//check to ensure that the input vector is the right size
		if(trueValues.length != size()) {
			throw new NNInvalidSizeException(size(), trueValues.length);
		}
		
		if(DEBUG_VIEW_INPUT) { qp(trueValues); }
		
		//what will be used to correct the weights on the next layer
		double[] sum = null;
		
		//update the weights in the layer one at a time
		for(int index = 0; index < size(); ++index) {
			sum = combine(sum, get(index).updateWeights(error(trueValues), learningRate));
		}
		
		//TODO is this right?
		//divide each of the corrections by the number of nodes
		for(int index = 0; index < sum.length; ++index) {
			sum[index] /= size();
		}
		
		return sum;
	}
	
	/**
	 * 
	 * @param trueValues
	 * @return total error of the neurons in the layer
	 */
	public double error(double[] trueValues) {
		if(allValues == null) {
			allValues = new double[size()];
			for(int ii = 0; ii < size(); ++ii) {
				allValues[ii] = get(ii).value();
			}
		}
		
		return eFunct.apply(allValues, trueValues);
	}
}
