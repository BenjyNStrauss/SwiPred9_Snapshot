package tools.reader.goa;

import assist.util.LabeledHash;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class GAF_Reader extends LocalToolBase {
	
	/**
	 * 
	 * @param goaLines: all lines from the .gaf file
	 * @return HashTable containing a mapping of Database ID to GO ID
	 */
	public static LabeledHash<String, Integer> getMappings(String[] goaLines) {
		return getMappings(goaLines, null);
	}
	
	/**
	 * 
	 * @param goaLines: all lines from the .gaf file
	 * @param db_filter: filter out all entries except those from the mentioned database
	 * @return HashTable containing a mapping of Database ID to GO ID
	 */
	public static LabeledHash<String, Integer> getMappings(String[] goaLines, String db_filter) {
		LabeledHash<String, Integer> mappings = new LabeledHash<String, Integer>();
		
		for(String line: goaLines) {
			String[] fields = line.split("\t");
			if(db_filter != null && (!fields[0].equals(db_filter))) {
				continue;
			}
			mappings.put(fields[1], Integer.parseInt(fields[4].substring(3)));
		}
		
		return mappings;
	}
	
	
	public static void main(String[] args) {
		

	}

}
