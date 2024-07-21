package regression.vectors;
import java.util.HashSet;
import java.util.List;

import biology.descriptor.Descriptor;
import biology.descriptor.Metric;

/**
 * 
 * @author Benjy Strauss
 *
 */

@SuppressWarnings("unchecked")
public class StringVector<T extends CharSequence> extends AbstractDataVector<T> {
	private static final long serialVersionUID = 1L;
	
	protected StringVector(Metric metric, String label, T[] data) {
		super(metric, label, data);
	}
	
	public StringVector(Metric metric, String label) {
		super(metric, label);
	}
	
	public StringVector(Descriptor colType, T... data) {
		super(colType, colType.toString(), data);
	}
	
	public StringVector(Descriptor descriptor, List<T> data) {
		super(descriptor);
		for(T t: data) { add(t); }
	}
	
	public DataVector<Integer>[] quantify() {
		DataVector<Integer>[] dv;
		HashSet<String> strSet = new HashSet<String>();
		int index = 0;
		for(index = 0; index < size(); ++index) {
			strSet.add(get(index).toString());
		}
		
		dv = new DataVector[strSet.size()];
		Integer ints[][] = new Integer[strSet.size()][size()];
		
		String strList[] = new String[strSet.size()];
		
		index = 0;
		for(String ch: strSet) {
			strList[index] = ch;
		}
		
		for(int charTypeIndex = 0; charTypeIndex < size(); ++charTypeIndex) {
			for(int dataIndex = 0; dataIndex < size(); ++dataIndex) {
				String meta = get(dataIndex).toString();
				if(strList[charTypeIndex].equals(meta)) {
					ints[charTypeIndex][dataIndex] = 1;
				} else {
					ints[charTypeIndex][dataIndex] = 0;
				}
			}
			dv[index] = new DataVector<Integer>(metric, label, ints[charTypeIndex]);
		}
		
		return dv;
	}
}
