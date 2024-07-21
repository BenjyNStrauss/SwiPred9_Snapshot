package biology.chainDescriptor;

import assist.util.LabeledHash;
import biology.amino.ChainObject;
import biology.descriptor.EncodingType;
import biology.protein.AminoChain;
import biology.protein.ChainID;
import scala.actors.threadpool.Arrays;

/**
 * A FeatureMatrix is created from the matrix embedding of a given sequence
 * It exists in the form 
 * 
 * 
 * @author Benjamin Strauss
 *
 */

public class FeatureMatrix extends LabeledHash<SequenceFeature, double[]> {
	private static final long serialVersionUID = 1L;
	
	public final int num_amino;
	public final EncodingType encodingType;
	public final ChainID id;
	
	//raw [FEATURE] by [AMINO] data from the protein
	private double[][] raw_data;
	
	public FeatureMatrix(AminoChain<?> chain, EncodingType feature) {
		num_amino = chain.size() - chain.nullResidues();
		encodingType = feature;
		id = chain.id();
		raw_data = new double[encodingType.encodingLength()][num_amino];
		
		for(int index = 0, arrayIndex = 0; index < chain.length(); ++index, ++arrayIndex) {
			ChainObject co = chain.get(index);
			if(co == null) { continue; }
			
			double[] array = co.getEncoding(feature);
			
			for(int feat = 0; feat < feature.encodingLength(); ++feat) {
				raw_data[feat][arrayIndex] = array[feat];
			}
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	public void generateData(SequenceFeature sf) {
		double[] vector = new double[encodingType.encodingLength()];
		
		switch(sf) {
		case ARITHMETIC_MEAN:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				double sum = 0;
				for(int amino = 0; amino < num_amino; ++amino) {
					sum += raw_data[feat][amino];
				}
				double tmp = num_amino;
				vector[feat] = sum/tmp;
			}
			break;
		case CUBIC_MEAN:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				double sum = 0;
				for(int amino = 0; amino < num_amino; ++amino) {
					sum += Math.pow(raw_data[feat][amino], 3);
				}
				double tmp = num_amino;
				vector[feat] = Math.pow(sum/tmp, 1/3);
			}
			break;
		case GEOMETRIC_MEAN:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				double inverse_sum = 0;
				for(int amino = 0; amino < num_amino; ++amino) {
					inverse_sum += 1/raw_data[feat][amino];
				}
				double tmp = num_amino;
				vector[feat] = inverse_sum / tmp;
				vector[feat] = 1/vector[feat];
			}
			break;
		case HARMONIC_MEAN:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				double sum = 0;
				for(int amino = 0; amino < num_amino; ++amino) {
					sum += raw_data[feat][amino];
				}
				double tmp = num_amino;
				vector[feat] = sum/tmp;
			}
			break;
		/*case KURTOSIS:
			break;*/
		case MAXIMUM:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				vector[feat] = max(raw_data[feat]);
			}
			break;
		case MEDIAN:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				double[] tmp = new double[num_amino];
				System.arraycopy(vector, 0, tmp, 0, num_amino);
				Arrays.sort(tmp);
				
				if(num_amino % 2 == 0) {
					vector[feat] = (tmp[num_amino/2] + tmp[num_amino/2+1])/2;
				} else {
					vector[feat] = tmp[num_amino/2];
				}
			}
			break;
		case MIDRANGE: 
			ensure(SequenceFeature.MINIMUM);
			ensure(SequenceFeature.MAXIMUM);
			double[] _min = get(SequenceFeature.MINIMUM);
			double[] _max = get(SequenceFeature.MAXIMUM);
			
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				vector[feat] = (_max[feat] + _min[feat]) / 2;
			}
			break;
		case MINIMUM:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				vector[feat] = min(raw_data[feat]);
			}
			break;
		case QUADRATIC_MEAN:
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				double sum = 0;
				for(int amino = 0; amino < num_amino; ++amino) {
					sum += Math.pow(raw_data[feat][amino], 2);
				}
				double tmp = num_amino;
				vector[feat] = Math.sqrt(sum/tmp);
			}
			break;
		case RANGE:
			ensure(SequenceFeature.MINIMUM);
			ensure(SequenceFeature.MAXIMUM);
			double[] min = get(SequenceFeature.MINIMUM);
			double[] max = get(SequenceFeature.MAXIMUM);
			
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				vector[feat] = max[feat] - min[feat];
			}
			break;
		/*case SKEW: {
			ensure(SequenceFeature.STANDARD_DEVIATION);
			double[] std_devs = get(SequenceFeature.STANDARD_DEVIATION);
			double[] means = get(SequenceFeature.ARITHMETIC_MEAN);
			
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				vector[feat]
			}
		} break;*/
		case STANDARD_DEVIATION:
			ensure(SequenceFeature.ARITHMETIC_MEAN);
			double[] means = get(SequenceFeature.ARITHMETIC_MEAN);
			
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				double sum = 0;
				for(int amino = 0; amino < num_amino; ++amino) {
					sum += Math.pow(means[feat] - raw_data[feat][amino], 2);
				}
				double tmp = num_amino;
				vector[feat] = sum/tmp;
			}
			break;
		case VARIANCE:
			ensure(SequenceFeature.STANDARD_DEVIATION);
			double[] std_devs = get(SequenceFeature.STANDARD_DEVIATION);
			
			for(int feat = 0; feat < encodingType.encodingLength(); ++feat) {
				vector[feat] = Math.sqrt(std_devs[feat]);
			}
			break;
		}
		put(sf, vector);
	}
	
	public void ensure(SequenceFeature sf) {
		if(!containsKey(sf)) {
			generateData(sf);
		}
	}
}
