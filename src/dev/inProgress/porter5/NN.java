package dev.inProgress.porter5;

import assist.Deconstructable;
import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.IStream;
import assist.translation.cplusplus.OStream;

/**
 * NN ver. 3.02 (27/11/2003)
 * Copyright (C) Gianluca Pollastri 2003
 * 
 * One-hidden layer Feedforward neural net.
 * Input: categorical data (one-hot), real valued or mixed.
 * ouput: softmax.
 * Cost function: the proper one
 * Gradient: plain backpropagation (no momentum)
 * 
 * 
 * Version 3.0:
 * compatible with Layer 3.0
 * 
 * Version 3.02
 * Added updateWeightsL1
 * 
 * @translator Benjamin Strauss
 *
 */

public class NN extends CppTranslator implements Deconstructable {
	
	private int NI;
	private	int NIr;
	private	int NItot;
	private	int NH;
	private	int NO;
	private	int[] NK;
	private	int[] NK2;
	private	int which;
	private	int outp;	
	private	int inp;

	private double[] backprop;

	private Layer_soft upper;
	private Layer_tanh lower;
	
	/**
	 * Constructor. Parameters:
	 * Number of input attributes, number of hidden units, number of output units;
	 * t_NK contains the cardinalities of the attribute spans.
	 * 
	 * @param t_NI
	 * @param t_NH
	 * @param t_NO
	 */
	public NN(int t_NI, int t_NH, int t_NO, int[] t_NK)  {
		NI = t_NI;
		NH = t_NH;
		NO = t_NO;
		
		NK=new int[t_NI];
		NItot=0;
		for (int i=0; i<t_NI; i++) {
			NK[i]=t_NK[i];
			NItot += t_NK[i];
		}
		upper= new Layer_soft(t_NO,t_NK,0,t_NH);
		upper.set_output(1);
		upper.set_ninput(2);
		lower= new Layer_tanh(t_NH,t_NK,t_NI);
		lower.set_ninput(1);
		NIr=0;
		outp=1;
		inp=1;
	};
	
	public NN(int t_NI,int t_NIr, int t_NH, int t_NO, int[] t_NK) {
		this(t_NI, t_NIr, t_NH, t_NO, t_NK, 1, 1, 1);
	}
	
	/**
	 * Constructor for a net with mixed inputs.
	 * NI = number of input attributes (categorical inputs)
	 * NIr = number of inputs (real valued)
	 * ..
	 * outp = output or non-output network (for backprop signal)
	 * inp = input or non-inpput network (for backprop signal)
	 * 
	 * @param t_NI
	 * @param t_NIr
	 * @param t_NH
	 * @param t_NO
	 * @param t_NK
	 * @param t_outp
	 */
	public NN(int t_NI,int t_NIr, int t_NH, int t_NO, int[] t_NK,
			int t_outp, int t_inp, int t_which) {
		
		NI = (t_NI);
		NIr = (t_NIr);
		NH = (t_NH); 
		NO = (t_NO);
		outp = (t_outp);
		inp=(t_inp);
		
		
		int i;
		NK=new int[NI];
		NItot=0;
		for (i=0; i<NI; i++) {
			NK[i]=t_NK[i];
			NItot += NK[i];
		}
		NK2=new int[NIr];
		for (i=0; i<NIr; i++) {
			NK2[i]=1;
		}

		which=1;
		upper= new Layer_soft(NO,NK,0,NH);

		if (outp != 0) {
			upper.set_output(1);
		}
		upper.set_ninput(2);

		lower= new Layer_tanh(NH,NK,NI,NIr);
		if (inp != 0) {
			lower.set_ninput(1);
		}
		backprop = new double[NItot+NIr];
	}

	public NN(NN from) {
		NI=from.NI; 
		NIr=from.NIr; 
		NH=from.NH;
		NO=from.NO;
		outp=from.outp;
		inp=from.inp;

		int i;
		NK=new int[NI];
		NItot=0;
		for (i=0; i<NI; i++) {
			NK[i]=from.NK[i];
			NItot += NK[i];
		}
		NK2=new int[NIr];
		for (i=0; i<NIr; i++) {
			NK2[i]=1;
		}

		which = 2;
		upper= new Layer_soft(from.upper);

		if (outp != 0) {
			upper.set_output(1);
		}
		upper.set_ninput(2);

		lower= new Layer_tanh(from.lower);
		if (inp != 0) {
			lower.set_ninput(1);
		}
		backprop=new double[NItot+NIr];
	};

	/**
	 * Create/read a net from file
	 * @param is
	 */
	public NN(IStream is) {
		//original: "is >> NO >> NH >> NI >> NIr >> which >> outp >> inp;"
		NO = is.nextInt();
		NH = is.nextInt();
		NI = is.nextInt();
		NIr = is.nextInt();
		which = is.nextInt();
		outp = is.nextInt();
		inp = is.nextInt();
		  
		upper= new Layer_soft(is);
		if (outp != 0) {
			upper.set_output(1);
		}
		upper.set_ninput(2);

		lower=new Layer_tanh(is);
		lower.set_ninput(inp);

		int i;
		NK=new int[NI];
		NItot=0;
		for (i=0; i<NI; i++) {
			NK[i]=lower.get_NK()[i];
			NItot += NK[i];
		}
		NK2=new int[NIr];
		for (i=0; i<NIr; i++) {
			NK2[i]=lower.get_NK()[NI+i];
		}
		backprop=new double[NItot+NIr];
	}

	public void copy_dW(NN from) {
		upper.copy_dW(from.upper);
		lower.copy_dW(from.lower);
	}

	public void dump_dW(OStream os) {
		upper.dump_dW(os);
		lower.dump_dW(os);
	}
	
	public void dump_W(OStream os) {
		upper.dump_W(os);
		lower.dump_W(os);
	}

	public void read(IStream is) {
		//original: "is >> NO >> NH >> NI >> NIr >> which >> outp >> inp;"
		NO = is.nextInt();
		NH = is.nextInt();
		NI = is.nextInt();
		NIr = is.nextInt();
		which = is.nextInt();
		outp = is.nextInt();
		inp = is.nextInt();

		upper.read(is);
		if (outp != 0) {
			upper.set_output(1);
		}
		upper.set_ninput(2);

		lower.read(is);
		lower.set_ninput(inp);

		int i;
		NItot =0;
		for (i=0; i<NI; i++) {
			NK[i]=lower.get_NK()[i];
			NItot += NK[i];
		}
		for (i=0; i<NIr; i++) {
			NK2[i]=lower.get_NK()[NI+i];
		}
	}

	// Forward pass
	public void forward(int[] I) {
		lower.forward(I);
		upper.forward(lower.out(),lower.out());
	}
	
	public void forward(double[] I) {
		lower.forward(I);
		upper.forward(lower.out(),lower.out());
	}
	
	public void forward(int[] I1, double[] I2) {
		lower.forward(I1,I2);
		upper.forward(lower.out(),lower.out());
	}
	
	public void forward(double[] I1, double[] I2) {
		lower.forward(I1,I2);
		upper.forward(lower.out(),lower.out());
	}
	
	public double f_cost(double[] t) { return upper.f_cost(t); }

	// Backprop
	public double backward(double[] t) { return backward(t, 1.0); }
	
	public double backward(double[] t, double weight) {
		double err = upper.backward(t,weight);
		double[] BKD = new double[1024];
		for (int i=0;i<NH;i++) {
			BKD[i]=upper.back_out()[i];
		}
		lower.backward(BKD,weight);
		if (inp==1) {
			for (int r=NItot;r<NItot+NIr;r++) {
				backprop[r]=lower.back_out()[r];
			}
		} else if (inp==2) {
			for (int r=0;r<NItot+NIr;r++) {
				backprop[r]=lower.back_out()[r];
			}
		}
	  return err;
	}
	public double[] back_out() { return backprop; }

	// Update gradients
	public void gradient(int[] I, double[] t) {
		upper.gradient();
		lower.gradient(I);
	}
	public void gradient(double[] I, double[] t) {
		upper.gradient();
		lower.gradient(I);
	}
	public void gradient(int[] I1, double[] I2, double[] t) {
		upper.gradient();
		lower.gradient(I1,I2);
	}
	public void gradient(double[] I1, double[] I2, double[] t)  {
		upper.gradient();
		lower.gradient(I1,I2);
	}

	// Update weights
	public void updateWeights(double epsilon) {
		lower.updateWeights(epsilon);
		upper.updateWeights(epsilon);
	}
	
	public void updateWeightsL1(double epsilon) {
	  	lower.updateWeightsL1(epsilon);
	  	upper.updateWeightsL1(epsilon);
	}
	
	public void updateWeightsClipped(double epsilon) {
		lower.updateWeightsClipped(epsilon);
		upper.updateWeightsClipped(epsilon);
	}
	
	public void resetGradient() {
		lower.resetGradient();
		upper.resetGradient();
	}
	
	public void initWeights(int seed) {
		  lower.initWeights(seed);
		  upper.initWeights(seed);
	}
	
	public double[] out() { return upper.out(); };
	
	public void write(OStream os) {
		os.write(NO, " ", NH, " " , NI, " " , NIr ," ");
		os.write(which , " " , outp , " " , inp , "\n");
		upper.write(os);
		lower.write(os);
	}

	public void set_input(int vi) {
		lower.set_ninput(vi);
		inp=vi;
	}
	
	public void set_output(int vo) {
		upper.set_output(vo);
		outp=vo;
	}

	public int get_NI() { return NI; };
	public int get_NIr() { return NIr; };
	public int get_NO() { return NO; };
	public int get_NH() { return NH; };

	public double dlength() {
		return upper.dlength()+lower.dlength();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		//delete[] NK;
		//delete[] NK2;
		upper.deconstruct();
		lower.deconstruct();
		//delete[] backprop;
		try {
			finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
