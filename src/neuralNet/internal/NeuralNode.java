package neuralNet.internal;

import java.util.Objects;

import assist.util.LabeledList;
import neuralNet.AbstractNeuralNet;
import neuralNet.nnFunction.NNFunction;
import utilities.LocalToolBase;

/**
 * A Node for a Feed-Forwards Neural Network
 * @author Benjamin Strauss
 *
 */

public class NeuralNode extends LocalToolBase implements Computable {
	private final AbstractNeuralLayer<?> inputs;
	private final LabeledList<Number> weights;
	
	private NNFunction function = null;
	
	private final double bias;
	//output of the node
	private double output = Double.NaN;
	
	public NeuralNode(AbstractNeuralLayer<?> inputs) {
		this(inputs, null, 0);
	}
	
	public NeuralNode(AbstractNeuralLayer<?> inputs, double bias) {
		this(inputs, null, bias);
	}
	
	public NeuralNode(AbstractNeuralLayer<?> inputs, NNFunction funct) {
		this(inputs, funct, 0);
	}
	
	/**
	 * Create a new neural network node
	 * @param inputs
	 */
	public NeuralNode(AbstractNeuralLayer<?> inputs, NNFunction funct, double bias) {
		Objects.requireNonNull(inputs, "Error!  No inputs for this node!");
		function = funct;
		this.inputs = inputs;
		this.bias = bias;
		weights = new LabeledList<Number>();
		for(int index = 0; index < inputs.size(); ++index) {
			weights.add(AbstractNeuralNet.random());
		}
	}
	
	/**
	 * Computes the output value
	 */
	public void compute() {
		output = 0.0;
		for(int index = 0; index < inputs.size(); ++index) { 
			if(!Double.isNaN(inputs.get(index).value())) {
				output += (inputs.get(index).value() * weights.get(index).doubleValue());
			}
		}
		output += bias;
		if(function != null) {
			output = function.apply(output);
		}
	}
	
	/** @param funct: function to set */
	public void setFunction(NNFunction funct) {
		function = funct;
	}
	
	/**
	 * TODO: incomplete – gets derivative of wrong value
	 * 
	 * Needs to be run BACKWARDS
	 * @param learningRate
	 * @return -- needs to return an array used to help correct the previous layer
	 */
	public double[] updateWeights(double error, double learningRate) {
		double[] newWeights = new double[weights.size()];
		
		for(int index = 0; index < weights.size(); ++index) {
			double weight_derivative = 1;
			double error_derivative = 1;
			
			if(function != null) {
				//TODO: likely wrong?
				weight_derivative = function.derivative(inputs.get(index).value());
				error_derivative = function.derivative(error);
			}
			double adjustment = error_derivative / weight_derivative;
			newWeights[index] = weights.get(index).doubleValue() - learningRate * adjustment;
		}
		
		for(int index = 0; index < weights.size(); ++index) {
			weights.set(index, newWeights[index]);
		}
		
		//is this correct???
		return newWeights;
	}

	@Override
	public double value() { return output; }
}
