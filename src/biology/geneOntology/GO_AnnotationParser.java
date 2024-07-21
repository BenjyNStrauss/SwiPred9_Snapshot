package biology.geneOntology;

import assist.util.Pair;
import biology.geneOntology.xref.GOxref;
import utilities.LocalToolBase;

/**
 * Class for doing all of the parsing of GO Annotations
 * @author Benjamin Strauss
 * 
 */

final class GO_AnnotationParser extends LocalToolBase {
	private static final String ID_PREFIX = "id: GO:";
	private static final String ALT_ID_PREFIX = "alt_id: GO:";
	private static final String NAME_PREFIX = "name:";
	private static final String NAMESPACE_PREFIX = "namespace:";
	private static final String DEF_PREFIX = "def: \":";
	private static final String IS_A_PREFIX = "is_a: GO:";
	private static final String IS_A_DELIM = " ! ";
	private static final String COMMENT_PREFIX = "comment:";
	private static final String CONSIDER_PREFIX = "consider: GO:";
	private static final String RELATIONSHIP_PREFIX = "relationship: ";
	private static final String REPLACED_BY_PREFIX = "replaced_by: GO:";
	
	private GO_AnnotationParser() { }
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	static void parseLine(String line, GO_AnnotationTerm annotation) {
		
		GO_AnnotationLineType lineType = GO_AnnotationLineType.getLineType(line);
		
		switch(lineType) {
		case COMMENT:			parseComment(line, annotation);						break;
		case CONSIDER:			parseConsider(line, annotation);					break;
		case DEF:				parseDef(line, annotation);							break;	
		case ID:				parseID(line, annotation);							break;	
		case ALT_ID:			parseAltID(line, annotation);						break;
		case IS_A:				parseIsA(line, annotation);							break;
		case IS_OBSOLETE:		parseIsObsolete(line.trim(), annotation);			break;
		case NAME:				parseName(line, annotation);						break;	
		case NAMESPACE:			parseNamespace(line, annotation);					break;
		case RELATIONSHIP:		parseRelationship(line, annotation);				break;
		case REPLACED_BY:		parseReplacedBy(line, annotation);					break;
		case SUBSET:			annotation.subsets.add(GO_Subset.parse(line));		break;
		case SYNONYM:			annotation.synonyms.add(new GO_Synonym(line));		break;
		case TERM:																	break;
		case XREF:				annotation.xrefs.add(GOxref.parse(line));			break;
		default:				throw new RuntimeException("System not yet equipped for: "+line);
		}
	}
	
	/**
	 * 
	String holds_over_chain;
	boolean is_transitive;
	boolean is_metadata_tag;
	boolean is_class_level;
	 * 
	 * 
	 * @param line
	 * @param annotation
	 */
	static void parseLineTypeDef(String line, GO_TypeDef def) {
		
		GO_AnnotationLineType lineType = GO_AnnotationLineType.getLineType(line);
		
		switch(lineType) {
		case TYPEDEF:														break;
		case ID:				parseID(line, def);							break;	
		case NAME:				parseName(line, def);						break;	
		case NAMESPACE:			parseNamespace(line, def);					break;
		case XREF:				def.xref = GOxref.parse(line);				break;
		case HOLDS_OVER_CHAIN:	parseHoldsOverChain(line, def);				break;
		case IS_TRANSITIVE:		def.is_transitive   = parseBoolField(line);	break;
		case IS_METADATA_TAG:	def.is_metadata_tag = parseBoolField(line);	break;
		case IS_CLASS_LEVEL:	def.is_class_level  = parseBoolField(line);	break;
		default:				throw new RuntimeException("System not yet equipped for: "+line);
		}
	}

	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseID(String line, GO_AnnotationTerm annotation) {
		if(annotation.id != GO_AnnotationTerm.NOT_SET) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		} else {
			annotation.id = Integer.parseInt(line.substring(ID_PREFIX.length()).trim());
		}
	}
	
	private static void parseID(String line, GO_TypeDef def) {
		if(def.id != null) {
			throw new DuplicateSingletonGOFieldException(line, def);
		} else {
			def.id = line.substring(ID_PREFIX.length()).trim();
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseName(String line, GO_AnnotationTerm annotation) {
		if(annotation.name != null) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		} else {
			annotation.name = line.substring(NAME_PREFIX.length()).trim();
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseName(String line, GO_TypeDef annotation) {
		if(annotation.name != null) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		} else {
			annotation.name = line.substring(NAME_PREFIX.length()).trim();
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseNamespace(String line, GO_AnnotationTerm annotation) {
		if(annotation.namespace != null) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		} else {
			annotation.namespace = line.substring(NAMESPACE_PREFIX.length()).trim();
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseNamespace(String line, GO_TypeDef annotation) {
		if(annotation.namespace != null) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		} else {
			annotation.namespace = line.substring(NAMESPACE_PREFIX.length()).trim();
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseDef(String line, GO_AnnotationTerm annotation) {
		if(annotation.def != null) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		}
		
		line = line.substring(DEF_PREFIX.length());
		String[] sections = line.split("\"");		
		annotation.def = sections[0].trim();
		
		sections[1] = sections[1].replaceAll("[\\]\\[]", "").trim();
		sections = sections[1].split(", ");
		for(String str: sections) {
			String[] subsections = str.split(":");
			annotation.def_codes.add(new GO_DefCode(subsections[0], subsections[1]));
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseIsA(String line, GO_AnnotationTerm annotation) {
		line = line.substring(IS_A_PREFIX.length(), line.indexOf(IS_A_DELIM));
		annotation.is_a.add(Integer.parseInt(line));
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseComment(String line, GO_AnnotationTerm annotation) {
		if(annotation.comment != null) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		} else {
			annotation.comment = line.substring(COMMENT_PREFIX.length()).trim();
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseIsObsolete(String line, GO_AnnotationTerm annotation) {
		if(line.endsWith("true")) {
			annotation.is_obsolete = true;
		} else if(line.endsWith("false")) {
			annotation.is_obsolete = false;
		} else {
			throw new GOParsingException("Maybe Obsolete? \"" + line + "\"");
		}
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseConsider(String line, GO_AnnotationTerm annotation) {
		line = line.substring(CONSIDER_PREFIX.length());
		annotation.consider.add(Integer.parseInt(line));
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseAltID(String line, GO_AnnotationTerm annotation) {
		line = line.substring(ALT_ID_PREFIX.length());
		annotation.altIDs.add(Integer.parseInt(line));
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseRelationship(String line, GO_AnnotationTerm annotation) {
		line = line.substring(RELATIONSHIP_PREFIX.length(), line.indexOf(IS_A_DELIM)).trim();
		
		String[] parts = line.split("\\s+");
		RelationshipType rst = RelationshipType.parse(parts[0]);
		parts[1] = parts[1].substring(3);
		
		annotation.relationships.add(new Pair<RelationshipType, Integer>(rst, Integer.parseInt(parts[1])));
	}
	
	/**
	 * 
	 * @param line
	 * @param annotation
	 */
	private static void parseReplacedBy(String line, GO_AnnotationTerm annotation) {
		if(annotation.replaced_by != GO_AnnotationTerm.NOT_SET) {
			throw new DuplicateSingletonGOFieldException(line, annotation);
		} else {
			annotation.replaced_by = Integer.parseInt(line.substring(REPLACED_BY_PREFIX.length()).trim());
		}
	}
	
	private static void parseHoldsOverChain(String line, GO_TypeDef def) {
		if(def.holds_over_chain != null) {
			throw new DuplicateSingletonGOFieldException(line, def);
		} else {
			def.holds_over_chain = line.substring("holds_over_chain:".length()).trim();
		}
	}
	
	private static boolean parseBoolField(String line) {
		if(line.endsWith("true")) {
			return true;
		} else if(line.endsWith("false")) {
			return false;
		} else {
			throw new GOParsingException("Boolean value unknown: \"" + line + "\"");
		}
	}
}
