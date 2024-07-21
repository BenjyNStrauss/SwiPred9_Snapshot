package tools.reader.schema;

import assist.util.LabeledList;
import biology.descriptor.Descriptor;
import biology.descriptor.Identifier;
import biology.descriptor.Metric;
import biology.descriptor.Switch;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class CSVSchema extends LabeledList<Metric> {
	private static final long serialVersionUID = 1L;
	
	public CSVSchema() { }
	public CSVSchema(int initialCapacity) { super(initialCapacity); }
	public CSVSchema(String label) { this.label = label; }
	public CSVSchema(String label, int initialCapacity) { super(label, initialCapacity); }
	
	/**
	 * gets the first index of a column type
	 * @param col
	 * @return
	 */
	public int indexOf(Descriptor col) {
		for(int index = 0; index < size(); ++index) {
			if(col == get(index)) {
				if(col.getWindowSize() == ((Descriptor) get(index)).getWindowSize() &&
						col.flip == ((Descriptor) get(index)).flip &&
						col.normalize == ((Descriptor)get(index)).normalize) {
					return index;
				}
			}
		}
		return -1;
	}
	
	public int indexOf(Switch col) {
		for(int index = 0; index < size(); ++index) {
			if(col == get(index)) {
				return index;
			}
		}
		return -1;
	}
	
	public int indexOf(Identifier col) {
		for(int index = 0; index < size(); ++index) {
			if(col == get(index)) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * @param col: the ColumnType to look for
	 * +.1 = flipped
	 * +.2 = normalized
	 * +.3 = flipped + normalized
	 * 
	 * @return
	 */
	double optimalIndexForParsing(Descriptor col) {
		//CSVSchema metaSchema = new CSVSchema();
		double loc = Double.NaN;
		
		for(int index = 0; index < size(); ++index) {
			if(col == get(index)) {
				if(Double.isNaN(loc)) {
					loc = index;
				} else if(((Descriptor) get((int) loc)).getWindowSize() > ((Descriptor) get(index)).getWindowSize()) {
					loc = index;
				}
			}
		}
		
		//not found
		if(Double.isNaN(loc)) { return loc; }
		
		double retValCode = 0;
		if(((Descriptor)get((int) loc)).flip) { retValCode += 0.1; }
		if(((Descriptor)get((int) loc)).normalize) { retValCode += 0.2; }
		
		loc += retValCode;
		
		return loc;
	}
}
