package examples.acetylSirt;

import java.io.FileNotFoundException;

import assist.script.PythonScript;
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
 * 
 * 
 * TODO: 
 * 		(1) Add ESM into SwiPred Pipeline
 * 		(2) Cluster based on PDB hashes
 * 
 * 
 * @author Benjy Strauss
 *
 */
//@SuppressWarnings("unused")
public class AceSirESM extends LocalToolBase {
	//public static double batchNo = -1;
	
	public static void main(String[] args) throws Exception {
		//qp(PythonScript.getDefaultPythonPath());
		PythonScript.setPythonPath("/Users/bns/miniconda3/bin/python");
		//batchNo = Double.parseDouble(args[0]);
		//qp("Running Batch #"+batchNo);
		
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
		ESM_Model in_use = ESM_Model.esm_msa1_t12_100M_UR50S;
		SwiPred.execute(new Instruction("setfasta pdb"));
		qp("Using RCSB-PDB");
		
		//PredOptions.preprocessChouFasman = true;
		//SwiPredNotebook.scriptMode = true;
		
		Project rp = new Project("bondugula-esm", "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		SwiPred.execute(new Instruction("dataset -name=acetyl-sirt"));
		SwiPred.execute("save");
		
		//cluster bondugula
		qp("Clustering Acetyl-Sirt (for ESM)");
		SwiPred.execute(new Instruction("cluster -p=acetyl-sirt -file=input/acetyl-sirt-2022.txt"));
		SwiPred.execute("save");
		
		qp("Assigning ESM");
		SwiPred.execute(new Instruction("esm -p=acetyl-sirt -model="+in_use.toString()));
		SwiPred.execute("save");
		
		//CSVWriter2 writer = CSVWriterFactory.getSwitchAndDescriptorWriter();
		//qp(writer.getColumnList());
		qp("Writing Data");
		DescriptorCSVWriter4 writer = CSVWriterFactory.getTransformerWriter(in_use);
		writer.writeData("output/s9-acetyl-sirt-esm.csv", (ProteinDataset) SwiPred.getProject().get("acetyl-sirt"));
		
		qp("Complete!");
		System.exit(0);
	}
}
