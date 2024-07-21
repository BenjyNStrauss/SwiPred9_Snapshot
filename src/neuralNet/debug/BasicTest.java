package neuralNet.debug;

import assist.util.Pair;
import neuralNet.OOFFNeuralNet;
import neuralNet.AbstractNeuralNet;
import neuralNet.NNLoader;
import neuralNet.errorFunction.BinaryCrossEntropy;
import neuralNet.nnFunction.*;
import utilities.LocalToolBase;

/**
 * 
 * @author bns
 *
 */

public class BasicTest extends LocalToolBase {
	private static final int[] SIZES = { 4, 48, 3 };
	private static final NNFunction FUNCT = new TanH();
	private static final String FILENAME = "output/acetyl-sirt-dataset-downsampled-nanless.csv";
	private static final int EPOCHS = 24;
	private static final int BATCH_SIZE = 32;
	private static final double LEARNING_RATE = 0.005;
	
	private static final double[] N = { 1, 0, 0 };
	private static final double[] A = { 0, 1, 0 };
	private static final double[] U = { 0, 0, 1 };
	
	public static void main(String[] args) {
		Pair<double[][], double[][]> inOut = loadData(FILENAME);
		
		OOFFNeuralNet nn = new OOFFNeuralNet(SIZES, FUNCT, new BinaryCrossEntropy());
		NNLoader loader = new NNLoader(nn, inOut.x, inOut.y, EPOCHS, BATCH_SIZE, LEARNING_RATE);
		
		//loader.runAllEpochs(true);
		//loader.runOne();
		//loader.runBatch();
		//System.exit(0);
		loader.runBatch();
		loader.runBatch();
		
		testPrediction(nn);
		testRandomPrediction(nn);
		
		loader.runBatch();
		loader.runBatch();
		
		testPrediction(nn);
		testRandomPrediction(nn);
		
		loader.runBatch();
		loader.runBatch();
		loader.runBatch();
		loader.runBatch();
		
		//loader.runEpoch();
		
		testPrediction(nn);
		testRandomPrediction(nn);
	}
	
	private static void testPrediction(AbstractNeuralNet nn) {
		double[] vector = {0.03, 0.0, -0.2, 0.4};
		double[] prediction = nn.predict(vector);
		qp("Prediction: ");
		qp(prediction);
	}
	
	private static void testRandomPrediction(AbstractNeuralNet nn) {
		double[] vector = {Math.random(), Math.random(), Math.random(), Math.random()};
		double[] prediction = nn.predict(vector);
		qp("Random Prediction: ");
		qp(prediction);
	}
	
	/**
	 * [row][col]
	 * @param filename
	 * @return
	 */
	private static Pair<double[][], double[][]> loadData(String filename) {
		Pair<double[][], double[][]> pseudoframe = new Pair<double[][], double[][]>();
		String[] lines = getFileLines(filename);
		pseudoframe.x = new double[lines.length-1][];
		pseudoframe.y = new double[lines.length-1][];
		
		for(int index = 1; index < lines.length; ++index) {
			String[] tokens = lines[index].split(",");
			pseudoframe.x[index-1] = new double[4];
			pseudoframe.x[index-1][0] = Double.parseDouble(tokens[1]); //isunstruct
			pseudoframe.x[index-1][1] = Double.parseDouble(tokens[3]); //e20
			pseudoframe.x[index-1][2] = Double.parseDouble(tokens[4]); //amber
			pseudoframe.x[index-1][3] = Double.parseDouble(tokens[5]); //vkbat
			
			switch(tokens[6]) {
			case "N":
				pseudoframe.y[index-1] = N;
				break;
			case "A":
				pseudoframe.y[index-1] = A;
				break;
			case "U":
				pseudoframe.y[index-1] = U;
				break;
			default:
				qerr("Parsing Error");
				System.exit(3);
			}
		}
		
		return pseudoframe;
	}
	
}
