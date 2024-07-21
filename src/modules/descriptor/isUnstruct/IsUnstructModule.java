package modules.descriptor.isUnstruct;

import java.util.List;

import assist.util.LabeledList;
import biology.amino.AminoAcid;
import biology.amino.AminoTools;
import biology.amino.Aminoid;
import biology.descriptor.DescriptorType;
import biology.exceptions.EmptyChainException;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import biology.protein.ChainFactory;
import biology.protein.MultiChain;
import biology.protein.ProteinChain;
import biology.tools.SequenceAligner;
//import javafx.event.ActionEvent;
import modules.descriptor.DescriptorAssignmentModule;
import modules.descriptor.isUnstruct.prog.IsUnstruct;
import project.ProteinDataset;
import system.Instruction;
//import system.SwiPred;
import utilities.LocalToolBase;

/**
 * Module to assign IsUnstruct Descriptor (Lobanov and Galzitskaya)
 * 
 * @author Benjy Strauss
 *
 */

public final class IsUnstructModule extends DescriptorAssignmentModule {
	private IsUnstructModule() {}
	
	public static void assign(Instruction instr, ProteinDataset... projects) {
		LabeledList<ProteinDataset> dataList = new LabeledList<ProteinDataset>();
		dataList.addAll(projects);
		assign(instr, projects);
	}
	
	/**
	 * Assign IsUnstruct to a project
	 * @param instr: instruction on how to assign IsUnstruct
	 * @param project: project to assign IsUnstruct to
	 */
	public static void assign(Instruction instr, List<ProteinDataset> projects) {
		boolean useDom = instr.hasArgumentNamed(true, "-d", "-dom");
		
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					if(instr.override || !chain.getMetaData().has_isunstruct) {
						if(chain instanceof MultiChain) {
							assignMulti(chain, useDom);
						} else {
							assign(chain);
						}
					}
				}
			}
		}
	}

	/**
	 * Assigns isUnstruct data to a ProteinChain object
	 * This method will fail if you try to assign isUnstruct data to a ProteinChain without sequence data
	 * To get a ProteinChain object with isUnstruct data, call "isUnstruct()" instead.
	 * 
	 * @param chain: the chain to assign isUnstruct values to
	 * @param regionMatchLength: The length of the region to match when aligning the generated chain with the chain passed in
	 * @param minResiduesPresent: The minimum residues that must be present in the region to match when aligning
	 * @return: The passed in object, now with isUnstruct values
	 * @throws ResidueAlignmentException: If the ProteinChain's name does not match the ProteinChain's sequence or if the ProteinChain doesn't have any sequence data
	 */
	public static void assign(AminoChain<?> chain) {
		String aminoSeq;
		
		try {
			aminoSeq = FastaCrafter.textSequenceNonNull(chain);
		} catch (Exception e) {
			//shouldn't happen, but if it does, print an error and exit.
			qerr("IsUnstruct: Exception occured in sequence determination for "+chain.id());
			e.printStackTrace();
			return;
		}
		
		if(aminoSeq.length() == 0) {
			throw new EmptyChainException("Chain: " + chain.id().standard() + " has no non-null residues!");
		}
		
		double plps[] = IsUnstruct.getPLPs(aminoSeq);
		int offset = 0;
		
		for(int index = 0; index < chain.size(); ++index) {
			if(chain.get(index) != null && chain.get(index).toChar() != '_') {
				
				try {
					if(chain.get(index) instanceof Aminoid) {
						((Aminoid) chain.get(index)).setDescriptor(DescriptorType.ISUNSTRUCT.toString(), plps[index-offset]);
					}
				} catch (ArrayIndexOutOfBoundsException AIOOBE) {
					error("DEBUG: InUnstructModule.assign()\nDEBUG: chain: " + chain.id().standard() +
							"\nDEBUG: chain.toSequence(): " + chain.toSequence());
					AIOOBE.printStackTrace();
					System.exit(ERR_NEED_DEBUG);
				}
			} else {
				++offset;
			}
		}
		
		chain.getMetaData().has_isunstruct = true;
	}

	private static void assignMulti(AminoChain<?> chain, boolean useDom) {
		if(useDom) {
			//make a ProteinChain with the data of this chain
			ProteinChain ref = new ProteinChain();
			ChainFactory.copyChainData(chain, ref);
			
			char[] origSeq = chain.toSequence().toCharArray();
			for(char ch: origSeq) {
				ref.add(new AminoAcid(ch));
			}
			
			IsUnstructModule.assign(ref);
			
			//TODO untested, but seems to always work...
			SequenceAligner.modifyToMatchSequence(ref, chain.toSequence());
			//comment out these lines after testing
			qp("MultiDataSet: DEBUG: " + ref.toSequence());
			qp("MultiDataSet: DEBUG: " + chain.toSequence());
			
			//copy isunstruct values
			for(int index = 0; index < chain.size(); ++index) {
				if(ref.get(index) != null && chain.get(index) instanceof Aminoid) {
					if(!Double.isNaN(((Aminoid) ref.get(index)).getDescriptor(DescriptorType.ISUNSTRUCT))) {
						AminoTools.copyDescriptor((Aminoid) ref.get(index), (Aminoid) chain.get(index), DescriptorType.ISUNSTRUCT);
					}
				}
			}
				
		} else {
			if(chain.size() == 0) {
				LocalToolBase.error("ERROR Chain: " + chain.id() + " is empty!");
				return;
			}
			
			IsUnstructModule.assign(chain);
		}
	}
	
	/*@Override
	public void handle(ActionEvent event) {
		if(SwiPred.getProject() != null) {
			//just assume all data sets are multi-datasets, if not this option does nothing
			IsUnstructOptionForm form = new IsUnstructOptionForm(true);
			runPopup(form);
			if(form.isOK()) {
				boolean useDom = form.useDom();
				boolean override = form.override();
				
				for(ProteinProject pp: SwiPred.getShell().getRelevantProjects(form)) {
					if(pp != null && pp.dataSet() != null) {
						for(AminoChain<?> chain: pp.dataSet()) {
							if(override || !chain.getMetaData().has_isunstruct) {
								if(chain instanceof MultiChain) {
									assignMulti(chain, useDom);
								} else {
									assign(chain);
								}
							}
						}
					}
				}
			}
		} else {
			guiError("Error! No Project!");
		}
	}*/
}
