package examples.bondugula;

import java.io.FileNotFoundException;
import java.util.List;

import assist.util.LabeledList;
import biology.amino.AminoAcid;
import biology.descriptor.VKPred;
import biology.molecule.types.AminoType;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import install.Installer;
import modules.descriptor.vkbat.Vkbat;
import modules.descriptor.vkbat.control.PredOptions;
import project.Project;
import project.ProteinDataset;
import system.Instruction;
import system.SwiPred;
import tools.DataSource;
import tools.download.fasta.PDB_Downloader;
import tools.reader.fasta.pdb.PDB_HashReader;
import utilities.LocalToolBase;

/**
 * WARNING: One-off script!  Do not try to run!
 * Add positive cases in learning set and in test set
 * 
 * Note: need to use PDB files
 * TODO: ensure all chains work with descriptors!
 * 
 * Chou-Fasman doesn't handle X residues
 * GOR-3 doesn't handle X residues
 * 
 * @author Benjy Strauss
 *
 */

public class BonduDebugSingle extends LocalToolBase {
	
	public static void main(String[] args) throws Exception {
		Installer.main("-no-dssp");
		
		ChainID id = new ChainID();
		id.setProtein("1FNL");
		id.setChain("A");
		
		PDB_Downloader.quickDownload(id);
		PDB_HashReader reader = new PDB_HashReader(id);
		reader.readPDB();
		reader.applyDSSP();
		ProteinChain chain = reader.toChain();
		
		qp("*"+chain.toSequence());
		
		//chain.add(0, new BioMolecule(Ligand.Water));
		chain.add(0, new AminoAcid(AminoType.INVALID));
		//chain1.add(22, new AminoAcid(AminoType.Ornithine));
		
		List<VKPred> algorithms = new LabeledList<VKPred>();
		algorithms.add(VKPred.CHOU_FASMAN);
		//algorithms.add(VKPred.gor3);
		//algorithms.add(VKPred.gor4);
		//algorithms.add(VKPred.psipred);
		//algorithms.add(VKPred.jnet);
		//algorithms.add(VKPred.SSPRO_5);
		//algorithms.add(VKPred.dsc);
		qp("<0>");
		for(VKPred pred: algorithms) {
			//qp("Working on: "+pred);
			//Vkbat.assign(chain1, pred, true);
			Vkbat.assign(chain, pred, true);
		}
		qp("<1>");
		AminoAcid aa = (AminoAcid) chain.get(5);
		//qp(aa.getVKPrediction(VKPred.gor3));
		
		aa = (AminoAcid) chain.get(20);
		//qp(aa.getVKPrediction(VKPred.gor3));
		//qp(aa.getVKPrediction(VKPred.gor4));
		
		System.exit(0);
	}
	
	private static void bondLearn() throws FileNotFoundException {
		SwiPred.getShell().setFastaSrc(DataSource.RCSB_PDB);
		qp("Using RCSB-PDB, filled in with Uniprot");
		
		PredOptions.preprocessChouFasman = true;
		SwiPred.askUserForHelp = false;
		
		Project rp = new Project("bondugula2", "benjynstrauss@gmail.com");
		SwiPred.getShell().setProject(rp);
		SwiPred.execute(new Instruction("save"));
		
		//cluster bondugula
		qp("Clustering bondugula");
		SwiPred.execute(new Instruction("cluster -l -fill-missing bon-lim.tsv"));
		SwiPred.execute(new Instruction("save"));
		
		//generate the multi-data set from clusters
		qp("Making multi-dataset");
		SwiPred.execute(new Instruction("data-set -l -m"));
		SwiPred.execute(new Instruction("save"));
		
		//assign isUnstruct
		qp("Assigning isUnstruct");
		SwiPred.execute(new Instruction("isu -l"));
		SwiPred.execute(new Instruction("save"));

		//assign entropy and charge
		qp("Assigning entropy");
		SwiPred.execute(new Instruction("get-s"));
		SwiPred.execute(new Instruction("save"));
		
		qp("Assigning residue charge");
		SwiPred.execute(new Instruction("charge"));
		SwiPred.execute(new Instruction("save"));
		
		List<VKPred> algorithms = new LabeledList<VKPred>();
		algorithms.add(VKPred.CHOU_FASMAN);
		algorithms.add(VKPred.gor4);
		algorithms.add(VKPred.psipred);
		algorithms.add(VKPred.jnet);
		algorithms.add(VKPred.SSPRO_5);
		algorithms.add(VKPred.dsc);
		
		qp("Assigning all local vkbat {Chou-Fasman, DSC, GOR-IV, JNET, PSIPred, SSpro5.2}");
		Vkbat.assign((ProteinDataset) SwiPred.getProject().get("bon-lim"), algorithms, false);
		SwiPred.execute(new Instruction("save"));
		
		//CSVWriter2 writer = CSVWriterFactory.getSwitchAndDescriptorWriter();
		//qp(writer.getColumnList());
		qp("Writing Data");
		SwiPred.execute(new Instruction("write -l -mode=basic"));
		qp("Complete!");
		System.exit(0);
	}
}
