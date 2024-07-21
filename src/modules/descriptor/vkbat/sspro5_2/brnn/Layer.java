package modules.descriptor.vkbat.sspro5_2.brnn;

import java.io.DataOutputStream;
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
/*  File        :  Layer.h                                                              *
/*  Description :  Single layer of a neural network                                     *
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

public class Layer extends CppTranslator implements Deconstructable {
	
	// Layer parameters
	public int Num_Nodes;             // Number of nodes in the layer
	public int Num_Inputs;            // Number of features in input of the layer
	public int IsOutputLay;           // Flag for the output layer of the BRNN

	// Layer data
	public float[] Bias;               // Bias value associated with each node in the layer
	public float[][] Weights;           // Matrix of weights associated with each node in the layer

	// Propagation data
	public float[] Direct_Outputs;     // Outputs of the layer after the last propagation
	public float[] SigApp_Outputs;     // Sigmoid approximation of the direct outputs

	// Back-Propagation data
	public float[] Gradient;           // Gradient contributions computed during the last back-propagation
	public float[] Back_Prop;          // Next values to back-propagate in lower layers
	public float[] dBias;              // Gradients of the Bias
	public float[][] dWeights;          // Gradients of the Weights

	// Interface

	public Layer() {
		Num_Nodes = 0;
		Num_Inputs = 0;
		IsOutputLay = 0;
		Bias= null;
		Weights= null;
		Direct_Outputs= null;
		SigApp_Outputs= null;
		Gradient= null;
		Back_Prop= null;
		dBias= null;
		dWeights= null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) { }
	}
			
	public void write(DataOutputStream os) throws IOException {
		os.writeChars(Num_Nodes + " " + Num_Inputs + " " + IsOutputLay + "\n");
		for(int n=0;n<Num_Nodes;n++){
			for(int i=0;i<Num_Inputs;i++){
				os.writeChars(Weights[n][i] + " ");
			}
			os.writeChars(Bias[n] + "\n");
		}
	}

	public void load(Scanner is) throws IOException {
		Num_Nodes = is.nextInt();
		Num_Inputs = is.nextInt();
		IsOutputLay = is.nextInt();
		Bias=new float[Num_Nodes];
		Weights= new float[Num_Nodes][];
		for(int n=0;n<Num_Nodes;n++){
			Weights[n]=new float[Num_Inputs];
			for(int i=0;i<Num_Inputs;i++){
				Weights[n][i] = is.nextFloat();
			}
			Bias[n] = is.nextFloat();
		}
		check_viability();
		Direct_Outputs=new float[Num_Nodes];
		SigApp_Outputs=new float[Num_Nodes];
	}
	
	public void initialize(int nodes, int inputs) {
		Num_Nodes=nodes;
		Num_Inputs=inputs;
		IsOutputLay=0;
		drand48();
		Bias=new float[Num_Nodes];
		Weights=new float[Num_Nodes][];
		for(int n=0;n<Num_Nodes;n++) {
			Bias[n]= (float) ((0.5-drand48())/10.0);
			Weights[n]=new float[Num_Inputs];
			for(int i=0;i<Num_Inputs;i++){
				Weights[n][i]=(float) ((0.5-drand48())/10.0);
			}
		}
		Direct_Outputs=new float[Num_Nodes];
		SigApp_Outputs=new float[Num_Nodes];
	}
	
	public void alloc_backpropagation() {
		Gradient=new float[Num_Nodes];
		Back_Prop=new float[Num_Inputs];
		dBias=new float[Num_Nodes];
		dWeights=new float[Num_Nodes][];
		for(int n=0;n<Num_Nodes;n++){
			dWeights[n]=new float[Num_Inputs];
		}
		reset_gradient();
	}

	public void reset_gradient() {
		for(int n=0;n<Num_Nodes;n++){ 
			for(int i=0;i<Num_Inputs;i++){
				dWeights[n][i]=(float) 0.0;
			}
			dBias[n]=(float) 0.0;
		}
	}
	
	public void propagate(float[] inputs) {
		for(int n=0;n<Num_Nodes;n++){
			Direct_Outputs[n]=Bias[n];
			for(int i=0;i<Num_Inputs;i++){
				Direct_Outputs[n]+=Weights[n][i]*inputs[i];
			}
			SigApp_Outputs[n]=(float)tanh(Direct_Outputs[n]);
		} if(IsOutputLay != 0){
			float norm=0,max=Direct_Outputs[0];
			int overflow=0;
			int argmax=0;
			for(int n=0;n<Num_Nodes;n++){
				float v=Direct_Outputs[n];
				if(v>max){
					max=v;
					argmax=n;
				} if(v>85){
					overflow=1;
				} else{
					norm+=(float)exp(v);
				}
			}
			for(int n=0;n<Num_Nodes;n++){
				if(overflow != 0){
					SigApp_Outputs[n]=(float) (n == argmax?1.0:0.0);
				} else{
					SigApp_Outputs[n]=(float)exp(Direct_Outputs[n])/norm;
				}
			}
		}
	}
	
	public void back_propagate(float[] target) {
		if(IsOutputLay != 0) {
			int overflow=0;
			for(int n=0;n<Num_Nodes;n++){
				if(Direct_Outputs[n]>85){ overflow=1; }
			}
			for(int n=0;n<Num_Nodes;n++){
				if(overflow != 0){
					Gradient[n]=0;
				} else{
					float app=SigApp_Outputs[n];
					Gradient[n]=(app-target[n])*app*(1-app);
				}
			}
		} else {
			for(int n=0;n<Num_Nodes;n++){
				float app=SigApp_Outputs[n];
				Gradient[n]=(float) (target[n]*(1.0-app*app));
			}
		}
		for(int i=0;i<Num_Inputs;i++) {
			Back_Prop[i]=(float) 0.0;
			for(int n=0;n<Num_Nodes;n++){
				Back_Prop[i]+=Weights[n][i]*Gradient[n];
			}
		}
	}
	
	public void update_gradient(float[] inputs) {
		for(int n=0;n<Num_Nodes;n++){
			for(int i=0;i<Num_Inputs;i++){
				dWeights[n][i]+=Gradient[n]*inputs[i];
			}
			dBias[n]+=Gradient[n];
		}
	}

	public void update_weights(float epsilon) {
		for(int n=0;n<Num_Nodes;n++){
			for(int i=0;i<Num_Inputs;i++){
				Weights[n][i]-=epsilon*dWeights[n][i];
			}
			Bias[n]-=epsilon*dBias[n];
		}
	}
	
	public void check_viability() {
		int v=1;
		if((Num_Nodes<1)||(Num_Inputs<1)){ v=0; }
		if((IsOutputLay<0)||(IsOutputLay>1)){ v=0; }
		if(v == 0){ throw new SSproExitException("Inconsistent model, cannot proceed\n"); }
	}
}
