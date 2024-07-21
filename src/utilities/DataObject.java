package utilities;

import java.io.Serializable;

/**
 * An object that holds data
 * @author Benjy Strauss
 *
 */

public abstract class DataObject extends LocalToolBase implements Serializable, SwiPredObject {
	private static final long serialVersionUID = 1L;
	
	protected DataObject() { }
	
	public String toString() {
		return "DataObject (" + super.toString() + ")";
	}
}
