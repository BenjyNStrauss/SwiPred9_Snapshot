package modules.encode.tokens;

import assist.util.LabeledHash;
import biology.descriptor.Metric;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public interface ResidueToken extends Metric {
	
	static final LabeledHash<String, AminoToken> STR_PARSE = AminoToken.STR_PARSE;
	
	static final LabeledHash<String, AminoSToken> STR_PARSE2 = new LabeledHash<String, AminoSToken>() {
		private static final long serialVersionUID = 1L;
		{
			for(AminoSToken token: AminoSToken.values()) {
				put(token.toString().replaceAll("_", "-"), token);
			}
			
			for(AminoSToken token: AminoSToken.values()) {
				put(token.condensed(), token);
			}
		}
	};
	
	public int ordinal();
	
	public default int[] toProteinBert(ResidueToken[] tokens) {
		int[] values = new int[tokens.length];
		
		for(int index = 0; index < tokens.length; ++index) {
			values[index] = tokens[index].ordinal();
		}
		
		return values;		
	}
}
