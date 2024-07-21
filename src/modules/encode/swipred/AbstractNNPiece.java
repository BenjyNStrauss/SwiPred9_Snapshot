package modules.encode.swipred;

import utilities.DataObject;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class AbstractNNPiece extends DataObject {
	private static final long serialVersionUID = 1L;
	
	//Input dimension: number of features
	public final int features;
	
	//Length of input sequence
	public final int sequenceLength;
	
	//Learning Rate
	private double learningRate;
	
	protected AbstractNNPiece(int features, int sequenceLength) {
		this.features = features;
		this.sequenceLength = sequenceLength;
	}
	
	protected AbstractNNPiece(int features, int sequenceLength, double learningRate) {
		this.features = features;
		this.sequenceLength = sequenceLength;
		this.learningRate = learningRate;
	}
	
	public double learningRate() { return learningRate; }
	
	public void setLearningRate(double newRate) {
		learningRate = newRate;
	}
}
