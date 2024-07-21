package dev.inProgress.porter5;

import assist.translation.cplusplus.IStream;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Layer_tanh extends Layer {

	public Layer_tanh(int t_NY, int[] t_NK, int t_NU) {
		super(t_NY, t_NK, t_NU);
	}
	
	public Layer_tanh(int t_NY, int t_NU) {
		super(t_NY, t_NU);
	}
	
	public Layer_tanh(int t_NY, int[] t_NK, int t_NU, int t_NUr) {
		super(t_NY, t_NK, t_NU, t_NUr);
	}
	
	public Layer_tanh(IStream is) {
		super(is);
	}
	
	public Layer_tanh(Layer from) {
		super(from);
	}
	
	public void forward(int[] I) {
		super.forward(I);
		squash();
	}
	
	public void forward(double[] I) {
		super.forward(I);
		squash();
	}
	
	public void forward(int[] I1,double[] I2) {
		super.forward(I1,I2);
		squash();
	}
	
	public void forward(double[] I1,double[] I2) {
		super.forward(I1,I2);
		squash();
	}
	
	//void forward(double* I,int nz_n,int*nz)
	//{
	//Layer::forward(I,nz_n,nz);
	//squash();
	//}
	
	public double backward(double[] t) {
		return backward(t, 1.0);
	}
	
	public double backward(double[] t, double weight) {
		return super.backward(t,weight);
	}
	
	public double f1(int y) {
		return 1.0-(Y[y]*Y[y]);
	}
	
	public double f_cost(double[] t) {
		return super.sq_cost(t);
	}
	
	public void initWeights(int seed) {
		super.initWeights(seed);
	}
	
	public void updateWeights(double epsilon) {
		super.updateWeights(epsilon);
	}

};
