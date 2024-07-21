package biology.geneOntology;

import assist.EnumParserHelper;
import assist.exceptions.UnmappedEnumValueException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum RelationshipType {
	PART_OF, REGULATES, POSITIVELY_REGULATES, NEGATIVELY_REGULATES;
	
	public static RelationshipType parse(String arg) {
		arg = EnumParserHelper.parseStringForEnumConversion(arg);
		
		switch(arg) {
		case "part-of":					return PART_OF;
		case "regulates":				return REGULATES;
		case "negatively-regulates":	return POSITIVELY_REGULATES;
		case "positively-regulates":	return NEGATIVELY_REGULATES;
		default:						throw new UnmappedEnumValueException(arg);
		}
		
	}
}
