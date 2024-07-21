package biology.geneOntology.xref;

import assist.ActuallyCloneable;
import biology.geneOntology.GOParsingException;

/**
 * Wikipedia-type GO xref
 * @author Benjamin Strauss
 *
 */

public class WikipediaRef extends GOxref implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	protected final String ref;
	
	/*public Wikipedia(String ref) {
		this.ref = ref;
	}*/
	
	public WikipediaRef(String xref) {
		if(!xref.startsWith("xref: Wikipedia:")) { throw new GOParsingException(xref); } 
		
		String[] parts = xref.split(":");
		if(parts.length == 3) {
			ref = parts[2];
		} else {
			String tmp = parts[2];
			for(int ii = 3; ii < parts.length; ++ii) {
				tmp+=":"+parts[ii];
			}
			ref = tmp;
		}
	}
	
	protected WikipediaRef(WikipediaRef wiki) {
		this.ref = wiki.ref;
	}
	 
	public WikipediaRef clone() { return new WikipediaRef(this); }
	
	public boolean equals(Object other) {
		if(other instanceof WikipediaRef) {
			WikipediaRef wiki = (WikipediaRef) other;
			if(wiki.ref != ref) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() { return "xref: Wikipedia:"+ref; }
}
