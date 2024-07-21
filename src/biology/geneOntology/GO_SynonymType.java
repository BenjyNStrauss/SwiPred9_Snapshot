package biology.geneOntology;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum GO_SynonymType {
	BROAD, EXACT, NARROW, RELATED;
	
	public static GO_SynonymType parse(String arg) {
		switch(EnumParserHelper.parseStringForEnumConversion(arg)) {
		case "broad":	return BROAD;
		case "exact":	return EXACT;
		case "narrow":	return NARROW;
		case "related":	return RELATED;
		default:		throw new UnmappedEnumValueException(arg);
		}
	}
	
}
