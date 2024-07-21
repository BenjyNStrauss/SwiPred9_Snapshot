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
import tools.writer.csv.CSVWriterFactory;
import tools.writer.csv.DescriptorCSVWriter3;
import utilities.LocalToolBase;
import utilities.SwiPredLogger;

/**
 * WARNING: One-off script!  Do not try to run!
 * Add positive cases in learning set and in test set
 * 
 * SwiPred8/SwiPred8-bon2-part.jar
 * Why do we see "Error: files/fasta/pdb/4V4P.pdb (No such file or directory)"
 * 
 * 
 * 
 * @author Benjy Strauss
 *
 */
//@SuppressWarnings("unused")
public class Bondu3Segment extends LocalToolBase {
	public static double batchNo = -1;
	//private static final File NEW_CODES = new File("files/logs/new-pdb-codes.txt");
	
	public static void main(String[] args) throws Exception {
		batchNo = Double.parseDouble(args[0]);
		qp("Running Batch #"+batchNo);
		
		Installer.main("-no-dssp");
		SwiPredLogger.clearLog();
		
		try {
			bondLearn();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		System.exit(0);
	}
	
	private static void bondLearn() throws FileNotFoundException {
		SwiPred.execute(new Instruction("setfasta pdb"));
		qp("Using RCSB-PDB, filled in with Uniprot");
		
		PredOptions.preprocessChouFasman = true;
		SwiPred.askUserForHelp = false;
		
		String name = "bondugula_"+batchNo;
		
		Project rp = new Project("bondugula3-"+batchNo, "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		SwiPred.execute(new Instruction("dataset -name="+name));
		SwiPred.execute("save");
		
		//cluster bondugula
		qp("Clustering bondugula");
		qp("bon"+batchNo+".tsv");
		//note "-fill-missing" no longer does anything!
		SwiPred.execute(new Instruction("cluster -m -p="+name+" -file=input/bon"+batchNo+".tsv"));
		SwiPred.execute("save");
		
		//assign isUnstruct
		qp("Assigning isUnstruct");
		SwiPred.execute(new Instruction("isu -p="+name));
		SwiPred.execute("save");

		/*assign entropy and charge*/
		qp("Assigning entropy");
		SwiPred.execute(new Instruction("get-s -p="+name));
		SwiPred.execute("save");
		
		qp("Assigning residue charge");
		SwiPred.execute(new Instruction("charge -p="+name));
		SwiPred.execute("save");
		
		List<VKPred> algorithms = new LabeledList<VKPred>();
		algorithms.add(VKPred.CHOU_FASMAN);
		algorithms.add(VKPred.gor3);
		algorithms.add(VKPred.gor4);
		algorithms.add(VKPred.psipred);
		algorithms.add(VKPred.jnet);
		algorithms.add(VKPred.SSPRO_5);
		algorithms.add(VKPred.dsc);
		
		qp("Assigning all local vkbat {Chou-Fasman, DSC, GOR-IV, JNET, PSIPred, SSpro5.2}");
		Vkbat.assign((ProteinDataset) SwiPred.getProject().get(name), algorithms, true);
		SwiPred.execute("save");
		
		//CSVWriter2 writer = CSVWriterFactory.getSwitchAndDescriptorWriter();
		//qp(writer.getColumnList());
		qp("Writing Data");
		DescriptorCSVWriter3 writer = CSVWriterFactory.getBatchWriter();
		writer.writeData("output/s9-bon-output-"+batchNo+".csv", (ProteinDataset) SwiPred.getProject().get(name));
		
		qp("Complete!");
		System.exit(0);
	}
}
