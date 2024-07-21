package modules.descriptor.vkbat.sspro5_2.brnn;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import assist.base.Assist;
import assist.translation.cplusplus.CppTranslator;
import dev.setup.dev.SSproDebugModule;
import modules.descriptor.vkbat.sspro5_2.SSproExitException;
import assist.Deconstructable;

/****************************************************************************************
/*                                                                                      *
/*  Project     :  1D-BRNN                                                              *
/*  Release     :  3.3                                                                  *
/*                                                                                      *
/*  File        :  Sequence.h                                                           *
/*  Description :  Sequence data and model predictions for the sequence                 *
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
/*  Translated to Java by Benjamin Strauss                                              *
/****************************************************************************************/

public class Sequence extends CppTranslator implements Deconstructable {

	// Sequence data
	public int Num_Positions;        // Number of positions in the sequence
	public int Num_Classes;          // Number of possible target classes
	public int[] Classes;             // Target class of each position in the sequence
	public float[][] Targets;          // Target outputs of the BRNN for each position
	public float[][] Features;         // Input data features for each position

	// Propagation data
	public int[] Predictions;         // Predicted class of each position in the sequence
	public float[][] Outputs_MAIN;     // Outputs of the main network of the BRNN for each position
	public float[][] Outputs_FWD;      // Outputs of the forward network of the BRNN for each position
	public float[][] Outputs_BWD;      // Outputs of the backward network of the BRNN for each position

	// Back-Propagation data
	public float[][] BackProp_FWD;     // Next values to back propagate in the forward network of the BRNN
	public float[][] BackProp_BWD;     // Next values to back propagate in the backward network of the BRNN

	// Interface
	public Sequence() {
		Num_Positions = -1;
		Num_Classes = -1;
		Classes = null;
		Targets = null;
		Features = null;
		Predictions = null;
		Outputs_MAIN = null;
		Outputs_FWD = null;
		Outputs_BWD = null;
		BackProp_FWD = null;
		BackProp_BWD = null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) { }
	}
	
	public void load(Scanner is, int feat, int cl) throws IOException {
		Num_Positions = is.nextInt();
		
		SSproDebugModule.record("Sequence.load(): Num_Positions = " + Num_Positions);
		
		Num_Classes=cl;
		Classes=new int[Num_Positions];
		Targets=new float[Num_Positions][];
		Features=new float[Num_Positions][];
		
		//TODO: something is weird about sspro.in, is it generated properly for 4jjx?
		for(int p=0; p < Num_Positions; p++) {
			SSproDebugModule.record("Sequence.load(): p = " + p);
			Classes[p] = is.nextInt();
			Targets[p]=new float[Num_Classes];
			for(int c=0;c<Num_Classes;c++){
				Targets[p][c]=(c==Classes[p]?1:0);
			}
			Features[p]=new float[feat];
			for(int f=0;f<feat;f++){
				Features[p][f] = is.nextFloat();
			}
		}
		check_viability();
	}
	
	public void alloc_propagation(int out_fwd,int out_bwd) {
		Predictions=new int[Num_Positions];
		Outputs_MAIN=new float[Num_Positions][];
		Outputs_FWD=new float[Num_Positions][];
		Outputs_BWD=new float[Num_Positions][];
		for(int p=0;p<Num_Positions;p++){
			Outputs_MAIN[p]=new float[Num_Classes];
			Outputs_FWD[p]=new float[out_fwd];
			Outputs_BWD[p]=new float[out_bwd];
		}
	}
	
	public void alloc_backpropagation(int out_fwd,int out_bwd) {
		BackProp_FWD=new float[Num_Positions][];
		BackProp_BWD=new float[Num_Positions][];
		for(int p=0;p<Num_Positions;p++){
			BackProp_FWD[p]=new float[out_fwd];
			BackProp_BWD[p]=new float[out_bwd];
		}
	}
	
	/**
	 * Reset back-propagation values
	 * @param out_fwd
	 * @param out_bwd
	 */
	public void reset_backpropagation(int out_fwd,int out_bwd) {
		 for(int p=0;p<Num_Positions;p++){
			 for(int i=0;i<out_fwd;i++){
				 BackProp_FWD[p][i]=(float) 0.0;
			 }
			 for(int i=0;i<out_bwd;i++){
				 BackProp_BWD[p][i]=(float) 0.0;
			 }
		 }
	}
	
	/**
	 * Write the model predictions in a file
	 * @throws IOException 
	 */
	public void write_predictions(DataOutputStream os) throws IOException {
		os.writeChars(Num_Positions + "\n");
		for(int i=0;i<Num_Positions;i++){
			os.writeChars( "in " + Classes[i] + " out " + Predictions[i] + " pb");
			for(int j=0; j<Num_Classes; j++){
				os.writeChars( " " + Outputs_MAIN[i][j] );
			}
			os.writeChars("\n");
		}
	}
	
	/**
	 * Return the last predictions error for a given position
	 * @param pos
	 * @return
	 */
	public float prediction_error(int pos) {
		float error=(float) 0.0;
		for(int c=0;c<Num_Classes;c++){
			if((Targets[pos][c] != 0) && (Outputs_MAIN[pos][c] != 0)){
				error-=Targets[pos][c] * (float)Math.log(Outputs_MAIN[pos][c]);
			}
		} return error;
	}
	
	/**
	 * Check the viability of the sequence
	 */
	public void check_viability() {
		if(Num_Positions<=0){
			throw new SSproExitException("\n\nERROR : empty sequence found\n");
		}
		for(int p=0; p < Num_Positions; p++){
			if((Classes[p]<0)||(Classes[p]>=Num_Classes)){
				throw new SSproExitException("\n\nERROR: incorrect class found: " + Classes[p] + "\n");
			}
		}
	}
	
	public void debugPrintAll() {
		qp("Num_Positions: " + Num_Positions);
		qp("Num_Classes: " + Num_Classes);
		qp("Classes: " + Assist.arrayToLine(Classes));
		print2DArray("Targets", Targets);
		print2DArray("Features", Features);
		qp("Predictions: " + Assist.arrayToLine(Predictions));
		print2DArray("Outputs_MAIN", Outputs_MAIN);
		print2DArray("Outputs_FWD", Outputs_FWD);
		print2DArray("Outputs_BWD", Outputs_BWD);
		print2DArray("BackProp_FWD", BackProp_FWD);
		print2DArray("BackProp_BWD", BackProp_BWD);
	}
	
	public static final void print2DArray(String name, float[][] array) {
		if(array == null) { qp(null); return; }
		for(int i = 0; i < array.length; ++i) {
			qp(name + "[" + i + "]: " + Assist.arrayToLine(array[i]));
		}
	}
}