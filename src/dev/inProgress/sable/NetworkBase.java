package dev.inProgress.sable;

import assist.translation.cplusplus.CppTranslator;
import assist.util.LabeledList;

/**
 * A base for the complexSA c++ code in Sable
 * @translator Benjamin Strauss
 * 
 */

public abstract class NetworkBase extends CppTranslator {
	public static final int NULL = 0;
	
	public static final String UNITS = "@@ units @@";
	public static final String WEIGHTS = "@@ weights @@";
	public static final String STRUCTS = "@@ structs @@";
	
	public abstract int net(float[] in, float[] out, int init);
	
	protected float Act_Logistic(double sum, double bias) { 
		return (float) ((sum+bias>-500.0) ? ( 1.0/(1.0 + exp(-sum-bias) ) ) : 0.0);
	}
	
	protected float Act_Identity(double sum, double bias) { return (float) sum; }
	
	public static final Object[] init(String init_file) {
		if(!init_file.startsWith(".txt")) {
			init_file += ".txt";
		}
		
		String[] fileLines = getFileLines(init_file);
		LabeledList<Integer> sources = new LabeledList<Integer>();
		LabeledList<Float> weights = new LabeledList<Float>();
		LabeledList<pUnit> units = new LabeledList<pUnit>();
		
		byte readMode = 0;
		
		for(String line: fileLines) {
			switch(line) {
			case UNITS:
				readMode = 1;
				break;
			case WEIGHTS:
				readMode = 2;
				break;
			case STRUCTS:
				readMode = 3;
				break;
			default:
				switch(readMode) {
				case 1:
					String[] fields1 = line.split(",");
					for(String val: fields1) {
						sources.add(Integer.parseInt(val));
					}
					break;
				case 2:
					String[] fields2 = line.substring(0,line.length()-1).split(",");
					for(String val: fields2) {
						weights.add(Float.parseFloat(val));
					}
					break;
				case 3:
					if(line.contains("null")) {
						units.add(new pUnit());
						break;
					}
					
					String[] fields3 = line.split(",");
					
					//qp(fields3);
					float act = Float.parseFloat(fields3[0]);
					float bias = Float.parseFloat(fields3[1]);
					int num_src = Integer.parseInt(fields3[2]);
					int src = Integer.parseInt(fields3[3]);
					float weight = Float.parseFloat(fields3[4]);
					units.add(new pUnit(act, bias, num_src, src, weight));
					break;
				}
			}
		}
		
		int[] Sources = new int[sources.size()];
		for(int ii = 0; ii < sources.size(); ++ii) {
			Sources[ii] = sources.get(ii);
		}
		
		float[] Weights = new float[weights.size()];
		for(int ii = 0; ii < weights.size(); ++ii) {
			Weights[ii] = weights.get(ii);
		}
		
		pUnit[] Units = new pUnit[units.size()];
		units.toArray(Units);
		
		for(pUnit unit: Units) {
			qp(unit);
			unit.realize(Units);
		}
		
		Object[] retval = new Object[3];
		retval[0] = Sources;
		retval[1] = Weights;
		retval[2] = Units;
		return retval;
	}
}
