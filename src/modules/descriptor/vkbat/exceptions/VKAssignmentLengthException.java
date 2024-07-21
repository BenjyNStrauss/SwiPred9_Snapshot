package modules.descriptor.vkbat.exceptions;

import biology.descriptor.VKPred;
import biology.protein.ChainID;
import utilities.exceptions.SwiPredException;

/**
 * Thrown when a Vkbat prediction cannot be assigned to a chain because the length of the chain
 * 		and the length of the prediction differ
 * @author Benjy Strauss
 *
 */

public class VKAssignmentLengthException extends SwiPredException {
	private static final long serialVersionUID = 1L;
	
	private ChainID id;
	private VKPred predictor;
	private String prediction;
	private String sequence;
	
	public VKAssignmentLengthException(ChainID id, VKPred predictor, String prediction, String sequence) {
		this.id = id;
		this.predictor = predictor;
		this.prediction = prediction;
		this.sequence = sequence;
	}
	
	protected VKAssignmentLengthException() { }
	
	protected VKAssignmentLengthException(String message) { super(message); }

	protected VKAssignmentLengthException(Throwable cause) { super(cause); }
	
	protected VKAssignmentLengthException(String message, Throwable cause) { super(message, cause); }
	
	protected VKAssignmentLengthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	/**
	 * Generate a report of the issue
	 * @return
	 */
	public String generateReport() {
		StringBuilder reportBuilder = new StringBuilder();
		reportBuilder.append("VKAssignmentLengthException for chain " + id + " with " + predictor);
		reportBuilder.append("\nSequence Length: " + sequence.length());
		reportBuilder.append("; Prediction Length: " + prediction.length());
		reportBuilder.append("\nSequence:   " + sequence);
		reportBuilder.append("\nPrediction: " + prediction);
		return reportBuilder.toString();
	}
	
}
