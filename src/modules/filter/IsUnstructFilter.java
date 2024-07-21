package modules.filter;

import biology.amino.Aminoid;
import biology.descriptor.DescriptorType;
import biology.protein.AminoChain;
import modules.descriptor.isUnstruct.IsUnstructModule;
import project.ProteinDataset;
import utilities.LocalToolBase;

/**
 * Filters by IsUnstruct values
 * @author Benjamin Strauss
 *
 */

public class IsUnstructFilter extends LocalToolBase {
	
	public static void filterTerminals(ProteinDataset project, double threshhold) {
		//ensure isUnstruct is assigned
		for(AminoChain<?> chain: project) {
			IsUnstructModule.assign(chain);
		}
		
		//delete disordered N and C terminals
		for(AminoChain<?> chain: project) {
			try {
				while(chain.get(0) == null || chain.get(0) instanceof Aminoid) {
					if(((Aminoid) chain.get(0)).getDescriptor(DescriptorType.ISUNSTRUCT.toString()) > threshhold) {
						chain.remove(0);
					}
				}
				
				for(int index = chain.size()-1; index >= 0; --index) {
					if(chain.get(index) == null || chain.get(index) instanceof Aminoid) {
						if(((Aminoid) chain.get(0)).getDescriptor(DescriptorType.ISUNSTRUCT.toString()) > threshhold) {
							chain.remove(index);
						} else {
							break;
						}
					}
				}
			} catch (IndexOutOfBoundsException IOOBE) {
				project.remove(chain);
			}
			//remove all chains of length 0
			if(chain.length() == 0) {
				project.remove(chain);
			}
		}
	}
}
