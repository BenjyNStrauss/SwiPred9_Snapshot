package tools.reader.schema;

import biology.descriptor.Descriptor;
import utilities.DataObject;

/**
 * Simple struct to contain:
 * 		descriptor list
 * 		column for propensities
 * 		column for test (to see if the prediction is right)
 * @author Benjy Strauss
 *
 */

public class DataPredLoc extends DataObject implements SchemaColumn {
	private static final long serialVersionUID = 1L;
	//list of descriptors
	public final Descriptor[] descriptors;
	//list of predicted propensities
	public final int predColLoc;
	//list of data to test propensities
	public final int testColLoc;
	
	/**
	 * 
	 * @param descriptors
	 * @param predColLoc
	 * @param testColLoc
	 */
	DataPredLoc(Descriptor[] descriptors, int predColLoc, int testColLoc) { 
		this.descriptors = descriptors;
		this.predColLoc = predColLoc;
		this.testColLoc = testColLoc;
	}
	
	public boolean equals(Object other) {
		if(other instanceof DataPredLoc) {
			DataPredLoc otherPredLoc = (DataPredLoc) other;
			
			for(Descriptor d: descriptors) {
				boolean reject = true;
				for(Descriptor d2: otherPredLoc.descriptors) {
					if(d == d2) { reject = false; }
				}
				if(reject) { return false; }
			}		
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder("DataPredLoc: (");
		for(Descriptor d: descriptors) {
			out.append(d+",");
		}
		out.setLength(out.length()-1);
		out.append(")");
		return out.toString();
	}
}
