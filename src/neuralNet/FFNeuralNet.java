package neuralNet;

import assist.base.Assist;
import assist.base.MathBase;
import assist.numerical.Calculator;
import assist.numerical.matrix.MatrixSizeException;
import assist.numerical.matrix.QuickMatrix;
import neuralNet.errorFunction.NNErrorFunction;
import neuralNet.nnFunction.NNFunction;

/**
 * 
 * @author Benjamin Strauss
 *
 * Setup [layer_i][wieght_i][layer_i+1][weight_i+1]...
 *
 */

@SuppressWarnings("unused")
public class FFNeuralNet extends AbstractNeuralNet {
	private final QuickMatrix[] layers;
	private final QuickMatrix[] weights;
	private final NNFunction function;
	private final NNErrorFunction errFunction;
	
	private double learningRate;
	
	/**
	 * 
	 * @param layerSizes
	 * @param function
	 * @param errFunction
	 */
	public FFNeuralNet(int[] layerSizes, NNFunction function, NNErrorFunction errFunction) {
		layers = new QuickMatrix[layerSizes.length];
		weights = new QuickMatrix[layerSizes.length-1];
		this.function = function;
		this.errFunction = errFunction;
		
		//initialize the layers
		for(int index = 0; index < layers.length; ++index) {
			layers[index] = new QuickMatrix(1, layerSizes[index]);
			//qp(layers[index].toStringFull());
		}
		
		for(int index = 0; index < weights.length; ++index) {
			weights[index] = QuickMatrix.getRandomMatrix(layers[index].cols, layers[index+1].cols, -1, 1);
		}
	}
	
	@Override
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;	
	}
	
	@Override
	public double[] process(double[] data, double[] trueValues) {
		//Do prediction
		double[] prediction = predict(data);
		qp(prediction);
		
		//qp(prediction);
		//Do back-propagation
		double[] trueVals = trueValues;
		
		qp("Adjusting weights");
		for(int index = layers.length - 1; index > 0; --index) {
			trueVals = adjustWeights(index, trueVals);
		}
		return prediction;
	}
	
	/**
	 * 
	 * @param matrixNo: the number of the matrix
	 * @param trueValues
	 * @return
	 */
	private double[] adjustWeights(int matrixNo, double[] trueValues) {
		qp(matrixNo);
		//make sure the true values is the same length as the output
		if(trueValues.length != layers[matrixNo].cols) {
			throw new MatrixSizeException(layers[matrixNo].cols, trueValues.length);
		}
		
		//initialize the array to store the error values
		double[] errors = new double[layers[matrixNo].cols];
		
		//initialize what are to be the true values for the next layer
		double[] newTrueVals = new double[weights[matrixNo-1].rows];
		qp(newTrueVals.length);
		
		for(int ii = 0; ii < errors.length; ++ii) {
			errors[ii] = errFunction.apply(layerAsArray(matrixNo-1), newTrueVals);
		}
		
		qp(errors);
		System.exit(0);
		
		//calculate the error (and adjust weights) for each value of output
		//ii = column, jj = row
		for(int ii = 0; ii < errors.length; ++ii) {
			
			//ii is the row, jj is the column
			for(int jj = 0; jj < weights[matrixNo-1].cols; ++jj) {
				qp("jj: "+jj);
				//qp(weights[matrixNo-1].shape());
				
				//TODO, these 3 lines might not be right...? 
				/*
				 * 'x' always goes with columns!
				 * 'y' always goes with rows!
				 */
				
				double deriv_weight = function.derivative(weights[matrixNo-1].matrix[ii][jj]);
				//qp("deriv_weight: " + deriv_weight);
				double deriv_err = function.derivative(errors[ii]);
				//qp(deriv_err);
				double adjustment = learningRate * deriv_weight / deriv_err;
				//qp(adjustment);
				
				weights[matrixNo-1].matrix[ii][jj] -= adjustment;
				newTrueVals[ii] += weights[matrixNo-1].matrix[ii][jj];
				//qp(newTrueVals[ii]);
			}
			
			newTrueVals[ii] /= weights[matrixNo-1].cols;
		}
		//System.exit(0);
		return newTrueVals;
	}
	
	@Override
	public double[] predict(double[] descriptors) {
		//load the input layer
		for(int index = 0; index < layers[0].rows; ++index) {
			layers[0].matrix[index][0] = descriptors[index];
		}
		
		//qp(layers[0].toStringFull());
		
		//run the data through the network via matrix multiplications
		for(int index = 1; index < layers.length; ++index) {
			layers[index].deconstruct();
			layers[index] = Calculator.mult(layers[index-1], weights[index-1]);
			layers[index].applyFunction(function);
		}
		
		double[] output = new double[layers[layers.length-1].cols];
		output = MathBase.softmax(output);
		for(int col = 0; col < layers[layers.length-1].cols; ++col) {
			layers[layers.length-1].set(0, col, output[col]);
		}
		
		return output;
	}
	
	/**
	 * 
	 * @param layer
	 * @return
	 */
	private double[] layerAsArray(int layer) {
		double[] output = new double[layers[layer].cols];
		for(int ii = 0; ii < layers[layer].cols; ++ii) {
			output[ii] = layers[layer].matrix[0][ii];
		}
		return output;
	}

	@Override
	NNErrorFunction getErrorFunc() { return errFunction; }
}
