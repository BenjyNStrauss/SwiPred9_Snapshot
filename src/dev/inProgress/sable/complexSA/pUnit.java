package dev.inProgress.sable.complexSA;

/**
 * 
 * @translator/adapter Benjamin Strauss
 *
 */

public class pUnit {
    float act;         /* Activation       */
    float Bias;        /* Bias of the Unit */
    int   NoOfSources; /* Number of predecessor units */
    
    int[] sources_indecies;
    pUnit[] sources; /* predecessor units */
    
 
    float[] weights; /* weights from predecessor units */
    
    public pUnit(float act, float bias, int num_sources, pUnit[] sources, float[] weights) {
    	this.act = act;
    	Bias = bias;
    	NoOfSources = num_sources;
    	this.sources = sources;
    	this.weights = weights;
    }
    
    public pUnit(float act, float bias, int num_sources, int[] sources_indecies, float[] weights) {
    	this.act = act;
    	Bias = bias;
    	NoOfSources = num_sources;
    	this.sources_indecies = sources_indecies;
    	this.weights = weights;
    }
    
    public pUnit(double act, double bias, int num_sources, int[] sources_indecies, double[] weights) {
    	this.act = (float) act;
    	Bias = (float) bias;
    	NoOfSources = num_sources;
    	this.sources_indecies = sources_indecies;
    	this.weights = new float[weights.length];
    	for(int ii = 0; ii < this.weights.length; ++ii) {
    		this.weights[ii] = (float) weights[ii];
    	}
    }
    
    public pUnit(double act, double bias, int num_sources, int[] sources_indecies, double weights) {
    	this.act = (float) act;
    	Bias = (float) bias;
    	NoOfSources = num_sources;
    	this.sources_indecies = sources_indecies;
    	this.weights = new float[1];
    	this.weights[0] = (float) weights;
    }
    
    public pUnit(double act, double bias, int num_sources, int sources2, double weights2) {
    	this.act = (float) act;
    	Bias = (float) bias;
    	NoOfSources = num_sources;
    	this.sources_indecies = new int[1];
    	this.sources_indecies[0] = sources2;
    	this.weights = new float[1];
    	this.weights[0] = (float) weights2;
    }
    
    public void realize(pUnit[] new_sources) {
    	sources = new pUnit[sources_indecies.length];
    	
    	//initialize the whole array
    	for(int ii = 0; ii < sources.length; ++ii) {
    		sources[ii] = new_sources[sources_indecies[ii]];
    	}
    }
    
    public static pUnit[] getUnits(pUnit[] units, int... indecies) {
    	pUnit[] retval = new pUnit[indecies.length];
    	for(int ii = 0; ii < indecies.length; ++ii) {
    		retval[ii] = units[indecies[ii]];
    	}
    	
    	return retval;
    }
}
