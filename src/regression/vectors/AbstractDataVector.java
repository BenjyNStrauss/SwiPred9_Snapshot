package regression.vectors;

import assist.util.LabeledList;
import biology.descriptor.Metric;

/**
 * 
 * @author Benjy Strauss
 *
 * @param <T>
 */

public abstract class AbstractDataVector<T> extends LabeledList<T> {
	private static final long serialVersionUID = 1L;
	
	protected final Metric metric;
	
	protected AbstractDataVector(Metric metric) {
		this.metric = metric;
		if(metric != null) {
			this.label = metric.toString();
		}
	}
	
	protected AbstractDataVector(Metric metric, String label) {
		this.metric = metric;
		if(label == null && metric != null) { this.label = metric.toString(); }
		else { this.label = label; }
	}
	
	protected AbstractDataVector(Metric metric, String label, T[] data) {
		this.metric = metric;
		
		for(T t: data) { add(t); }
		
		if(label == null && metric != null) {
			label = metric.toString();
		} else {
			this.label = label;
		}
	}
	
	public String toString() { return label; }
	
	public Metric descriptor() { return metric; }
	
	public abstract DataVector<? extends Number>[] quantify();
}
