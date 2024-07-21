package biology.geneOntology;

import assist.util.Pair;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class GO_DefCode extends Pair<String,String> {
	private static final long serialVersionUID = 1L;

	public GO_DefCode() { }
	
	public GO_DefCode(String x, String y) {
		this.x = x;
		this.y = y;
	}
	
	public GO_DefCode clone() { return new GO_DefCode(x, y); }
}
