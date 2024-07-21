package examples;

import utilities.LocalToolBase;

/**
 * 
 * @author bns
 *
 */

public class ParamGen extends LocalToolBase {
	
	public static void main(String[] args) {
		for(int ii = 0; ii < 99; ++ii) {
			qp("../SwiPred9/output/s9-bondugula-esm-output-"+ii+".csv");
		}
	}
	
}
