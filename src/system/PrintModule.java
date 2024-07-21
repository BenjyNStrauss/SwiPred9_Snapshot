package system;

import java.util.List;

import assist.util.LabeledList;
import biology.protein.AminoChain;
import project.ProteinDataset;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class PrintModule extends LocalToolBase {
	private static final char TAB = '\t';
	
	public static void printDataset(Instruction instr, ProteinDataset... projects) {
		LabeledList<ProteinDataset> dataList = new LabeledList<ProteinDataset>();
		dataList.addAll(projects);
		printDataset(instr, projects);
	}
	
	/**
	 * 
	 * @param instr
	 * @param projects
	 */
	public static void printDataset(Instruction instr, List<ProteinDataset> projects) {
		for(ProteinDataset project: projects) {
			qp("[[ProteinDataset: " + project.label()+"]]");
			for(AminoChain<?> chain: project) {
				qp(TAB+chain.toString());
			}
		}
	}
	
}
