package biology.descriptor;

import biology.protein.AminoChain;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum CustomEncoding implements EncodingType {
	DUMMY;

	@Override
	public void setName(String arg) { 
		throw new SwiPredRuntimeException("This method is a dummy!  It should not be called!");
	}

	@Override
	public int encodingLength() {
		throw new SwiPredRuntimeException("This method is a dummy!  It should not be called!");
	}

	@Override
	public void assignTo(AminoChain<?> chain, int layer, boolean reextract) {
		throw new SwiPredRuntimeException("This method is a dummy!  It should not be called!");
	}

	@Override
	public int default_layer() {
		throw new SwiPredRuntimeException("This method is a dummy!  It should not be called!");
	}
}
