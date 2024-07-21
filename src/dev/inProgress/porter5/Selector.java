package dev.inProgress.porter5;

import assist.translation.cplusplus.IStream;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Selector extends Layer {
	int[] edges;

	public Selector(int t_NY, int[] t_NK, int t_NU) {
		super(t_NY, t_NK, t_NU);
		edges=new int[NY];
	}

	public Selector(int t_NY, int t_NU) {
		super(t_NY, t_NU);
		edges=new int[NY];
	}

	public Selector(int t_NY, int[] t_NK, int t_NU, int t_NUr) {
		super(t_NY, t_NK, t_NU, t_NUr);
		edges=new int[NY];
	}

	public Selector(IStream is) {
		super(is);
		edges=new int[NY];
	}
	
	public void forward(int[] I) {}
	//public void forward(double[] I);
	public void forward(int[] I1, double[] I2) {}
	public void forward(double[] I1, double[] I2) {}

	//public double f1(double a);

	//public double f_cost(double[] t);
	
	public double backward(double[] t) {
		return backward(t, 1.0);
	}
	
	//public double backward(double[] t, double weight);

	public void setEdges() {
		//TODO code not available
	}

	public void initWeights(int seed) {
		super.initWeights(seed);
		setEdges();
	}

	public void updateWeights(double epsilon) {
		super.updateWeights(epsilon);
		setEdges();
	}
}
