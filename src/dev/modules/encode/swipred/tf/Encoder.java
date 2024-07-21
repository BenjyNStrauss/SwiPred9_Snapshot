package dev.modules.encode.swipred.tf;

import org.tensorflow.Tensor;
import org.tensorflow.ndarray.DoubleNdArray;
import org.tensorflow.ndarray.NdArray;
import org.tensorflow.ndarray.StdArrays;

import assist.numerical.matrix.QuickMatrix;
import biology.protein.AminoChain;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import modules.encode.swipred.Evaluator;
import modules.encode.swipred.MatrixEncoder;
import modules.encode.swipred.PostitionalEncoder;
import modules.encode.tokens.AminoToken;
import modules.encode.tokens.MandatorilyInaccurateRepresentationException;
import modules.encode.tokens.TokenUtils;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class Encoder extends LocalToolBase {
	//Default 6 Layers as mentioned in Attention is All You Need
	public static final int DEFAULT_LAYERS = 6;
	//
	public static final int DEFAULT_INPUT_LENGTH = 1024;
	
	//Matrix encoder – what we want to train
	public final MatrixEncoder encoder;
	
	//Length of input sequence
	private final int sequenceLength;
	
	//this are hyperparameters
	public final int input_dimension;
	public final int num_heads;
	public final int key_dim;
	public final int val_dim;
	
	private final TFAttentionLayer[] layers;
	
	/**
	 * 
	 * @param matrixFile
	 */
	public Encoder(String matrixFile) { 
		this(matrixFile, DEFAULT_LAYERS, DEFAULT_INPUT_LENGTH, 1, 64, 32);
	}
	
	/**
	 * 
	 * @param matrixFile:		file containing initial (random) protein embedding
	 * @param layers:			number of attention layers (default = 6)
	 * @param sequenceLength:	length of input sequence
	 * @param num_heads
	 */
	public Encoder(String matrixFile, int no_layers, int sequenceLength, int num_heads,
			int key_dim, int val_dim) {
		encoder = new MatrixEncoder(matrixFile);
		this.sequenceLength = sequenceLength;
		this.num_heads = num_heads;
		this.input_dimension = encoder.getInputDimension();
		
		this.key_dim = key_dim;
		this.val_dim = val_dim;
		
		this.layers = new TFAttentionLayer[no_layers];
		
		for(int layer = 0; layer < this.layers.length; ++layer) {
			this.layers[layer] = new TFAttentionLayer(input_dimension, sequenceLength, key_dim, val_dim, num_heads);
		}
	}
	
	/**
	 * Likely incomplete – needs ReLU
	 * 
	 * @param tokens
	 * @return
	 */
	private double[][] forwardPass(AminoToken[] tokens) {
		
		//encode the tokens into a matrix
		double[][] matrix = encoder.encode(tokens);
		//apply positional encoding -- Mahdi confirmed
		matrix = PostitionalEncoder.encodePos_Vaswani(matrix);
		
		//convert matrix to NdArray
		DoubleNdArray nd_matrix = StdArrays.ndCopyOf(matrix);
		
		for(TFAttentionLayer layer: layers) {
			matrix = layer.forwardsPass(matrix);
		}
		
		return protMatrix.matrix;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param loss
	 */
	private void backPropagate(double[][] matrix, double loss) {
		
		//convert double array to QuickMatrix object
		QuickMatrix gradients = new QuickMatrix(matrix);
		
		//update the queries, keys, and values -- TODO currently uncoded
		for(int ii = layers.length - 1; ii >= 0; --ii) {
			gradients = layers[ii].backPropagate(gradients, loss);
		}
	}
	
	/**
	 * 
	 * @param chain
	 * @throws MandatorilyInaccurateRepresentationException
	 */
	public void iteration(AminoChain<?> chain) throws MandatorilyInaccurateRepresentationException {
		
		//turn the chain into tokens
		AminoToken[] tokens = TokenUtils.fixLen(AminoToken.parse(chain), sequenceLength);
		
		//do forwards pass
		double[][] vals = forwardPass(tokens);
		
		//calculate the loss with masked language modeling
		double loss = Evaluator.masked_language_modeling_loss(vals);
		
		//do back propagation
		backPropagate(vals, loss);
	}
	
	/**
	 * Main Method for testing
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final int sequenceLength = 512;
		final int features = 1024;
		final int key_dim = 80;
		final int val_dim = 32;
		final int heads = 1;
		String infile = "files/encode/naive/attempt-00.txt";
		
		Encoder debug = new Encoder(infile, 6, sequenceLength, heads, key_dim, val_dim);
		ChainID id = new ChainID();
		id.setProtein("5BTR");
		id.setChain("A");
		ProteinChain _5BTR_A = SequenceReader.readChain_pdb(id, true);
		
		long start = System.currentTimeMillis();
		debug.iteration(_5BTR_A);
		long end = System.currentTimeMillis();
		qp("Elapsed: " + (end-start));
	}
}
