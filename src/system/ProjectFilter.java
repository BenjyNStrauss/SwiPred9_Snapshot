package system;

import java.util.Set;

import assist.util.LabeledList;
import project.Project;
import project.ProteinDataset;
import project.SwiPredDataset;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class ProjectFilter extends LocalToolBase {
	
	protected static LabeledList<SwiPredDataset> applicable(Project active, Instruction ins) {
		LabeledList<SwiPredDataset> applicableList = new LabeledList<SwiPredDataset>("relevant");
		
		Set<String> results = ins.getArgumentNamed(ParamTable.PROJECT_CODES);
		
		for(String str: results) {
			if(active.get(str) != null) {
				applicableList.add(active.get(str));
			}
		}
		
		for(String str: ins.values()) {
			if(active.get(str) != null) {
				applicableList.add(active.get(str));
			}
		}
		
		if(applicableList.size() == 0) { error("Warning: no projects selected!"); }
		
		return applicableList;
	}
	
	protected static LabeledList<ProteinDataset> applicableProt(Project active, Instruction ins) {
		LabeledList<ProteinDataset> temp = new LabeledList<ProteinDataset>();
		
		for(SwiPredDataset spds: applicable(active, ins)) {
			if(spds instanceof ProteinDataset) {
				temp.add((ProteinDataset) spds);
			}
		}
		
		if(temp.size() == 0) { error("Warning: no projects selected were applicable!"); }
		
		return temp;
	}
}
