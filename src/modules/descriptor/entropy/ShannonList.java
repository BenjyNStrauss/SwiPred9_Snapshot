package modules.descriptor.entropy;

import assist.util.LabeledList;

/**
 * ShannonList is a list of ShannonVectors
 * @author Benjy Strauss
 *
 */

public class ShannonList extends LabeledList<ShannonVector> implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	public ShannonList() { }
	public ShannonList(int initialCapacity) { super(initialCapacity); }
	public ShannonList(String label) { this.label = label; }
	public ShannonList(String label, int initialCapacity) { super(label, initialCapacity); }
	
	public ShannonList clone() {
		ShannonList myClone = new ShannonList(label);
		
		for(ShannonVector vect: this) {
			myClone.add(vect.clone());
		}
		
		return myClone;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof ShannonList)) { return false; }
		ShannonList meta = (ShannonList) other;
		
		if(size() != meta.size()) { return false; }
		
		for(int ii = 0; ii < size(); ++ii) {
			if(!get(ii).equals(meta.get(ii))) {
				return false;
			}
		}
		
		return true;
	}
	
	public String toSequence() {
		return toString().replaceAll("\\.", "_");
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(ShannonVector vect: this) {
			if(vect == null) {
				builder.append(".");
			} else {
				builder.append(vect.toString());
			}
		}
		
		return builder.toString();
	}
}
