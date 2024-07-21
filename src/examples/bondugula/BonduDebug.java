package examples.bondugula;

import java.io.FileNotFoundException;
import java.util.List;

import assist.util.LabeledList;
import biology.amino.AminoPosition;
import biology.amino.ResidueConfig;
import biology.amino.SecondaryStructure;
import biology.descriptor.VKPred;
import biology.protein.AminoChain;
import biology.protein.MultiChain;
import install.Installer;
import modules.descriptor.vkbat.Vkbat;
import modules.descriptor.vkbat.control.PredOptions;
import project.Project;
import project.ProteinDataset;
import system.Instruction;
import system.SwiPred;
import tools.DataSource;
import tools.writer.csv.CSVWriterFactory;
import tools.writer.csv.DescriptorCSVWriter3;
import utilities.LocalToolBase;

/**
 * WARNING: One-off script!  Do not try to run!
 * Add positive cases in learning set and in test set
 * 
 * Note: need to use PDB files
 * 
 * Discovery:
 * 		BioJava's aligner does NOT necessarily align identical chains in the same way
 * 
 * TODO database of missing ligands
 * 
 * @author Benjy Strauss
 *
 */

public class BonduDebug extends LocalToolBase {
	
	public static void main(String[] args) throws Exception {
		Installer.main("-no-dssp");
		
		try {
			bondLearn();
			//tempTest();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.exit(0);
	}

	private static void bondLearn() throws FileNotFoundException {
		SwiPred.getShell().setFastaSrc(DataSource.RCSB_PDB);
		qp("Using RCSB-PDB");
		
		PredOptions.preprocessChouFasman = true;
		SwiPred.askUserForHelp = false;
		
		Project rp = new Project("bon-debug", "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		SwiPred.execute(new Instruction("dataset -name=bon-lim"));
		
		SwiPred.execute(new Instruction("save"));
		//cluster bondugula
		qp("Clustering bondugula");
		SwiPred.execute(new Instruction("cluster -p=bon-lim -m -file=input/bon1-lim.tsv"));
		SwiPred.execute(new Instruction("save"));
		
		ProteinDataset pp = (ProteinDataset) SwiPred.getProject().get("bon-lim");
		MultiChain mc = (MultiChain) pp.get(0);
		qp(mc);
		/*qp(mc.length());
		System.exit(0);*/
		
		//assign isUnstruct
		/*qp("Assigning isUnstruct");
		SwiPred.execute(new Instruction("isu -p=bon-lim"));
		SwiPred.execute(new Instruction("save"));

		qp("Assigning entropy");
		SwiPred.execute(new Instruction("get-s -p=bon-lim"));
		SwiPred.execute(new Instruction("save"));
		
		qp("Assigning residue charge");
		SwiPred.execute(new Instruction("charge -p=bon-lim"));
		SwiPred.execute(new Instruction("save"));
		//qp(((AminoPosition) SwiPred.getProject().learn().dataSet().get(0).get(0)).residueType());
		*/
		
		List<VKPred> algorithms = new LabeledList<VKPred>();
		algorithms.add(VKPred.gor4);
		//algorithms.add(VKPred.gor4);
		//algorithms.add(VKPred.gor3);
		//algorithms.add(VKPred.psipred);
		//algorithms.add(VKPred.jnet);
		//algorithms.add(VKPred.SSPRO_5);
		//algorithms.add(VKPred.dsc);
		
		qp("Assigning all local vkbat {Chou-Fasman, DSC, GOR-IV, JNET, PSIPred, SSpro5.2}");
		Vkbat.assign((ProteinDataset) SwiPred.getProject().get("bon-lim"), algorithms, false);
		//SwiPred.execute(new Instruction("save"));*/
		
		//CSVWriter2 writer = CSVWriterFactory.getSwitchAndDescriptorWriter();
		//qp(writer.getColumnList());
		qp("Writing Data");
		DescriptorCSVWriter3 writer = CSVWriterFactory.getBatchWriter();
		writer.writeData("output/s8-bon-output-debug.csv", (ProteinDataset) SwiPred.getProject().get("bon-lim"));

		qp("Complete!");
		System.exit(0);
	}
}
