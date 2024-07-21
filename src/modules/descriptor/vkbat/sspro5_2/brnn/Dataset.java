package modules.descriptor.vkbat.sspro5_2.brnn;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import assist.translation.cplusplus.CppTranslator;
import dev.setup.dev.SSproDebugModule;
import modules.descriptor.vkbat.sspro5_2.SSproExitException;
import assist.Deconstructable;

/****************************************************************************************
/*                                                                                      *
/*  Project     :  1D-BRNN                                                              *
/*  Release     :  3.3                                                                  *
/*                                                                                      *
/*  File        :  Dataset.h                                                            *
/*  Description :  Dataset of sequences in input of the program                         *
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

public class Dataset extends CppTranslator implements Deconstructable {
	// Dataset content
	public int Num_Sequences;        // Number of sequences in the dataset
	public int Num_Positions;        // Number of positions in the sequences
	public int Num_Features;         // Number of features for each position
	public int Num_Classes;          // Number of possible target classes
	public Sequence[] Sequences;     // Sequences in the dataset

	// Interface
	public Dataset() {
		Num_Sequences = -1;
		Num_Positions = 0;
		Num_Features = -1;
		Num_Classes = -1;
		Sequences = null;
	}
	
	public void deconstruct() {
		for(int i=0; i < Num_Sequences; i++){
			Sequences[i].deconstruct();
		}
	}
	
	/**
	 * 
	 * @param filein
	 */
	public void load(String filein) {
		FileInputStream in_buffer;
		
		SSproDebugModule.record("Dataset.load(): filein = " + filein);
		try {
			in_buffer = new FileInputStream(filein);
			Scanner is = new Scanner(in_buffer);
			
			Num_Sequences = is.nextInt();
			Num_Features = is.nextInt();
			Num_Classes = is.nextInt();
			SSproDebugModule.record("Dataset.load(): Num_Sequences = " + Num_Sequences);
			SSproDebugModule.record("Dataset.load(): Num_Features = " + Num_Features);
			SSproDebugModule.record("Dataset.load(): Num_Classes = " + Num_Classes);
			
			Num_Positions=0;
			Sequences = new Sequence[Num_Sequences];
			for(int i=0; i < Num_Sequences; i++) {
				Sequences[i]=new Sequence();
				
				try {
					Sequences[i].load(is, Num_Features, Num_Classes);
				} catch (InputMismatchException IME) {
					throw new SSproExitException("DataSet.load(): Error Loading sequence from: " + filein, IME);
				}
				
				Num_Positions+=Sequences[i].Num_Positions;
			}
			in_buffer.close();
			check_viability();
		} catch(IOException IOE) {
			throw new SSproExitException(IOE);
		}
	}

	public void alloc_propagation(int out_fwd, int out_bwd) {
		for(int i=0;i<Num_Sequences;i++){
			Sequences[i].alloc_propagation(out_fwd,out_bwd);
		}
	}

	public void alloc_backpropagation(int out_fwd,int out_bwd) {
		for(int i=0;i<Num_Sequences;i++){
			Sequences[i].alloc_backpropagation(out_fwd,out_bwd);
		}
	}
	
	public void write_predictions(String fileout) {
		//qp("SSpro output file: " + fileout);
		FileOutputStream out_buffer;
		try {
			out_buffer = new FileOutputStream(fileout);
			DataOutputStream os = new DataOutputStream(out_buffer);
			//qp("SSpro 3 of sequences" + Num_Sequences);
			for(int i=0;i<Num_Sequences;i++){
				Sequences[i].write_predictions(os);
			}
			out_buffer.close();
		} catch (IOException IOE) {
			throw new SSproExitException("Cannot write into file \"" + fileout + "\"\n");
		}
	}
	
	public void display_confusion() {
		int i,j, cor=0, n=Num_Classes;
		float acc;
		int[][] conf;
		conf = new int[n][];
		for(i=0;i<n;i++){
			conf[i] = new int[n];
			for(j=0;j<n;j++){
				conf[i][j]=0;
			}
		}
		for(i=0;i<Num_Sequences;i++){
			Sequence s=Sequences[i];
			for(j=0;j<s.Num_Positions;j++){
				conf[s.Classes[j]][s.Predictions[j]]++;
			}
		}
		for(i=0;i<n;i++){
			cor+=conf[i][i];
		}
		acc= (float) ((float)cor / Num_Positions*100.0);
		cerr(cor + "/" + Num_Positions + " correctly classified");
		cerr(" (" + acc + "%)\n");
		for(i=0; i < n; i++){
			cerr("  ");
			for(j=0;j<n;j++){
				cerr(" " + conf[i][j]);
			}
			cerr("\n");
		}
		cerr("\n");
	}
	
	public float empirical_risk() {
		float error=(float) 0.0;
		for(int i=0;i<Num_Sequences;i++){
			Sequence s=Sequences[i];
			for(int p=0;p<s.Num_Positions;p++){
				error+=s.prediction_error(p);
			}
		} return error;
	}
	
	public void check_viability() {
		if((Num_Sequences<=0)||(Num_Positions<=0)||(Sequences == null)){
			throw new SSproExitException("\n\nERROR : the input dataset is empty\n");
		}
	}
	
	public void debugPrintAll() {
		qp("Num_Sequences: " + Num_Sequences);
		qp("Num_Positions: " + Num_Positions);
		qp("Num_Features:  " + Num_Features);
		qp("Num_Classes:   " + Num_Classes);
		for(Sequence seq: Sequences) {
			seq.debugPrintAll();
		}
	}
}
