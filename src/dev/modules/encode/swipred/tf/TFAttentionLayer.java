package dev.modules.encode.swipred.tf;

import java.io.Serializable;

import org.tensorflow.Graph;
import org.tensorflow.Operand;
import org.tensorflow.Session;
import org.tensorflow.ndarray.DoubleNdArray;
import org.tensorflow.ndarray.NdArray;
import org.tensorflow.ndarray.StdArrays;
import org.tensorflow.op.OpScope;
import org.tensorflow.op.Scope;
import org.tensorflow.op.linalg.MatMul;
import org.tensorflow.types.TFloat64;

import assist.numerical.Calculator;
import assist.numerical.matrix.QuickMatrix;
import assist.numerical.matrix.QuickMatrixRowIterator;
import modules.encode.swipred.EncoderTools;
import neuralNet.nnFunction.ReLU;
import utilities.LocalToolBase;

/**
 * As done in "Attention is all you Need" by Vaswani et al
 * 
 * TODO:
 * 		attention() needs softmax -- to each row of the matrix
 * 		is normalize() in "add+norm" a standard normalization or a division?
 * 
 * @author Benjamin Strauss
 *
 */

public class TFAttentionLayer extends LocalToolBase implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected final int input_dimension;
	protected final int seqLen;
	protected final int num_heads;
	protected final int key_dim;
	protected final int val_dim;
	
	//these start out as random
	private final DoubleNdArray[] w_values, w_keys, w_queries;
	private final DoubleNdArray ff_layer1, ff_layer2;
	private final double[] bias_layer1, bias_layer2;
	
	/**
	 * 
	 * @param input_dim
	 * @param key_dim
	 * @param val_dim
	 * @param heads
	 */
	public TFAttentionLayer(int input_dim, int seqLen, int key_dim, int val_dim, int heads) {
		input_dimension = input_dim;
		this.key_dim = key_dim;
		this.val_dim = val_dim;
		this.seqLen = seqLen;
		
		num_heads = heads;
		w_values  = new DoubleNdArray[num_heads];
		w_keys    = new DoubleNdArray[num_heads];
		w_queries = new DoubleNdArray[num_heads];
		
		for(int ii = 0; ii < num_heads; ++ii) {
			w_queries[ii]	= EncoderTools.getRandomMatrix(input_dimension, key_dim, -2, 2);
			w_keys[ii]		= EncoderTools.getRandomMatrix(input_dimension, key_dim, -2, 2);
			w_values[ii]	= EncoderTools.getRandomMatrix(input_dimension, val_dim, -2, 2);
		}
		
		ff_layer1 = EncoderTools.getRandomMatrix(input_dimension, input_dimension*4, -2, 2);
		ff_layer2 = EncoderTools.getRandomMatrix(input_dimension*4, input_dimension, -2, 2);
		
		//Initialize the bias vectors
		//TODO - are the lengths correct?
		bias_layer1 = new double[seqLen];
		bias_layer2 = new double[seqLen];
		
		for(int index = 0; index < bias_layer1.length; ++index) {
			bias_layer1[index] = Math.random();
		}
		
		for(int index = 0; index < bias_layer2.length; ++index) {
			bias_layer2[index] = Math.random();
		}
	}
	
	/**
	 * 
	 * @param protMatrix
	 * @return
	 */
	public DoubleNdArray forwardsPass(DoubleNdArray protMatrix) {
		DoubleNdArray protMatrix2;
		if(num_heads == 1) {
			//Seems to work -- check normalization factor, (should it be d_k or standard normalization over features_
			protMatrix2 = attention(protMatrix);
		} else {
			//needs implementation of multi-head
			protMatrix2 = multiHeadAttention(protMatrix);
		}
		
		//add and norm
		addAndNorm(protMatrix, protMatrix2);	
		
		//feed forwards
		protMatrix2 = feedForwards(protMatrix);
		
		//add and norm
		addAndNorm(protMatrix, protMatrix2);
		
		return protMatrix;
	}
	
	/**
	 * 
	 * @param protein
	 * @return
	 */
	public DoubleNdArray attention(DoubleNdArray protein) {
		Graph attn_graph = new Graph();
		
		
		Session attn_session = new Session(attn_graph);
		
		Scope scope = new OpScope(attn_graph);
		
		
		Operand<TFloat64> w_values_op = Tensor.create();
		
		Operand<TFloat64> valuesMaker = MatMul.create(scope, w_values[0], protein);
		
		
		//Calculator.mult(protein, w_values[0]);
		DoubleNdArray keys		= Calculator.mult(protein, w_keys[0]);
		DoubleNdArray queries	= Calculator.mult(protein, w_queries[0]);
		
		keys = keys.transpose();
		
		DoubleNdArray weights = Calculator.mult(queries, keys);
		double scale = Math.sqrt(keys.rows);
		weights.div(scale);
		
		//softmax -- on what? (rows according to chatGPT4)
		QuickMatrixRowIterator qmri = weights.iterator();
		while(qmri.hasNext()) {
			qmri.applySoftmax(true);
		}
		
		weights = Calculator.mult(weights, values);
		return weights;
	}
	
	/**
	 * TODOWhat happens here??
	 * @param protein
	 * @param w_values
	 * @param w_keys
	 * @param w_queries
	 * @return
	 */
	public DoubleNdArray multiHeadAttention(DoubleNdArray protein) {
		
		
		return null;
	}
	
	/**
	 * Layer normalization seems to be across features -- each column is a set of features
	 * see: https://www.pinecone.io/learn/batch-layer-normalization/
	 * Jimmy Lei Ba, Jamie Ryan Kiros, and Geoffrey E Hinton. Layer normalization.
	 * 
	 * @param original
	 * @param modified
	 */
	private static void addAndNorm(DoubleNdArray original, DoubleNdArray modified) {
		modified = EncoderTools.stretchMatrix(modified, original.rows, original.cols);
		original.add(modified);
		original.normalizeOnRows();
	}
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public DoubleNdArray feedForwards(DoubleNdArray matrix) {
		
		//first matrix multiplication
		DoubleNdArray tmp = Calculator.mult(matrix, ff_layer1);
		
		//add bias #1
		tmp.applyNNRowBias(bias_layer1);
		
		//apply ReLU
		tmp.applyFunction(new ReLU());
		
		//second matrix multiplication
		tmp = Calculator.mult(tmp, ff_layer2);
		
		//add bias #2
		tmp.applyNNRowBias(bias_layer2);
		
		return tmp;
	}
	
	/**
	 * Does the back-propagation
	 * Updates the values, keys, and queries matrices via gradient descent
	 * @param loss 
	 * @param protein:	Protein Matrix
	 * @param values: 	Values Matrix
	 * @param keys:		Keys Matrix
	 * @param queries:	Queries Matrix
	 */
	public DoubleNdArray backPropagate(DoubleNdArray gradients, double loss) {
		
		
		
		
		qerr("Error, not yet implemented");
		return gradients;
	}
}
