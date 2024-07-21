package biology.geneOntology;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;
/**
 * 
 * @author Benjamin Strauss
 *
 */

enum GO_AnnotationLineType {
	TERM, ID, NAME, NAMESPACE, DEF, COMMENT, SUBSET, SYNONYM, XREF, IS_A, IS_OBSOLETE, CONSIDER,
	ALT_ID, RELATIONSHIP, REPLACED_BY, TYPEDEF,
	HOLDS_OVER_CHAIN, IS_TRANSITIVE, IS_METADATA_TAG, IS_CLASS_LEVEL;
	
	static GO_AnnotationLineType getLineType(String arg) {
		if(arg.toLowerCase().equals("[term]")) { return TERM; }
		return parse(arg.substring(0, arg.indexOf(":")));
	}
	
	static GO_AnnotationLineType parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		
		switch(arg) {
		case "[typedef]":
		case "typedef":				return TYPEDEF;
		case "[term]":
		case "term":				return TERM;
		case "alt-id":				return ALT_ID;
		case "is-class-level":		return IS_CLASS_LEVEL;
		case "comment":				return COMMENT;
		case "consider":			return CONSIDER;
		case "def":					return DEF;
		case "holds-over-chain":	return HOLDS_OVER_CHAIN;
		case "id":					return ID;
		case "is-a":				return IS_A;
		case "is-metadata-tag":		return IS_METADATA_TAG;
		case "is-obsolete":			return IS_OBSOLETE;
		case "is-transitive":		return IS_TRANSITIVE;
		case "name":				return NAME;
		case "namespace":			return NAMESPACE;
		case "relationship":		return RELATIONSHIP;
		case "replaced-by":			return REPLACED_BY;
		case "subset":				return SUBSET;
		case "synonym":				return SYNONYM;
		case "xref":				return XREF;
		default:					throw new UnmappedEnumValueException(arg);
		}
	}
}
