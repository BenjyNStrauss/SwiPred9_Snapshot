package modules.encode.swipred;

import assist.util.LabeledHash;
import modules.encode.tokens.AminoToken;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class TrueValueEncoder extends LabeledHash<AminoToken, double[]> {
	private static final long serialVersionUID = 1L;

	//map based on ordinal number, rather than position in sequence
	final boolean staticMapping;
	final boolean equivalence;
	final double primary_e6_equivalence;
	final double secondary_equivalence;
	
	public TrueValueEncoder() {
		this(null, false, false, 0.0, 0.0);
	}
	
	public TrueValueEncoder(boolean staticMapping) {
		this(null, staticMapping, false, 0.0, 0.0);
	}
	
	public TrueValueEncoder(boolean staticMapping, boolean equivalence) {
		this(null, staticMapping, equivalence, 0.0, 0.0);
	}
	
	public TrueValueEncoder(boolean staticMapping, boolean equivalence, 
			double primary_e6_equivalence, double secondary_equivalence) {
		this(null, staticMapping, equivalence, primary_e6_equivalence, secondary_equivalence);
	}
	
	public TrueValueEncoder(String label, boolean staticMapping, boolean equivalence, 
			double primary_e6_equivalence, double secondary_equivalence) {
		super(label);
		this.staticMapping = staticMapping;
		this.equivalence = equivalence;
		this.primary_e6_equivalence = primary_e6_equivalence;
		this.secondary_equivalence = secondary_equivalence;
	}
	
	//intentionally disabled
	public double[] put(AminoToken key, double[] value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Add primary_e6_equivalence, multiply by secondary_equivalence
	 * Note: in order to not be zero, both primary and secondary structure must be partially correct
	 * 
	 * @param tokens
	 * @param index
	 * @return
	 */
	public double[] getTrueValues(AminoToken[] tokens, int index) {
		int mappingLen = (staticMapping) ? AminoToken.values().length : tokens.length;
		double[] mapping = new double[mappingLen]; 
		
		if(staticMapping) {
			AminoToken predicted = AminoToken.parse(index);
			if(containsKey(predicted)) { return get(predicted); }
			
			mapping[index] = 1.0;
			
			if(equivalence) {
				for(int tokenIndex = 0; tokenIndex < AminoToken.values().length; ++tokenIndex) {
					if(predicted.primary() == AminoToken.values()[tokenIndex].primary()) {
						mapping[tokenIndex] = 1.0;
					} else if(predicted.primary().clazz == AminoToken.values()[tokenIndex].primary().clazz) {
						mapping[tokenIndex] = primary_e6_equivalence;
					}
				}
				
				for(int tokenIndex = 0; tokenIndex < AminoToken.values().length; ++tokenIndex) {
					if(predicted.secondary() == AminoToken.values()[tokenIndex].secondary()) {
						//no penalty
					} else if(predicted.secondary().simpleClassify() == AminoToken.values()[tokenIndex].secondary().simpleClassify()) {
						mapping[tokenIndex] *= secondary_equivalence;
					} else {
						mapping[tokenIndex] = 0;
					}
				}
			}
			
			super.put(predicted, mapping);
		} else {
			AminoToken predicted = tokens[index];
			if(containsKey(predicted)) { return get(predicted); }
			
			mapping[index] = 1.0;
			
			if(equivalence) {
				for(int ii = 0; ii < tokens.length; ++ii) {
					if(tokens[index].primary() == tokens[ii].primary()) {
						mapping[ii] = 1.0;
					} else if(predicted.primary().clazz == tokens[ii].primary().clazz) {
						mapping[ii] = primary_e6_equivalence;
					}
				}
				
				for(int ii = 0; ii < tokens.length; ++ii) {
					if(tokens[index].secondary() == tokens[ii].secondary()) {
						
					} else if(predicted.secondary().simpleClassify() == tokens[ii].secondary().simpleClassify()) {
						mapping[ii] *= secondary_equivalence;
					} else {
						mapping[ii] = 0;
					}
				}
			}
			
			super.put(predicted, mapping);
		}
		
		return mapping;
	}
}
