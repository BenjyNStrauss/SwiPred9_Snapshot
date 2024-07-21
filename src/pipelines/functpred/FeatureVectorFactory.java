package pipelines.functpred;

import java.util.Objects;
import java.util.Set;

import assist.util.LabeledSet;
import biology.chainDescriptor.FeatureMatrix;
import biology.chainDescriptor.SequenceFeature;
import biology.descriptor.EncodingType;
import biology.protein.ChainFactory;
import biology.protein.ProteinChain;
import modules.encode.esm.ESM_Model;
import scala.actors.threadpool.Arrays;
import utilities.LocalToolBase;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class FeatureVectorFactory extends LocalToolBase {
	
	public static double[] createFeatureVector(String sequence, EncodingType type, boolean reextract, 
			Set<SequenceFeature> summarizers) {
		return createFeatureVector(sequence, type, reextract, -1, summarizers);
	}
	
	/**
	 * 
	 * @param sequence: input seequence
	 * @param type: type of encoding/model
	 * @param reextract: re-extract from model if model does not exist
	 * @param layer: layer of model to extract from
	 * @param summarizers: arithmetic_mean, geometric_mean, standard_deviation, etc 
	 * @return
	 */
	public static double[] createFeatureVector(String sequence, EncodingType type, boolean reextract, 
			int layer, Set<SequenceFeature> summarizers) {
		//Do error checking
		Objects.requireNonNull(sequence, "No sequence specified");
		Objects.requireNonNull(type, "No model specified");
		Objects.requireNonNull(summarizers, "No summarizers specified");
		if(summarizers.size() == 0) {
			throw new SwiPredRuntimeException("No summarizers specified");
		}
		 
		if(layer < 0) { layer = type.default_layer(); }
		 
		ProteinChain chain = ChainFactory.makeDummy(sequence);
		type.assignTo(chain, layer, reextract);
		 
		FeatureMatrix matrix = new FeatureMatrix(chain, type);
		 
		//generate all of the sequence features
		for(SequenceFeature sf: summarizers) {
			matrix.ensure(sf);
		}
		 
		double[] vector = new double[type.encodingLength() * summarizers.size()];
		int offset = 0;
		 
		for(SequenceFeature sf: summarizers) {
			for(int index = 0; index < type.encodingLength(); ++index) {
				int vect_index = index * summarizers.size() + offset;
				vector[vect_index] = matrix.get(sf)[index];
			}
			++offset;
		}
		return vector;
	}
	
	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @param type
	 * @param summarizers
	 */
	public static void vectorizeFile(String infile, String outfile, EncodingType type, 
			Set<SequenceFeature> summarizers) {
		vectorizeFile(infile, outfile, type, false, type.default_layer(), summarizers);
	}
	
	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @param type
	 * @param layer
	 * @param summarizers
	 */
	public static void vectorizeFile(String infile, String outfile, EncodingType type, int layer, 
			Set<SequenceFeature> summarizers) {
		vectorizeFile(infile, outfile, type, true, layer, summarizers);
	}
	
	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @param type
	 * @param reextract
	 * @param summarizers
	 */
	public static void vectorizeFile(String infile, String outfile, EncodingType type, boolean reextract, 
			Set<SequenceFeature> summarizers) {
		vectorizeFile(infile, outfile, type, reextract, type.default_layer(), summarizers);
	}
	
	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @param type
	 * @param reextract
	 * @param layer
	 * @param summarizers
	 */
	public static void vectorizeFile(String infile, String outfile, EncodingType type, boolean reextract, 
			int layer, Set<SequenceFeature> summarizers) {
		Objects.requireNonNull(infile, "Infile not specified.");
		Objects.requireNonNull(outfile, "Outfile not specified.");
		Objects.requireNonNull(type, "Embedding model not specified.");
		Objects.requireNonNull(summarizers, "Summarizers not specified.");
		
		String[] sequences = getFileLines(infile);
		
		for(int index = 0; index < sequences.length; ++index) {
			sequences[index] = Arrays.toString(createFeatureVector(sequences[index], type, reextract, layer, summarizers));
			appendFileLines(outfile, sequences[index]);
		}
	}
	
	public static void main(String[] args) {
		final String infile = "input/sequence-lib.txt";
		final String outfile = "output/encodings.txt";
		final EncodingType type = ESM_Model.esm2_t36_3B_UR50D;
		final boolean reextract = false;
		
		final SequenceFeature[] _summarizers_ = new SequenceFeature[] { 
				SequenceFeature.ARITHMETIC_MEAN, SequenceFeature.STANDARD_DEVIATION, 
				SequenceFeature.RANGE
		};
		
		final LabeledSet<SequenceFeature> summarizers = new LabeledSet<SequenceFeature>();
		summarizers.addAll(_summarizers_);
		
		vectorizeFile(infile, outfile, type, reextract, summarizers);
	}
}
