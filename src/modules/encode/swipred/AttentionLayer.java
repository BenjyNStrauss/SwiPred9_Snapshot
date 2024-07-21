package modules.encode.swipred;

//import assist.exceptions.NotYetImplementedError;
import assist.numerical.Calculator;
import assist.numerical.matrix.QuickMatrix;
import assist.numerical.matrix.QuickMatrixRowIterator;
import neuralNet.nnFunction.ReLU;

/**
 * As done in "Attention is all you Need" by Vaswani et al
 * 
 * 6 Layers at { 240066, 249939, 246063 }
 * 
 * TODO:
 * 		attention() needs softmax -- to each row of the matrix
 * 		is normalize() in "add+norm" a standard normalization or a division?
 * 
 * @author Benjamin Strauss
 *
 */

public class AttentionLayer extends AbstractNNPiece {
	private static final long serialVersionUID = 1L;
	
	protected final int num_heads;
	protected final int key_dim;
	protected final int val_dim;
	
	//these start out as random
	private final QuickMatrix[] w_values, w_keys, w_queries;
	private final QuickMatrix ff_layer1, ff_layer2;
	private final double[] bias_layer1, bias_layer2;
	
	/**
	 * 
	 * @param input_dim
	 * @param key_dim
	 * @param val_dim
	 * @param heads
	 */
	public AttentionLayer(int features, int seqLen, int key_dim, int val_dim, int heads, double learningRate) {
		super(features, seqLen, learningRate);
		this.key_dim = key_dim;
		this.val_dim = val_dim;
		
		num_heads = heads;
		w_values  = new QuickMatrix[num_heads];
		w_keys    = new QuickMatrix[num_heads];
		w_queries = new QuickMatrix[num_heads];
		
		for(int ii = 0; ii < num_heads; ++ii) {
			w_queries[ii]	= QuickMatrix.getRandomMatrix("w_queries["+ii+"]", features, key_dim, -2, 2);
			w_keys[ii]		= QuickMatrix.getRandomMatrix("w_keys["+ii+"]",    features, key_dim, -2, 2);
			w_values[ii]	= QuickMatrix.getRandomMatrix("w_values["+ii+"]",  features, val_dim, -2, 2);
		}
		
		ff_layer1 = QuickMatrix.getRandomMatrix("FF-1", features, features*4, -2, 2);
		ff_layer2 = QuickMatrix.getRandomMatrix("FF-2", features*4, features, -2, 2);
		
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
	public QuickMatrix forwardsPass(QuickMatrix protMatrix) {
		QuickMatrix protMatrix2;
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
	public QuickMatrix attention(QuickMatrix protein) {
		
		QuickMatrix values	= Calculator.mult(protein, w_values[0]);
		QuickMatrix keys	= Calculator.mult(protein, w_keys[0]);
		QuickMatrix queries = Calculator.mult(protein, w_queries[0]);
		
		keys = keys.transpose();
		
		QuickMatrix weights = Calculator.mult(queries, keys);
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
	 * TODO What happens here??
	 * @param protein
	 * @param w_values
	 * @param w_keys
	 * @param w_queries
	 * @return
	 */
	public QuickMatrix multiHeadAttention(QuickMatrix protein) {
		
		
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
	private static void addAndNorm(QuickMatrix original, QuickMatrix modified) {
		modified = EncoderTools.stretchMatrix(modified, original.rows, original.cols);
		original.add(modified);
		original.normalizeOnRows();
	}
	
	/**
	 * 
	 * @param matrix
	 * @return
	 */
	public QuickMatrix feedForwards(QuickMatrix matrix) {
		
		//first matrix multiplication
		QuickMatrix tmp = Calculator.mult(matrix, ff_layer1);
		
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
	public QuickMatrix backPropagate(QuickMatrix gradients, double loss) {
		
		
		
		
		
		qerr("Error, not yet implemented");
		return gradients;
	}
}
