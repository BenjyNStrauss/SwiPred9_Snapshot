package modules.descriptor.vkbat.sspro5_2.brnn;

import assist.translation.cplusplus.CppTranslator;
import modules.descriptor.vkbat.sspro5_2.SSproExitException;

/****************************************************************************************
 *                                                                                      *
 *  Project     :  1D-BRNN                                                              *
 *  Release     :  3.3                                                                  *
 *                                                                                      *
 *  File        :  Predict_Multi_Models.cpp                                             *
 *  Description :  Predictions of several BRNN models on a dataset of sequences         *
 *                                                                                      *
 *  Author(s)   :  Christophe Magnan  (2013) - New generic version                      *
 *              :  Jianlin Cheng      (2003) - New custom version for SCRATCH           *
 *              :  Gianluca Pollastri (2001) - Customized version for SCRATCH           *
 *              :  Paolo Frasconi     (1997) - Initial generic version                  *
 *                                                                                      *
 *  Copyright   :  Institute for Genomics and Bioinformatics                            *
 *                 University of California, Irvine                                     *
 *                                                                                      *
 *  Modified    :  2015/07/01                                                           *
 *  Translated to Java by Benjamin Strauss (2019)                                       *
 ****************************************************************************************/

public class PredictSingleModels extends CppTranslator {
	
	static void predict_single(Model BRNN,Dataset DATA, String fileout) {
		for(int i=0; i < DATA.Num_Sequences; i++){
		  BRNN.propagate(DATA.Sequences[i]);
		}
		DATA.write_predictions(fileout);
	}

	public static void main(String[] args) {
		// Script Arguments
		String usage = " dataset model predictions\n\n";
		if(args.length!=4){
			throw new SSproExitException("\n  Script usage : " + args[0] + usage);
		}

		// Input Dataset
		Dataset data=new Dataset();
		data.load(args[1]);

		// Input Model
		Model brnn=new Model(); brnn.load(args[2]);

		// Data Compatibility
		int c=1; if(data.Num_Features!=brnn.Num_Features){ c=0; }
	  	if(data.Num_Classes!=brnn.Num_Classes){ c=0; }
	  	if(c == 0){
	  		throw new SSproExitException("Incompatible data, cannot proceed\n");
	  	}

	  	// Memory allocation
	  	data.alloc_propagation(brnn.Num_Outputs_FWD,brnn.Num_Outputs_BWD);

	  	// Predictions
	  	predict_single(brnn,data,args[3]);
	}
}
