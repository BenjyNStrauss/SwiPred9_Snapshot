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
 * @author Benjamin Strauss
 *
 */

public class DateTest extends LocalToolBase {
	private static final int[] SIZES = { 34, 120, 256, 7 };
	private static final NNFunction FUNCT = new ReLU(0.2);
	private static final String FILENAME = "../SwiPredNN/datasets/Date_Fruit_Datasets.csv";
	private static final int EPOCHS = 24;
	private static final int BATCH_SIZE = 32;
	private static final double LEARNING_RATE = 0.005;
	
	private static final double[] BERHI  = { 1, 0, 0, 0, 0, 0, 0 };
	private static final double[] DEGLET = { 0, 1, 0, 0, 0, 0, 0 };
	private static final double[] DOKOL  = { 0, 0, 1, 0, 0, 0, 0 };
	private static final double[] IRAQI  = { 0, 0, 0, 1, 0, 0, 0 };
	private static final double[] ROTANA = { 0, 0, 0, 0, 1, 0, 0 };
	private static final double[] SAFAVI = { 0, 0, 0, 0, 0, 1, 0 };
	private static final double[] SOGAY  = { 0, 0, 0, 0, 0, 0, 1 };
	
	public static void main(String[] args) {
		Pair<double[][], double[][]> inOut = loadData(FILENAME);
		
		OOFFNeuralNet nn = new OOFFNeuralNet(SIZES, FUNCT, new BinaryCrossEntropy());
		NNLoader loader = new NNLoader(nn, inOut.x, inOut.y, EPOCHS, BATCH_SIZE, LEARNING_RATE);
		
		qp("loaded");
		loader.verbose = true;
		loader.runAllEpochs();
		
		qp("testing");
		testPrediction(nn);
		testRandomPrediction(nn);
	}
	
	private static void testPrediction(AbstractNeuralNet nn) {
		double[] vector = {422163,2378.908,837.8484,645.6693,0.6373,733.1539,0.9947,424428,
				0.7831,1.2976,0.9374,0.875,0.002,0.0015,0.7657,0.9936,117.4466,109.9085,
				95.6774,26.5152,23.0687,30.123,-0.5661,-0.0114,0.6019,3.237,2.9574,4.2287,
				-59191263232.0,-50714214400.0, -39922372608.0 ,58.7255,54.9554,47.84};
		double[] prediction = nn.predict(vector);
		qp("Prediction: ");
		qp(prediction);
	}
	
	private static void testRandomPrediction(AbstractNeuralNet nn) {
		double[] vector = {
				Math.random(), Math.random(), Math.random(), Math.random(), Math.random(),
				Math.random(), Math.random(), Math.random(), Math.random(), Math.random(),
				Math.random(), Math.random(), Math.random(), Math.random(), Math.random(),
				Math.random(), Math.random(), Math.random(), Math.random(), Math.random(),
				Math.random(), Math.random(), Math.random(), Math.random(), Math.random(),
				Math.random(), Math.random(), Math.random(), Math.random(), Math.random(),
				Math.random(), Math.random(), Math.random(), Math.random()
			};
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
			pseudoframe.x[index-1] = new double[34];
			for(int ii = 0; ii < 34; ++ii) {
				pseudoframe.x[index-1][ii] = Double.parseDouble(tokens[ii]);
			}
			
			switch(tokens[tokens.length-1]) {
			case "BERHI":			pseudoframe.y[index-1] = BERHI;			break;
			case "DEGLET":			pseudoframe.y[index-1] = DEGLET;		break;
			case "DOKOL":			pseudoframe.y[index-1] = DOKOL;			break;
			case "IRAQI":			pseudoframe.y[index-1] = IRAQI;			break;
			case "ROTANA":			pseudoframe.y[index-1] = ROTANA;		break;
			case "SAFAVI":			pseudoframe.y[index-1] = SAFAVI;		break;
			case "SOGAY":			pseudoframe.y[index-1] = SOGAY;			break;
			default:
				qerr("Parsing Error: " + tokens[tokens.length-1]);
				System.exit(3);
			}
			
		}
		
		return pseudoframe;
	}
	
}
