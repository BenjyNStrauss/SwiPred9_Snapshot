package neuralNet.internal;

import assist.util.LabeledList;

/**
 * 
 * @author bns
 *
 */

public abstract class AbstractNeuralLayer<E extends Computable> extends LabeledList<E >{
	private static final long serialVersionUID = 1L;
	
	public AbstractNeuralLayer(int size) { super(size); }
	
	public AbstractNeuralLayer(String label, int size) {
		super(size);
		this.label = label;
	}
	
	public abstract void process();
	
	protected static double[] combine(double[] sum, double[] term) {
		if(sum == null) { return term; }
		if(term == null) { return sum; }
		if(sum.length != term.length) { throw new NNInvalidSizeException(sum.length, term.length); }
		for(int index = 0; index < sum.length; ++index) {
			sum[index] += term[index];
		}
		
		return sum;
	}
}
