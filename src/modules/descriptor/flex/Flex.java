package modules.descriptor.flex;

import java.util.List;

import assist.util.LabeledList;
import biology.protein.AminoChain;
//import javafx.event.ActionEvent;
import modules.descriptor.DescriptorAssignmentModule;
import project.ProteinDataset;
import system.Instruction;
//import system.SwiPred;
import utilities.exceptions.DataRetrievalException;

/**
 * Module to assign Flex Descriptor
 * @author Benjy Strauss
 *
 */

public class Flex extends DescriptorAssignmentModule {
	
	public Flex() { }
	
	public static void assign(Instruction instr, ProteinDataset... projects) {
		LabeledList<ProteinDataset> dataList = new LabeledList<ProteinDataset>();
		dataList.addAll(projects);
		assign(instr, projects);
	}
	
	public static void assign(Instruction instr, List<ProteinDataset> projects) {
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					if(instr.override || (!chain.getMetaData().has_flex)) {
						try {
							Flex.assign(chain);
						} catch (DataRetrievalException DRE) {
							qerr("Error for " + chain.id().standard() + ": " + DRE.getMessage());
						}
					}
					chain.getMetaData().has_flex = true;
				}
			}
		}
	}
	
	public static void assign(AminoChain<?> chain) throws DataRetrievalException {
		chain.getMetaData().has_flex = true;
		String queryData = PDBFlexInterface.getRMSDData(chain)[0];
		String values = queryData.substring(queryData.indexOf("[")+1, queryData.indexOf("]"));
		
		if(values.equals("")) {
			for(int index = 0; index < chain.size(); ++index) {
				chain.setFlexibility(index, Double.NaN);
			}
			throw new DataRetrievalException("Flexibility data could not be found.");
		}
		
		values += ",";
		
		for(int index = 0; index < chain.size(); ++index) {
			String thisDouble = values.substring(0, values.indexOf(","));
			values = values.substring(values.indexOf(",")+1);
			
			try {
				chain.setFlexibility(index, Double.parseDouble(thisDouble));
			} catch (NumberFormatException NFE) {
				chain.getMetaData().has_flex = false;
				throw new DataRetrievalException("Data Format Unrecognized");
			}
		}
	}
	
	/*@Override
	public void handle(ActionEvent event) {
		if(SwiPred.getProject() != null) {
			FlexOptionForm form = new FlexOptionForm();
			runPopup(form);
			if(form.isOK()) {
				boolean override = form.override();
				
				for(ProteinProject pp: SwiPred.getShell().getRelevantProjects(form)) {
					if(pp != null && pp.dataSet() != null) {
						for(AminoChain<?> chain: pp.dataSet()) {
							if(override || !chain.getMetaData().has_flex) {
								try {
									assign(chain);
								} catch (DataRetrievalException e) {
									error("Failed to assign flex to chain: " + chain.id().standard());
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
