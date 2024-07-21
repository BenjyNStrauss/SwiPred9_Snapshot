package modules.compare;

import java.util.Arrays;

import biology.descriptor.EncodingType;
import biology.protein.AminoChain;

/**
 * Turns the embeddings assigned to a protein into a vector which is ideally
 *  representative of that protein
 *  
 * @author Benjamin Strauss
 *
 * TODO class in progress
 * 
 */

public final class ProteinVectorizer extends VectorizeHelper {
	private ProteinVectorizer() { }
	
	/**
	 * Given a chain of length "n" with embeddings of length "m" (m features), this method creates
	 * a single vector embedding of length "m" where each feature is the average across the residues
	 * in the chain for that feature.
	 * 
	 * @param chain: the chain to make the single vector embedding for
	 * @param type: The type of encoding to use
	 * @return
	 */
	public static ProtVectorEncoding featureAverage(AminoChain<?> chain, EncodingType type) {
		double[] vect = new double[type.encodingLength()];
		StringBuilder relevantSeq = new StringBuilder();
		double[][] matrix = getMatrix(chain, type, relevantSeq);
		
		for(double[] aminoVector: matrix) {
			for(int featureNo = 0; featureNo < type.encodingLength(); ++featureNo) {
				vect[featureNo] += aminoVector[featureNo];
			}
		}
		
		for(int featureNo = 0; featureNo < type.encodingLength(); ++featureNo) {
			vect[featureNo] /= relevantSeq.length();
		}
		
		return new ProtVectorEncoding(chain.id().mostUseful(), relevantSeq.toString(), type, vect);
	}
	
	/**
	 * Given a chain of length "n" with embeddings of length "m" (m features), this method creates
	 * a single vector embedding of length 2m-2 where
	 * 		vector[2i] = distance(feature[i], feature[i+1])
	 * 		vector[2i+1] = cosine(feature[i], feature[i+1])
	 * 
	 * @param chain: the chain to make the single vector embedding for
	 * @param type: The type of encoding to use
	 * @return
	 */
	public static ProtVectorEncoding featureDistAndCos(AminoChain<?> chain, EncodingType type) {
		double[] vect = new double[type.encodingLength()*2-2];
		StringBuilder relevantSeq = new StringBuilder();
		double[][] matrix = getMatrix(chain, type, relevantSeq);
		
		double[] prevFeat = null;
		double[] nextFeat = getFeatureVector(matrix, 0);
		
		for(int feat = 0; feat < type.encodingLength()-1; ++feat) {
			prevFeat = nextFeat;
			nextFeat = getFeatureVector(matrix, feat+1);
			vect[feat] = distance(prevFeat, nextFeat);
			vect[feat+1] = cosine(prevFeat, nextFeat);
			
		}
		
		return new ProtVectorEncoding(chain.id().mostUseful(), relevantSeq.toString(), type, vect);
	}
	
	/**
	 * Given a chain of length "n" with embeddings of length "m" (m features), this method creates
	 * a single vector embedding of length "m" where each feature is the median across the residues
	 * in the chain for that feature.
	 * 
	 * @param chain: the chain to make the single vector embedding for
	 * @param type: The type of encoding to use
	 * @return
	 */
	public static ProtVectorEncoding featureMedian(AminoChain<?> chain, EncodingType type) {
		double[] vect = new double[type.encodingLength()];
		StringBuilder relevantSeq = new StringBuilder();
		double[][] matrix = getMatrix(chain, type, relevantSeq);
		
		
		for(int feat = 0; feat < type.encodingLength(); ++feat) {
			double[] featureVect = getFeatureVector(matrix, feat);
			Arrays.sort(featureVect);
			int median = featureVect.length/2;
			vect[feat] = featureVect[median];
			
		}
		
		return new ProtVectorEncoding(chain.id().mostUseful(), relevantSeq.toString(), type, vect);
	}
}
