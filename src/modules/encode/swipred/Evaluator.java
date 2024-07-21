package modules.encode.swipred;

import java.util.Objects;

import assist.base.MathBase;
import assist.exceptions.ArrayLengthMismatchException;
import assist.numerical.Calculator;
import assist.numerical.matrix.QuickMatrix;
import assist.numerical.matrix.QuickMatrixRowIterator;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.LabeledSet;
import modules.encode.tokens.AminoToken;
//import modules.encode.tokens.TokenLoss;

/**
 * The Evaluator masks and calculates the loss of Masked Language Modeling (MLM) models
 * 
 * @author Benjamin Strauss
 *
 */

public class Evaluator extends AbstractNNPiece {
	private static final long serialVersionUID = 1L;
	
	//percentage of the words that will be masked
	private final double mask_percent;
	
	//number of "words" being masked
	private final int numToMask;
	
	//mask that will be used
	private final double[] mask;
	
	private LabeledList<Integer> masked_ind;
	
	private final LabeledList<Double> losses;
	
	//masking matrix
	private QuickMatrix maskingMatrix;
	
	private final TrueValueEncoder encoder;
	
	/**
	 * 
	 * @param mask_percent
	 * @param features
	 * @param sequenceLength
	 */
	public Evaluator(double mask_percent, int features, int sequenceLength) {
		this(mask_percent, features, sequenceLength, new TrueValueEncoder());
	}
	
	/**
	 * 
	 * @param mask_percent
	 * @param features
	 * @param sequenceLength
	 * @param staticMapping
	 * @param equivalence
	 */
	public Evaluator(double mask_percent, int features, int sequenceLength, boolean staticMapping,
			boolean equivalence) {
		this(mask_percent, features, sequenceLength,
				new TrueValueEncoder(staticMapping, equivalence));
	}
	
	/**
	 * 
	 * @param mask_percent
	 * @param features
	 * @param sequenceLength
	 * @param staticMapping
	 * @param equivalence
	 * @param primary_e6_equivalence
	 * @param secondary_equivalence
	 */
	public Evaluator(double mask_percent, int features, int sequenceLength, boolean staticMapping,
			boolean equivalence, double primary_e6_equivalence, double secondary_equivalence) {
		this(mask_percent, features, sequenceLength,
				new TrueValueEncoder(staticMapping, equivalence, primary_e6_equivalence, 
						secondary_equivalence));
	}
	
	/**
	 * 
	 * @param mask_percent
	 * @param features
	 * @param sequenceLength
	 * @param tve
	 */
	public Evaluator(double mask_percent, int features, int sequenceLength, TrueValueEncoder tve) {
		super(features, sequenceLength);
		Objects.requireNonNull(tve);
		this.mask_percent = mask_percent;
		
		losses = new LabeledList<Double>();
		
		encoder = tve;
		
		double num_to_mask_raw = sequenceLength * this.mask_percent;
		numToMask = (int) Math.ceil(num_to_mask_raw);
		
		int maskingTransform = (encoder.staticMapping) ? AminoToken.values().length : sequenceLength;
		
		maskingMatrix = QuickMatrix.getRandomMatrix(features, maskingTransform, -2, 2);
		
		mask = new double[features];
		for(int ii = 0; ii < features; ++ii) {
			mask[ii] = -100.0;
		}
	}
	
	
	/**
	 * - ∑_i real_i * Log(pred_i)
	 * 
	 * NEW
	 * @param labels
	 * @param predictions: seqLen x Features Matrix
	 */
	public void calcLoss(AminoToken[] labels, QuickMatrix predictions) {
		
		/*
		 * Use Matrix multiplication to change the dimensions from [seqLen * features] to
		 * 		[seqLen * seqLen]
		 */
		QuickMatrix maskPred = Calculator.mult(predictions, maskingMatrix);
		
		/*
		 * Contains the probability distributions for the masked tokens
		 * Key = index of masked token
		 * Value = probability distribution for said masked token
		 */
		final LabeledHash<Integer, double[]> probabilities = new LabeledHash<Integer, double[]>();
		
		//apply softmax to the relevant rows of the matrix
		for(Integer index: masked_ind) {
			double[] row = maskPred.matrix[index];
			row = MathBase.softmax(row);
			probabilities.put(index, row);
		}
		
		//Do the cross entropy loss
		for(Integer index: probabilities.keySet()) {
			double[] realValues = encoder.getTrueValues(labels, index);
			losses.add(crossEntropyLoss(probabilities.get(index), realValues));
		}
	}
	
	/**
	 * Returns and resets the average loss
	 * @return avergae loss
	 */
	public double getLoss() {
		double sum = 0.0;
		
		for(double singleLoss: losses) {
			sum += singleLoss;
		}
		
		sum /= losses.size();
		losses.clear();
		return sum;
	}
	
	/**
	 * 
	 * @param labels
	 * @param predictions
	 * @return
	 */
	public AminoToken[] getPredictions(AminoToken[] labels, QuickMatrix predictions) {
		//convert matrix to vector(or vectors equal to masks) for softmax with matrix multiplication
		QuickMatrix maskPred = Calculator.mult(predictions, maskingMatrix);
		
		/*
		 * This will contain the predictions for the masked values
		 */
		int[] indicies = new int[labels.length];
		
		/*
		 * Guide: According to: https://www.youtube.com/watch?v=q9NS5WpfkrU&ab_channel=JamesBriggs
		 * Apply softmax to each of the rows (for each amino acid)
		 * Remember: the matrix is transposed from what's expected (rows = amino acids)
		 */
		QuickMatrixRowIterator qmri = maskPred.iterator();
		for(int ii = 0; qmri.hasNext(); ++ii) {
			
			if(masked_ind.contains(ii)) {
				//do softmax
				double[] row = qmri.next();
				row = MathBase.softmax(row);
				
				/*
				 * do argmax
				 * indicies[ii] = most likely position to replace what was masked
				 * TODO why do this if cross-entropy loss will be applied later?
				 */
				double maxVal = 0;
				for(int rowIndex = 0; rowIndex < row.length; ++rowIndex) {
					if(row[rowIndex] > maxVal) {
						indicies[ii] = rowIndex;
					}
				}
			} else {
				indicies[ii] = -1;
			}
		}
		
		//Our prediction for each of the masked tokens
		AminoToken[] predictedTokens = new AminoToken[labels.length];
		//The true values for each of the masked tokens
		//AminoToken[] originalTokens = new AminoToken[labels.length];
		
		for(int ii = 0; ii < labels.length; ++ii) {
			if(indicies[ii] == -1) { continue; }
			
			if(encoder.staticMapping) {
				//look for amino acid with that index in the original protein
				predictedTokens[ii] = AminoToken.parse(indicies[ii]);
			} else {
				//look for amino acid with that index in the original protein
				predictedTokens[ii] = labels[indicies[ii]];
			}
		}
		
		return predictedTokens;
	}
	
	public static double crossEntropyLoss(double[] predicted, double[] real) {
		double loss = 0.0;
		if(predicted.length != real.length) { throw new ArrayLengthMismatchException(real.length, predicted.length); }
		
		for(int ii = 0; ii < predicted.length; ++ii) {
			loss -= real[ii]*Math.log(predicted[ii]);
		}
		 
		return loss;
	}
	
	/**
	 * Masks N tokens at random (# corresponds to class parameters)
	 * @param predictions
	 * @return
	 */
	public double[][] mask(double[][] predictions) {
		double[][] clonedPredictions = new QuickMatrix(predictions).matrix;
		
		masked_ind = new LabeledList<Integer>();
		
		for(int masked = 0; masked <= numToMask; ++masked) {
			double rawInd = Math.random()*predictions.length;
			int ind = (int) rawInd;
			
			if(masked_ind.contains(ind)) {
				--masked;
				continue;
			} else {
				masked_ind.add(ind);
			}
			
			clonedPredictions[ind] = mask;
		}
		
		return clonedPredictions;
	}
	
	/**
	 * Randomly masks tokens
	 * @param predictions - matrix of predictions
	 * @param mask - mask to use
	 * @param mask_percent - percent of the tokens to mask
	 * @return
	 */
	public static double[][] mask(double[][] predictions, double[] mask, double mask_percent) {
		double[][] clonedPredictions = new QuickMatrix(predictions).matrix;
		
		double num_to_mask_raw = predictions.length*mask_percent;
		int numToMask = (int) Math.ceil(num_to_mask_raw);
		
		LabeledSet<Integer> masked_ind = new LabeledSet<Integer>();
		
		for(int masked = 0; masked <= numToMask; ++masked) {
			double rawInd = Math.random()*predictions.length;
			int ind = (int) rawInd;
			
			if(masked_ind.contains(ind)) {
				--masked;
				continue;
			} else {
				masked_ind.add(ind);
			}
			
			clonedPredictions[ind] = mask;
		}
		
		return clonedPredictions;
	}
}
