package chem;

import utilities.DataObject;

/**
 * SuperClass for all objects that represents chemical objects.
 * 
 * @author Benjy Strauss
 *
 */

public abstract class ChemObject extends DataObject {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @return: what we should call this object if we save it.
	 */
	public abstract String defaultFileName();
	
}
