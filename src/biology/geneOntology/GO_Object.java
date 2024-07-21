package biology.geneOntology;

import assist.ActuallyCloneable;
import utilities.DataObject;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class GO_Object extends DataObject implements ActuallyCloneable {

	private static final long serialVersionUID = 1L;
	
	public GO_Object() {
		
	}
	
	public abstract GO_Object clone();

}
