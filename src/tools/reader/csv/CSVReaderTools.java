package tools.reader.csv;

import assist.exceptions.UnmappedEnumValueException;
import biology.descriptor.Identifier;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class CSVReaderTools extends LocalToolBase {
	
	public static int detectResCol(String[] tokens) {
		for(int index = 0; index < tokens.length; ++index) {
			try {
				Identifier id = Identifier.parse(tokens[index]);
				if(id.isRes()) {
					return index;
				}
			} catch (UnmappedEnumValueException UEVE) { }
		}
		return -1;
	}
	
}
