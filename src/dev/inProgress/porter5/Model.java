package dev.inProgress.porter5;

import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.IStream;
import assist.translation.cplusplus.OStream;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Model extends CppTranslator {
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
	private int Cseg;
	private int Cwin;
	private int shortcut;
	private int Step;

	private double[] Thresholds;

	private int cycles;
	private double[] dcycles;

	private int modular;

	private BRNN Net;
	private BRNN NetF;

	private int[][] Conf;

	// double temp_error;
	private int temp_aas;
  
	private int[] counted;
	private double squared_error;
	private double error;
	private int nerrors;
	private int[] nerrors_;

	private double epsilon;

	private void alloc() {

		counted = new int[NY];
		nerrors_ = new int[NY];
		dcycles = new double[cycles];

		Conf = new int[NY][];
		for (int y=0;y<NY;y++) {
			Conf[y]=new int[NY];
		}
	}
	
	public Model(int NU, int NY, int NH,  int context ,int Moore, int NF, int NB, int NH2, int CoF, 
	        int CoB, int Cseg, int Cwin, int Step, int shortcut, double[] Thresholds) {
		this(NU, NY, NH, context, Moore, NF, NB, NH2, CoF, CoB, Cseg, Cwin, Step,
				shortcut, Thresholds, 1);
	}

	public Model(int NU, int NY, int NH,  int context ,int Moore, int NF, int NB, int NH2, int CoF, 
        int CoB, int Cseg, int Cwin, int Step, int shortcut, double[] Thresholds, int cycles) {

		this.Thresholds = new double[NY];
		for (int y=0;y<NY-1;y++) {
			this.Thresholds[y] = Thresholds[y];
		}
		
		Net = new BRNN(NU,NY,NH,context,Moore,NF,NB,NH2,CoF,CoB,Step,shortcut,0);
		Net.resetGradient();
		NetF = new BRNN(NY*(2*Cseg+2),NY,(int)(0.5*NH),context,Moore,(int)(0.5*NF),(int)(0.5*NB),(int)(0.5*NH2),CoF,CoB,Step,shortcut,0);
		NetF.resetGradient();
		
		alloc();
	}
	
	public Model(IStream is) {
	 	//is >> NU >> NY >> NH >> context; 
		NU = is.nextInt();
		NY = is.nextInt();
		NH = is.nextInt();
		context = is.nextInt();
		
		//is >> NF >> NB >> NH2 >> CoF >> CoB >> Cseg >> Cwin >> Step >> shortcut >> Moore >> cycles;
		NF = is.nextInt();
		NB = is.nextInt();
		NH2 = is.nextInt();
		CoF = is.nextInt();
		CoB = is.nextInt();
		Cseg = is.nextInt();
		Cwin = is.nextInt();
		Step = is.nextInt();
		shortcut = is.nextInt();
		Moore = is.nextInt();
		cycles = is.nextInt();
		
		Thresholds = new double[NY];
		for (int y=0;y<NY-1;y++) {
			Thresholds[y] = is.nextInt();
		}

		Net = new BRNN(is);
		Net.resetGradient();
		NetF = new BRNN(is);
		NetF.resetGradient();
		
		alloc();
	}
	
	public void read(IStream is) {
		//is >> NU >> NY >> NH >> context; 
		NU = is.nextInt();
		NY = is.nextInt();
		NH = is.nextInt();
		context = is.nextInt();
		
		//is >> NF >> NB >> NH2 >> CoF >> CoB >> Cseg >> Cwin >> Step >> shortcut >> Moore >> cycles;
		NF = is.nextInt();
		NB = is.nextInt();
		NH2 = is.nextInt();
		CoF = is.nextInt();
		CoB = is.nextInt();
		Cseg = is.nextInt();
		Cwin = is.nextInt();
		Step = is.nextInt();
		shortcut = is.nextInt();
		Moore = is.nextInt();
		cycles = is.nextInt();
		
		for (int y=0;y<NY-1;y++) {
			Thresholds[y] = is.nextInt();
		}

		Net.read(is);
		Net.resetGradient();
		NetF.read(is);
		NetF.resetGradient();
	}
	
	public void write(OStream os) {
		os.write(NU , " " , NY , " " , NH , " " , context , "\n");
		os.write(NF," ",NB," ",NH2," ",CoF," ",CoB," ",Cseg," ",Cwin," ",Step," ",shortcut," ",Moore," ",cycles,"\n");

		for (int y=0;y<NY-1;y++) {
			os.write(Thresholds[y] , " ");
		}
		os.write("\n");

		Net.write(os);
		NetF.write(os);
	}

	public void randomize(int seed)  {
		Net.initWeights(seed);
		NetF.initWeights(seed);
	}

	public void extimation(Sequence[] seq) {
		int t,y;
		
		int[] O;
		int a,c,cycle;//,m,maxm;
		double sum=0;
		double[] If;
		
		double[] app=new double[NY*(seq.length+1)];
		
		if (true) {
			sum=0;
		
			O=new int[seq.length+1];
		
			for (t=1; t<=seq.length; t++) {
		
				int close = 0;
				for (y=0;y<NY-1;y++) {
					if (seq.y[t]>Thresholds[y]) {
						close =y+1;
					}
				}
				O[t]= close;
				seq.yc[t] = close;
			}
		
			BRNN tempo = new BRNN(Net, seq.length+1);
			tempo.resetError();
		
			tempo.extimation(seq.u,O,seq.length);
			for (t=1; t<=seq.length; t++) {
				for (c=0; c<NY; c++) {
					app[NY*t+c]=tempo.out()[NY*t+c];
				}
			}
			dcycles[0] += sum;
		
			synchronized (getweights) {
				Net.copy_dW(tempo);
			}
			tempo.deconstruct();
			tempo = null;
		
			If=new double[NY*(2*Cseg+2)*(seq.length+1)];
		
			for (cycle=1;cycle<cycles;cycle++) {
				sum=0;
			
				memset(If,0,NY*(2*Cseg+2)*(seq.length+1));
			
				for (t=1; t<=seq.length; t++) {
					for (c=0; c<NY; c++) {
						If[(NY*(2*Cseg+2))*t+c]=app[NY*t+c];
					}
					for (int cs=-Cseg;cs<=Cseg;cs++) {
						for (int tcs=t+cs*(2*Cwin+1)-Cwin;tcs<=t+cs*(2*Cwin+1)+Cwin;tcs++) {
							if (tcs>0 && tcs<=seq.length) {
								for (c=0;c<NY;c++) {
									If[(NY*(2*Cseg+2))*t+NY+NY*(Cseg+cs)+c] += app[NY*tcs+c]/(2*Cwin+1);
								} 
							} else {
								for (c=0;c<NY;c++) {
									If[(NY*(2*Cseg+2))*t+NY+NY*(Cseg+cs)+c] += 0;
								}
							}
						}
					}
				}
		
				tempo = new BRNN(NetF, seq.length+1);
				tempo.resetError();
				tempo.extimation(If,O,seq.length);
			
				for (t=1; t<=seq.length; t++) {
					for (c=0; c<NY; c++) {
						sum += (app[NY*t+c]-tempo.out()[NY*t+c])*
							(app[NY*t+c]-tempo.out()[NY*t+c]);
						app[NY*t+c]=tempo.out()[NY*t+c];
					}
				}
			
				//#pragma omp critical(getweightsF)
				synchronized(getweightsF) {
					NetF.copy_dW(tempo);
				}
	
				tempo.deconstruct();
				tempo = null;
				dcycles[cycle] += sum;
			}
			//delete[] If;
			//delete[] O;
			
		} else {
			
		}
		//cout << "\n"<<flush;
		//delete[] app;
	}
	
	public void maximization() {
		Net.maximization();
		NetF.maximization();
	}
	
	public void maximizationL1() {
		Net.maximizationL1();
		NetF.maximizationL1();
	}
	
	public void maximizationClipped() {
		Net.maximizationClipped();
		NetF.maximizationClipped();
	}

	public void predict(Sequence[] seq) {
		int t,y;
		int a,c,cycle;//,m,maxm;
		double sum=0;
		double[] If;
		int[] O;
		double[] app=new double[NY*(seq.length+1)];
	
		if (true) {
			sum=0;
	
			O=new int[seq.length+1];
			for (t=1; t<=seq.length; t++) {
				int close = 0;
				for (y=0;y<NY-1;y++) {
					if (seq.y[t]>Thresholds[y]) {
						close =y+1;
					}
				}
				O[t]= close;
				seq.yc[t] = close;
			}
	
			BRNN tempo = new BRNN(Net, seq.length+1);
			tempo.resetError();
			tempo.predict(seq.u,seq.length);
			for (t=1; t<=seq.length; t++) {
				for (c=0; c<NY; c++) {
					sum += (app[NY*t+c]-tempo.out()[NY*t+c])*(app[NY*t+c]-tempo.out()[NY*t+c]);
					app[NY*t+c]=tempo.out()[NY*t+c];
				}
			}
			dcycles[0] += sum;
			tempo.deconstruct();
			tempo = null;
	
			If=new double[NY*(2*Cseg+2)*(seq.length+1)];
			
			for (cycle=1;cycle<cycles;cycle++) {
				sum=0;
		
				memset(If,0,NY*(2*Cseg+2)*(seq.length+1));
		
				for (t=1; t<=seq.length; t++) {
					for (c=0; c<NY; c++) {
						If[(NY*(2*Cseg+2))*t+c]=app[NY*t+c];
					}
					for (int cs=-Cseg;cs<=Cseg;cs++) {
						for (int tcs=t+cs*(2*Cwin+1)-Cwin;tcs<=t+cs*(2*Cwin+1)+Cwin;tcs++) {
							if (tcs>0 && tcs<=seq.length) {
								for (c=0;c<NY;c++) {
									If[(NY*(2*Cseg+2))*t+NY+NY*(Cseg+cs)+c] += app[NY*tcs+c]/(2*Cwin+1);
								}
							} else {
								for (c=0;c<NY;c++) {
									If[(NY*(2*Cseg+2))*t+NY+NY*(Cseg+cs)+c] += 0;
								}
							}
						}
					}
					//O[t]=seq.y[t];
				}
		
				tempo = new BRNN(NetF, seq.length+1);
				tempo.resetError();
				tempo.predict(If,seq.length);
				for (t=1; t<=seq.length; t++) {
					for (c=0; c<NY; c++) {
						sum += (app[NY*t+c]-tempo.out()[NY*t+c])*(app[NY*t+c]-tempo.out()[NY*t+c]);
						app[NY*t+c]=tempo.out()[NY*t+c];
					}
				}
				tempo.deconstruct();
				tempo = null;
				dcycles[cycle] += sum;
			}
			//delete[] If;
			//delete[] O;
		}
	
		for (t=1; t<=seq.length; t++) {
			double pred=0.0;
			int argp=-1;
	
			for (c=0; c<NY; c++) {
				if (app[NY*t+c]>pred) {
					pred = app[NY*t+c];
					argp = c;
				}
			}
			seq.y_pred[t] = argp;
		}
	
	
		synchronized (errors) {
			for (t=1; t<=seq.length; t++) {
				if (seq.y_pred[t]!=seq.yc[t]) {
					nerrors++;
					nerrors_[seq.yc[t]]++;
				}
				
				if (seq.yc[t] != -1 && seq.y_pred[t] != -1) {
					Conf[seq.y_pred[t]][seq.yc[t]]++;
					counted[seq.yc[t]]++;
				}
			}
		}
	
		//delete[] app;
	}
	
	public void predict(Sequence[] seq, int cy) {
		int temp=cycles;
		cycles=cy;
		predict(seq);
		cycles=temp;
	}
	
	//  void predict(Sequence* seq, int W);
	public double[] out() { return NetF.out(); }
	public int[][] getConf() {return Conf;}

	public int getNErrors() { return nerrors;};

	public int getNErrors_(int i) { return nerrors_[i];};
	public int getClasses() { return NY;};

	public int[] getCounted() {return counted;}
	
	public double[] getdcycles() {return dcycles;}

	public void resetNErrors() { 
		error=0;
		nerrors=0;
		memset(nerrors_,0,NY);
		memset(counted,0,NY);
		for (int p=0;p<NY;p++) {
			for (int y=0;y<NY;y++) {
				Conf[p][y]=0;
			}
		}
		for (int c=0;c<cycles;c++) {
			dcycles[c]=0;
	  	}
		Net.resetError();
		NetF.resetError();
	};

	public double get_error() { return error; };
	
	double get_squared_error() { return Net.getError(); };
	
	double get_squared_errorf() { return NetF.getError(); };
	
	double get_squared_errorF() { return Net.getErrorF(); };
	
	double get_squared_errorB() { return Net.getErrorB(); };
	
	public void reset_squared_error() { 
		Net.resetError();
		NetF.resetError();
		for (int c=0;c<cycles;c++) {
			dcycles[c]=0;
	  	}
	};

	public void setEpsilon(double eps) { 
		Net.setEpsilon(eps);
		NetF.setEpsilon(eps);
	};
}
