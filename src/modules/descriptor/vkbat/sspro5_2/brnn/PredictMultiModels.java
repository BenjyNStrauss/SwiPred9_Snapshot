package modules.descriptor.vkbat.sspro5_2.brnn;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import assist.translation.cplusplus.CppTranslator;
import dev.setup.dev.SSproDebugModule;
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

public class PredictMultiModels extends CppTranslator {
	
	static void predict_multi(int Num_Models, Model[] BRNNs, Dataset DATA, String fileout) {
		int i,j,c,p,l,n = DATA.Num_Classes;
		Sequence s;
		for(i=0; i < DATA.Num_Sequences; i++) {
			s = DATA.Sequences[i];
			l = s.Num_Positions;
			float[][] sum = new float[l][];
			for(p=0; p < l; p++){
				sum[p] = new float[n];
				for(c = 0; c < n; c++){
					sum[p][c] = (float) 0.0;
				}
			}
			for(j=0; j < Num_Models; j++){
				BRNNs[j].propagate(s);
				for(p=0; p < l; p++){
					for(c=0; c < n; c++){
						sum[p][c]+=s.Outputs_MAIN[p][c];
					}
				}
			}
		    for(p=0; p<l; p++){
		    	float max=0;
		    	int cl=0;
		    	for(c=0; c < n; c++){
		    		float pb=sum[p][c];
		    		s.Outputs_MAIN[p][c]=pb/(float)Num_Models;
		    		if(pb>max){
		    			max=pb;
		    			cl=c;
		    		}
		    	}
		    	s.Predictions[p]=cl;
		    }
		}
		DATA.write_predictions(fileout);
	}
	
	public static void main(String[] args) throws IOException {
		File in = new File("files/blast/sspro-blast/sspro.in");
		File model_list = new File("files/predict/sspro/models.txt");
		File out = new File("files/blast/sspro-blast/sspro.out");
		do_pred(in, model_list, out);
	}
	
	public static void do_pred(File in, File model_list, File out) throws IOException {
		// Input Dataset
		Dataset data = new Dataset();
		data.load(in.getPath());

		SSproDebugModule.record("PredictMultiModels.do_pred(): in = " + in.getPath());
		
		// Models List
		Model[] models;
		int num_models=0;
		String model_file;
		SSproDebugModule.record("PredictMultiModels.do_pred(): model_list = " + model_list.getPath());
		FileReader in_buffer = new FileReader(model_list);
		int max_out_fwd=0;
		int max_out_bwd=0;
		
		if(in_buffer.ready()) {
			Scanner is = new Scanner(in_buffer);
			num_models = is.nextInt();
			if(num_models < 1){
				is.close();
				throw new SSproExitException("Incorrect format, exit.");
			}
			
			// Input Models
			models = new Model[num_models];
			for(int i=0; i < num_models; i++){
				model_file = is.next();
				models[i] = new Model();
				SSproDebugModule.record("PredictMultiModels.do_pred(): model_file = " + model_file);
				
				models[i].load(model_file);
				int of = models[i].Num_Outputs_FWD;
				if(of > max_out_fwd){
					max_out_fwd = of;
				}
				int ob = models[i].Num_Outputs_BWD;
				if(ob > max_out_bwd){
					max_out_bwd = ob;
				}

				// Data Compatibility
				if(data.Num_Features != models[i].Num_Features){
					is.close();
					throw new SSproExitException("Incompatible data (features), cannot proceed: " 
							+ data.Num_Features + " vs model[" + i + "] " + models[i].Num_Features);
				}
				
				if(data.Num_Classes != models[i].Num_Classes){
					is.close();
					throw new SSproExitException("Incompatible data (classes), cannot proceed: "
							+ data.Num_Classes + " vs model[" + i + "] " + models[i].Num_Classes);
				}
			}
			is.close();
			// Close Models List
			in_buffer.close();
		} else {
			in_buffer.close();
			throw new SSproExitException("Cannot read file \"" + model_list + "\"\n");
		}
		
		// Memory allocation
		data.alloc_propagation(max_out_fwd, max_out_bwd);
		
		// Predictions
		predict_multi(num_models, models, data, out.getPath());
	}
}
