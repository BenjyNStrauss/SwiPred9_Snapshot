package dev.inProgress.porter5;

import assist.Deconstructable;
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

public class BRNN extends CppTranslator implements Deconstructable {
	public int MAX;

	public int NU;
	public int NY;
	public int NH;

	public int context;
	public int Moore;

	public int NF;
	public int NB;
	public int NH2;

	public int CoF;
	public int CoB;
	public int Step;
	public int shortcut;
	public int doubleo;
	public int modular;

	public NN NetOut;
//  NN* NetOut2;
	public NNt NetF;
	public NNt NetB;

	public double[][] FF;
	public double[][] BB;
	public double[][] FFbp;
	public double[][] BBbp;

	public double[] P_F;
	public double[] P_B;

	public double[] Y;
	public double[] BP;

	public double error;
	public double errorF;
	public double errorB;

	public double epsilon;

	public void alloc(int the_MAX) {
		MAX = the_MAX;
		
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
		
		for (int f=0;f<NF;f++) {
			P_F[f]=0;
		}
		for (int b=0;b<NB;b++) {
			P_B[b]=0;
		}
	}
	
	public void dealloc() {
		/*int t;

		for (t=0;t<MAX;t++) {
			delete[] FF[t];
			delete[] FFbp[t];
			delete[] BB[t];
			delete[] BBbp[t];
		}

		delete[] FF;
		delete[] FFbp;
		delete[] BB;
		delete[] BBbp;
		delete[] P_F;
		delete[] P_B;

		delete[] Y;*/
	}
	
	public BRNN(int NU, int NY, int NH,  int context ,int Moore,
			int NF, int NB, int NH2, int CoF, int CoB, int Step,
			int shortcut) {
		this(NU, NY, NH, context, Moore, NF, NB, NH2, CoF, CoF, Step, shortcut, 0);
	}
	
	public BRNN(int NU, int NY, int NH,  int context ,int Moore,
		int NF, int NB, int NH2, int CoF, int CoB, int Step,
		int shortcut, int doubleo) {

		int[] NK = new int[8196];
		
		for (int c=0;c<2*context+1;c++) {
			NK[c]=NU;
		}
		if (Moore != 0) {
			NetOut = new NN((2*context+1), (2*CoF+1)*NF+(2*CoB+1)*NB, NH, NY, NK);
		} else {
			NetOut = new NN(0, (2*CoF+1)*NF+(2*CoB+1)*NB, NH, NY, NK);
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
		
		alloc(8196);
		resetGradient();
	}
	
	public BRNN(IStream is) {
		//is >> NU >> NY >> NH >> context >> Moore;
		NU = is.nextInt();
		NY = is.nextInt();
		NH = is.nextInt();
		context = is.nextInt();
		Moore = is.nextInt();
		
		//is >> NF >> NB >> NH2 >> CoF >> CoB >> Step >> shortcut >> doubleo;
		NF = is.nextInt();
		NB = is.nextInt();
		NH2 = is.nextInt();
		CoF = is.nextInt();
		CoB = is.nextInt();
		Step = is.nextInt();
		shortcut = is.nextInt();
		doubleo = is.nextInt();

		NetOut = new NN(is);
		NetF = new NNt(is);
		NetB = new NNt(is);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);

		NetOut.set_output(1);
		NetF.set_output(0);
		NetB.set_output(0);

		alloc(8192);
		resetGradient();
	}
	
	public BRNN(BRNN from, int seq_length) {
		NU=from.NU;
		NY=from.NY;
		NH=from.NH;
		context=from.context;
		Moore=from.Moore;
		NF=from.NF;
		NB=from.NB;
		NH2=from.NH2;
		CoF=from.CoF;
		CoB=from.CoB;
		Step=from.Step;
		shortcut=from.shortcut;
		doubleo=from.doubleo;
		
		int[] NK = new int[8196];
		
		for (int c=0;c<2*context+1;c++) {
			NK[c]=NU;
		}
		NetOut = new NN(from.NetOut);
		NetF = new NNt(from.NetF);
		NetB = new NNt(from.NetB);
		
		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);
		
		NetOut.set_output(1);
		NetF.set_output(0);
		NetB.set_output(0);
		
		alloc(seq_length+1);
		resetGradient();
	}
	
	public void read(IStream is) {
		//is >> NU >> NY >> NH >> context >> Moore;
		NU = is.nextInt();
		NY = is.nextInt();
		NH = is.nextInt();
		context = is.nextInt();
		Moore = is.nextInt();
		
		//is >> NF >> NB >> NH2 >> CoF >> CoB >> Step >> shortcut >> doubleo;
		NF = is.nextInt();
		NB = is.nextInt();
		NH2 = is.nextInt();
		CoF = is.nextInt();
		CoB = is.nextInt();
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
		if (Moore != 0) Moore=1;
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

	public void copy_dW(BRNN from) {
		NetOut.copy_dW(from.NetOut);
		NetF.copy_dW(from.NetF);
		NetB.copy_dW(from.NetB);
		error += from.error;
		errorF += from.errorF;
		errorB += from.errorB;
	}
	
	//TODO there could be an issue with integer operations here
	public void F1_F(double[] seq, int t, int length) {
		int[] I = new int[8196];
		int f,s,c;
		
		for (c=-context; c<=context; c++) {
		    if (t+c <= 0 || t+c > length) {
		    	for (int i=0;i<NU;i++) {
		    		I[NU*(context+c)+i] = (int) 0.0;
		    	}
		    } else {
		    	for (int i=0;i<NU;i++) {
		    		I[NU*(context+c)+i] = (int) seq[NU*(t+c)+i];
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
		for (b=0; b<NB; b++) {
			BB[t][b] = NetB.out()[b];
		}
		
		//delete[] X;
	}
	
	public void B1_B(int[] seq, int t, int length) {
		double[] I = new double[8196];
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
		for (b=0; b<NB; b++) {
			BB[t][b] = NetB.out()[b];
		}
		
		//delete[] X;
	}
	
	public void propagate(double[] seq, int length) {
		int T=length;
		int t,f,b;

		for (f=0; f<NF; f++) {
			FF[0][f] = P_F[f];
		}
		for (b=0; b<NB; b++) {
			BB[T+1][b] = P_B[b];
		}

		for (t=1; t<=T; t++) {
			F1_F(seq,t,T);
		}
		for (t=T; t>0; t--) {
			B1_B(seq,t,T);
		}
	}
	
	public void propagate(int[] seq, int length) {
		int T=length;
		int t,f,b;

		for (f=0; f<NF; f++) {
			FF[0][f] = P_F[f];
		}
		for (b=0; b<NB; b++) {
			BB[T+1][b] = P_B[b];
		}

		for (t=1; t<=T; t++) {
			F1_F(seq,t,T);
		}
		for (t=T; t>0; t--) {
			B1_B(seq,t,T);
		}
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

	public void forward(double[] seq, int t1, int t2, int length) {
		int f,b,v,c;
		double[] I = new double[8196];

		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB];

		for (int t=t1;t<t2;t++) {
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

		} // t
		//delete[] X;
	}
	
	public void forward(int[] seq, int t1, int t2, int length) {
		int f,b,v,c;
		int[] I = new int[8196];
	
		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB+1024];
	
		for (int t=t1;t<t2;t++) {
		
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
		
		}
	
		//delete[] X;
	}
	
	public void forward_backward(double[] seq, int t, int length, double[] target) {
		forward_backward(seq, t, length, target, 0);
	}

	public void forward_backward(double[] seq, int t, int length, double[] target, int backp) {
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

		double errloc = NetOut.backward(target);

		error += errloc;

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

		NetOut.gradient(I,X,target);

		//delete[] X;
	}
	
	public void forward_backward(int[] seq, int t, int length, double[] target) {
		forward_backward(seq, t, length, target, 0);
	}
	
	public void forward_backward(int[] seq, int t, int length, double[] target, int backp) {
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

		double errloc = NetOut.backward(target);

		error += errloc;

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
		
		NetOut.gradient(I,X,target);

		//delete[] X;
	}

	public void forward_backward(double[] seq, int t1, int t2, int length, double[][] target) {
		forward_backward(seq, t1, t2, length, target, 0);
	}
	
	public void forward_backward(double[] seq, int t1, int t2, int length, double[][] target, int backp) {
		int f,b,v,c;
		double[] I = new double[8196];

		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB];

		for (int t=t1;t<t2;t++) {

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
	
			double errloc = NetOut.backward(target[t]);
	
			error += errloc;
	
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
			NetOut.gradient(I,X,target[t]);
		}
		
		//delete[] X;
	}
	
	public void forward_backward(int[] seq, int t1, int t2, int length, double[][] target) {
		forward_backward(seq, t1, t2, length, target, 0);
	}
	
	public void forward_backward(int[] seq, int t1, int t2, int length, double[][] target, int backp) {
		int f,b,v,c;
		int[] I = new int[8196];

		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB+1024];

		for (int t=t1;t<t2;t++) {
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
	
			double errloc = NetOut.backward(target[t]);
	
			error += errloc;
	
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
	
	
			NetOut.gradient(I,X,target[t]);
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
				}
				else { X[f]=0; }
			}
		}

		NetF.forward(I,X);

		errorF+=NetF.backward(FFbp[t]);
		for (f=0;f<NF;f++) {
			FFbp[t-1][f] += NetF.back_out()[NU*(2*context+1)+f];
		}

		for (s=2;s<=shortcut;s++) {
		    for (f=(s-1)*NF; f<s*NF; f++) {
		    	if (t-s>=0)
		    		FFbp[t-s][f-(s-1)*NF] += NetF.back_out()[NU*(2*context+1)+f];
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
		    for (f=(s-1)*NF; f<s*NF; f++) {
		    	if (t-s>=0) {
		    		FFbp[t-s][f-(s-1)*NF] += NetF.back_out()[NU*(2*context+1)+f];
		    	}
			}
		}

		NetF.gradient(I,X,FFbp[t]);

		//delete[] X;
	}
	
	public void B1_Bbp(double[] seq, int t, int length) {
		F1_Fbp(seq, t, length, 0);
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

		NetB.gradient(I,X,BBbp[t]);

		//delete[] X;
	}
	
	public void B1_Bbp(int[] seq, int t, int length) {
		F1_Fbp(seq, t, length, 0);
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
		    	}
		    	else { X[b]=0; }
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

		NetB.gradient(I,X,BBbp[t]);

		//delete[] X;
	}
	
	public void back_propagate(double[] seq, int length) {
		F1_Fbp(seq, length, 0);
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
		F1_Fbp(seq, length, 0);
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
		int t,f,b,v,c;
		
		//cout << length << " "<<flush;
		//cout << "*" << flush;
		propagate(seq,length);
		//cout << "*" << flush;
		
		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}
		
		double[][] target= new double[length+1][];
		for ( c = 0;c<=length;c++) {
			target[c] = new double[NY];
			memset(target[c],0,NY);
			if (y[c]>=0 && y[c]<NY) {
				target[c][y[c]]=1.0;
			}
		}
		
		int chunk = length/NTH;
		
		for (t=1;t<=length;t+=chunk) {
			forward_backward(seq,t,min(t+chunk,length+1),length,target);
		}
		
		/*for (int c=0;c<=length;c++) {
			delete[] target[c];
		}
		delete[] target;*/
		
		back_propagate(seq,length,backp);
		
		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);
		
	}
	
	public void extimation(int[] seq, int[] y, int length) {
		extimation(seq, y, length, 0);
	}
	
	public void extimation(int[] seq, int[] y, int length, int backp) {
		int t,f,b,v,c;
		
		propagate(seq,length);
		
		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}
		
		double[][] target= new double[length+1][];
		for (c = 0;c<=length;c++) {
			target[c] = new double[NY];
			memset(target[c],0,NY);
			if (y[c]>=0 && y[c]<NY) {
				target[c][y[c]]=1.0;
			}
		}
		
		int chunk = length/NTH;
		
		for (t=1;t<=length;t+=chunk) {
			forward_backward(seq,t,min(t+chunk,length+1),length,target);
		}
		
		/*for (int c=0;c<=length;c++) {
			delete[] target[c];
		}
		delete[] target;*/
		
		back_propagate(seq,length,backp);
		
		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);	
	}

	public void extimation(double[] seq, double[] y, int length) {
		extimation(seq, y, length, 0);
	}
	
	@SuppressWarnings("unused")
	public void extimation(double[] seq, double[] y, int length, int backp) {
		int t,f,b,v,c;
		
		double[] target = new double[1024];
		
		//cout << length << " "<<flush;
		
		propagate(seq,length);
		
		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}
		
		for (t=1; t<=length; t++) {
			for (c=0; c<NY; c++) {
				target[c]=y[NY*t+c];
			}
			forward_backward(seq,t,length,target);
		}
		back_propagate(seq,length,backp);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);

		//delete[] target;
	}
	
	public void extimation(int[] seq, double[] y, int length) {
		extimation(seq, y, length, 0);
	}
	
	@SuppressWarnings("unused")
	public void extimation(int[] seq, double[] y, int length, int backp) {
		int t,f,b,v,c;
		int[] I = new int[8192];
		double[] X=new double[(2*CoF+1)*NF+(2*CoB+1)*NB];

		propagate(seq,length);

		for (t=0;t<length+2;t++) {
			memset(FFbp[t],0,NF);
			memset(BBbp[t],0,NB);
		}


		for (t=1; t<=length; t++) {
			double[] target=new double[1024];

			for (c=0; c<NY; c++) {
				target[c]=y[NY*t+c];
			}

			forward_backward(seq,t,length,target);
			//delete[] target;
		}

		back_propagate(seq,length,backp);

		NetOut.set_input(1);
		NetF.set_input(1);
		NetB.set_input(1);
	}

	public void backthrough(double[] seq, double[] y, int length) {
		backthrough(seq, y, length, 0);
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
	
	public void maximizationClipped() {
		NetOut.updateWeights(epsilon);
		NetOut.resetGradient();

		NetF.updateWeightsClipped(epsilon);
		NetF.resetGradient();

		NetB.updateWeightsClipped(epsilon);
		NetB.resetGradient();
	}

	public void Feed(double[] seq, int length) {
		int t;

		propagate(seq,length);

		int chunk = length/NTH;
		for (t=1;t<=length;t+=chunk) {
			forward(seq,t,min(t+chunk,length+1),length);
		}
	}
	
	public void Feed(int[] seq, int length) {
		int t;

		propagate(seq,length);

		int chunk = length/NTH;
		for (t=1;t<=length;t+=chunk) {
			forward(seq,t,min(t+chunk,length+1),length);
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

	public double getError() { return error; };
	public double getErrorF() { return errorF; };
	public double getErrorB() { return errorB; };
	
	void resetError() {
		error=0.0;
		errorF=0.0;
		errorB=0.0;
	};

	public void setEpsilon(double eps) { epsilon=eps; }

	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		dealloc();
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	};
}
