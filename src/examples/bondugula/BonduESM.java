package examples.bondugula;

import java.io.FileNotFoundException;

import install.Installer;
import modules.encode.esm.ESM_Model;
import project.Project;
import project.ProteinDataset;
import system.Instruction;
import system.SwiPred;
import tools.writer.csv.CSVWriterFactory;
import tools.writer.csv.DescriptorCSVWriter4;
import utilities.LocalToolBase;
import utilities.SwiPredLogger;

/**
 * WARNING: One-off script!  Do not try to run!
 * Apply ESM descriptors to see if we can predict secondary structure variability?
 * 
 * SwiPred8/SwiPred8-bon3-part.jar
 * 
 * TODO: 
 * 		(1) Add ESM into SwiPred Pipeline
 * 		(2) Cluster based on PDB hashes
 * 
 * "/usr/bin/pip3 install fair-esm"
 * default python pip3 = usr/bin/pip3
 * 
 * 
 * @author Benjy Strauss
 *
 */
//@SuppressWarnings("unused")
public class BonduESM extends LocalToolBase {
	
	public static void main(String[] args) throws Exception {
		final int batchNo = Integer.parseInt(args[0]);
		boolean exit_on_completion = true;
		if(args.length > 1 && args[1].equals("-no-exit")) {
			exit_on_completion = false;
		}
		
		qp("Running Batch #"+batchNo);
		
		Installer.main("-no-dssp");
		SwiPredLogger.clearLog();
		
		try {
			bondugulaBatch(batchNo);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		if(exit_on_completion) {
			System.exit(0);
		}
	}
	
	/*private static void bondLearn() throws FileNotFoundException {
		ESM_Model in_use = ESM_Model.esm1_t34_670M_UR50D;
		SwiPred.execute(new Instruction("setfasta pdb"));
		qp("Using RCSB-PDB");
		
		//PredOptions.preprocessChouFasman = true;
		//SwiPredNotebook.scriptMode = true;
		
		Project rp = new Project("bondugula-esm", "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		SwiPred.execute(new Instruction("dataset -name=bondugula-esm"));
		SwiPred.execute("save");
		
		//cluster bondugula
		qp("Clustering Bondugula (for ESM)");
		SwiPred.execute(new Instruction("cluster -p=bondugula -file=input/bon.tsv"));
		SwiPred.execute("save");
		
		qp("Assigning ESM");
		SwiPred.execute(new Instruction("esm -p=bondugula -model="+in_use.toString()));
		SwiPred.execute("save");
		
		//CSVWriter2 writer = CSVWriterFactory.getSwitchAndDescriptorWriter();
		//qp(writer.getColumnList());
		qp("Writing Data");
		DescriptorCSVWriter4 writer = CSVWriterFactory.getTransformerWriter(in_use);
		writer.writeData("output/s8-bon-output-esm.csv", (ProteinDataset) SwiPred.getProject().get("bondugula"));
		
		qp("Complete!");
		System.exit(0);
	}*/
	
	private static void bondugulaBatch(int batch) throws FileNotFoundException {
		ESM_Model in_use = BonduESM_LaptopMain.MODEL;
		SwiPred.execute(new Instruction("setfasta pdb"));
		qp("Using RCSB-PDB");
		
		//PredOptions.preprocessChouFasman = true;
		//SwiPredNotebook.scriptMode = true;
		
		Project rp = new Project("bondugula-esm", "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		String projectName = "bondugula-esm-"+batch;
		SwiPred.execute(new Instruction("dataset -name="+projectName));
		SwiPred.execute("save");
		
		//cluster bondugula
		qp("Clustering Bondugula (for ESM)");
		SwiPred.execute(new Instruction("cluster -p="+projectName+" -file=input/bondugula_"+batch+".tsv"));
		SwiPred.execute("save");
		
		qp("Assigning ESM");
		SwiPred.execute(new Instruction("esm -p="+projectName+" -model="+in_use.toString()));
		SwiPred.execute("save");
		
		//CSVWriter2 writer = CSVWriterFactory.getSwitchAndDescriptorWriter();
		//qp(writer.getColumnList());
		qp("Writing Data");
		DescriptorCSVWriter4 writer = CSVWriterFactory.getTransformerWriter(in_use);
		writer.writeData("output/s9-bondugula-esm-output-"+batch+".csv", (ProteinDataset) SwiPred.getProject().get(projectName));
		
		qp("Complete!");
	}
}
