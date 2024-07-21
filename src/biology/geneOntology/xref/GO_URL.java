package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * URL-type GO xref
 * @author Benjamin Strauss
 *
 */

public class GO_URL extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	public final String url;
	public final boolean http_secure;
	
	public GO_URL(String xref) {
		if(!xref.startsWith("xref: https:") && !xref.startsWith("xref: http:")) {
			throw new GOParsingException();
		} 
		http_secure = xref.startsWith("xref: https:");
		url = compactArray(xref.split(":"), 1)[1].trim();
	}
	
	protected GO_URL(GO_URL url) {
		this.url = url.url;
		this.http_secure = url.http_secure;
	}
	
	public GO_URL clone() { return new GO_URL(this); }
	
	public boolean equals(Object other) {
		if(other instanceof GO_URL) {
			GO_URL mc = (GO_URL) other;
			if(!mc.url.equals(url)) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: "+url; }
}
