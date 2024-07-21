package dev;

import java.io.File;

import assist.util.LabeledList;
import biology.descriptor.DescriptorType;
import biology.descriptor.Identifier;
import biology.descriptor.VKPred;
import biology.protein.AminoChain;
import biology.protein.ProteinChain;
import install.DirectoryManager;
import modules.descriptor.vkbat.Vkbat;
import modules.descriptor.vkbat.control.VKThreadClassic;
import tools.reader.fasta.SequenceReader;
import tools.writer.csv.DescriptorCSVWriter3;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class LegacyMains extends LocalToolBase {
	
	public static void vkbat_main(String... args) {
		//Installer.main("-silent", "-no-dssp");
		DirectoryManager.makeFolders();
		
		//Setup writer
		DescriptorCSVWriter3 writer = new DescriptorCSVWriter3();
		writer.add(Identifier.UNIPROT_ID);
		writer.add(Identifier.RESIDUE_CODE_CONSENSUS);
		writer.add(Identifier.RESIDUE_NUMBER);
		for(VKPred vkp: VKThreadClassic.getClassicAlgorithms()) {
			writer.add(vkp);
		}
		
		writer.add(DescriptorType.VKBAT);
		LabeledList<AminoChain<?>> chains = new LabeledList<AminoChain<?>>();
		
		for(String arg: args) {
			ProteinChain chain = SequenceReader.readChain(new File(arg));
			for(VKPred vkp: VKThreadClassic.getClassicAlgorithms()) {
				Vkbat.assign(chain, vkp, true);
			}
			chains.add(chain);
		}
		
		writer.writeData("VK-output.txt", (Iterable<AminoChain<?>>) chains);
	}
}
