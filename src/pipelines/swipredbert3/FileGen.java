package pipelines.swipredbert3;

import modules.encode.tokens.AminoToken;
import modules.encode.tokens.PrimaryToken;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class FileGen extends LocalToolBase {

	public static void main(String[] args) {
		StringBuilder builder = new StringBuilder();
		for(AminoToken token: AminoToken.values()) {
			builder.append(token.condensed()+",");
		}
		for(PrimaryToken token: PrimaryToken.values()) {
			builder.append(token.condensed()+",");
		}
		qp(builder);
	}

}
