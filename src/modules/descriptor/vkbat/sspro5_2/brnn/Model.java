package modules.descriptor.vkbat.sspro5_2.brnn;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import assist.translation.cplusplus.CppTranslator;
import modules.descriptor.vkbat.sspro5_2.SSproExitException;
import assist.Deconstructable;

/****************************************************************************************
 *                                                                                      *
 *  Project     :  1D-BRNN                                                              *
 *  Release     :  3.3                                                                  *
 *                                                                                      *
 *  File        :  Model.h                                                              *
 *  Description :  BRNN model with three neural networks                                *
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
 *  translated to Java by Benjamin Strauss                                              *
 ****************************************************************************************/

public class Model extends CppTranslator implements Deconstructable {

	// Model parameters
	public int Num_Features;        // Number of data features in input of the BRNN
	public int Num_Classes;         // Number of target classes in output of the BRNN
	public int Num_Hidden;          // Number of hidden nodes in the main network of the BRNN
	public int Num_AdjPos_FWD;      // Number of adjacent positions s.t. outputs of forward network . inputs of main network
	public int Num_AdjPos_BWD;      // Number of adjacent positions s.t. outputs of backward network . inputs of main network
	public int Num_Outputs_FWD;     // Number of output nodes in the forward network of the BRNN
	public int Num_Outputs_BWD;     // Number of output nodes in the backward network of the BRNN
	public int Num_Hidden_FWD;      // Number of hidden nodes in the forward network of the BRNN
	public int Num_Hidden_BWD;      // Number of hidden nodes in the backward network of the BRNN

	// Model data
	public Network MAINnet;        // Main network of the BRNN (with output layer)
	public Network FWDnet;         // Forward network of the BRNN (left context)
	public Network BWDnet;         // Backward network of the BRNN (right context)

	// Propagation data
	public float[] Inputs_MAIN;      // Next inputs to propagate in the main network of the BRNN
	public float[] Inputs_FWD;       // Next inputs to propagate in the forward network of the BRNN
	public float[] Inputs_BWD;       // Next inputs to propagate in the backward network of the BRNN

	// Interface
	public Model() {
		Num_Features = -1;
		Num_Classes = -1;
		Num_Hidden = -1;
		Num_AdjPos_FWD = -1;
		Num_AdjPos_BWD = -1;
		Num_Outputs_FWD = -1;
		Num_Outputs_BWD = -1;
		Num_Hidden_FWD = -1;
		Num_Hidden_BWD = -1;
		MAINnet = null;
		FWDnet = null;
		BWDnet = null;
		Inputs_MAIN = null;
		Inputs_FWD = null;
		Inputs_BWD = null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		FWDnet.deconstruct();
		BWDnet.deconstruct();
		MAINnet.deconstruct();
		try {
			finalize();
		} catch (Throwable e) {	}
	}
	
	public void write(String fileout) {
		qp("writing");
		FileOutputStream out_buffer;
		try {
			out_buffer = new FileOutputStream(fileout);
			DataOutputStream os = new DataOutputStream(out_buffer);
			
			os.writeChars(Num_Features + " " + Num_Classes + " " + Num_Hidden + "\n");
			os.writeChars(Num_AdjPos_FWD + " " + Num_Outputs_FWD + " " + Num_Hidden_FWD + "\n");
			os.writeChars(Num_AdjPos_BWD + " " + Num_Outputs_BWD + " " + Num_Hidden_BWD + "\n");
			MAINnet.write(os);
			FWDnet.write(os);
			BWDnet.write(os);
			out_buffer.close();
		} catch (IOException IOE) {
			throw new SSproExitException(IOE);
		}
		
	}

	public void load(String filein) {
		FileReader in_buffer;
		try {
			in_buffer = new FileReader(filein);
			Scanner is = new Scanner(in_buffer);
			Num_Features = is.nextInt();
			Num_Classes = is.nextInt();
			Num_Hidden = is.nextInt();
			Num_AdjPos_FWD = is.nextInt();
			Num_Outputs_FWD = is.nextInt();
			Num_Hidden_FWD = is.nextInt();
			Num_AdjPos_BWD = is.nextInt();
			Num_Outputs_BWD = is.nextInt();
			Num_Hidden_BWD = is.nextInt();
		  	MAINnet=new Network();
		  	MAINnet.load(is);
		  	FWDnet=new Network();
		  	FWDnet.load(is);
		  	BWDnet=new Network();
		  	BWDnet.load(is);
		  	Inputs_MAIN=new float[MAINnet.Num_Inputs];
		  	Inputs_FWD=new float[FWDnet.Num_Inputs];
		  	Inputs_BWD=new float[BWDnet.Num_Inputs];
		  	in_buffer.close();
		  	check_viability();
		} catch (IOException IOE) {
			throw new SSproExitException(IOE);
		}
	}
	
	public void initialize(int feat,int cl,int hid,int cF,int cB,int oF,int oB,int hF,int hB) {
		Num_Features=feat;
		Num_Classes=cl;
		Num_Hidden=hid;
		Num_AdjPos_FWD=cF;
		Num_Outputs_FWD=oF;
		Num_Hidden_FWD=hF;
		Num_AdjPos_BWD=cB;
		Num_Outputs_BWD=oB;
		Num_Hidden_BWD=hB;
		MAINnet=new Network();
		MAINnet.initialize(feat+(2*cF+1)*oF+(2*cB+1)*oB,hid,cl);
		FWDnet=new Network(); 
		FWDnet.initialize(feat+oF,hF,oF);
		BWDnet=new Network();
		BWDnet.initialize(feat+oB,hB,oB);
		MAINnet.Output_Layer.IsOutputLay=1;
		Inputs_MAIN = new float[MAINnet.Num_Inputs];
		FWDnet.Output_Layer.IsOutputLay=0;
		Inputs_FWD = new float[FWDnet.Num_Inputs];
		BWDnet.Output_Layer.IsOutputLay=0;
		Inputs_BWD = new float[BWDnet.Num_Inputs];
	}
	
	public void alloc_backpropagation() {
		MAINnet.alloc_backpropagation();
		FWDnet.alloc_backpropagation();
		BWDnet.alloc_backpropagation();
	}
	
	public void propagate(Sequence seq) {
		int length=seq.Num_Positions;
		for(int p=0;p<length;p++){
			propagate_FWD(seq,p);
		}
		for(int p=length-1;p>=0;p--){
			propagate_BWD(seq,p);
		}
		for(int p=0;p<length;p++){
			propagate_MAIN(seq,p);
			float max=0;
			int cl=0;
			for(int i=0;i<Num_Classes;i++){
				float pb=seq.Outputs_MAIN[p][i];
				if(pb>max){
					max=pb; cl=i;
				}
			}
			seq.Predictions[p]=cl;
		}
	}
	
	public void expectation(Sequence seq) {
		int length=seq.Num_Positions;
		seq.reset_backpropagation(Num_Outputs_FWD,Num_Outputs_BWD);
		for(int p=0;p<length;p++){
			propagate_FWD(seq,p);
		}
		for(int p=length-1;p>=0;p--){
			propagate_BWD(seq,p);
		}
		for(int p=0;p<length;p++){
			propagate_MAIN(seq,p);
			if(seq.prediction_error(p)>0){
				back_propagate_MAIN(seq,p);
			}
		}
		for(int p=length-1;p>=0;p--){
			back_propagate_FWD(seq,p);
		}
		for(int p=0;p<length;p++){
			back_propagate_BWD(seq,p);
		}
	}
	
	public void maximization(float epsilon) {
		MAINnet.update_weights(epsilon);
		MAINnet.reset_gradient();
		FWDnet.update_weights(epsilon);
		FWDnet.reset_gradient();
		BWDnet.update_weights(epsilon);
		BWDnet.reset_gradient();
	}

	// Methods
	private void propagate_FWD(Sequence seq,int pos) {
		int nf=Num_Features;
		int nof=Num_Outputs_FWD;
		for(int i=0;i<nf;i++){
			Inputs_FWD[i]=seq.Features[pos][i];
		}
		if((pos-1)<0){
			for(int i=0;i<nof;i++){
				Inputs_FWD[nf+i]=(float) 0.0;
			}
		} else{
			for(int i=0;i<nof;i++){
				Inputs_FWD[nf+i]=seq.Outputs_FWD[pos-1][i];
			}
		}
		FWDnet.propagate(Inputs_FWD);
		for(int i=0;i<nof;i++){
			seq.Outputs_FWD[pos][i]=FWDnet.Outputs[i];
		}
	}
	
	private void propagate_BWD(Sequence seq,int pos) {
		int nf=Num_Features;
		int nob=Num_Outputs_BWD;
		for(int i=0;i<nf;i++){
			Inputs_BWD[i]=seq.Features[pos][i];
		}
		if((pos+1)>=seq.Num_Positions){
			for(int i=0;i<nob;i++){
				Inputs_BWD[nf+i]=(float) 0.0;
			}
		} else{
			for(int i=0;i<nob;i++){
				Inputs_BWD[nf+i]=seq.Outputs_BWD[pos+1][i];
			}
		}
		BWDnet.propagate(Inputs_BWD);
		for(int i=0;i<nob;i++){
			seq.Outputs_BWD[pos][i]=BWDnet.Outputs[i];
		}
	}
	
	private void propagate_MAIN(Sequence seq, int pos) {
		int nf=Num_Features;
		int nof=Num_Outputs_FWD;
		int nob=Num_Outputs_BWD;
		int length=seq.Num_Positions;
		int index=nf;
		for(int i=0;i<nf;i++){
			Inputs_MAIN[i]=seq.Features[pos][i];
		}
		for(int p=(pos-Num_AdjPos_FWD); p<=(pos+Num_AdjPos_FWD); p++){
			if((p<0)||(p>=length)){
				for(int i=0;i<nof;i++){
					Inputs_MAIN[index+i]=(float) 0.0;
				}
			} else{
				for(int i=0;i<nof;i++){
					Inputs_MAIN[index+i]=seq.Outputs_FWD[p][i];
				}
			} index+=nof;
		}
		for(int p=(pos-Num_AdjPos_BWD);p<=(pos+Num_AdjPos_BWD);p++){
			if((p<0)||(p>=length)){
				for(int i=0;i<nob;i++){
					Inputs_MAIN[index+i]=(float) 0.0;
				}
			} else{
				for(int i=0;i<nob;i++){
					Inputs_MAIN[index+i]=seq.Outputs_BWD[p][i];
				}
			}
			index+=nob;
		}
		MAINnet.propagate(Inputs_MAIN);
		for(int i=0;i<Num_Classes;i++){	
			seq.Outputs_MAIN[pos][i]=MAINnet.Outputs[i];
		}
	}

	private void back_propagate_FWD(Sequence seq,int pos) {
		propagate_FWD(seq,pos);
		FWDnet.back_propagate(seq.BackProp_FWD[pos]);
		if((pos-1)>=0){
			for(int i=0;i<Num_Outputs_FWD;i++){
				seq.BackProp_FWD[pos-1][i]+=FWDnet.Back_Prop[i];
			}
		}
		FWDnet.update_gradient(Inputs_FWD);
	}
	
	private void back_propagate_BWD(Sequence seq,int pos) {
		propagate_BWD(seq,pos);
		BWDnet.back_propagate(seq.BackProp_BWD[pos]);
		if((pos+1)<seq.Num_Positions){
			for(int i=0;i<Num_Outputs_BWD;i++){
				seq.BackProp_BWD[pos+1][i]+=BWDnet.Back_Prop[i];
			}
		}
		BWDnet.update_gradient(Inputs_BWD);
	}
	
	private void back_propagate_MAIN(Sequence seq, int pos) {
		float[] Target = seq.Targets[pos];
		int length=seq.Num_Positions;
		int index=Num_Features;
		int nof=Num_Outputs_FWD;
		int nob=Num_Outputs_BWD;
		MAINnet.back_propagate(Target);
		MAINnet.update_gradient(Inputs_MAIN);
		for(int p=(pos-Num_AdjPos_FWD); p<=(pos+Num_AdjPos_FWD); p++) {
			if((p>=0)&&(p<length)){
				for(int i=0;i<nof;i++){
					seq.BackProp_FWD[p][i] += MAINnet.Back_Prop[index+i];
				}
			}
			index+=nof;
		}
		for(int p=(pos-Num_AdjPos_BWD);p<=(pos+Num_AdjPos_BWD);p++){
			if((p>=0)&&(p<length)){
				for(int i=0;i<nob;i++){
					seq.BackProp_BWD[p][i]+=MAINnet.Back_Prop[index+i];
				}
			}
			index+=nob;
		}
	}
	
	private void check_viability() {
		int v=1;
		int nF=(2*Num_AdjPos_FWD+1)*Num_Outputs_FWD;
		int nB=(2*Num_AdjPos_BWD+1)*Num_Outputs_BWD;
		if(MAINnet.Num_Inputs!=(Num_Features+nF+nB)){ v=0; }
		if(MAINnet.Num_Hidden!=Num_Hidden){ v=0; }
		if(MAINnet.Num_Outputs!=Num_Classes){ v=0; }
		if(MAINnet.Output_Layer.IsOutputLay!=1){ v=0; }
		if(MAINnet.Hidden_Layer.IsOutputLay!=0){ v=0; }
		if(FWDnet.Num_Inputs!=(Num_Features+Num_Outputs_FWD)){ v=0; }
		if(FWDnet.Num_Hidden!=Num_Hidden_FWD){ v=0; }
		if(FWDnet.Num_Outputs!=Num_Outputs_FWD){ v=0; }
		if(FWDnet.Output_Layer.IsOutputLay!=0){ v=0; }
		if(FWDnet.Hidden_Layer.IsOutputLay!=0){ v=0; }
		if(BWDnet.Num_Inputs!=(Num_Features+Num_Outputs_BWD)){ v=0; }
		if(BWDnet.Num_Hidden!=Num_Hidden_BWD){ v=0; }
		if(BWDnet.Num_Outputs!=Num_Outputs_BWD){ v=0; }
		if(BWDnet.Output_Layer.IsOutputLay!=0){ v=0; }
		if(BWDnet.Hidden_Layer.IsOutputLay!=0){ v=0; }
		if(v == 0){
			throw new SSproExitException("Inconsistent model, cannot proceed\n");
		}
	}
}
