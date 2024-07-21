package tools.reader.schema;

import assist.util.LabeledList;

/**
 * 
 * @author Benjy Strauss
 *
 */

public abstract class FileSchema<E extends SchemaColumn> extends LabeledList<E> {
	private static final long serialVersionUID = 1L;
	
	public static final int COLUMN_NOT_FOUND = -1;
	
	public FileSchema() { }
	public FileSchema(String label) { super(label); }
	
	/**
	 * Finds the index of the given column
	 * @param column
	 * @return
	 */
	public abstract int findIndexOf(E column);
}
