package regression.vectors;
import java.util.List;

import biology.descriptor.Descriptor;
import biology.descriptor.Metric;

/**
 * 
 * @author Benjy Strauss
 *
 */

@SuppressWarnings("unchecked")
public class BoolVector extends AbstractDataVector<Boolean> {
	private static final long serialVersionUID = 1L;
	
	public BoolVector(Descriptor colType, Boolean... data) {
		super(colType, colType.toString(), data);
	}
	
	public BoolVector(Descriptor descriptor, List<Boolean> data) {
		super(descriptor);
		for(Boolean b: data) { add(b); }
	}
	
	public BoolVector(Metric metric, String label) {
		super(metric, label);
	}
	
	public BoolVector(DataVector<Double> vector) {
		super(vector.metric);
		Number[] numData = (Number[]) vector.toArray();
		Boolean[] boolData = new Boolean[numData.length];
		
		for(int ii = 0; ii < numData.length; ++ii) {
			boolData[ii] = (numData[ii].intValue() == 0) ? true : false;
		}
		
		label = vector.toString();
		for(Boolean b: boolData) { add(b); }
	}
	
	public DataVector<Integer>[] quantify() {
		DataVector<Integer>[] dv = new DataVector[1];
		Integer ints[] = new Integer[size()];
		for(int index = 0; index < size(); ++index) {
			ints[index] = (get(index)) ? 1 : 0;
		}
		dv[0] = new DataVector<Integer>(metric, label, ints);
		
		return dv;
	}
}
