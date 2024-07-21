package examples.bondugula;

import java.io.FileNotFoundException;
import java.util.List;

import assist.util.LabeledList;
import biology.descriptor.VKPred;
import install.Installer;
import modules.descriptor.vkbat.Vkbat;
import modules.descriptor.vkbat.control.PredOptions;
import project.Project;
import project.ProteinDataset;
import system.Instruction;
import system.SwiPred;
import tools.DataSource;
import utilities.LocalToolBase;
import utilities.SwiPredLogger;

/**
 * WARNING: One-off script!  Do not try to run!
 * Add positive cases in learning set and in test set
 * 
 * 
 * Why do we see "Error: files/fasta/pdb/4V4P.pdb (No such file or directory)"
 * 
 * 
 * 
 * @author Benjy Strauss
 *
 */
//@SuppressWarnings("unused")
public class Bondu3 extends LocalToolBase {
	
	public static void main(String[] args) throws Exception {
		Installer.main("-no-dssp");
		SwiPredLogger.clearLog();
		
		try {
			bondLearn();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.exit(0);
	}
	
	private static void bondLearn() throws FileNotFoundException {
		SwiPred.getShell().setFastaSrc(DataSource.RCSB_PDB);
		qp("Using RCSB-PDB, filled in with Uniprot");
		
		PredOptions.preprocessChouFasman = true;
		SwiPred.askUserForHelp = false;
		
		Project rp = new Project("bondugula3", "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		SwiPred.execute(new Instruction("dataset -name=bondugula"));
		SwiPred.execute(new Instruction("save"));
		
		//cluster bondugula
		qp("Clustering bondugula");
		SwiPred.execute(new Instruction("cluster -p=bondugula -file=bon.tsv"));
		SwiPred.execute(new Instruction("save"));
		
		//assign isUnstruct
		qp("Assigning isUnstruct");
		SwiPred.execute("isu");
		SwiPred.execute("save");

		//assign entropy and charge
		qp("Assigning entropy");
		SwiPred.execute("get-s");
		SwiPred.execute("save");
		
		qp("Assigning residue charge");
		SwiPred.execute("charge");
		SwiPred.execute("save");
		
		List<VKPred> algorithms = new LabeledList<VKPred>();
		algorithms.add(VKPred.CHOU_FASMAN);
		algorithms.add(VKPred.gor4);
		algorithms.add(VKPred.psipred);
		algorithms.add(VKPred.jnet);
		algorithms.add(VKPred.SSPRO_5);
		algorithms.add(VKPred.dsc);
		
		qp("Assigning all local vkbat {Chou-Fasman, DSC, GOR-IV, JNET, PSIPred, SSpro5.2}");
		Vkbat.assign((ProteinDataset) SwiPred.getProject().get("bondugula"), algorithms, false);
		SwiPred.execute(new Instruction("save"));
		
		//CSVWriter2 writer = CSVWriterFactory.getSwitchAndDescriptorWriter();
		//qp(writer.getColumnList());
		qp("Writing Data");
		SwiPred.execute("write -l -mode=basic");
		qp("Complete!");
		System.exit(0);
	}
}
