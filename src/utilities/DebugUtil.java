package utilities;

import java.io.File;

/**
 * Contains methods for specialized debugging
 * @author Benjy Strauss
 *
 */

public class DebugUtil extends LocalToolBase {
	
	public static void checkForFile(String filename) {
		File flagFile = new File(filename);
		
		assert flagFile.exists(): "Error: key file missing!" ;
		if(!flagFile.exists()) {
			error("Condition failed!");
			System.exit(-8);
		}
	}
	
}
