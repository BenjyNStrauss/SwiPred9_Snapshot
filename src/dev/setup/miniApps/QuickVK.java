package dev.setup.miniApps;

import assist.util.LabeledList;
import biology.amino.Aminoid;
import biology.amino.BioMolecule;
import biology.descriptor.VKPred;
import biology.protein.ChainFactory;
import biology.protein.ProteinChain;
import install.DirectoryManager;
import modules.descriptor.vkbat.Vkbat;
import modules.descriptor.vkbat.control.VKThreadClassic;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class QuickVK extends LocalToolBase {
	
	public static void main(String... args) {
		//Installer.main("-silent", "-no-dssp");
		DirectoryManager.makeFolders();
		
		for(String arg: args) {
			ProteinChain chain = ChainFactory.makeDummy(arg);
			
			for(VKPred vkp: VKThreadClassic.getClassicAlgorithms()) {
				Vkbat.assign(chain, vkp, true);
			}
			
			LabeledList<Double> preds = new LabeledList<Double>();
			for(BioMolecule bMol: chain) {
				if(bMol instanceof Aminoid) {
					preds.add(((Aminoid) bMol).vkbat());
				} else {
					preds.add(Double.NaN);
				}
			}
			qp(preds);
		}
		
	}
}
