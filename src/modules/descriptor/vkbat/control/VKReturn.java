package modules.descriptor.vkbat.control;

import biology.descriptor.VKPred;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class VKReturn extends LocalToolBase {
	public final VKPred algorithm;
	public String prediction;
	public boolean DSC_remove_isolated_error;
	
	public VKReturn(VKPred algorithm) {
		this.algorithm = algorithm;
	}
	
}
