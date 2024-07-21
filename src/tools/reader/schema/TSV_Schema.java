package tools.reader.schema;

/**
 * Represents the Schema of a .tsv file
 * @author Benjy Strauss
 *
 */

public class TSV_Schema extends FileSchema<TSV_Column> {
	private static final long serialVersionUID = 1L;
	
	public TSV_Schema() { }
	public TSV_Schema(String label) { super(label); }
	
	public int findIndexOf(TSV_Column column) {
		for(int index = 0; index < size(); ++index) {
			if(column == get(index)) { return index; }
		}
		return COLUMN_NOT_FOUND;
	}
}
