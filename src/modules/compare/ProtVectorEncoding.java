package modules.compare;

import biology.descriptor.EncodingType;
import utilities.DataObject;

/**
 * Just a holder class for vector encodings
 * @author Benjamin Strauss
 *
 */

public class ProtVectorEncoding extends DataObject {
	private static final long serialVersionUID = 1L;
	public final String id;
	public final String sequence;
	public final EncodingType encoding;
	public final double[] vector;
	
	public ProtVectorEncoding(String id, String seq, EncodingType encoding, double[] vector) {
		this.id = id;
		this.sequence = seq;
		this.encoding = encoding;
		this.vector = vector;
	}
	
	public String toString() {
		return "ProtVectorEncoding for " + id + " with " + encoding;
	}
}
