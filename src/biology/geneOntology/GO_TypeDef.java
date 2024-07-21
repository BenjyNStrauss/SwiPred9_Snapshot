package biology.geneOntology;

import java.util.List;

import biology.geneOntology.xref.GOxref;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class GO_TypeDef extends GO_Object {
	private static final long serialVersionUID = 1L;

	String id = null;
	String name = null;
	String namespace = null;
	GOxref xref = null;
	String holds_over_chain = null;
	boolean is_transitive = false;
	boolean is_metadata_tag = false;
	boolean is_class_level = false;
	
	public GO_TypeDef(String[] lines) {
		for(String line: lines) {
			GO_AnnotationParser.parseLineTypeDef(line, this);
		}
	}
	
	public GO_TypeDef(List<String> lines) {
		for(String line: lines) {
			GO_AnnotationParser.parseLineTypeDef(line, this);
		}
	}
	
	private GO_TypeDef(GO_TypeDef cloneFrom) {
		id = cloneFrom.id;
		name = cloneFrom.name;
		namespace = cloneFrom.namespace;
		xref = cloneFrom.xref.clone();
		holds_over_chain = cloneFrom.id;
		is_transitive = cloneFrom.is_transitive;
		is_metadata_tag = cloneFrom.is_metadata_tag;
		is_class_level = cloneFrom.is_class_level;
	}

	public int hashCode() { return id.hashCode(); }
	
	public GO_TypeDef clone() { return new GO_TypeDef(this); }
	
	public boolean equals(Object other) {
		if(other instanceof GO_TypeDef) {
			GO_TypeDef def = (GO_TypeDef) other;
			return id.equals(def.id);
		} else {
			return false;
		}
	}
	
	//TODO modify at some point
	public String toString() {
		return id;
	}
}
