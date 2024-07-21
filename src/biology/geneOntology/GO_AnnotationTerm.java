package biology.geneOntology;

import java.io.File;
import java.util.List;

import assist.util.LabeledList;
import assist.util.LabeledSet;
import assist.util.Pair;
import biology.geneOntology.xref.GOxref;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class GO_AnnotationTerm extends GO_Object {
	private static final long serialVersionUID = 1L;
	
	public static final int NOT_SET = -1;
	
	protected int id = NOT_SET;
	protected String name = null;
	protected String namespace = null;
	protected final LabeledSet<Integer> altIDs = new LabeledSet<Integer>();
	
	protected String def = null;
	protected LabeledSet<GO_DefCode> def_codes = new LabeledSet<GO_DefCode>();
	
	protected String comment = null;
	protected LabeledSet<GO_Synonym> synonyms  = new LabeledSet<GO_Synonym>();
	
	protected LabeledSet<GO_Subset> subsets = new LabeledSet<GO_Subset>();
	protected LabeledSet<Integer> is_a = new LabeledSet<Integer>();
	protected boolean is_obsolete = false;
	protected LabeledSet<Integer> consider = new LabeledSet<Integer>();
	
	protected LabeledSet<GOxref> xrefs = new LabeledSet<GOxref>();
	protected int replaced_by = NOT_SET;
	
	protected LabeledSet<Pair<RelationshipType, Integer>> relationships = 
			new LabeledSet<Pair<RelationshipType, Integer>>();
	
	public GO_AnnotationTerm(String[] lines) {
		for(String line: lines) {
			GO_AnnotationParser.parseLine(line, this);
		}
	}
	
	public GO_AnnotationTerm(List<String> lines) {
		for(String line: lines) {
			GO_AnnotationParser.parseLine(line, this);
		}
	}
	
	private GO_AnnotationTerm(GO_AnnotationTerm cloneFrom) {
		id = cloneFrom.id;
		name = cloneFrom.name;
		namespace = cloneFrom.namespace;
		def = cloneFrom.def;
		comment = cloneFrom.comment;
		is_obsolete = cloneFrom.is_obsolete;
		replaced_by = cloneFrom.replaced_by;
		
		for(Integer alt_id: cloneFrom.altIDs) {
			altIDs.add(alt_id);
		}
		
		for(GO_DefCode code: cloneFrom.def_codes) {
			def_codes.add(code.clone());
		}
		
		for(GO_Subset subset: cloneFrom.subsets) {
			subsets.add(subset);
		}
		
		for(Integer alt: cloneFrom.is_a) {
			is_a.add(alt);
		}
		
		for(Integer val: cloneFrom.consider) {
			consider.add(val);
		}
		
		for(GOxref xref: cloneFrom.xrefs) {
			xrefs.add(xref.clone());
		}
	}
	
	public boolean is_obsolete() { return is_obsolete; }
	public int id() { return id; }
	public String comment() { return comment; }
	public String def() { return def; }
	public String name() { return name; }
	public String namespace() { return namespace; }
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static GO_AnnotationTerm[] parseFile(String filename) {
		return parseFile(new File(filename));
	}
	
	/**
	 * 
	 * @param infile
	 * @return
	 */
	public static GO_AnnotationTerm[] parseFile(File infile) {
		String[] lines = getFileLines(infile);
		
		//the list of annotations
		LabeledList<GO_AnnotationTerm> termBuffer = new LabeledList<GO_AnnotationTerm>();
		//the list of lines for a given annotation
		LabeledList<String> lineBuffer = new LabeledList<String>();
		
		boolean termOK = false;
		for(String line: lines) {
			if(line.toLowerCase().trim().equals("[term]")) {
				termOK = true;
				continue;
			}
			
			if(termOK) {
				if(line.trim().length() != 0) {
					lineBuffer.add(line);
				} else {
					termBuffer.add(new GO_AnnotationTerm(lineBuffer));
					lineBuffer.clear();
					termOK = false;
				}
			}
		}
		
		GO_AnnotationTerm[] termArray = new GO_AnnotationTerm[termBuffer.size()];
		termBuffer.toArray(termArray);
		return termArray;
	}
	
	public int hashCode() { return id; }
	
	public GO_AnnotationTerm clone() { return new GO_AnnotationTerm(this); }
	
	public boolean equals(Object other) {
		return (other instanceof GO_AnnotationTerm && hashCode() != other.hashCode());
	}
	
	public String toString() { return id + " ! " + name; }
	
	public static void main(String[] args) {
		GO_AnnotationTerm[] annotations = parseFile("@dev/go-basic-restricted.obo");
		
		qp(annotations.length);
	}
}
