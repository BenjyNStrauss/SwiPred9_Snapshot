package install;

import java.io.File;
import java.io.IOException;

import assist.script.Script;
import assist.script.ScriptException;
import tools.DataSource;
import utilities.LocalToolBase;

/**
 * Creates most of the directories necessary for this program to function
 * Can also download the DSSP files
 * @author Benjy Strauss
 *
 */

public final class DirectoryManager extends Script {
	public  static final String FILES						= "files";
	
	public  static final String FILES_BLASTS				= FILES + "/blasts";
	public  static final String FILES_ESM					= FILES + "/esm";
	public  static final String FILES_FASTA					= FILES + "/fasta";
	public  static final String FILES_NEXTFLOW				= FILES + "/nextflow";
	private static final String FILES_JARS					= FILES + "/jars";
	
	//not all of these technically contain FASTAs
	private static final String FILES_FASTA_CUSTOM			= FILES_FASTA + "/custom";
	private static final String FILES_FASTA_DSSP			= FILES_FASTA + "/dssp";
	private static final String FILES_FASTA_GENBANK			= FILES_FASTA + "/genbank";
	private static final String FILES_FASTA_NCBI			= FILES_FASTA + "/ncbi";
	public  static final String FILES_FASTA_NCBI_PCLA		= FILES_FASTA + "/ncbi-pcla";
	private static final String FILES_FASTA_OTHER			= FILES_FASTA + "/other";
	public  static final String FILES_FASTA_PDB				= FILES_FASTA + "/pdb";
	private static final String FILES_FASTA_PFAM			= FILES_FASTA + "/pfam";
	public  static final String FILES_FASTA_RCSB			= FILES_FASTA + "/rcsb";
	private static final String FILES_FASTA_SWISSPROT		= FILES_FASTA + "/swissprot";
	private static final String FILES_FASTA_UNIPROT			= FILES_FASTA + "/uniprot";
	
	private static final String FILES_GI_NUMBER 			= FILES + "/gi_number";
	public  static final String FILES_LOGS					= FILES + "/logs";
	private static final String FILES_MATRICES				= FILES + "/matrices";
	private static final String FILES_PATCHES				= FILES + "/patches";
	public  static final String FILES_SAVED					= FILES + "/saved";
	private static final String FILES_TOOLS					= FILES + "/tools";
	public  static final String FILES_VKBAT					= FILES + "/vkbat";
	public  static final String FILES_TMP					= FILES + "/tmp";

		    static final String FILES_PREDICT 				= FILES + "/predict";
	public  static final String FILES_PREDICT_JNET			= FILES_PREDICT + "/jnet";
	public  static final String FILES_PREDICT_JPRED			= FILES_PREDICT + "/jpred";
		    static final String FILES_PREDICT_PSIPRED		= FILES_PREDICT + "/psipred";
	public  static final String FILES_PREDICT_SSPRO			= FILES_PREDICT + "/sspro";
	public  static final String FILES_PREDICT_SYMPRED		= FILES_PREDICT + "/sympred";
	public  static final String FILES_PREDICT_GOR3			= FILES_PREDICT + "/gor3";
	public  static final String FILES_PREDICT_GOR4			= FILES_PREDICT + "/gor4";
	//not fully functional yet
	private static final String FILES_PREDICT_PROF			= FILES_PREDICT + "/prof";
	public  static final String FILES_PREDICT_SABLE			= FILES_PREDICT + "/sable";
	public  static final String FILES_PREDICT_SABLE_NR		= FILES_PREDICT_SABLE+"/nr";
	public  static final String FILES_PREDICT_SABLE_COV		= FILES_PREDICT_SABLE+"/cov";
	public  static final String FILES_PREDICT_SABLE_COVSA	= FILES_PREDICT_SABLE+"/covSA";
	
	public  static final String FILES_BLASTS_SSPRO			= FILES_BLASTS + "/sspro";
	//public static final String FILES_BLASTS_SABLE			= FILES_BLASTS + "/sable";
	
		    static final String FILES_PREDICT_SSPRO_MODELS	= FILES_PREDICT_SSPRO + "/models";
		    static final String FILES_PREDICT_SSPRO_PDBDATA = FILES_PREDICT_SSPRO + "/pdb_data";
	private static final String FILES_PREDICT_SSPRO_TMP 	= FILES_PREDICT_SSPRO + "/tmp";
	public  static final String FILES_PREDICT_URIREF50		= FILES_PREDICT_SSPRO + "/uniref50";
	
		    static final String FILES_TOOLS_BIOJAVA	    	= FILES_TOOLS + "/biojava";
	public  static final String FILES_TOOLS_BLAST			= FILES_TOOLS + "/blast-2.2.26";
	//private static final String FILES_TOOLS_HDF5_DIR_DARWIN = "HDF5-1.14.3-Darwin";
	
	public  static final String FILES_TOOLS_BLAST_BIN		= FILES_TOOLS_BLAST + "/bin";
	private static final String FILES_TOOLS_BLAST_DATA		= FILES_TOOLS_BLAST + "/data";
	private static final String FILES_TOOLS_BLAST_DOC		= FILES_TOOLS_BLAST + "/doc";
	
	private static final String FILES_TOOLS_BLAST_PLUS		= FILES_TOOLS + "/ncbi-blast-2.13.0+";
	public  static final String FILES_TOOLS_BLAST_PLUS_BIN	= FILES_TOOLS_BLAST_PLUS + "/bin";
	private static final String FILES_TOOLS_BLAST_PLUS_DOC	= FILES_TOOLS_BLAST_PLUS + "/doc";
	
	public  static final String INPUT						= "input";
	public  static final String OUTPUT						= "output";
	public  static final String SRC_PY						= "src-py";
	
	private static final String FILES_REGRESSION 			= FILES + "/regression";
	private static final String FILES_REGRESSION_INPUT		= FILES_REGRESSION + "/input";
	
	public  static final String FILES_ENCODE 				= FILES + "/encode";
	public  static final String FILES_ENCODE_NAIVE 			= FILES_ENCODE + "/naive";
	
	//list of all the folders needed for the program to run
	private static final String NESSESARY_FOLDERS[] = { FILES, FILES_BLASTS, FILES_TMP,
			FILES_FASTA, FILES_FASTA_CUSTOM, FILES_FASTA_DSSP, FILES_FASTA_GENBANK, 
			FILES_FASTA_NCBI, FILES_FASTA_PDB, FILES_FASTA_PFAM, FILES_FASTA_RCSB,
			FILES_FASTA_SWISSPROT, FILES_FASTA_UNIPROT, FILES_FASTA_OTHER, FILES_FASTA_NCBI_PCLA,
			
			FILES_GI_NUMBER, FILES_MATRICES, FILES_PREDICT, FILES_ESM,
			FILES_PREDICT_JNET, FILES_PREDICT_PSIPRED, FILES_PREDICT_JPRED, FILES_PREDICT_SSPRO,
			FILES_PREDICT_SYMPRED, FILES_PREDICT_GOR3, FILES_PREDICT_GOR4, FILES_PREDICT_PROF,
			FILES_PREDICT_SABLE, FILES_SAVED, FILES_TOOLS,
			FILES_TOOLS_BIOJAVA, FILES_TOOLS_BLAST, FILES_LOGS, 
			
			FILES_PREDICT_SSPRO_MODELS, FILES_PREDICT_SSPRO_PDBDATA, FILES_PREDICT_SSPRO_TMP,
			FILES_PREDICT_URIREF50, FILES_PREDICT_SABLE_COV, FILES_PREDICT_SABLE_COVSA,
			
			FILES_TOOLS_BLAST_BIN, FILES_TOOLS_BLAST_DATA, FILES_TOOLS_BLAST_DOC,
			FILES_TOOLS_BLAST_PLUS, FILES_TOOLS_BLAST_PLUS_BIN, FILES_TOOLS_BLAST_PLUS_DOC,
			
			FILES_BLASTS_SSPRO, //FILES_BLASTS_SABLE, 
			FILES_NEXTFLOW,
			FILES_VKBAT, FILES_PATCHES, 
			
			INPUT, FILES_JARS, OUTPUT, SRC_PY,
			
			FILES_REGRESSION, FILES_REGRESSION_INPUT,
			FILES_ENCODE, FILES_ENCODE_NAIVE };
	
	//all of the folders that contain data that gets downloaded or generated by use of the program
	private static final String DATA_FOLDERS[] = { 
			FILES_FASTA_CUSTOM, FILES_FASTA_DSSP, FILES_FASTA_GENBANK, 
			FILES_FASTA_NCBI, FILES_FASTA_PDB, FILES_FASTA_PFAM, FILES_FASTA_RCSB,
			FILES_FASTA_SWISSPROT, FILES_FASTA_UNIPROT, FILES_FASTA_OTHER,
			
			FILES_GI_NUMBER, FILES_SAVED, OUTPUT,
			FILES_PATCHES, FILES_REGRESSION_INPUT };
	
	private static final String DSSP_URL = "rsync://rsync.cmbi.umcn.nl/dssp/";
	private static final String[] DSSP_ARGS = { "rsync", "-avz", DSSP_URL, DataSource.DSSP.fastaFolder() + "/" };
	private static final String[] DSSP_UPDATE_ARGS = { "rsync", "-avz", "--delete", DSSP_URL, DataSource.DSSP.fastaFolder() + "/" };
	
	private static String dssp_output;
	private static String dssp_error;
	
	/**
	 * Sets up the file system to use this program
	 * @param args: not used
	 */
	public static void main(String[] args) {
		int newFolders = makeFolders();
		qp("Made " + newFolders + " folders.");
		loadDSSP();
	}
	
	/**
	 * Downloads the DSSP Files into files/DSSP.
	 * If this method fails to download everything, just run it again.
	 */
	public static void loadDSSP() {
		ProcessBuilder builder;
		if(hasDSSP()) {
			LocalToolBase.qpl("Redownloading DSSP Files...");
			builder = new ProcessBuilder(DSSP_UPDATE_ARGS);
		} else {
			LocalToolBase.qpl("Downloading DSSP Files...");
			builder = new ProcessBuilder(DSSP_ARGS);
		}
		
		Process proc;
		
		int retval = 0;
		
		try {
			proc = builder.start();
			dssp_output = getInputAsString(proc.getInputStream());
			dssp_error = getInputAsString(proc.getErrorStream());
			
			retval = proc.waitFor();
			qpl(dssp_output);
			
			analyzeScriptReturnValue(retval);
			
		} catch (IOException e) {
			throw new ScriptException("I/O Error for DSSP Download Process");
		} catch (InterruptedException e) {
			throw new ScriptException("DSSP Download Process Interrupted");
		}
		
		if(dssp_error != "") { 
			qerr(dssp_error);
			throw new ScriptException("DSSP Download Error:\n" + dssp_error);
		}
		qpl("Finished downloading DSSP files.");
	}

	/**
	 * Make all of the folders if they don't already exist
	 * Also check for missing files (some can be auto-generated)
	 */
	public static final int makeFolders() {
		qp("Starting SwiPred file system verification...");
		int newFolders = 0;
		for(String folder: NESSESARY_FOLDERS) { 
			if(verifyFolder(folder)) { ++newFolders; }
		}
		
		for(DataSource d_src: DataSource.values()) {
			if(verifyFolder(d_src.fastaFolder())) { ++newFolders; }
		}
		
		return newFolders;
	}
	
	/**
	 * Remove all generated files
	 */
	@SuppressWarnings("unused")
	private static final void cleanUp() {
		qpl("Cleaning Data Files...");
		for(String folder: DATA_FOLDERS) { 
			removeDataFromFolder(folder);
		}
		for(DataSource d_src: DataSource.values()) {
			removeDataFromFolder(d_src.fastaFolder());
		}
		qpl("Finished");
	}
	
	/**
	 * Verifies that a single folder exists
	 */
	public static final boolean verifyFolder(String path) {
		File folder = new File(path);
		if(!folder.exists()) { folder.mkdir(); return true; }
		return false;
	}
	
	/**
	 * Deletes all fastas that have been downloaded
	 */
	@SuppressWarnings("unused")
	private static final void clearFastas() {
		for(DataSource d_src: DataSource.values()) {
			removeDataFromFolder(d_src.fastaFolder());
		}
	}
	
	/**
	 * Removes all files from a folder
	 * @param path
	 */
	private static final void removeDataFromFolder(String path) {
		File folder = new File(path);
		if(!folder.exists()) { folder.mkdir(); return; }
		
		String[] entries = folder.list();
		for(String str: entries) {
			File entry = new File(path + "/" + str);
			if(!entry.isDirectory()) {
				entry.delete();
			}
		}
	}
	
	public static final void fileSystemSetupError() {
		qerr("Please run \"setup\" from main program prompt.");
	}
	
	private static boolean hasDSSP() {
		File DSSPFolder = new File(DataSource.DSSP.fastaFolder());
		return (DSSPFolder.list().length > 0);
	}
	
	protected static final void qpl(String arg0) { LocalToolBase.qpl(arg0); }
}
