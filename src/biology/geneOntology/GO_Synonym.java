package biology.geneOntology;

import assist.util.LabeledList;
import biology.geneOntology.xref.GOxref;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class GO_Synonym extends GO_Object {
	private static final long serialVersionUID = 1L;
	private static final String COMMENT = "[] {comment=\"";
	private static final String XREF_PREFIX = "xref: ";
	
	protected static final String TAG = "synonym: ";
	
	public final String comment;
	public final GO_SynonymType type;
	public final String tag;
	public final LabeledList<GOxref> links;
	
	public GO_Synonym(String arg) { 
		if(!arg.startsWith(TAG)) { throw new GOParsingException(); }
		
		links = new LabeledList<GOxref>();
		arg = arg.substring(TAG.length()+1);
		
		String[] parts = GOParsingTools.compactArray(arg.split("\""), 1 , "\"");
		comment = parts[0].trim();
		
		parts[1] = parts[1].trim();
		parts = parts[1].split("\\s+");
		
		type = GO_SynonymType.parse(parts[0]);
		
		parts[1] = parts[1].trim();
		
		if(!parts[1].contains("[")) {
			tag = parts[1];
		} else {
			tag = null;
		}
		
		parts = GOParsingTools.compactArray(parts, (tag == null) ? 1 : 2, " ");
		
		String linkStr = (tag == null) ? parts[1] : parts[2];
		
		if(!linkStr.startsWith("[]")) {
			linkStr = linkStr.substring(1, linkStr.length()-1);
			
			parts = linkStr.split(", ");
			
			for(String str: parts) {
				links.add(GOxref.parse("xref: "+str));
			}
		} else if(!linkStr.equals("[]")) {
			linkStr = linkStr.substring(COMMENT.length(), linkStr.length()-2);
			
			parts = linkStr.split(", ");
			
			for(String str: parts) {
				links.add(GOxref.parse("xref: "+str));
			}
		}
	}
	
	private GO_Synonym(GO_Synonym cloneFrom) {
		comment = cloneFrom.comment;
		type = cloneFrom.type;
		tag = cloneFrom.tag;
		links = new LabeledList<GOxref>();
		for(GOxref link: cloneFrom.links) {
			links.add(link);
		}
	}
	
	public boolean equals(Object other) {
		if(other instanceof GO_Synonym) {
			GO_Synonym _other_ = (GO_Synonym) other;
			if(!comment.equals(_other_.comment)) { return false; }
			
			if(type != _other_.type) { return false; }
			
			if(tag == null) {
				if(_other_.tag != null) { return false; }
			} else if(!tag.equals(_other_.tag)) { 
				return false;
			}
			
			if(links.size() != _other_.links.size()) { return false; }
			
			for(int index = 0; index < links.size(); ++ index) {
				if(!links.get(index).equals(_other_.links.get(index))) {
					return false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public GO_Synonym clone() { return new GO_Synonym(this); }
	
	public String toString() {
		StringBuilder builder = new StringBuilder(TAG+"\""+comment + "\" "+ type+" ");
		if(tag != null) { builder.append(tag+" "); }
		builder.append("[");
		
		for(GOxref link: links) {
			builder.append(link.toString().substring(XREF_PREFIX.length()));
		}
		
		if(links.size() > 0) {
			trimLastChar(builder);
			trimLastChar(builder);
		}
		
		builder.append("]");
		return builder.toString();
	}
	
	/*public static void main(String[] args) {
		String[] lines = LocalToolBase.getFileLines("@dev/go-basic.obo");
		//alt_id: GO:0019952
		for(String line: lines) {
			if(line.startsWith("synonym: ")) { // && (!line.startsWith("consider: GO:"))
				try {
					GO_Synonym test = new GO_Synonym(line);
					if(!test.toString().equals(line)) {
						qp(line);
						qp(test.toString());
						qp("****");
					}
					
					
				} catch (Exception e) {
					
				}
			}
		}
		
	}*/
}
