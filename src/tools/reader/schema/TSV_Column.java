package tools.reader.schema;

import assist.EnumParserHelper;
import utilities.SwiPredObject;

/**
 * 
 * @author Benjy Strauss
 *
 */

public enum TSV_Column implements SwiPredObject, SchemaColumn {
	CLUSTER_ID, COUNT_ALL_CHAINS, FULL_CLUSTER,
	COUNT_BONDUGULA_CHAINS, BONDUGULA_CHAINS,
	UNKNOWN_COLUMN;
	
	/**
	 * Determine the TSV column type from the header of a file
	 * @param arg0
	 * @return
	 */
	public static TSV_Column parse(String arg0) {
		arg0 = EnumParserHelper.parseStringForEnumConversion(arg0);
		
		switch(arg0) {
		case "clusterid":					return CLUSTER_ID;
		case "count-all-chains":			return COUNT_ALL_CHAINS;
		case "count-bondugula-chains":		return COUNT_BONDUGULA_CHAINS;
		case "full-cluster":				return FULL_CLUSTER;
		case "bondugula-chains":			return BONDUGULA_CHAINS;
		default:							return UNKNOWN_COLUMN;
		}
	}
	
	/**
	 * 
	 * @param line
	 * @return
	 */
	public static TSV_Schema determineSchema(String line) {
		return determineSchema(line.split("\t"));
	}
	
	/**
	 * 
	 * @param lines
	 * @return
	 */
	public static TSV_Schema determineSchema(String[] lines) {
		TSV_Schema fileSchema = new TSV_Schema();
		
		for(int index = 0; index < lines.length; ++index) {
			fileSchema.add(parse(lines[index]));
		}
		
		return fileSchema;
	}
}
