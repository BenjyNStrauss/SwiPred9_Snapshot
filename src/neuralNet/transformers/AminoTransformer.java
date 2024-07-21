package neuralNet.transformers;

import biology.protein.AminoChain;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public interface AminoTransformer {
	
	public double[][] getEncoding(AminoChain<?> chain, int start, int en);
	
}
