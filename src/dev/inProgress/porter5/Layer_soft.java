package dev.inProgress.porter5;

import assist.translation.cplusplus.IStream;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Layer_soft extends Layer {

	public Layer_soft(int t_NY, int[] t_NK, int t_NU) {
		super(t_NY, t_NK, t_NU);
	}

	public Layer_soft(int t_NY, int t_NU) {
		super(t_NY, t_NU);
	}

	public Layer_soft(int t_NY, int[] t_NK, int t_NU, int t_NUr) {
		super(t_NY, t_NK, t_NU, t_NUr);
	}
	
	public Layer_soft(IStream is) {
		super(is);
	}
	
	public Layer_soft(Layer from) {
		super(from);
	}
	
	public void forward(int[] I) {
		super.forward(I);
		softmax();
	}
	
	public void forward(double[] I) {
	super.forward(I);
	softmax();
	}
	
	public void forward(int[] I1,double[] I2) {
		super.forward(I1,I2);
		softmax();
	}
	
	public void forward(double[] I1,double[] I2) {
		super.forward(I1,I2);
		softmax();
	}
	
	//void forward(double* I,int nz_n,int*nz)
	//{
	//super.forward(I,nz_n,nz);
	//softmax();
	//}
	
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

		// If isn't an output layer
		// rbackprop[] is a backprop contribution
		// coming from upwards.
		if (output == 0) {
			for (y=0; y<NY; y++) {
				BKD[y] *= f1(y);
				delta[y]=weight*BKD[y];
			}
		}
		// If this is an output layer
		// rbackprop[] is the target vector.
		else {
			for (y=0; y<NY; y++) {
				delta[y]=weight*(Y[y]-BKD[y]);
		    }
		}

		double sum;
		int i=0;

		// If this isn't an input layer
		// the backprop contribution
		// must be computed, either for
		// the real input part or fully.
		if (ninput==1) {
			for (u=0; u<NUr; u++) {
				sum=0.0;
				for (y=0; y<NY; y++) {
					sum += W[y*NUtot+NUplain+u]*delta[y];
				}
				backprop[NUplain+u]=sum;
			}
		}
		//}
		else if (ninput==2) {
			for (u=0; u<NU+NUr; u++) {
				sum=0.0;
				for (y=0; y<NY; y++) {
					sum += W[y*NUtot+u]*delta[y];
				}
				backprop[u]=sum;
			}
		}

		double err=0.0;
		if (output != 0) {
			err=f_cost(rbackprop);
		} else {
			for (int yyy=0;yyy<NY;yyy++) {
				err+= delta[yyy]*delta[yyy];
			}
		}
		return err;
	}
	
	public double f1(int y) {
		return (Y[y] - Y[y]*Y[y]);
	}
	
	public double f_cost(double[] t) {
		return super.log_cost(t);
	}
	
	public void initWeights(int seed) {
		super.initWeights(seed);
	}
	
	public void updateWeights(double epsilon) {
		super.updateWeights(epsilon);
	}
}
