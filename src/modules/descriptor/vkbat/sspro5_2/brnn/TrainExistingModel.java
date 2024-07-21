package modules.descriptor.vkbat.sspro5_2.brnn;

import assist.translation.cplusplus.CppTranslator;
import modules.descriptor.vkbat.sspro5_2.SSproExitException;

/****************************************************************************************
/*                                                                                      *
/*  Project     :  1D-BRNN                                                              *
/*  Release     :  3.3                                                                  *
/*                                                                                      *
/*  File        :  Train_Existing_Model.cpp                                             *
/*  Description :  Retrain an existing BRNN model on a dataset of sequences             *
/*                                                                                      *
/*  Author(s)   :  Christophe Magnan  (2013) - New generic version                      *
/*              :  Jianlin Cheng      (2003) - New custom version for SCRATCH           *
/*              :  Gianluca Pollastri (2001) - Customized version for SCRATCH           *
/*              :  Paolo Frasconi     (1997) - Initial generic version                  *
/*                                                                                      *
/*  Copyright   :  Institute for Genomics and Bioinformatics                            *
/*                 University of California, Irvine                                     *
/*                                                                                      *
/*  Modified    :  2015/07/01                                                           *
/*  Translated to Java by Benjamin Strauss (2019)                                       *
/****************************************************************************************/

public class TrainExistingModel extends CppTranslator {

	static void test_model(Model BRNN, Dataset DATA) {
		for(int i=0;i<DATA.Num_Sequences;i++){
			BRNN.propagate(DATA.Sequences[i]);
				if(i%100==0){
					qp(".");
				}
		}
		qp(" Done!\n");
		DATA.display_confusion();
	}

	static void retrain_model(Options OPT, Model BRNN, Dataset TRAIN, Dataset TEST, String fileout) {
		// Initial Model Evaluation
		qp("Testing model to retrain on train dataset");
		test_model(BRNN,TRAIN);
		qp("Testing model to retrain on test dataset");
		test_model(BRNN,TEST);

		// Retraining Options
		float epsilon=OPT.LEARN_RATE;
		int num_epochs=OPT.NUM_EPOCHS;
		int num_batchs=OPT.NUM_BATCHS;
		int adap_max_epochs=OPT.ADAP_EPOCHS;
		int adap_reload=OPT.ADAP_RELOAD;
		int shuffle=OPT.SHUFFLE;

		// Initial settings
		int adap_num_epochs=0;
		float previous_error=TRAIN.empirical_risk();
		float current_error=previous_error;
		int num_seq=TRAIN.Num_Sequences;
		int[] train_seq=new int[num_seq];
		for(int i=0;i<num_seq;i++){
			train_seq[i]=i;
		}

		// Training iterations
		qp("Ready for retraining the BRNN model...\n\n");
		for(int epoch=0;epoch<num_epochs;epoch++) {
			qp("Training epoch " + epoch+1 + " (eps=" + epsilon + ").");

			// Initializing iteration
			if(shuffle != 0){
				int n=num_seq;
				for(int i=0;i<n;i++){
					int p= (int) (rand() % n);
					int q= (int) (rand() % n);
					int t=train_seq[p];
					train_seq[p]=train_seq[q];
					train_seq[q]=t;
				}
			}
			float batch_step=(float)num_seq/num_batchs;
			float next_batch=batch_step;
			int num_pos_batch=0;

			// Back-propagation method
			for(int i=0;i<num_seq;i++) {
				Sequence seq=TRAIN.Sequences[train_seq[i]];
				BRNN.expectation(seq);
				num_pos_batch+=seq.Num_Positions;
				if(((i+2)>=next_batch)&&((num_seq-i)>=batch_step)) {
					BRNN.maximization(epsilon/(float)num_pos_batch);
					next_batch+=batch_step;
					num_pos_batch=0;
				}
				if(i%100==0){
					qp(".");
				}
			}
			BRNN.maximization(epsilon/(float)num_pos_batch);
			current_error=TRAIN.empirical_risk();
			if(current_error != current_error) {
				current_error=2*previous_error;
				adap_num_epochs=adap_max_epochs;
			}
			qp("\nPrevious error: " + previous_error + " ");
			qp("Current error: " + current_error + " . ");

			// Adaptative Procedure
			if(current_error<previous_error) {
				qp("writing new model in \"" + fileout + "\"...\n\n");
				BRNN.write(fileout);
				previous_error=current_error;
				adap_num_epochs=0;
				qp("Testing new model on train dataset");
				test_model(BRNN,TRAIN);
				qp("Testing new model on test dataset");
				test_model(BRNN,TEST);
			}else{
					adap_num_epochs++;
			    if((adap_max_epochs != 0)&&(adap_num_epochs >= adap_max_epochs)) {
			    	adap_num_epochs=0; epsilon*=0.5;
			    	qp("max number of iterations reached !\n");
			    	qp("New learning rate = " + epsilon + "\n\n");
			    	if(adap_reload != 0){
			    		qp("Loading the last model saved...\n\n");
			    		BRNN.deconstruct();
			    		BRNN = null;
			    		BRNN=new Model();
			    		BRNN.load(fileout);
			    		BRNN.alloc_backpropagation();
			    	}
			    } else{
			    	qp(adap_num_epochs + " epochs without improvement.\n\n");
			    }
		    }
		}
	}

	public static void main(String[] args) {
		// Script Arguments
		String usage =" options_file train_dataset test_dataset input_model output_model\n\n";
		if(args.length!=6){
			throw new SSproExitException("\n  Script usage : " + args[0] + usage);
		}

		// Retraining Options
		qp("\n\nReading options to retrain the BRNN...\n");
		Options opt = new Options();
		opt.load(args[1],0);
		opt.display();
		srand48(opt.SEED);
		srand(opt.SEED);

		// Train Dataset
		qp("Loading training dataset... ");
		Dataset train=new Dataset();
		train.load(args[2]);
		qp("Done!\n" + train.Num_Sequences + " sequences");
		qp(" (" + train.Num_Positions + " positions)\n\n");

		// Test Dataset
		qp("Loading test dataset... ");
		Dataset test=new Dataset();
		test.load(args[3]);
		qp("Done!\n" + test.Num_Sequences + " sequences");
		qp(" (" + test.Num_Positions + " positions)\n\n");

		// Initial Model
		qp("Loading model to retrain...\n");
		Model brnn=new Model();
		brnn.load(args[4]);
		qp("Writing model in \"" + args[5]);
		qp("\"...\n\n");
		brnn.write(args[5]);

		// Data Compatibility
		int c=1; if(train.Num_Features!=test.Num_Features){ c=0; }
		if(train.Num_Classes!=test.Num_Classes){ c=0; }
		if(train.Num_Features!=brnn.Num_Features){ c=0; }
		if(train.Num_Classes!=brnn.Num_Classes){ c=0; }
		if(c == 0){
			throw new SSproExitException("Incompatible data, cannot proceed\n");
		}

		// Memory allocation
		train.alloc_propagation(brnn.Num_Outputs_FWD,brnn.Num_Outputs_BWD);
		train.alloc_backpropagation(brnn.Num_Outputs_FWD,brnn.Num_Outputs_BWD);
		test.alloc_propagation(brnn.Num_Outputs_FWD,brnn.Num_Outputs_BWD);
		brnn.alloc_backpropagation();

		// Retrain Model
		retrain_model(opt,brnn,train,test,args[5]);
	}
}
