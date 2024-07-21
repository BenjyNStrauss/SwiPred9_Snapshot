package dev.inProgress.sable;

import assist.base.Assist;
import utilities.LocalToolBase;

/**
 * 
 * @translator/adapter Benjamin Strauss
 *
 */

public class pUnit extends LocalToolBase {
    public float act;         /* Activation       */
    public float Bias;        /* Bias of the Unit */
    public final int   NoOfSources; /* Number of predecessor units */
    
    int[] sources_indecies;
    public pUnit[] sources; /* predecessor units */
    
    public final float[] weights; /* weights from predecessor units */
    
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
    
    public pUnit() {
    	act = 0;
    	Bias = 0;
    	NoOfSources = 0;
    	sources_indecies = null;
    	weights = null;
	}

	public void realize(pUnit[] new_sources) {
		//LocalToolBase.qp(new_sources.length);
		//LocalToolBase.qp(sources_indecies.length);
		
		if(new_sources == null || sources_indecies == null) {
			return;
		}
		
		//LocalToolBase.qp(NoOfSources == new_sources.length);
    	sources = new pUnit[new_sources.length];
    	
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
    
    /**
     *     public float act;         /* Activation       *
    public float Bias;        /* Bias of the Unit *
    public final int   NoOfSources; /* Number of predecessor units *
    
    int[] sources_indecies;
    public pUnit[] sources; /* predecessor units *
    
    public final float[] weights; /* weights from predecessor units *
     */
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("pUnit {");
    	builder.append("\n\tact:              " + act);
    	builder.append("\n\tbias:             " + Bias);
    	builder.append("\n\t#sources:         " + NoOfSources);
    	builder.append("\n\tsources_indecies: " + Assist.arrayToLine(sources_indecies));
    	builder.append("\n\tweights:          " + Assist.arrayToLine(weights));
    	builder.append("\n}");
    	return builder.toString();
    }
}
