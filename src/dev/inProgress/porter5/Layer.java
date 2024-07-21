package dev.inProgress.porter5;

import assist.Deconstructable;
import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.IStream;
import assist.translation.cplusplus.OStream;

/**
 * Layer ver 3.03
 * 12/12/2003
 * Copyright (C) Gianluca Pollastri 2003
 * 
 * ANN Layers
 * Linear, tanh and softmax outputs
 * Categorical (one-hot), real-valued and mixed inputs.
 * 
 * 
 * In version 3.0:
 * -fixed 'overflow problem' that output error=0
 *  for saturated softmax units
 * -all (but the last one..) compatibility issues fixed
 * -added updateWeightsClipped
 * 
 * In version 3.01
 * -fixed all the versions of gradient();
 * -NUtot made an attribute
 * 
 * In version 3.02:
 * -fixed Linux bug in initWeights
 * -added updateWeightsL1
 * 
 * In version 3.03:
 * -gradient fixed for softmax: (y-t)x, no f1
 * 
 * @translator Benjamin Strauss
 *
 */

public class Layer extends CppTranslator implements Deconstructable {
	protected static final double miny = 1e-4;
	protected static final double momentum = 0.9;
	
	public int NY;
	public int NU;
	public int NUr;
	public int[] NK;

	public double[] Y;
	public double[] A;
	public double[] U;	 //NU*NK
	public double[] delta;    //NY
	public double[] backprop; //NU*NK

	public double[] W;
	public double[] dW;
	public double[] d2W;

	public double[] B;        //NY
	public double[] dB;       //NY
	public double[] d2B;       //NY

	public int output; 	//0=no,1=yes
	public int ninput;		//0=input layer,1=just real side backprop,2=full backprop

	public int NUtot,NUplain;
	
	@SuppressWarnings("unused")
	public void alloc(int NY, int nu, int[] NK) {
		int y,u;
		NUtot=0;

		for (u=0; u<nu; u++) {
			NUtot += NK[u];
		}

		Y=new double[NY];
		A=new double[NY];
		U=new double[NUtot];

		delta=new double[NY];
		backprop=new double[NUtot];

		W=new double[NY*NUtot];
		dW=new double[NY*NUtot];
		d2W=new double[NY*NUtot];
		memset(d2W,0,NY*NUtot);
		B=new double[NY];
		dB=new double[NY];
		d2B=new double[NY];
		memset(d2B,0,NY);
	}

	public void softmax() {
		int y;

		int overflow=0;
		double max=A[0];
		int amax=0;
		double norm=0;
		for (y=0; y<NY; y++) {
			if (A[y]>85) {
				overflow=1;
			} else {
				norm += (double)exp(A[y]);
			}
			if (A[y]>max) {
				max = A[y];
				amax = y;
			}
	    }

		if (overflow != 0) {
			for (y=0; y<NY; y++) {
				Y[y] = miny;
			}
			Y[amax]=1.0-miny*(NY-1);
		} else {
			for (y=0; y<NY; y++) {
				Y[y] = (double)exp(A[y])/norm;
			}
		}
		for (y=0; y<NY; y++) {
			if (Y[y]<miny) {
				Y[y] = miny;
			}
		}	
	}
	
	public void squash() {
		for (int y=0; y<NY; y++) {
			Y[y]=(double)tanh(A[y]);
		}
	}

	public Layer(Layer from) {
		int y;
		NY = from.NY;
		NU = from.NU;
		NUr = from.NUr;

		NK=new int[NU+NUr];
		for (int i=0; i<NU; i++) {
			NK[i]=from.NK[i];
		}
		for (int i=NU; i<NU+NUr; i++) {
			NK[i]=1;
		}
		alloc(NY,NU+NUr,NK);
		ninput=0;
		output=0;

		NUplain =0;
		for (int u=0; u<NU; u++) {
			NUplain += NK[u];
		}

		for (y=0; y<NY; y++) {
			B[y] = from.B[y];
			dB[y] = 0;
			d2B[y] = 0;
			for (int u=0; u<NUtot; u++) {
				W[y*NUtot+u] = from.W[y*NUtot+u];
				dW[y*NUtot+u] = 0;
				d2W[y*NUtot+u] = 0;
			}
        }
	}

	public void copy_dW(Layer from) {
		for (int y=0; y<NY; y++) {
			dB[y] += from.dB[y];
			//cout << from.dB[y] << " ";
        	for (int u=0; u<NUtot; u++) {
					dW[y*NUtot+u] += from.dW[y*NUtot+u];
            }
        }
	}

	public void dump_dW(OStream os) {
		for (int y=0; y<NY; y++) {
			os.write(dB[y] , " ");
			for (int u=0; u<NUtot; u++) {
				os.write(dW[y*NUtot+u] , " ");
            }
			os.write("\n");
        }
	}
	
	public void dump_W(OStream os) {
		for (int y=0; y<NY; y++) {
			os.write(B[y], " ");
        	for (int u=0; u<NUtot; u++) {
        		os.write(W[y*NUtot+u], " ");
            }
        	os.write("\n");
		}
	}
	
	/**
	 * Constructor
	 * Categorical inputs
	 * 
	 * @param t_NY
	 * @param t_NK
	 * @param t_NU
	 */
	public Layer(int t_NY, int[] t_NK, int t_NU) {
		NY = t_NY;
		NU = t_NU;
		
		NK=new int[NU];
		for (int i=0; i<NU; i++) {
			NK[i]=t_NK[i];
		}
		alloc(NY,NU,NK);
		ninput=0;
		output=0;
		
		NUplain =0;
		for (int u=0; u<NU; u++) {
			NUplain += NK[u];
		}
	}

	/**
	 * Constructor
	 * Real-valued inputs
	 * @TODO :NY(t_NY), NU(t_NU)
	 * @param t_NY
	 * @param t_NU
	 */
	public Layer(int t_NY, int t_NU) {
		NY = t_NY;
		NU = t_NU;
		
		NK=new int[NU];
		for (int i=0; i<NU; i++) {
			NK[i]=1;
		}
		NUr=0;
		alloc(NY,NU,NK);
		ninput=0;
		output=0;

		NUplain =0;
		for (int u=0; u<NU; u++) {
			NUplain += NK[u];
		}
		//cout << NUplain << " " << NUtot << "a " << flush;
	}
	
	/**
	 * Constructor
	 * Mixed inputs (NU categorical attributes, NUr real-valued)
	 * 
	 * @param t_NY
	 * @param t_NK
	 * @param t_NU
	 * @param t_NUr
	 */
	public Layer(int t_NY, int[] t_NK, int t_NU, int t_NUr) {
		NY = t_NY;
		NU = t_NU; 
		NUr = t_NUr;
		
		int i;
		NK=new int[NU+NUr];
		for (i=0; i<NU; i++) {
			NK[i]=t_NK[i];
		}
		for (i=NU; i<NU+NUr; i++) {
			NK[i]=1;
		}
		alloc(NY,NU+NUr,NK);
		ninput=0;
		output=0;


		NUplain =0;
		for (int u=0; u<NU; u++) {
			NUplain += NK[u];
		}
		//cout << NY << " " << flush;
		//cout << NUplain << " " << NUtot << "b " << flush;
	}

	public Layer(IStream is) {
		int y,u,k;
		
		NY = is.nextInt();
		NU = is.nextInt();
		NUr = is.nextInt();
		NK=new int[NU+NUr];

		for (u=0; u<NU+NUr; u++)  {
			NK[u] = is.nextInt();
		}
		NUplain =0;
		for (u=0; u<NU; u++) {
			NUplain += NK[u];
		}

		alloc(NY,NU+NUr,NK);

		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				W[y*NUtot+u] = is.nextInt();
		    }
			B[y] = is.nextInt();
		}
		ninput=0;
		output=0;
	}
	
	public void set_ninput(int vi) {
		ninput=vi;
	};

	public void set_output(int vo) {
		output=vo;
	};

	public void read(IStream is) {
		int y,u,k;

		NY = is.nextInt();
		NU = is.nextInt();
		NUr = is.nextInt();

		for (u=0; u<NU+NUr; u++) { NK[u] = is.nextInt(); }
		NUplain =0;
		for (u=0; u<NU; u++) {
			NUplain += NK[u];
		}
		NUtot = NUplain+NUr;

		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				W[y*NUtot+u] = is.nextInt();
		    }
			B[y] = is.nextInt();
		}
		ninput=0;
		output=0;
	}
	
	@SuppressWarnings("unused")
	public void write(OStream os) {
		int y,u,k;

		os.write(NY + "\n");
		os.write(NU + "\n");
		os.write(NUr + "\n");

		for (u=0; u<NU+NUr; u++) {
			os.write(NK[u] + " ");
		}
		os.write("\n");

		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				os.write(W[y*NUtot+u] , " ");
		    }
			os.write(B[y]+ "\n");
		}
	}

	public void forward(int[] I) {
		int y,u,nur;
		memset(U,0,NUtot);

		for (y=0; y<NY; y++) {
			double a=B[y];
			nur=0;
			for (u=0; u<NU; u++) {
				if (I[u]>=0) {
					a+=W[y*NUtot+u*NK[0]+I[u]];
					U[nur+I[u]]=1.0;
				}
				nur += NK[u];
			}
			Y[y]=A[y]=a;
		}
	}
	
	@SuppressWarnings("unused")
	public void forward(double[] I) {
		int y,u,k,i;
		memset(U,0,NUtot);

		for (y=0; y<NY; y++) {
			i=0;
			double a=B[y];
		    for (u=0; u<NUplain; u++) {
		        U[i]=I[i];
		        a += W[y*NUtot+u]*I[i++];
		    }
		    Y[y]=A[y]=a;
		}
	}	
	
	@SuppressWarnings("unused")
	public void forward(int[] I1, double[] I2) {
		int y,u,k,i,nur;
		memset(U,0,NUtot);


		for (y=0; y<NY; y++) {
			double a=B[y];
			nur=0;
	
		    for (u=0; u<NU; u++) {
				if (I1[u]>=0) {
					a += W[y*NUtot+u*NK[0]+I1[u]];
					U[nur+I1[u]]=1.0;
				}
				nur += NK[u];
			}
	
			i=0;
		    for (u=0; u<NUr; u++) {
		        U[NUplain+i]=I2[i];
		        a += W[y*NUtot+NUplain+u]*I2[i++];
		    }
		    Y[y]=A[y]=a;
		}
	}
	
	@SuppressWarnings("unused")
	public void forward(double[] I1, double[] I2) {
		int y,u,k,i1,i2;

		for (y=0; y<NY; y++) {
			i1=0;
		    i2=0;
		    double a=B[y];
		    for (u=0; u<NUplain; u++) {
		    	U[i1]=I1[i1];
		    	a += W[y*NUtot+u]*I1[i1];
		    	i1++;
		    }
		    for (u=0; u<NUr; u++) {
		    	U[NUplain+i2]=I2[i2];
		        a += W[y*NUtot+NUplain+u]*I2[i2];
		        i2++;
		    }
		    Y[y]=A[y]=a;
		}
	}
	
	/*virtual void forward(double* I, int nz_n, int* nz) {
	int y,u,k,i1,i2;

	  for (y=0; y<NY; y++) {
	    i1=0;
	    i2=0;
	    double a=B[y];
	    for (u=0; u<nz_n; u++) {
				U[nz[u]]=I[nz[u]];
				a += W[y][nz[u]][0]*I[nz[u]];
	      }
	    Y[y]=A[y]=a;
	    }
	}*/

	public double f1(int y) { return 1.0; }
	public double f_cost(double[] t) {
		double sum=0.0;
		//cout << "L"<<flush;

		for (int y=0; y<NY; y++)
		sum += (t[y]-Y[y])*(t[y]-Y[y]);
		return sum;
	}
	
	public double log_cost(double[] t) {
		double sum=0.0;

		for (int y=0; y<NY; y++) {
			if ((t[y] != 0) && (Y[y] != 0)) {
				sum -= t[y]*(double)log(Y[y]);
			}
		}
		return sum;
	}
	
	public double sq_cost(double[] t) {
		double sum=0.0;

		for (int y=0; y<NY; y++) {
			sum += (t[y]-Y[y])*(t[y]-Y[y]);
		}
		return sum;
	}
	
	public double backward(double[] t) {
		return backward(t, 1.0);
	}
	
	@SuppressWarnings("unused")
	public double backward(double[] rbackprop, double weight) {
		int y,u,k;

		double[] BKD = new double[1024];
		for (y=0; y<NY; y++) {
			BKD[y]=rbackprop[y];
		}

		/* If isn't an output layer
		 * rbackprop[] is a backprop contribution
		 * coming from upwards.
		 */
		if (output == 0) {
		  for (y=0; y<NY; y++) {
			  BKD[y] *= f1(y);
			  delta[y]=weight*BKD[y];
		  }
		}
		/* If this is an output layer
		 * rbackprop[] is the target vector.
		 */
		else {
			for (y=0; y<NY; y++) {
				delta[y]=weight*(Y[y]-BKD[y])*f1(y);
			}
		}

		double sum;
		int i=0;

		/* If this isn't an input layer
		 * the backprop contribution
		 * must be computed, either for
		 * the real input part or fully.
		 */
		if (ninput==1) {
			for (u=0; u<NUr; u++) {
				sum=0.0;
				for (y=0; y<NY; y++) {
					sum += W[y*NUtot+NUplain+u]*delta[y];
				}
				backprop[NUplain+u]=sum;
			}
		} else if (ninput==2) {
			for (u=0; u<NU+NUr; u++) {
				sum=0.0;
				for (y=0; y<NY; y++) {
					sum += W[y*NUtot+u]*delta[y];
				}
				backprop[u]=sum;
			}
	  	}

		double err=0.0;
		if (output == 0) {
			err=f_cost(rbackprop);
		} else {
			for (int yyy=0;yyy<NY;yyy++)
				err+= delta[yyy]*delta[yyy];
			}
		return err;
	}

	public void gradient(int[] I) {
		int y,u;

		for (y=0; y<NY; y++) {
			for (u=0; u<NU; u++) {
				if (I[u]>=0) {
					dW[y*NUtot+u*NK[0]+I[u]] += delta[y];
				}
			}
			dB[y] += delta[y];
		}
	}
	
	@SuppressWarnings("unused")
	public void gradient(double[] I) {
		int y,u,k;
		int i;

		for (y=0; y<NY; y++) {
			i=0;
			for (u=0; u<NUplain; u++) {
				dW[y*NUtot+u] += delta[y]*I[i++];
			}
			dB[y] += delta[y];
		}
	}
	
	@SuppressWarnings("unused")
	public void gradient(int[] I1, double[] I2) {
		int y,u,k;
		int i;

		for (y=0; y<NY; y++) {
			for (u=0; u<NU; u++) {
				if (I1[u]>=0) {
					dW[y*NUtot+u*NK[0]+I1[u]] += delta[y];
				}
			}
			i=0;
			for (u=0; u<NUr; u++) {
				dW[y*NUtot+NUplain+u] += delta[y]*I2[i++];
			}
			dB[y] += delta[y];
		}
	}
	
	@SuppressWarnings("unused")
	public void gradient(double[] I1, double[] I2) {
		int y,u,k;
		int i1,i2;

		for (y=0; y<NY; y++) {
			i1=0;
			for (u=0; u<NUplain; u++) {
				dW[y*NUtot+u] += delta[y]*I1[i1];
				i1++;
		    }
			i2=0;
			for (u=0; u<NUr; u++) {
				dW[y*NUtot+NUplain+u] += delta[y]*I2[i2];
				i2++;
			}
			dB[y] += delta[y];
		}
	}
	
	/*public void gradient(double[] I, int nz_n,int[] nz) {
		int y,u,k;
		int i1,i2;
	
		for (y=0; y<NY; y++) {
			i1=0;
			for (u=0; u<nz_n; u++) {
				dW[y][nz[u]][0] += delta[y]*I[nz[u]];
		    }
			dB[y] += delta[y];
		}
	}*/
	
	public void gradient() {
		gradient(U, (U[NUplain]));
	}
	
	private void gradient(double[] I1, double I2) {
		gradient(I1, new double[] {I2});
	}

	protected static double sign(double a) {
		if (a>0) return 1.0;
		if (a<0) return -1.0;
		return 0.0;
	}
	
	protected static double clipped(double a) {
		double b=sign(a)*a;
		if (b>1) return sign(a)*1.0;
		if (b<0.1) return sign(a)*0.1;
		return a;
	}
	
	@SuppressWarnings("unused")
	public void updateWeights(double epsilon) {
		int y,u,k;
		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				d2W[y*NUtot+u] = momentum*d2W[y*NUtot+u] + dW[y*NUtot+u];
				W[y*NUtot+u] -= epsilon*d2W[y*NUtot+u];
			}
			d2B[y] = momentum*d2B[y] + dB[y];
			B[y] -= epsilon*d2B[y];
		}
	}
	
	@SuppressWarnings("unused")
	public void updateWeightsL1(double epsilon) {
		int y,u,k;
		double sum=0;

		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				sum += dW[y*NUtot+u]*dW[y*NUtot+u];
			}
			sum += dB[y]*dB[y];
		}
		sum = sqrt(sum);
		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				W[y*NUtot+u] -= epsilon*dW[y*NUtot+u]/sum;
		    }
			B[y] -= epsilon*dB[y]/sum;
		}
	}
	
	@SuppressWarnings("unused")
	public void updateWeightsClipped(double epsilon) {
		int y,u,k;
		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				W[y*NUtot+u] -= epsilon*clipped(dW[y*NUtot+u]);
			}
			B[y] -= epsilon*clipped(dB[y]);
		}
	}
	
	@SuppressWarnings("unused")
	public void resetGradient() {
		int y,u;
		memset(dW,0,NY*NUtot);
		memset(dB,0,NY);
	}
	
	@SuppressWarnings("unused")
	public void initWeights(int seed) {
		int y,u,k;
		double D=(double)(NU+NUr);

		//srand48(seed);
		srand(seed);
		for (y=0; y<NY; y++) {
			double rand_max = RAND_MAX;
			for (u=0; u<NUtot; u++) {
				W[y*NUtot+u] = (double)(0.5-(double)rand()/(rand_max))/D;
		    }
			B[y] = (double)(0.5-(double)rand()/(rand_max))/D;
		}
	}

	public double[] back_out() { return backprop; }
	public double[] Aout() { return A; }
	public double[] out() { return Y; }


	public int get_NY() { return NY; }
	public int get_NU() { return NU; }
	public int[] get_NK() { return NK; }

	public double[] get_dW() { return dW; }

	@SuppressWarnings("unused")
	public double dlength() {
		int y,u,k;
		double sum=0.0;

		for (y=0; y<NY; y++) {
			for (u=0; u<NUtot; u++) {
				sum += dW[y*NUtot+u]*dW[y*NUtot+u];
			}
			sum += dB[y]*dB[y];
		}
		return sqrt(sum);
	}

	public void set_dW(double[] newdW) {
		for (int y=0; y<NY; y++) {
			for (int u=0; u<NUtot; u++) {
				dW[y*NUtot+u]=newdW[y*NUtot+u];
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		//delete[] NK;
		//delete[] Y;
		//delete[] A;
		//delete[] U;
	
		//delete[] delta;
		//delete[] backprop;
		
		//delete[] B;
		//delete[] dB;
		//delete[] d2B;
		//delete[] W;
		//delete[] dW;
		//delete[] d2W;
		try {
			finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
	};
};