package modules.descriptor.vkbat.sspro5_2.brnn;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import assist.translation.cplusplus.CppTranslator;
import modules.descriptor.vkbat.sspro5_2.SSproExitException;
import assist.Deconstructable;

/****************************************************************************************
/*																						*
/*	Project	    : 1D-BRNN																*
/*	Release	    : 3.3																	*
/*																						*
/*	File		:	Network.h															*
/*	Description :	Single neural network with one hidden layer							*
/*																						*
/*	Author(s)   :	Christophe Magnan  (2013) - New generic version						*
/*				:	Jianlin Cheng	   (2003) - New custom version for SCRATCH			*
/*				:	Gianluca Pollastri (2001) - Customized version for SCRATCH			*
/*				:	Paolo Frasconi     (1997) - Initial generic version					*
/*																						*
/*	Copyright	:	Institute for Genomics and Bioinformatics							*
/*					University of California, Irvine									*
/*																						*
/*	Modified	:	2015/07/01														    *
/*	Translated to Java by Benjamin Strauss (2019)										*
/****************************************************************************************/

public class Network extends CppTranslator implements Deconstructable {

	// Network parameters
	public int Num_Inputs;			// Number of features in input of the network
	public int Num_Hidden;			// Number of hidden nodes in the network
	public int Num_Outputs;			// Number of output nodes in the network

	// Network data
	public Layer Output_Layer;		// Output layer of the network
	public Layer Hidden_Layer;		// Hidden layer of the network

	// Propagation data
	public float[] Outputs;			// Outputs of the network after the last propagation

	// Back-Propagation data
	public float[] Back_Prop;		// Next values to back-propagate in lower networks

	// Interface
	public Network() {
		Num_Inputs = -1;
		Num_Hidden = -1;
		Num_Outputs = -1;
		Output_Layer = null;
		Hidden_Layer = null;
		Outputs = null;
		Back_Prop = null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		Output_Layer.deconstruct();
		Hidden_Layer.deconstruct();
		try {
			finalize();
		} catch (Throwable e) { }
	}
	
	public void write(DataOutputStream os) throws IOException {
		os.writeChars(Num_Outputs + " " + Num_Hidden + " " + Num_Inputs + "\n");
		Output_Layer.write(os);
		Hidden_Layer.write(os);
	}
	
	public void load(Scanner is) throws IOException {
		Num_Outputs = is.nextInt();
		Num_Hidden  = is.nextInt();
		Num_Inputs  = is.nextInt();
		Output_Layer=new Layer();
		Output_Layer.load(is);
		Hidden_Layer=new Layer();
		Hidden_Layer.load(is);
		check_viability();
	}
	
	public void initialize(int inputs,int hidden,int outputs) {
		Num_Inputs=inputs; Num_Hidden=hidden; Num_Outputs=outputs;
		Output_Layer=new Layer();
		Output_Layer.initialize(Num_Outputs,Num_Hidden);
		Hidden_Layer=new Layer();
		Hidden_Layer.initialize(Num_Hidden,Num_Inputs);
	}

	
	public void alloc_backpropagation() {
		Output_Layer.alloc_backpropagation();
		Hidden_Layer.alloc_backpropagation();
	}

	
	public void reset_gradient() {
		Hidden_Layer.reset_gradient();
		Output_Layer.reset_gradient();
	}
	
	public void propagate(float[] inputs) {
		Hidden_Layer.propagate(inputs);
		Output_Layer.propagate(Hidden_Layer.SigApp_Outputs);
		Outputs=Output_Layer.SigApp_Outputs;
	}
	
	public void back_propagate(float[] target) {
		Output_Layer.back_propagate(target);
		Hidden_Layer.back_propagate(Output_Layer.Back_Prop);
		Back_Prop = Hidden_Layer.Back_Prop;
	}
	
	public void update_gradient(float[] inputs) {
		Output_Layer.update_gradient(Hidden_Layer.SigApp_Outputs);
		Hidden_Layer.update_gradient(inputs);
	}
	
	public void update_weights(float epsilon) {
		Hidden_Layer.update_weights(epsilon);
		Output_Layer.update_weights(epsilon);
	}
	
	public void check_viability(){
		int v=1;
		if(Output_Layer.Num_Nodes!=Num_Outputs){ v=0; }
		if(Hidden_Layer.Num_Nodes!=Num_Hidden){ v=0; }
		if(Output_Layer.Num_Inputs!=Num_Hidden){ v=0; }
		if(Hidden_Layer.Num_Inputs!=Num_Inputs){ v=0; }
		if(v == 0){
			throw new SSproExitException("Inconsistent model, cannot proceed\n");
		}
	}
}
