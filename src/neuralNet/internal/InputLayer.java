package neuralNet.internal;

/**
 * An input of nodes for a Feed-Forwards Neural Network
 * An input layer has no function other that to allow the other layers access to the data
 * @author Benjamin Strauss
 *
 */

public class InputLayer extends AbstractNeuralLayer<InputNode> {
	private static final long serialVersionUID = 1L;
	private static final boolean DEBUG_VIEW_INPUT = false;
	
	public InputLayer(int size) { 
		this("SwiPredNN Input Layer", size);
	}
	
	public InputLayer(String label, int size) {
		super(label, size);
		for(int index = 0; index < size; ++index) {
			super.add(new InputNode());
		}
	}
	
	@Override
	public void process() {
		//Nothing to do here…
	}
	
	/**
	 * Loads values into the input layer
	 * @param values
	 */
	public void loadValues(Number[] values) {
		if(values.length != size()) { throw new NNInvalidSizeException(values.length, size()); }
		for(int index = 0; index < values.length; ++index) {
			get(index).setValue(values[index].doubleValue());
		}
	}
	
	/**
	 * Loads values into the input layer
	 * @param values
	 */
	public void loadValues(double[] values) {
		if(values.length != size()) { throw new NNInvalidSizeException(values.length, size()); }
		
		if(DEBUG_VIEW_INPUT) { qp(values); }
		
		for(int index = 0; index < values.length; ++index) {
			get(index).setValue(values[index]);
		}
	}
}
