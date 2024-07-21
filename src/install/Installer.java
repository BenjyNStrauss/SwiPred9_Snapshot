package install;

import assist.base.Assist;
import assist.script.ScriptException;
import system.Instruction;
import utilities.LocalToolBase;

/**
 * Used to set up files and Folders for SwiPred installation
 * @author Benjamin Strauss
 *
 */

public class Installer extends LocalToolBase {
	
	/**
	 * Runs the SetupManager
	 * @param args: SetupManager arguments 
	 */
	public static void main(String... args) {
		for(int ii = 0; ii < args.length; ++ii) {
			args[ii] = args[ii].toLowerCase().trim();
		}
		
		boolean silent = false;
		boolean noDSSP = false;
		if(Assist.stringArrayContains(args, "-silent")) {
			silent = true;
		}
		if(Assist.stringArrayContains(args, "-no-dssp")) {
			noDSSP = true;
		}
		
		int newFolders = DirectoryManager.makeFolders();
		if(!silent) { qp("Made " + newFolders + " folders."); }
		
		
		if(!noDSSP) {
			downloadDSSP();
		} else if(!silent) {
			qp("Skipping DSSP.");
		}
		
		//FileManager.download_ss_dis();
		String[] missingFiles = FileManager.verifyFiles();
		if(missingFiles.length > 0 && (!silent)) {
			pause(2);
			qerr("Please manually import the following files:");
			qerr(missingFiles);
			pause(2);
		}
		
		PythonScriptManager.main(args);
		PerlScriptManager.main(args);
		Gor4FileManager.main(args);
		JNETSources.main(args);
		
		if(!silent) { qp("Finished verifying SwiPred file system."); }
	}
	
	/**
	 * Runs the SetupManager
	 * @param instr: instruction object holding SetupManager arguments
	 */
	public static void main(Instruction instr) {
		boolean silent = false;
		boolean noDSSP = false;
		if(instr.hasArgumentNamed(true, "-silent")) {
			silent = true;
		}
		if(instr.hasArgumentNamed(true, "-no-dssp")) {
			noDSSP = true;
		}
		
		int newFolders = DirectoryManager.makeFolders();
		if(!silent) { qp("Made " + newFolders + " folders."); }
		
		if(!noDSSP) {
			downloadDSSP();
		} else if(!silent) {
			qp("Skipping DSSP.");
		}
		
		FileManager.download_ss_dis();
		String[] missingFiles = FileManager.verifyFiles();
		if(missingFiles.length > 0 && (!silent)) {
			pause(2);
			qerr("Please manually import the following files:");
			qerr(missingFiles);
			pause(2);
		}
		
		generateFiles();
		
		if(!silent) { qp("Finished verifying SwiPred file system."); }
	}
	
	/**
	 * Generates files
	 */
	public static void generateFiles() {
		PythonScriptManager.main(null);
		PerlScriptManager.main(null);
		Gor4FileManager.main(null);
	}
	
	private static void downloadDSSP() {
		try {
			DirectoryManager.loadDSSP();
		} catch (ScriptException SE) {
			SE.printStackTrace();
		}
	}
}
