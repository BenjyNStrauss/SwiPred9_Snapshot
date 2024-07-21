package biology.descriptor;

import biology.protein.AminoChain;

/**
 * Used to mark amino acid encodings
 * @author Benjamin Strauss
 *
 */

public interface EncodingType extends Metric {
	
	public int encodingLength();
	
	public int default_layer();
	
	public default void assignTo(AminoChain<?> chain) {
		assignTo(chain, default_layer(), false);
	}
	
	public default void assignTo(AminoChain<?> chain, boolean reextract) {
		assignTo(chain, default_layer(), reextract);
	}

	public void assignTo(AminoChain<?> chain, int layer, boolean reextract);
}
