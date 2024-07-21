package modules.descriptor.vkbat.sspro5_2.brnn;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import assist.translation.cplusplus.CppTranslator;
import modules.descriptor.vkbat.sspro5_2.SSproExitException;
import assist.Deconstructable;

/****************************************************************************************
/*                                                                                      *
/*  Project     :  1D-BRNN                                                              *
/*  Release     :  3.3                                                                  *
/*                                                                                      *
/*  File        :  Options.h                                                            *
/*  Description :  Options to train or retrain a BRNN model                             *
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

public class Options extends CppTranslator implements Deconstructable {
	
	public class CLADOGRAM_TYPE {

	}

	// Model Options
	public int FEATURES;         // Number of data features in input of the BRNN
	public int CLASSES;          // Number of target classes in output of the BRNN
	public int HIDDEN;           // Number of hidden nodes in the main network of the BRNN
	public int CONTEXT_FWD;      // Number of adjacent positions s.t. outputs of forward network -> inputs of main network
	public int CONTEXT_BWD;      // Number of adjacent positions s.t. outputs of backward network -> inputs of main network
	public int OUTPUTS_FWD;      // Number of output nodes in the forward network of the BRNN
	public int OUTPUTS_BWD;      // Number of output nodes in the backward network of the BRNN
	public int HIDDEN_FWD;       // Number of hidden nodes in the forward network of the BRNN
	public int HIDDEN_BWD;       // Number of hidden nodes in the backward network of the BRNN

	// Training Options
	public float LEARN_RATE;     // Learning rate - controls the weights update during the maximization step
	public int NUM_EPOCHS;       // Number of training periods to run with or without the adaptative procedure
	public int NUM_BATCHS;       // Number of model updates by training period (dataset divided in batchs)
	public int ADAP_EPOCHS;      // Number of periods without improvement before decreasing the learning rate
	public int ADAP_RELOAD;      // Reload the last model saved when the adaptative procedure starts
	public int SHUFFLE;          // Shuffle the training dataset before each training period
	public int SEED;             // Seed for randomization functions (0 to generate it automatically)

	// Interface
	public Options() {
		FEATURES = -1;
		CLASSES = -1;
		HIDDEN = -1;
		CONTEXT_FWD = -1;
		CONTEXT_BWD = -1;
		OUTPUTS_FWD = -1;
		OUTPUTS_BWD = -1;
		HIDDEN_FWD = -1;
		HIDDEN_BWD = -1;
		LEARN_RATE = (float) -1.0;
		NUM_EPOCHS = -1;
		NUM_BATCHS = -1;
		ADAP_EPOCHS = -1;
		ADAP_RELOAD = -1;
		SHUFFLE = -1;
		SEED = -1;	
	}
	
	public void deconstruct() {
		
	}
	
	public void display(){
		if(FEATURES!=-1) {
			qp("\n  MODEL OPTIONS:\n\n");
			qp("  FEATURES     =  " + FEATURES + "\n");
			qp("  CLASSES      =  " + CLASSES + "\n");
		  	qp("  HIDDEN       =  " + HIDDEN + "\n");
		  	qp("  CONTEXT_FWD  =  " + CONTEXT_FWD + "\n");
		  	qp("  CONTEXT_BWD  =  " + CONTEXT_BWD + "\n");
		  	qp("  OUTPUTS_FWD  =  " + OUTPUTS_FWD + "\n");
		  	qp("  OUTPUTS_BWD  =  " + OUTPUTS_BWD + "\n");
		  	qp("  HIDDEN_FWD   =  " + HIDDEN_FWD + "\n");
		  	qp("  HIDDEN_BWD   =  " + HIDDEN_BWD + "\n");
		}
		qp("\n  TRAINING OPTIONS:\n\n");
		qp("  LEARN_RATE   =  " + LEARN_RATE + "\n");
		qp("  NUM_EPOCHS   =  " + NUM_EPOCHS + "\n");
		qp("  NUM_BATCHS   =  " + NUM_BATCHS + "\n");
		qp("  ADAP_EPOCHS  =  " + ADAP_EPOCHS + "\n");
		qp("  ADAP_RELOAD  =  " + ADAP_RELOAD + "\n");
		qp("  SHUFFLE      =  " + SHUFFLE + "\n");
		qp("  SEED         =  " + SEED + "\n\n");
	}

	public void load(String filein, int model) {
		FileReader in_buffer;
		try {
			in_buffer = new FileReader(filein);
			Scanner is = new Scanner(in_buffer);
			String option;
			while((option = is.next()) != null){
			    if(!strcmp(option,"FEATURES")){ FEATURES = is.nextInt(); }
			    else if(!strcmp(option,"CLASSES")){ CLASSES = is.nextInt(); }
			    else if(!strcmp(option,"HIDDEN")){ HIDDEN = is.nextInt(); }
			    else if(!strcmp(option,"CONTEXT_FWD")){ CONTEXT_FWD = is.nextInt(); }
			    else if(!strcmp(option,"CONTEXT_BWD")){ CONTEXT_BWD = is.nextInt(); }
			    else if(!strcmp(option,"OUTPUTS_FWD")){ OUTPUTS_FWD = is.nextInt(); }
			    else if(!strcmp(option,"OUTPUTS_BWD")){ OUTPUTS_BWD = is.nextInt(); }
			    else if(!strcmp(option,"HIDDEN_FWD")){ HIDDEN_FWD = is.nextInt(); }
			    else if(!strcmp(option,"HIDDEN_BWD")){ HIDDEN_BWD = is.nextInt(); }
			    else if(!strcmp(option,"LEARN_RATE")){ LEARN_RATE = is.nextInt(); }
			    else if(!strcmp(option,"NUM_EPOCHS")){ NUM_EPOCHS = is.nextInt(); }
			    else if(!strcmp(option,"NUM_BATCHS")){ NUM_BATCHS = is.nextInt(); }
			    else if(!strcmp(option,"ADAP_EPOCHS")){ ADAP_EPOCHS = is.nextInt(); }
			    else if(!strcmp(option,"ADAP_RELOAD")){ ADAP_RELOAD = is.nextInt(); }
			    else if(!strcmp(option,"SHUFFLE")){ SHUFFLE = is.nextInt(); }
			    else if(!strcmp(option,"SEED")){ SEED = is.nextInt(); }
			}
			in_buffer.close();
			is.close();	
		} catch (IOException IOE) {
			throw new SSproExitException("Cannot read file \"" + filein + "\"\n");
		}
		if((SEED<=0)||(SEED>=10000000)){
			SEED = (int) (time()%10000000);
		}
		if(model != 0){
			check_model_viability();
		} else{
			FEATURES=-1;
		}
		check_training_viability();
	}

	// Methods
	private void check_model_viability() {
		if(FEATURES<1){ throw new SSproExitException("ERROR : option FEATURES missing or incorrect.\n"); }
		if(CLASSES<2){ throw new SSproExitException("ERROR : option CLASSES missing or incorrect.\n"); }
		if(HIDDEN<1){ throw new SSproExitException("ERROR : option HIDDEN missing or incorrect.\n"); }
		if(CONTEXT_FWD<0){ throw new SSproExitException("ERROR : option CONTEXT_FWD missing or incorrect.\n"); }
		if(CONTEXT_BWD<0){ throw new SSproExitException("ERROR : option CONTEXT_BWD missing or incorrect.\n"); }
		if(OUTPUTS_FWD<1){ throw new SSproExitException("ERROR : option OUTPUTS_FWD missing or incorrect.\n"); }
		if(OUTPUTS_BWD<1){ throw new SSproExitException("ERROR : option OUTPUTS_BWD missing or incorrect.\n"); }
		if(HIDDEN_FWD<1){ throw new SSproExitException("ERROR : option HIDDEN_FWD missing or incorrect.\n"); }
		if(HIDDEN_BWD<1){ throw new SSproExitException("ERROR : option HIDDEN_BWD missing or incorrect.\n"); }
	}
	
	private void check_training_viability() {
		if((LEARN_RATE<=0)||(LEARN_RATE>1)){ throw new SSproExitException("ERROR : option LEARN_RATE missing or incorrect.\n"); }
		if(NUM_EPOCHS<1){ throw new SSproExitException("ERROR : option NUM_EPOCHS missing or incorrect.\n"); }
		if(NUM_BATCHS<1){ throw new SSproExitException("ERROR : option NUM_BATCHS missing or incorrect.\n"); }
		if(ADAP_EPOCHS<0){ throw new SSproExitException("ERROR : option ADAP_EPOCHS missing or incorrect.\n"); }
		if(ADAP_RELOAD<0){ throw new SSproExitException("ERROR : option ADAP_RELOAD missing or incorrect.\n"); }
		if(SHUFFLE<0){ throw new SSproExitException("ERROR : option SHUFFLE missing or incorrect.\n"); }
		if((SEED<=0)||(SEED>=10000000)){ throw new SSproExitException("ERROR : option SEED missing or incorrect.\n"); }
	}
}
