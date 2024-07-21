package regression.vectors;
import java.util.List;

import biology.descriptor.Metric;

/**
 * 
 * @author Benjy Strauss
 *
 */

@SuppressWarnings("unchecked")
public class DataVector<T extends Number> extends AbstractDataVector<T> {
	private static final long serialVersionUID = 1L;
	
	protected DataVector(Metric metric, String label, T[] data) {
		super(metric, label, data);
	}
	
	public DataVector(Metric metric, String label) {
		super(metric, label);
	}
	
	public DataVector(Metric metric, T... data) {
		super(metric, metric.toString(), data);
	}
	
	public DataVector(Metric metric, List<T> data) {
		super(metric);
		for(T t: data) { add(t); }
	}
	
	public DataVector<? extends Number>[] quantify() {
		DataVector<? extends Number>[] dv = new DataVector[1];
		dv[0] = this;
		return dv;
	}
}
