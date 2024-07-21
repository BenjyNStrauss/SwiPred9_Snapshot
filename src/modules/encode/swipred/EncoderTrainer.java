package modules.encode.swipred;

import assist.numerical.matrix.QuickMatrix;
import biology.protein.*;
import modules.encode.tokens.*;
import tools.reader.fasta.SequenceReader;

/**
 * Shape: [rows, columns]
 * NOTE: rows = sequenceLength
 * 
 * TODO:
 * • In the attention layer, are we actually normalizing or just dividing by √d_k?
 * 
 * Feed-Forward Neural Network - Complete
 * 		• Still needs multi-head attention
 * (2) Loss Function - masked language modeling
 * 		• What are the inputs?
 * (3) Back propagation – Gradient Descent
 * 		• what are the inputs?
 * 		• what gets updated
 * 
 * look up Andrej Karpathy videos on nn
 * 
 * @author Benjamin Strauss
 *
 */

public class EncoderTrainer extends AbstractNNPiece {
	private static final long serialVersionUID = 1L;
	
	//Default 6 Layers as mentioned in Attention is All You Need
	public static final int DEFAULT_LAYERS = 6;
	//
	public static final int DEFAULT_INPUT_LENGTH = 1024;
	
	//Matrix encoder – what we want to train
	public final MatrixEncoder encoder;
	
	//this are hyperparameters
	public final int num_heads;
	public final int key_dim;
	public final int val_dim;
	
	public final int batch_size;
	
	private final AttentionLayer[] layers;
	
	private final Evaluator evaluator;
	
	private int iteration = 0;
	
	/**
	 * 
	 * @param matrixFile
	 */
	public EncoderTrainer(String matrixFile, double learningRate) { 
		this(matrixFile, learningRate, DEFAULT_LAYERS, DEFAULT_INPUT_LENGTH, 1, 64, 32, 0.15, 1);
	}
	
	/**
	 * 
	 * @param matrixFile:		file containing initial (random) protein embedding
	 * @param layers:			number of attention layers (default = 6)
	 * @param sequenceLength:	length of input sequence
	 * @param num_heads
	 */
	public EncoderTrainer(String matrixFile, double learningRate, int no_layers, int sequenceLength,
			int num_heads, int key_dim, int val_dim, double mask_percent, int batch_size) {
		this(new MatrixEncoder(matrixFile), learningRate, no_layers, sequenceLength, 
				num_heads, key_dim, val_dim, mask_percent, batch_size);
	}
	
	/**
	 * 
	 * @param matrixFile:		file containing initial (random) protein embedding
	 * @param layers:			number of attention layers (default = 6)
	 * @param sequenceLength:	length of input sequence
	 * @param num_heads
	 */
	public EncoderTrainer(MatrixEncoder encoder, double learningRate, int no_layers, int sequenceLength,
			int num_heads, int key_dim, int val_dim, double mask_percent, int batch_size) {
		super(encoder.getInputDimension(), sequenceLength, learningRate);
		this.encoder = encoder;
		this.num_heads = num_heads;
		
		this.batch_size = batch_size;
		
		this.key_dim = key_dim;
		this.val_dim = val_dim;
		
		this.layers = new AttentionLayer[no_layers];
		evaluator = new Evaluator(mask_percent, features, sequenceLength);
		
		for(int layer = 0; layer < this.layers.length; ++layer) {
			this.layers[layer] = new AttentionLayer(features, sequenceLength, key_dim, val_dim, num_heads, learningRate);
		}
	}
	
	public void setLearningRate(double newRate) {
		super.setLearningRate(newRate);
		for(AttentionLayer layer: layers) {
			layer.setLearningRate(newRate);
		}
		evaluator.setLearningRate(newRate);
	}
	
	/**
	 * Likely incomplete – needs ReLU {where?}
	 * 
	 * @param tokens
	 * @return
	 */
	private QuickMatrix forwardPass(AminoToken[] tokens) {
		
		//encode the tokens into a matrix
		double[][] matrix = encoder.encode(tokens);
		
		//apply positional encoding -- Mahdi confirmed
		matrix = PostitionalEncoder.encodePos_Vaswani(matrix);
		
		//apply masking
		matrix = evaluator.mask(matrix);
		
		/*
		 * convert double array to (transposed) QuickMatrix object
		 * 		each row should correspond to a feature
		 * 		each column should correspond to an amino acid
		 */
		QuickMatrix protMatrix = new QuickMatrix(matrix);
		
		for(AttentionLayer layer: layers) {
			protMatrix = layer.forwardsPass(protMatrix);
		}
		
		return protMatrix;
	}
	
	/**
	 * 
	 * @param matrix
	 * @param loss
	 */
	private void backPropagate(QuickMatrix gradients, double loss) {
		
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
		QuickMatrix prediction = forwardPass(tokens);
		
		//calculate the loss with masked language modeling
		evaluator.calcLoss(tokens, prediction);
		
		if(iteration >= batch_size) {
			double loss = evaluator.getLoss();
			
			//do back propagation
			backPropagate(prediction, loss);
		}
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
		final double mask_percent = 0.15;
		final int batch_size = 1;
		String infile = "files/encode/naive/attempt-00.txt";
		
		EncoderTrainer debug = new EncoderTrainer(infile, 0.001, 2, sequenceLength, heads, key_dim,
				val_dim, mask_percent, batch_size);
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
