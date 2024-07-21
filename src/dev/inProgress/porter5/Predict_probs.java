package dev.inProgress.porter5;

import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.IFStream;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Predict_probs extends CppTranslator {
	
	public static void evaluate(MModel M, DataSet D, char[] which, int cycles) {
		int y;
		
		System.out.print("\n counting_"+new String(which)+"_errors");
		System.out.flush();
		M.resetNErrors();
		int p;
		for (p=0; p<D.length; p++) {
			M.predict_probs(D.seq[p], cycles);
			if (p%20==0) { System.out.print("."); }
		}

		System.out.print("\n");
		System.out.flush();
	}

	public static void main(String[] args) {

		// This is for predicting
		if (args.length<3) {
			System.err.print("Usage: " + args[0] + " model_file protein_file\n");
			System.exit(1);
		}

	    char[] model = new char[256];
	    String prot;
	    char[] tmp = new char[256];
	    //char alig[256];
	    strcpy(model, args[1]);
	    prot = args[2];
	    //strcpy(alig,argv[3]);

	    double th = 0.5;

	    MModel M;
	    strcpy(tmp, model);
	    IFStream mstream = new IFStream(tmp);
	    M = new MModel(mstream);

	    System.out.print("Reading " + prot + " .. ");
	    IFStream tstream = new IFStream(prot);
	    DataSet T = new DataSet(tstream);

	    System.out.print("read\n");
	    System.out.flush();

	    evaluate(M, T, prot.toCharArray(), 1);
	    prot += ".probs";
	    T.write_probs(prot);
	    evaluate(M, T, prot.toCharArray(), 2);
	    prot += "F";
	    T.write_probs(prot);
	}
}
