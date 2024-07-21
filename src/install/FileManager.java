package install;

import java.io.File;

import assist.base.FileToolBase;
import assist.script.Script;
import assist.util.LabeledList;

/**
 * Manages all of the data files
 * No one should make instances of this class...
 * 
 * @author Benjy Strauss
 *
 */

public class FileManager extends Script {
	//public static final String CHARGE_DB_PATH = "files/JBioDataBase.db";
	public static final String SSPRO_MODELS_LIST = DirectoryManager.FILES_PREDICT_SSPRO + "/models.txt";
	public static final String RCSB_SS_DIS = DirectoryManager.FILES + "/ss_dis.txt";
	public static final String RCSB_SS_DIS_GZ = RCSB_SS_DIS + ".gz";
	
	private static final String PSIPRED_WEIGHTS_1 = "weights.dat";
	private static final String PSIPRED_WEIGHTS_2 = "weights.dat2";
	private static final String PSIPRED_WEIGHTS_3 = "weights.dat3";
	
	public static final String GOR3_DSSP = DirectoryManager.FILES_PREDICT_GOR3+"/dssp_info.txt";
	
	private static final String[] GOR_FILES = {
			GOR3_DSSP,
			DirectoryManager.FILES_PREDICT_GOR4+"/New_KS.267.obs",
			DirectoryManager.FILES_PREDICT_GOR4+"/New_KS.267.seq"
	};
	
	private static final String[] BIOJAVA_FILES = {
			"blosum30.txt", "blosum35.txt", "blosum40.txt", "blosum45.txt",
			"blosum50.txt", "blosum55.txt", "blosum60.txt", "blosum62.mat", 
			"blosum62.txt", "blosum65.txt", "blosum70.txt", "blosum75.txt", 
			"blosum80.txt", "blosum85.txt", "blosum90.txt", "blosum100.txt"
	};
	
	private static final String[] JNET_FILES = {
			"consnet-sources.txt",		"consnet-units.txt",		"consnet-weights.txt",
			"hmm1-sources.txt",			"hmm1-units.txt",			"hmm1-weights.txt",
			"hmm2-sources.txt",			"hmm2-units.txt",			"hmm2-weights.txt",
			"hmmsol0-sources.txt",		"hmmsol0-units.txt",		"hmmsol0-weights.txt",
			"hmmsol5-sources.txt",		"hmmsol5-units.txt",		"hmmsol5-weights.txt",
			"hmmsol25-sources.txt",		"hmmsol25-units.txt",		"hmmsol25-weights.txt",
			"net1-sources.txt",			"net1-units.txt",			"net1-weights.txt",
			"net1b-sources.txt",		"net1b-units.txt",			"net1b-weights.txt",
			"net2-sources.txt",			"net2-units.txt",			"net2-weights.txt",
			"net2b-sources.txt",		"net2b-units.txt",			"net2b-weights.txt",
			"psinet-sources.txt",		"psinet-units.txt",			"psinet-weights.txt",
			"psinet1-sources.txt",		"psinet1-units.txt",		"psinet1-weights.txt",
			"psinet1b-sources.txt",		"psinet1b-units.txt",		"psinet1b-weights.txt",
			"psinet2-sources.txt",		"psinet2-units.txt",		"psinet2-weights.txt",
			"psinet2b-sources.txt",		"psinet2b-units.txt",		"psinet2b-weights.txt",
			"psisol0-sources.txt",		"psisol0-units.txt",		"psisol0-weights.txt",
			"psisol5-sources.txt",		"psisol5-units.txt",		"psisol5-weights.txt",
			"psisol25-sources.txt",		"psisol25-units.txt",		"psisol25-weights.txt"
	};
	
	private static final String[] PSIPRED_FILES = {
			PSIPRED_WEIGHTS_1,	PSIPRED_WEIGHTS_2,	PSIPRED_WEIGHTS_3,	"weights_p2.dat",
			"weights_s.dat2",	"weights_s.dat3",	"weights_s.dat",					
	};
	
	private static final String[] SSPRO_PDB_FILES = { "pdb_full.fa", "pdb_full.ids", "pdb_full.ss" };
	
	private static final String[] SSPRO_UNIREF50 = { 
			"uniref50.00.phr",		"uniref50.00.pin",		"uniref50.00.psq",
			"uniref50.01.phr",		"uniref50.01.pin",		"uniref50.01.psq",
			"uniref50.02.phr",		"uniref50.02.pin",		"uniref50.02.psq",
			"uniref50.03.phr",		"uniref50.03.pin",		"uniref50.03.psq",
			"uniref50.04.phr",		"uniref50.04.pin",		"uniref50.04.psq",
			"uniref50.05.phr",		"uniref50.05.pin",		"uniref50.05.psq",
			"uniref50.06.phr",		"uniref50.06.pin",		"uniref50.06.psq",
			"uniref50.07.phr",		"uniref50.07.pin",		"uniref50.07.psq",
			"uniref50.08.phr",		"uniref50.08.pin",		"uniref50.08.psq",
			"uniref50.09.phr",		"uniref50.09.pin",		"uniref50.09.psq",
			"uniref50.pal"
	};
	
	@SuppressWarnings("unused")
	private static final String[] PROF_FILES = { 
			"Blast_blosum62.metric",
			"Maxhom_Blosum.metric",
			"Maxhom_GCG.metric",
			"Maxhom_McLachlan.metric",
			"Maxhom_Sec_Struc.metric",
			"Maxhom_Struc_IO.metric"
	};
	
	private static final String[] BLAST_FILES = {
			"VERSION",
			
			"bin/bl2seq",		"bin/blastall",		"bin/blastclust",		"bin/blastpgp",
			"bin/copymat",		"bin/fastacmd",		"bin/formatdb",			"bin/formatrpsdb",
			"bin/impala",		"bin/makemat",		"bin/megablast",		"bin/rpsblast",
			"bin/seedtop",		"bin/Text",
			
			"data/asn2ff.prt",				"data/BLOSUM45",			"data/BLOSUM62",				
			"data/BLOSUM80",				"data/bstdt.val",			"data/ecnum_ambiguous.txt",
			"data/ecnum_specific.txt",		"data/featdef.val",			"data/gc.val",
			"data/humrep.fsa",				"data/KSat.flt",			"data/KSchoth.flt",
			"data/KSgc.flt",				"data/KShopp.flt",			"data/KSkyte.flt",
			"data/KSpcc.mat",				"data/KSpur.flt",			"data/KSpyr.flt",
			"data/lineages.txt",			"data/makerpt.prt",			"data/objprt.prt",
			"data/organelle_products.prt",	"data/PAM30",				"data/PAM70",
			"data/product_rules.prt",		"data/pubkey.enc",			"data/seqcode.val",
			"data/sequin.hlp",				"data/sgmlbb.ent",			"data/taxlist.txt",
			"data/UniVec_Core.nhr",			"data/UniVec_Core.nin",		"data/UniVec_Core.nsq",
			"data/UniVec.nhr",				"data/UniVec.nin",			"data/UniVec.nsq"
	};
	
	/**
	 * Verifies all of the required files are in place
	 * @return: list of all of the missing program files
	 */
	public static String[] verifyFiles() {
		LabeledList<String> missingFiles = new LabeledList<String>();
		
		//if(!checkFile(CHARGE_DB_PATH)) { missingFiles.add(CHARGE_DB_PATH); }
		if(!checkFile(SSPRO_MODELS_LIST)) { generateSSproModelsFile(); }
		
		if(!checkFile(RCSB_SS_DIS)) { download_ss_dis(); }
		
		//verify all of the files BioJava needs to function
		for(String file: BIOJAVA_FILES) {
			String path = DirectoryManager.FILES_TOOLS_BIOJAVA + "/" + file;
			if(!checkFile(path)) { missingFiles.add(path); }
		}
		
		for(String file: GOR_FILES) {
			if(!checkFile(file)) { missingFiles.add(file); }
		}
		
		//verify all of the files JNET needs to function
		for(String file: JNET_FILES) {
			String path = DirectoryManager.FILES_PREDICT_JNET + "/" + file;
			if(!checkFile(path)) { missingFiles.add(path); }
		}
		
		//verify all of the files PSIPred needs to function
		for(String file: PSIPRED_FILES) {
			String path = DirectoryManager.FILES_PREDICT_PSIPRED + "/"  + file;
			if(!checkFile(path)) { missingFiles.add(path); }
		}
		
		//verify all of the SSpro models are present
		StringBuilder filenameBuilder = new StringBuilder();
		for(int index = 1; index < 101; ++index) {
			filenameBuilder.setLength(0);
			filenameBuilder.append(DirectoryManager.FILES_PREDICT_SSPRO_MODELS + "/model");
			if(index < 10) { filenameBuilder.append("0"); }
			if(index < 100) { filenameBuilder.append("0"); }
			filenameBuilder.append(index + ".brnn");
			String path = filenameBuilder.toString();
			if(!checkFile(path)) { missingFiles.add(path); }
		}
		
		//verify all of the files SSpro needs to function
		for(String file: SSPRO_PDB_FILES) {
			String path = DirectoryManager.FILES_PREDICT_SSPRO_PDBDATA + "/" + file;
			if(!checkFile(path)) { missingFiles.add(path); }
		}
		
		//verify all of the files in SSpro's Uriref50 database
		for(String file: SSPRO_UNIREF50) {
			String path = DirectoryManager.FILES_PREDICT_URIREF50 + "/" + file;
			if(!checkFile(path)) { missingFiles.add(path); }
		}
		
		//verify all of the files in SSpro's Uriref50 database
		for(String file: BLAST_FILES) {
			String path = DirectoryManager.FILES_TOOLS_BLAST + "/" + file;
			if(!checkFile(path)) { missingFiles.add(path); }
		}
		
		String[] retval = new String[missingFiles.size()];
		missingFiles.toArray(retval);
		return retval;
	}

	/**
	 * Generates SSpro model index
	 */
	private static void generateSSproModelsFile() {
		LabeledList<String> out = new LabeledList<String>();
		out.add("100");
		
		StringBuilder filenameBuilder = new StringBuilder();
		for(int index = 1; index < 101; ++index) {
			filenameBuilder.setLength(0);
			filenameBuilder.append(DirectoryManager.FILES_PREDICT_SSPRO_MODELS + "/model");
			if(index < 10) { filenameBuilder.append("0"); }
			if(index < 100) { filenameBuilder.append("0"); }
			filenameBuilder.append(index + ".brnn");
			out.add(filenameBuilder.toString());
		}
		
		out.add("");
		FileToolBase.writeFileLines(SSPRO_MODELS_LIST, out);
	}
	
	public static String getPsiPredWeights(int fileNo) {
		switch(fileNo) {
		case 1: return DirectoryManager.FILES_PREDICT_PSIPRED + "/"+ PSIPRED_WEIGHTS_1;
		case 2: return DirectoryManager.FILES_PREDICT_PSIPRED + "/"+ PSIPRED_WEIGHTS_2;
		case 3: return DirectoryManager.FILES_PREDICT_PSIPRED + "/"+ PSIPRED_WEIGHTS_3;
		default: throw new InternalError("Requested file does not exist!");
		}
	}
	
	protected static final boolean checkFile(String filename) {
		File check = new File(filename);
		return (check.exists() && (!check.isDirectory()));
	}
	
	/**
	 * TODO: needs code
	 * Downloads RCSB's ss-dis.txt
	 */
	public static void download_ss_dis() {
		qp("Downloading ss-dis.txt");
		
		//this needs to be it's own object or an exception will be thrown!
		Script ssDisDownload = new Script("curl", "-o", RCSB_SS_DIS_GZ, "https://cdn.rcsb.org/etl/kabschSander/ss_dis.txt.gz");
		Script.runScript(ssDisDownload);
		
		File ssDIS = new File(RCSB_SS_DIS);
		
		if(ssDIS.exists()) { ssDIS.delete(); }
 		
		//unzip the file (hangs when done with ArchiveUtility on Mac OS 11.2.1 (Big Sur)
		Script.runScript("gzip", "-d", RCSB_SS_DIS_GZ);
	}
	
	public static final boolean hasMissingFiles() {
		return verifyFiles().length != 0;
	}
	
	private FileManager() { }
}
