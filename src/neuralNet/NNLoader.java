package neuralNet;

import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * Used to load Neural Networks
 * @author Benjamin Strauss
 *
 */

public class NNLoader extends LocalToolBase {
	private final AbstractNeuralNet nnPointer;
	//training set descriptors
	private final Number[][] descriptorSet;
	//training set classifications
	private final Number[][] classificationSet;
	
	//validation set descriptors
	private Number[][] val_descriptorSet = null;
	//validation set classifications
	private Number[][] val_classificationSet = null;
	
	//validation set descriptors
	private Number[][] test_descriptorSet = null;
	//validation set classifications
	private Number[][] test_classificationSet = null;
	
	private final int epochs;
	private int batchSize = 1;
	private double learningRate = 0.01;
	
	private int processed = 0;
	private int epochsRun = 0;
	
	public boolean verbose = false;
	
	//
	private final LabeledList<String> outputBuffer = new LabeledList<String>("Loader Output");
	
	/**
	 * 
	 * @param nnPointer
	 * @param descriptorSet
	 * @param classificationSet
	 * @param epochs
	 */
	public NNLoader(AbstractNeuralNet nnPointer, Number[][] descriptorSet, Number[][] classificationSet,
			int epochs) {
		this.nnPointer = nnPointer;
		this.descriptorSet = descriptorSet;
		this.classificationSet = classificationSet;
		this.epochs = epochs;
	}
	
	/**
	 * 
	 * @param nnPointer
	 * @param descriptorSet
	 * @param classificationSet
	 * @param epochs
	 */
	public NNLoader(AbstractNeuralNet nnPointer, double[][] descriptorSet, double[][] classificationSet,
			int epochs) {
		this.nnPointer = nnPointer;
		this.descriptorSet = OONeuralNet.convertToNumberArray(descriptorSet);
		this.classificationSet = OONeuralNet.convertToNumberArray(classificationSet);
		this.epochs = epochs;
	}
	
	/**
	 * 
	 * @param nnPointer
	 * @param descriptorSet
	 * @param classificationSet
	 * @param epochs
	 * @param batchSize
	 * @param learningRate
	 */
	public NNLoader(AbstractNeuralNet nnPointer, Number[][] descriptorSet, Number[][] classificationSet,
			int epochs, int batchSize, double learningRate) {
		this.nnPointer = nnPointer;
		this.descriptorSet = descriptorSet;
		this.classificationSet = classificationSet;
		this.epochs = epochs;
		setBatchSize(batchSize);
		setLearningRate(learningRate);
	}
	
	/**
	 * 
	 * @param nnPointer
	 * @param descriptorSet
	 * @param classificationSet
	 * @param epochs
	 * @param batchSize
	 * @param learningRate
	 */
	public NNLoader(AbstractNeuralNet nnPointer, double[][] descriptorSet, double[][] classificationSet,
			int epochs, int batchSize, double learningRate) {
		this.nnPointer = nnPointer;
		this.descriptorSet = OONeuralNet.convertToNumberArray(descriptorSet);
		this.classificationSet = OONeuralNet.convertToNumberArray(classificationSet);
		this.epochs = epochs;
		setBatchSize(batchSize);
		setLearningRate(learningRate);
	}
	
	/**
	 * 
	 * @param batchSize
	 */
	public void setBatchSize(int batchSize) {
		if(batchSize < 0) {
			error("Warning! Negative batch size.  Absolute value of batch size will be used.");
			batchSize = Math.abs(batchSize);
		}
		if(batchSize == 0) {
			error("Warning!  Batch size 0.  Batch size will be set to 1 instead.");
			++batchSize;
		}
		
		this.batchSize = batchSize;
	}
	
	/**
	 * 
	 * @param learningRate
	 */
	public void setLearningRate(double learningRate) {
		if(learningRate < 0) {
			error("Warning! Negative learning rate.  Absolute value of learning rate will be used.");
			learningRate = Math.abs(learningRate);
		}
		if(batchSize == 0) {
			error("Soft error!  Learning Rate cannot be 0.");
			return;
		}
		this.learningRate = learningRate;
		nnPointer.setLearningRate(this.learningRate);
	}
	
	public void setValidationSet(Number[][] descriptorSet, Number[][] classificationSet) {
		this.val_descriptorSet = descriptorSet;
		this.val_classificationSet = classificationSet;
	}
	
	public void setValidationSet(double[][] descriptorSet, double[][] classificationSet) {
		this.val_descriptorSet = OONeuralNet.convertToNumberArray(descriptorSet);
		this.val_classificationSet = OONeuralNet.convertToNumberArray(classificationSet);
	}
	
	public void setTestSet(Number[][] descriptorSet, Number[][] classificationSet) {
		this.test_descriptorSet = descriptorSet;
		this.test_classificationSet = classificationSet;
	}
	
	public void setTestSet(double[][] descriptorSet, double[][] classificationSet) {
		this.test_descriptorSet = OONeuralNet.convertToNumberArray(descriptorSet);
		this.test_classificationSet = OONeuralNet.convertToNumberArray(classificationSet);
	}
	
	/**
	 * TODO: add error
	 * @return
	 */
	public double[] runOne() {
		double[] val = nnPointer.process(descriptorSet[processed], classificationSet[processed]);
		outputBuffer.add("Epoch: " + epochsRun + ":: Entry" + processed);
		//record training data
		outputBuffer.add("\tTrain descriptors:          "+getString(descriptorSet[processed]));
		outputBuffer.add("\tTrain classifications:      "+getString(classificationSet[processed]));
		outputBuffer.add("\tTrain error:                "+nnPointer.getErrorFunc().apply(val, 
				convertToDoubleArray(classificationSet[processed])));
		
		//if validation data exists, record it too
		if(val_descriptorSet != null) {
			double[] val2 = nnPointer.predict(convertToDoubleArray(descriptorSet[processed]));
			outputBuffer.add("\tValidation descriptors:     "+getString(val_descriptorSet[processed]));
			outputBuffer.add("\tValidation classifications: "+getString(val_classificationSet[processed]));
			outputBuffer.add("\tValidation error:           "+nnPointer.getErrorFunc().apply(val2, 
					convertToDoubleArray(classificationSet[processed])));
		}
		
		++processed;
		if(processed == descriptorSet.length) {
			processed = 0;
			++epochsRun;
		}
		return val;
	}

	/**
	 * Runs a batch as determined by the batch size parameter
	 */
	public void runBatch() {
		Number[][] descBatch, classBatch;
		
		if(processed+batchSize <= descriptorSet.length) {
			descBatch = new Number[batchSize][];
			classBatch = new Number[batchSize][];
			
			for(int index = processed; index < processed+batchSize; ++index) {
				descBatch[index - processed] = descriptorSet[index];
				classBatch[index - processed] = classificationSet[index];
			}
			
			processed += batchSize;
		} else {
			descBatch = new Number[descriptorSet.length - processed][];
			classBatch = new Number[descriptorSet.length - processed][];
			
			for(int index = processed; index < descriptorSet.length; ++index) {
				descBatch[index - processed] = descriptorSet[index];
				classBatch[index - processed] = classificationSet[index];
			}
			
			processed = 0;
			++epochsRun;
		}
		nnPointer.processBatch(descBatch, classBatch);
	}
	
	/**
	 * Runs an entire epoch
	 */
	public void runEpoch() {
		int marker = epochsRun;
		while(marker == epochsRun) {
			runBatch();
		}
	}
	
	/**
	 * 
	 */
	public void runAllEpochs() { runAllEpochs(verbose); }
	
	/**
	 * 
	 * @param verbose
	 */
	public void runAllEpochs(boolean verbose) {
		while(epochsRun < epochs) {
			double startTime = System.currentTimeMillis();
			runEpoch();
			double endTime = System.currentTimeMillis();
			if(verbose) { 
				qp("Completed Epoch: " + epochsRun);
				qp("\tRuntime: " + (endTime - startTime) +"ms");
			}
		}	
	}
	
	public String[] getLog() {
		String[] retval = new String[outputBuffer.size()];
		outputBuffer.toArray(retval);
		return retval;
	}
	
	private static String getString(Number[] numbers) {
		StringBuilder builder = new StringBuilder("[");
		for(Number num: numbers) {
			builder.append(num + ",");
		}
		trimLastChar(builder);
		builder.append("]");
		return builder.toString();
	}
	
	private static double[] convertToDoubleArray(Number[] numbers) {
		double[] retval = new double[numbers.length];
		for(int index = 0; index < numbers.length; ++index) {
			retval[index] = numbers[index].doubleValue();
		}
		return retval;
	}
	
	private static Number[] convertToNumberArray(double[] numbers) {
		Number[] retval = new Double[numbers.length];
		for(int index = 0; index < numbers.length; ++index) {
			retval[index] = numbers[index];
		}
		return retval;
	}
}
