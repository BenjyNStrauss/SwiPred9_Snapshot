package modules.encode.esm;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class DebugESM extends LocalToolBase {
	
	public static void main(String args) {
		String[] py_args = {"python", "src-py/esm/scripts/extract.py", 
				"esm1_t34_670M_UR50S", "files/tmp/esm_fasta.txt", "files/tmp",
				"--repr_layers", "33", "--include mean", "per_tok", "--truncate"};
		
		
	}
	
}
