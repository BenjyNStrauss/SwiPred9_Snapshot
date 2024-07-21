package biology;

import tools.Lookup;
import utilities.DataObject;
import utilities.SwiPredObject;

/**
 * A BioObject is represents anything that is made of biological matter
 * 
 * @author Benjy Strauss
 *
 */

public abstract class BioObject extends DataObject implements SwiPredObject {
	private static final long serialVersionUID = 1L;
	//used for writing FASTAs
	public static final String STATIC_FASTA_HEADER = "|PDBID|CHAIN|SEQUENCE";
	
	//The default weight of a disordered switch to use in a "weighted" data set file
	public static final double DISORDERED_WEIGHT = 0.5;
	
	public static final String NORMALIZE = "norm";
	public static final String STANDARD_ATOM_KEYS[] = Lookup.ATOM_CODES;
	
	//mass of the BioObject
	protected double mass;
	
	/**
	 * Default Constructor
	 */
	protected BioObject() { }
	
	/**
	 * Returns the implied file name: this string is used as the default file name to save
	 * the object as
	 * @return String to use as default file name
	 */
	public String saveString() { return toString(); }
}
