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
public class CharVector extends AbstractDataVector<Character> {
	private static final long serialVersionUID = 1L;
	
	public CharVector(Descriptor colType, Character... data) {
		super(colType, colType.toString(), data);
	}
	
	public CharVector(Metric metric, String label) {
		super(metric, label);
	}
	
	public CharVector(Descriptor descriptor, List<Character> data) {
		super(descriptor);
		for(Character ch: data) { add(ch); }
	}
	
	public DataVector<Integer>[] quantify() {
		DataVector<Integer>[] dv;
		HashSet<Character> charSet = new HashSet<Character>();
		int index = 0;
		for(index = 0; index < size(); ++index) {
			charSet.add(get(index));
		}
		
		dv = new DataVector[charSet.size()];
		Integer ints[][] = new Integer[charSet.size()][size()];
		
		char charList[] = new char[charSet.size()];
		
		index = 0;
		for(Character ch: charSet) {
			charList[index] = ch;
		}
		
		for(int charTypeIndex = 0; charTypeIndex < size(); ++charTypeIndex) {
			for(int dataIndex = 0; dataIndex < size(); ++dataIndex) {
				char meta = get(dataIndex);
				if(charList[charTypeIndex] == meta) {
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
