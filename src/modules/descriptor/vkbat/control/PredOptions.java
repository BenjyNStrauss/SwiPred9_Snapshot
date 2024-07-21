package modules.descriptor.vkbat.control;

import utilities.LocalToolBase;

/**
 * Contains variables for modifying behavior of VKbat algorithms
 * @author Benjamin Strauss
 *
 */

public final class PredOptions extends LocalToolBase {
	
	//pre-process Chou-Fasman input to turn non-standard residues to Alanine
	public static boolean preprocessChouFasman = false;
	
	//filter level used by DSC algorithm
	public static int dsc_filterLevel = 1;
	
	//suppress warning messages from the JNET algorithm
	public static boolean suppressWarningsJNET = true;

	private PredOptions() { }
}
