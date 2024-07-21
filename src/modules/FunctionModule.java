package modules;

import assist.util.LabeledList;
import install.DirectoryManager;
import install.Installer;
import utilities.LocalToolBase;

/**
 * A FunctionModule performs a GUI function of SwiPred
 * non-Abstract classes that extend this class should also have method for handling non-GUI functions
 * 
 * @author Benjy Strauss
 *
 */

public abstract class FunctionModule extends LocalToolBase {
	private static boolean csvMode;
	
	protected FunctionModule() { }
	
	/**
	 * Verifies that the file system is set up correctly
	 */
	protected static void verifyRelevantFileSystem() {
		if((!fileExists(DirectoryManager.FILES_LOGS)) ||
			(fileExists(DirectoryManager.FILES_BLASTS)) ||
			(!fileExists(DirectoryManager.SRC_PY)) || 
			(!fileExists(DirectoryManager.FILES_PREDICT_SYMPRED))) {
			Installer.generateFiles();
		}
	}
	
	/**
	 * Filter out arguments from sequences
	 * @param args
	 * @return
	 */
	protected static String[] parseArgs(String[] args) {
		LabeledList<String> parsedArgs = new LabeledList<String>();
		for(String arg: args) {
			if(arg.startsWith("-")) {
				switch(arg) {
				case "-csv":		csvMode = true;		break;
				default:			qp("Argument not recognized: " + arg);
				}
			} else {
				parsedArgs.add(arg);
			}
		}
		
		args = new String[parsedArgs.size()];
		return parsedArgs.toArray(args);
	}
	
	protected static boolean csvMode() { return csvMode; }
}
