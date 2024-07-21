package dev.inProgress.porter5;

import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.IStream;
import assist.translation.cplusplus.OStream;

/**
 * BRNNr ver. 3.01 (5/11/2003)
 * Copyright (C) Gianluca Pollastri 2003
 * 
 * BRNNr with linear output
 * inputs real valued or categorical
 * 
 * Version 3.0:
 * -compatible with NN,NNt,NNr 3.0
 * -can be used as module in larger architecture (see 'backthrough'
 * and the 'backp' parameter)
 * -full shortcuts now operative
 * 
 * @translator Benjamin Strauss
 *
 */

public class BRNNr extends CppTranslator {
	private static final int MAX = 2500;

	private int NU;
	private int NY;
	private int NH;

	private int context;
	private int Moore;

	private int NF;
	private int NB;
	private int NH2;

	private int CoF;
	private int CoB;
	private int Step;
	private int shortcut;
	private int doubleo;
	private int modular;

	private NNr NetOut;
	//  NN* NetOut2;
	private NNt NetF;
	private NNt NetB;

	private double[][] FF;
	private double[][] BB;
	private double[][] FFbp;
	private double[][] BBbp;

	private double[] P_F;
	private double[] P_B;

	private double[] Y;
	private double[] BP;

	private double error;
	private double errorF;
	private double errorB;

	private double epsilon;

	private void alloc() {
		int t;

		FF = new double[MAX][];
		FFbp = new double[MAX][];
		BB = new double[MAX][];
		BBbp = new double[MAX][];
		P_F = new double[NF];
		P_B = new double[NB];

		for (t=0;t<MAX;t++) {
			FF[t] = new double[NF];
			FFbp[t] = new double[NF];
			BB[t] = new double[NB];
			BBbp[t] = new double[NB];
		}

		Y=new double[MAX*NY];
		BP=new double[MAX*NU];

		for (int f=0;f<NF;f++) {
			P_F[f]=0;
		}
		for (int b=0;b<NB;b++) {
			P_B[b]=0;
		}
	}

	public BRNNr(int NU, int NY, int NH,  int context ,int Moore,
			  int NF, int NB, int NH2, int CoF, int CoB, int Step,
			  int shortcut) {
		this(NU, NY, NH, context, Moore, NF, NB, NH2, CoF, CoB, Step, shortcut, 0);
	}
	
	public BRNNr(int NU, int NY, int NH,  int context, int Moore, int NF, int NB, 
		  int NH2, int CoF, int CoB, int Step, int shortcut, int doubleo) {
		int[] NK = new int[8196];
	
		for (int c=0;c<2*context+1;c++) {
			NK[c]=NU;
		}
		if (Moore != 0) {
			NetOut = new NNr((2*context+1), (2*CoF+1)*NF+(2*CoB+1)*NB, NH, NY, NK);
		} else {
			NetOut = new NNr(0, (2*CoF+1)*NF+(2*CoB+1)*NB, NH, NY, NK);
		}
	
		if (shortcut>0) {
			NetF = new NNt((2*context+1), (shortcut)*NF, NH2, NF, NK, 0);
			NetB = new NNt((2*context+1), (shortcut)*NB, NH2, NB, NK, 0);
		} else {
			NetF = new NNt((2*context+1), NF, NH2, NF, NK, 0);
			NetB = new NNt((2*context+1), NB, NH2, NB, NK, 0);
		}
	
		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);
	
		NetOut.set_output(1);
		NetF.set_output(0);
		NetB.set_output(0);
	
		alloc();
		resetGradient();
	}
	
	public BRNNr(IStream is) {
		//is >> NU >> NY >> NH >> context >> Moore;
		NU = is.nextInt();
		NY = is.nextInt();
		NH = is.nextInt();
		context = is.nextInt();
		Moore = is.nextInt();
		
		//is >> NF >> NB >> NH2 >> CoF >> CoF >> Step >> shortcut >> doubleo;
		NF = is.nextInt();
		NB = is.nextInt();
		NH2 = is.nextInt();
		CoF = is.nextInt();
		CoF = is.nextInt();
		Step = is.nextInt();
		shortcut = is.nextInt();
		doubleo = is.nextInt();

		NetOut = new NNr(is);
		NetF = new NNt(is);
		NetB = new NNt(is);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);

		NetOut.set_output(1);
		NetF.set_output(0);
		NetB.set_output(0);
		
		alloc();
		resetGradient();
	}
	
	public void read(IStream is) {
		//is >> NU >> NY >> NH >> context >> Moore;
		NU = is.nextInt();
		NY = is.nextInt();
		NH = is.nextInt();
		context = is.nextInt();
		Moore = is.nextInt();
		
		//is >> NF >> NB >> NH2 >> CoF >> CoF >> Step >> shortcut >> doubleo;
		NF = is.nextInt();
		NB = is.nextInt();
		NH2 = is.nextInt();
		CoF = is.nextInt();
		CoF = is.nextInt();
		Step = is.nextInt();
		shortcut = is.nextInt();
		doubleo = is.nextInt();

		NetOut.read(is);
		NetF.read(is);
		NetB.read(is);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);
		
		Moore=NetOut.get_NI();
		if (Moore != 0) { Moore=1; }
	}
	
	public void write(OStream os) {
		os.write(NU , " " , NY , " " , NH , " " , context , " " , Moore , "\n");
		os.write(NF," ",NB," ",NH2," ",CoF," ",CoB," ",Step," ",shortcut, " ",doubleo,"\n");

		NetOut.write(os);
		NetF.write(os);
		NetB.write(os);
	}

	public void resetGradient() {
		NetOut.resetGradient();
		NetF.resetGradient();
		NetB.resetGradient();
	}
	
	public void initWeights(int seed) {
		NetOut.initWeights(seed++);
		NetF.initWeights(seed++);
		NetB.initWeights(seed++);
	}

	public void F1_F(double[] seq, int t, int length) {
		
		double[] I = new double[8196];
		int f,s,c;
		
	  	for (c=-context; c<=context; c++) {
		    if (t+c <= 0 || t+c > length) {
		    	for (int i=0;i<NU;i++)
		    		I[NU*(context+c)+i] = 0.0;
		    } else {
			for (int i=0;i<NU;i++)
				I[NU*(context+c)+i] = seq[NU*(t+c)+i];
		    }
	  	}
		double[] X=new double[(1+shortcut)*NF];
		for (f=0; f<NF; f++) {
			X[f] = FF[t-1][f];
		}
		
		for (s=2;s<=shortcut;s++) {
			for (f=(s-1)*NF; f<s*NF; f++) {
				if (t-s>=0) {
					X[f] = FF[t-s][f-(s-1)*NF];
				} else { X[f]=0; }
			}
		}
		
		NetF.forward(I,X);
		for (f=0; f<NF; f++) {
			FF[t][f] = NetF.out()[f];
		}
		//delete[] X;
	}

	public void F1_F(int[] seq, int t, int length) {
		int[] I = new int[8196];
		int f,s,c;
		
		  for (c=-context; c<=context; c++) {
		    if (t+c <= 0 || t+c > length) {
				I[context+c] = 0;
		      }
		    else {
				I[context+c] = seq[t+c];
		      }
		    }
		double[] X=new double[(1+shortcut)*NF];
		for (f=0; f<NF; f++) {
			X[f] = FF[t-1][f];
		}
		
		  for (s=2;s<=shortcut;s++)
		    for (f=(s-1)*NF; f<s*NF; f++) {
			if (t-s>=0)
				X[f] = FF[t-s][f-(s-1)*NF];
			else X[f]=0;
		  }
		
		NetF.forward(I,X);
		for (f=0; f<NF; f++) {
			FF[t][f] = NetF.out()[f];
			}
		//delete[] X;
	}
	
	public void B1_B(double[] seq, int t, int length) {

		double[] I = new double[8196];
		int b,s,c;
		
		  for (c=-context; c<=context; c++) {
		    if (t+c <= 0 || t+c > length) {
			for (int i=0;i<NU;i++)
				I[NU*(context+c)+i] = 0.0;
		      }
		    else {
			for (int i=0;i<NU;i++)
				I[NU*(context+c)+i] = seq[NU*(t+c)+i];
		      }
		    }
		double[] X=new double[(1+shortcut)*NB];
		for (b=0; b<NB; b++) {
			X[b] = BB[t+1][b];
			}
		
		  for (s=2;s<=shortcut;s++)
		    for (b=(s-1)*NB; b<s*NB; b++) {
			if (t+s<=length+1)
				X[b] = BB[t+s][b-(s-1)*NB];
			else X[b]=0;
		    }
		
		NetB.forward(I,X);
		for (b=0; b<NB; b++) {
			BB[t][b] = NetB.out()[b];
			}
		
		//delete[] X;
	}
	
	public void B1_B(int[] seq, int t, int length) {

		int[] I = new int[8196];
		int b,s,c;
		
		  for (c=-context; c<=context; c++) {
		    if (t+c <= 0 || t+c > length) {
				I[context+c] = 0;
		      }
		    else {
				I[context+c] = seq[t+c];
		      }
		    }
		double[] X=new double[(1+shortcut)*NB];
		for (b=0; b<NB; b++) {
			X[b] = BB[t+1][b];
			}
		
		  for (s=2;s<=shortcut;s++)
		    for (b=(s-1)*NB; b<s*NB; b++) {
			if (t+s<=length+1)
				X[b] = BB[t+s][b-(s-1)*NB];
			else X[b]=0;
		    }
		
		NetB.forward(I,X);
		for (b=0; b<NB; b++) {
			BB[t][b] = NetB.out()[b];
			}
		
		//delete[] X;
	}
	
	public void propagate(double[] seq, int length) {
		int T=length;
		int t,f,b;

		for (f=0; f<NF; f++)
			FF[0][f] = P_F[f];
		for (b=0; b<NB; b++)
			BB[T+1][b] = P_B[b];

		for (t=1; t<=T; t++)
			F1_F(seq,t,T);
		for (t=T; t>0; t--)
			B1_B(seq,t,T);
	}
	
	public void propagate(int[] seq, int length) {
		int T=length;
		int t,f,b;

		for (f=0; f<NF; f++)
			FF[0][f] = P_F[f];
		for (b=0; b<NB; b++)
			BB[T+1][b] = P_B[b];

		for (t=1; t<=T; t++)
			F1_F(seq,t,T);
		for (t=T; t>0; t--)
			B1_B(seq,t,T);
	}

	public void forward(double[] seq, int t, int length) {
		int f,b,v,c;
		double[] I = new double[8196];

		// Next cycle sets the input vector
		if (Moore != 0) {
			for (c=-context; c<=context; c++) {
				if (t+c <= 0 || t+c > length) {
					for (int i=0;i<NU;i++) {
						I[NU*(context+c)+i] = 0.0;
					}
				} else {
					for (int i=0;i<NU;i++) {
						I[NU*(context+c)+i] = seq[NU*(t+c)+i];
					}
				}
			}
	  	}

		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB];
		// We now set the hidden inputs
		for (v=-CoF;v<=CoF;v++) {
			if ((t+(Step*v))<0 || (t+(Step*v))>length) {
				for (f=0;f<NF;f++) {
					X[NF*(CoF+v)+f]=0;
				}
			} else {
				for (f=0;f<NF;f++) {
					X[NF*(CoF+v)+f]=FF[t+(Step*v)][f];
				}
		    }
		}

		for (v=-CoB;v<=CoB;v++) {
			if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {
				for (b=0;b<NB;b++) {
					X[NF*(2*CoF+1) + NB*(CoB+v)+b]=0;
				}
			} else {
				for (b=0;b<NB;b++) {
					X[NF*(2*CoF+1) + NB*(CoB+v)+b]=BB[t+(Step*v)][b];
				}
		    }
		}

		NetOut.forward(I,X);

		for (int y=0;y<NY;y++) {
			Y[NY*t+y] = NetOut.out()[y];
		}
		
		//delete[] X;
	}
	
	public void forward(int[] seq, int t, int length) {
		int f,b,v,c;
		int[] I = new int[8196];

		// Next cycle sets the input vector
		if (Moore != 0) {
			for (c=-context; c<=context; c++) {
				if (t+c <= 0 || t+c > length) {
					I[context+c] = 0;
				} else {
					I[context+c] = seq[t+c];
				}
		    }
		}

		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB+1024];
		// We now set the hidden inputs
		for (v=-CoF;v<=CoF;v++) {
			if ((t+(Step*v))<0 || (t+(Step*v))>length) {
				for (f=0;f<NF;f++) {
					X[NF*(CoF+v)+f]=0;
				}
			} else {
				for (f=0;f<NF;f++) {
					X[NF*(CoF+v)+f]=FF[t+(Step*v)][f];
				}
		    }
		}

		for (v=-CoB;v<=CoB;v++) {
			if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {
				for (b=0;b<NB;b++) {
					X[NF*(2*CoF+1) + NB*(CoB+v)+b]=0;
				}
			} else {
				for (b=0;b<NB;b++) {
					X[NF*(2*CoF+1) + NB*(CoB+v)+b]=BB[t+(Step*v)][b];
				}
			}
		}

	  	NetOut.forward(I,X);

	  	for (int y=0;y<NY;y++) {
			Y[NY*t+y] = NetOut.out()[y];
	  	}

		//delete[] X;
	}

	public void F1_Fbp(double[] seq, int t, int length) {
		F1_Fbp(seq, t, length, 0);
	}
	
	public void F1_Fbp(double[] seq, int t, int length, int backp) {
		double[] I = new double[8196];
		int f,s,c;

		for (c=-context; c<=context; c++) {
			if (t+c <= 0 || t+c > length) {
				for (int i=0;i<NU;i++) {
					I[NU*(context+c)+i] = 0.0;
				}
			} else {
				for (int i=0;i<NU;i++) {
					I[NU*(context+c)+i] = seq[NU*(t+c)+i];
				}
			}
		}
		double[] X=new double[(1+shortcut)*NF];
		for (f=0; f<NF; f++) {
			X[f] = FF[t-1][f];
		}

		for (s=2;s<=shortcut;s++) {
			for (f=(s-1)*NF; f<s*NF; f++) {
				if (t-s>=0) {
					X[f] = FF[t-s][f-(s-1)*NF];
				} else { X[f]=0; }
			}
		}

		NetF.forward(I,X);
		
		errorF+=NetF.backward(FFbp[t]);
		for (f=0;f<NF;f++) {
			FFbp[t-1][f] += NetF.back_out()[NU*(2*context+1)+f];
		}

		for (s=2;s<=shortcut;s++) {
			for (f=(s-1)*NF; f<s*NF; f++) {
				if (t-s>=0) {
		    		FFbp[t-s][f-(s-1)*NF] += NetF.back_out()[NU*(2*context+1)+f];
		    	}
			}
		}

		if (backp != 0) {
			for (c=-context; c<=context; c++) {
				if (t+c > 0 && t+c <= length) {
					for (int i=0;i<NU;i++) {
						BP[NU*(t+c)+i]+=NetF.back_out()[NU*(context+c)+i];
					}
				}
		    }
		}
		NetF.gradient(I,X,FFbp[t]);

		//delete[] X;
	}
	
	public void F1_Fbp(int[] seq, int t, int length) {
		F1_Fbp(seq, t, length, 0);
	}
	
	public void F1_Fbp(int[] seq, int t, int length, int backp) {
		int[] I = new int[8196];
		int f,s,c;

		for (c=-context; c<=context; c++) {
			if (t+c <= 0 || t+c > length) {
				I[context+c] = 0;
			} else {
				I[context+c] = seq[t+c];
			}
	    }
		double[] X=new double[(1+shortcut)*NF];
		for (f=0; f<NF; f++) {
			X[f] = FF[t-1][f];
		}

		for (s=2;s<=shortcut;s++) {
			for (f=(s-1)*NF; f<s*NF; f++) {
				if (t-s>=0) {
					X[f] = FF[t-s][f-(s-1)*NF];
				} else { X[f]=0; }
			}
		}

		NetF.forward(I,X);

		errorF+=NetF.backward(FFbp[t]);
		for (f=0;f<NF;f++) {
			FFbp[t-1][f] += NetF.back_out()[NU*(2*context+1)+f];
		}

		for (s=2;s<=shortcut;s++) {
			for (f=(s-1)*NF; f<s*NF; f++) { {
				if (t-s>=0)
					FFbp[t-s][f-(s-1)*NF] += NetF.back_out()[NU*(2*context+1)+f];
				}
			}
		}

		if (backp != 0) {
			for (c=-context; c<=context; c++) {
				if (t+c > 0 && t+c <= length) {
					for (int i=0;i<NU;i++) {
						BP[NU*(t+c)+i]+=NetF.back_out()[NU*(context+c)+i];
					}
				}
			}
		}
		NetF.gradient(I,X,FFbp[t]);

		//delete[] X;
	}
	
	public void B1_Bbp(double[] seq, int t, int length) {
		B1_Bbp(seq, t, length, 0);
	}
	
	public void B1_Bbp(double[] seq, int t, int length, int backp) {
		double[] I = new double[8196];
		int b,s,c;

		for (c=-context; c<=context; c++) {
			if (t+c <= 0 || t+c > length) {
				for (int i=0;i<NU;i++) {
					I[NU*(context+c)+i] = 0.0;
				}
			} else {
				for (int i=0;i<NU;i++) {
					I[NU*(context+c)+i] = seq[NU*(t+c)+i];
				}
			}
	    }
		double[] X=new double[(1+shortcut)*NB];
		for (b=0; b<NB; b++) {
			X[b] = BB[t+1][b];
		}

		for (s=2;s<=shortcut;s++) {
			for (b=(s-1)*NB; b<s*NB; b++) {
				if (t+s<=length+1) {
					X[b] = BB[t+s][b-(s-1)*NB];
				} else { X[b]=0; }
		    }
		}

		NetB.forward(I,X);

		errorB+=NetB.backward(BBbp[t]);
		for (b=0;b<NB;b++) {
			BBbp[t+1][b] += NetB.back_out()[NU*(2*context+1)+b];
		}

		for (s=2;s<=shortcut;s++) {
			for (b=(s-1)*NB; b<s*NB; b++) {
				if (t+s<=length+1) {
					BBbp[t+s][b-(s-1)*NB] += NetB.back_out()[NU*(2*context+1)+b];
				}
			}
		}

		if (backp != 0) {
			for (c=-context; c<=context; c++) {
				if (t+c > 0 && t+c <= length) {
					for (int i=0;i<NU;i++) {
						BP[NU*(t+c)+i]+=NetB.back_out()[NU*(context+c)+i];
					}
				}
			}
		}

		NetB.gradient(I,X,BBbp[t]);
		//delete[] X;
	}
	
	public void B1_Bbp(int[] seq, int t, int length) {
		B1_Bbp(seq, t, length, 0);
	}
	
	public void B1_Bbp(int[] seq, int t, int length, int backp) {
		int[] I = new int[8196];
		int b,s,c;

		for (c=-context; c<=context; c++) {
			if (t+c <= 0 || t+c > length) {
				I[context+c] = 0;
			} else {
				I[context+c] = seq[t+c];
			}
	    }
		double[] X=new double[(1+shortcut)*NB];
		for (b=0; b<NB; b++) {
			X[b] = BB[t+1][b];
		}

	 	for (s=2;s<=shortcut;s++)
		    for (b=(s-1)*NB; b<s*NB; b++) {
		    	if (t+s<=length+1) {
		    		X[b] = BB[t+s][b-(s-1)*NB];
		    	} else { X[b]=0; }
		    }

		NetB.forward(I,X);

		errorB+=NetB.backward(BBbp[t]);
		for (b=0;b<NB;b++) {
			BBbp[t+1][b] += NetB.back_out()[NU*(2*context+1)+b];
		}

		for (s=2;s<=shortcut;s++) {
		    for (b=(s-1)*NB; b<s*NB; b++) {
		    	if (t+s<=length+1) {
		    		BBbp[t+s][b-(s-1)*NB] += NetB.back_out()[NU*(2*context+1)+b];
		    	}
		    }
		}

		if (backp != 0) {
			for (c=-context; c<=context; c++) {
				if (t+c > 0 && t+c <= length) {
					for (int i=0;i<NU;i++) {
						BP[NU*(t+c)+i]+=NetB.back_out()[NU*(context+c)+i];
					}
				}
		    }
		}
		
		NetB.gradient(I,X,BBbp[t]);
	}
	
	public void back_propagate(double[] seq, int length) {
		B1_Bbp(seq, length, 0);
	}
	
	public void back_propagate(double[] seq, int length, int backp) {
		int T=length;
		int t;

		for (t=T; t>0; t--) {
			F1_Fbp(seq,t,T,backp);
		}
		for (t=1; t<=T; t++) {
			B1_Bbp(seq,t,T,backp);
		}
	}
	
	public void back_propagate(int[] seq, int length) {
		B1_Bbp(seq, length, 0);
	}
	
	public void back_propagate(int[] seq, int length, int backp) {
		int T=length;
		int t;

		for (t=T; t>0; t--) {
		F1_Fbp(seq,t,T,backp);
			}
		for (t=1; t<=T; t++) {
			B1_Bbp(seq,t,T,backp);
		}
	}

	public void extimation(double[] seq, int[] y, int length) {
		extimation(seq, y, length, 0);
	}
	public void extimation(double[] seq, int[] y, int length, int backp) {

		// If need backprop through..
		if (backp != 0) {
			NetOut.set_input(2);
			NetF.set_input(2);
			NetB.set_input(2);
			memset(BP,0,MAX*NU);
		}

		int t,f,b,v,c;
		double[] I = new double[8192];
		double[] target=new double[1024];
		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB+1024];

		//cout << length << " "<<flush;

		propagate(seq,length);

		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}

		for (t=1; t<=length; t++) {

			forward(seq,t,length);

			for (c=0; c<NY; c++) {
				target[c]=0.0;
			}
			if (y[t]<0) {
				continue;
			}

			target[y[t]]=1.0;
			error += NetOut.backward(target);

			//cout << error << " " << flush;
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {}
				else {
					for (f=0;f<NF;f++) {
						FFbp[t+(Step*v)][f] += NetOut.back_out()[NU*(2*context+1)+NF*(CoF+v)+f];
					}
				}
			}
			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {}
				else {
					for (b=0;b<NB;b++) {
						BBbp[t+(Step*v)][b] += NetOut.back_out()[NU*(2*context+1)+NF*(2*CoF+1) + NB*(CoB+v)+b];
					}
				}
			}
			if (backp != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c > 0 && t+c <= length) {
						for (int i=0;i<NU;i++) {
							BP[NU*(t+c)+i]+=NetOut.back_out()[NU*(context+c)+i];
						}
					}
				}
			}
			
			// Compute inputs for gradient
			if (Moore != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c <= 0 || t+c > length) {
						for (int i=0;i<NU;i++) {
							I[NU*(context+c)+i] = 0.0;
						}
					} else {
						for (int i=0;i<NU;i++) {
							I[NU*(context+c)+i] = seq[NU*(t+c)+i];
						}
					}
			    }
			}
	
			// We now set the hidden inputs
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {
					for (f=0;f<NF;f++) {
						X[NF*(CoF+v)+f]=0; 
					}
			    } else {
			    	for (f=0;f<NF;f++) {
			    		X[NF*(CoF+v)+f]=FF[t+(Step*v)][f];
			    	}
			    }
			}
	
			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=0;
					}
				} else {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=BB[t+(Step*v)][b];
					}
				}
			}
			NetOut.gradient(I,X,target);
		}

		back_propagate(seq,length,backp);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);

		//delete[] target;
		//delete[] X;
	}
	
	public void extimation(int[] seq, int[] y, int length) {
		extimation(seq, y, length, 0);
	}
	
	public void extimation(int[] seq, int[] y, int length, int backp) {

		// If need backprop through..
		if (backp != 0) {
			NetOut.set_input(2);
			NetF.set_input(2);
		  	NetB.set_input(2);
		  	memset(BP,0,MAX*NU);
		}

		int t,f,b,v,c;
		int[] I = new int[8192];
		double[] target=new double[1024];
		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB];

		propagate(seq,length);

		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}

		for (t=1; t<=length; t++) {

			forward(seq,t,length);

			for (c=0; c<NY; c++) {
				target[c]=0.0;
			}
			if (y[t]<0) {
				continue;
			}

			target[y[t]]=1.0;
			error += NetOut.backward(target);
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {}
				else {
					for (f=0;f<NF;f++) {
						FFbp[t+(Step*v)][f] += NetOut.back_out()[NU*(2*context+1)+NF*(CoF+v)+f];
					}
				}
			}
			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {}
				else {
					for (b=0;b<NB;b++) {
						BBbp[t+(Step*v)][b] += NetOut.back_out()[NU*(2*context+1)+NF*(2*CoF+1) + NB*(CoB+v)+b];
					}	
				}
			}
			if (backp != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c > 0 && t+c <= length) {
						for (int i=0;i<NU;i++) {
							BP[NU*(t+c)+i]+=NetOut.back_out()[NU*(context+c)+i];
						}
					}
				}
			}

			// Compute inputs for gradient
			if (Moore != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c <= 0 || t+c > length) {
						I[context+c] = 0;
					} else {
						I[context+c] = seq[t+c];
					}
				}
			}

			// We now set the hidden inputs
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {
					for (f=0;f<NF;f++) {
						X[NF*(CoF+v)+f]=0;
					}
				} else {
					for (f=0;f<NF;f++) {
						X[NF*(CoF+v)+f]=FF[t+(Step*v)][f];
					}
				}
			}

			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=0;
					}
				} else {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=BB[t+(Step*v)][b];
					}
				}
			}
			NetOut.gradient(I,X,target);
		}

		back_propagate(seq,length,backp);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);

		//delete[] target;
		//delete[] X;
	}

	public void extimation(double[] seq, double[] y, int length) {
		extimation(seq, y, length, 0);
	}
	
	public void extimation(double[] seq, double[] y, int length, int backp) {

		// If need backprop through..
		if (backp != 0) {
			NetOut.set_input(2);
			NetF.set_input(2);
			NetB.set_input(2);
			memset(BP,0,MAX*NU);
		}

		int t,f,b,v,c;
		double[] I = new double[8192];
		double[] target=new double[1024];
		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB+1024];

		//cout << length << " "<<flush;

		propagate(seq,length);

		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}

		for (t=1; t<=length; t++) {

			forward(seq,t,length);

			for (c=0; c<NY; c++) {
				target[c]=y[NY*t+c];
			}
			error += NetOut.backward(target);

			//cout << error << " " << flush;
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {}
				else {
					for (f=0;f<NF;f++) {
						FFbp[t+(Step*v)][f] += NetOut.back_out()[NU*(2*context+1)+NF*(CoF+v)+f];
					}
				}
			}
			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {}
				else {
					for (b=0;b<NB;b++) {
						BBbp[t+(Step*v)][b] += NetOut.back_out()[NU*(2*context+1)+NF*(2*CoF+1) + NB*(CoB+v)+b];
					}
				}
			}
			if (backp != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c > 0 && t+c <= length) {
						for (int i=0;i<NU;i++) {
							BP[NU*(t+c)+i]+=NetOut.back_out()[NU*(context+c)+i];
						}
					}
				}
			}

			// Compute inputs for gradient
			if (Moore != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c <= 0 || t+c > length) {
						for (int i=0;i<NU;i++) {
							I[NU*(context+c)+i] = 0.0;
						}
					} else {
						for (int i=0;i<NU;i++) {
							I[NU*(context+c)+i] = seq[NU*(t+c)+i];
						}
					}
				}
			}

			// We now set the hidden inputs
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {
					for (f=0;f<NF;f++) {
						X[NF*(CoF+v)+f]=0;
					}
				} else {
					for (f=0;f<NF;f++)
						X[NF*(CoF+v)+f]=FF[t+(Step*v)][f];
				}
			}

			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=0;
					}
				} else {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=BB[t+(Step*v)][b];
					}
				}
			}
			NetOut.gradient(I,X,target);
		}

		back_propagate(seq,length,backp);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);

		//delete[] target;
		//delete[] X;
	}
	
	public void extimation(int[] seq, double[] y, int length) {
		extimation(seq, y, length, 0);
	}
	
	public void extimation(int[] seq, double[] y, int length, int backp) {

		// If need backprop through..
		if (backp != 0) {
			NetOut.set_input(2);
			NetF.set_input(2);
			NetB.set_input(2);
			memset(BP,0,MAX*NU);
		}

		int t,f,b,v,c;
		int[] I = new int[8192];
		double[] target=new double[1024];
		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB];

		propagate(seq,length);

		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}

		for (t=1; t<=length; t++) {

			forward(seq,t,length);

			for (c=0; c<NY; c++) {
				target[c]=y[NY*t+c];
			}

			error += NetOut.backward(target);
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {}
				else {
					for (f=0;f<NF;f++) {
						FFbp[t+(Step*v)][f] += NetOut.back_out()[NU*(2*context+1)+NF*(CoF+v)+f];
					}
				}
			}
			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {}
				else {
					for (b=0;b<NB;b++) {
						BBbp[t+(Step*v)][b] += NetOut.back_out()[NU*(2*context+1)+NF*(2*CoF+1) + NB*(CoB+v)+b];
					}
				}
			}
			if (backp != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c > 0 && t+c <= length) {
						for (int i=0;i<NU;i++) {
							BP[NU*(t+c)+i]+=NetOut.back_out()[NU*(context+c)+i];
						}
					}
				}
			}

			// Compute inputs for gradient
			if (Moore != 0) {
				for (c=-context; c<=context; c++) {
					if (t+c <= 0 || t+c > length) {
						I[context+c] = 0;
					} else {
						I[context+c] = seq[t+c];
					}
				}
			}

			// We now set the hidden inputs
			for (v=-CoF;v<=CoF;v++) {
				if ((t+(Step*v))<0 || (t+(Step*v))>length) {
					for (f=0;f<NF;f++) {
						X[NF*(CoF+v)+f]=0;
					}
				} else {
					for (f=0;f<NF;f++) {
						X[NF*(CoF+v)+f]=FF[t+(Step*v)][f];
					}
				}
			}
			
			for (v=-CoB;v<=CoB;v++) {
				if ((t+(Step*v))<1 || (t+(Step*v))>length+1) {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=0;
					}
				} else {
					for (b=0;b<NB;b++) {
						X[NF*(2*CoF+1) + NB*(CoB+v)+b]=BB[t+(Step*v)][b];
					}
				}
			}
			NetOut.gradient(I,X,target);
		}

		back_propagate(seq,length,backp);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);

		//delete[] target;
		//delete[] X;
	}

	public void backthrough(double[] seq, double[] y, int length) {
		backthrough(seq, y, length);
	}
	
	public void backthrough(double[] seq, double[] y, int length, int backp) {
	    NetOut.set_output(0);
	    extimation(seq,y,length,backp);
	    NetOut.set_output(1);
	}
	
	public void backthrough(int[] seq, double[] y, int length) {
		backthrough(seq, y, length, 0);
	}
	
	public void backthrough(int[] seq, double[] y, int length, int backp) {
	    NetOut.set_output(0);
	    extimation(seq,y,length,backp);
	    NetOut.set_output(1);
	}

	public void resetBP(int length) {
		for (int t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}
	}

	public void maximization() {

		NetOut.updateWeights(epsilon);
		NetOut.resetGradient();
		
		NetF.updateWeights(epsilon);
		NetF.resetGradient();
		
		NetB.updateWeights(epsilon);
		NetB.resetGradient();
	
	}
	public void maximizationL1() {

		NetOut.updateWeightsL1(epsilon);
		NetOut.resetGradient();
		
		NetF.updateWeightsL1(epsilon);
		NetF.resetGradient();
		
		NetB.updateWeightsL1(epsilon);
		NetB.resetGradient();
	}
	
	public void Feed(double[] seq, int length) {
		int t;

		propagate(seq,length);

		for (t=1; t<=length; t++) {
			forward(seq,t,length);
			}
	}
	
	public void Feed(int[] seq, int length) {
		int t;

		propagate(seq,length);

		for (t=1; t<=length; t++) {
			forward(seq,t,length);
			}
	}
	public void predict(double[] seq, int length) {
		Feed(seq,length);
	}

	public void predict(int[] seq, int length) {
		Feed(seq,length);
	}

	public double[] out() {return Y;}
	public double[] back_out() {return BP;}
	
	public double getError() {
		return error;
	};
	
	public double getErrorF() {
		return errorF;
	};
	
	public double getErrorB() {
		return errorB;
	};
	
	public void resetError() {
		error=0.0;
		errorF=0.0;
		errorB=0.0;
	};

	public void setEpsilon(double eps) { epsilon=eps; };
};
