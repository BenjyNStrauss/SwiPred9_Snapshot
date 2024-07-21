package examples.bondugula;

import assist.script.PythonScript;
import modules.encode.esm.ESM_Model;
import utilities.LocalToolBase;

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
 * 
 * @author Benjy Strauss
 *
 */
//@SuppressWarnings("unused")
public class BonduESM_LaptopMain extends LocalToolBase {
	public static final ESM_Model MODEL = ESM_Model.esm2_t36_3B_UR50D;
	
	public static void main(String[] args) throws Exception {
		PythonScript.setPythonPath("/Users/bns/opt/anaconda3/bin/python");
		
		for(int ii = 66; ii < 99; ++ii) {
			BonduESM.main(new String[]{""+ii, "-no-exit"});
		}
		
		System.exit(0);
	}
}
