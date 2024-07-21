package tools.reader.schema;

import biology.descriptor.Descriptor;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class PredictionSchema extends FileSchema<DataPredLoc> {
	private static final long serialVersionUID = 1L;
	
	private final int prot_col;
	private final int res_num_col;
	
	/**
	 * 
	 * @param prot_col
	 * @param res_num_col
	 */
	public PredictionSchema(int prot_col, int res_num_col) {
		this.prot_col = prot_col;
		this.res_num_col = res_num_col;
	}
	
	public int prot_col() { return prot_col; }
	public int res_num_col() { return res_num_col; }
	
	public void add(Descriptor[] descriptors, int predColLoc, int testColLoc) {
		add(new DataPredLoc(descriptors, predColLoc, testColLoc));
	}

	@Override
	public int findIndexOf(DataPredLoc column) {
		for(int index = 0; index < size(); ++index) {
			if(column.equals(get(index))) { return index; }
		}
		return COLUMN_NOT_FOUND;
	}
	
}
