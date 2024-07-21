package regression;

import java.util.regex.Pattern;

import assist.util.LabeledList;
import regression.vectors.AbstractDataVector;
import regression.vectors.BoolVector;

/**
 * A List of DataVectors that represents a table of values
 * @author Benjy Strauss
 *
 */

public class DataTable extends LabeledList<AbstractDataVector<?>> {
	private static final long serialVersionUID = 1L;
	private static final String DECIMAL_PATTERN = "-?([0-9]*)\\.([0-9]*)";
	
	protected BoolVector test;
	
	public DataTable() { }
	public DataTable(String label) { super(label); }
	public DataTable(int initialCapacity) { super(initialCapacity); }
	public DataTable(String label, int initialCapacity) { super(label, initialCapacity); }
	
	public DataTable(BoolVector testVect) { this(null, testVect); }
	public DataTable(String label, BoolVector testVect) { super(label); }
	
	public DataTable(AbstractDataVector<?>... descriptors) { this(null, null, descriptors); }
	
	public DataTable(BoolVector testVect,  AbstractDataVector<?>... descriptors) { 
		this(null, testVect, descriptors);
	}
	
	public DataTable(String label, BoolVector testVect,  AbstractDataVector<?>... descriptors) { 
		test = testVect;
		for(AbstractDataVector<?> descr : descriptors) {
			if(descr != null) { add(descr); }
		}
	}
	
	/**
	 * Sets the test vector
	 * @param testVect
	 */
	public void setTestVector(BoolVector testVect) { test = testVect; }
	
	public void addDescriptors(AbstractDataVector<?>... descriptors) {
		for(AbstractDataVector<?> descr : descriptors) {
			if(descr != null) {
				add(descr);
			}
		}
	}
	
	public DataMatrix generateMatrix() {
		return new DataMatrix((AbstractDataVector<?>[]) this.toArray());
	}
	
	public AbstractDataVector<?>[] toArray() {
		return (AbstractDataVector<?>[]) super.toArray();
	}
	
	public String toString() {
		StringBuilder outBuilder = new StringBuilder();
		outBuilder.append("pandas.DataFrame(");
		for(AbstractDataVector<?> dv: this) {
			if(dv != null) {
				outBuilder.append("[");
				for(int ii = 0; ii < dv.size(); ++ii) {
					String s_val = dv.get(ii) + "";
					if(Pattern.matches(DECIMAL_PATTERN, s_val)) {
						outBuilder.append(dv.get(ii) + ",");
					} else {
						outBuilder.append("'" + dv.get(ii) + "',");
					}
				}
				outBuilder.setLength(outBuilder.length()-1);
				outBuilder.append("],");
			}
		}
		outBuilder.setLength(outBuilder.length()-1);
		outBuilder.append("], columns=[");
		
		for(AbstractDataVector<?> dv: this) {
			outBuilder.append("'" + dv.toString() + "',");
		}
		outBuilder.setLength(outBuilder.length()-1);
		outBuilder.append("])");
		
		return outBuilder.toString();
	}
}
