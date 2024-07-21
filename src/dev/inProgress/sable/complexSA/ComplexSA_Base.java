package dev.inProgress.sable.complexSA;

import assist.translation.cplusplus.CppTranslator;

/**
 * A base for the complexSA c++ code in Sable
 * @translator Benjamin Strauss
 *
 */

public abstract class ComplexSA_Base extends CppTranslator {
	public static final int NULL = 0;
	
	public static final String UNITS = "@@ units @@";
	public static final String WEIGHTS = "@@ weights @@";
	public static final String STRUCTS = "@@ structs @@";
	
	abstract int net(float[] in, float[] out, int init);
	
	float Act_Logistic(double sum, double bias) { 
		return (float) ((sum+bias>-500.0) ? ( 1.0/(1.0 + exp(-sum-bias) ) ) : 0.0);
	}
	
	float Act_Identity(double sum, double bias) { return (float) sum; }
}
