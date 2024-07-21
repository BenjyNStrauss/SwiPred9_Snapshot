package regression;

import java.util.ArrayList;

import regression.vectors.AbstractDataVector;
import regression.vectors.DataVector;
import utilities.DataObject;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class DataMatrix extends DataObject {
	private static final long serialVersionUID = 1L;
	//can't be modified publicly
	public final double original_matrix[][];
	//can be modified publicly
	public double matrix[][];
	
	public DataMatrix(AbstractDataVector<?>... dataVectors) {
		//check to make sure all of the same length
		ArrayList<DataVector<?>> vectors = new ArrayList<DataVector<?>>();
		
		//convert all to datavectors
		int size = -1;
		for(AbstractDataVector<?> adv: dataVectors) {
			if(size < 0) { size = adv.size(); }
			else if(adv.size() != size) {
				throw new InconsistentMatrixSizeException();
			}
			
			DataVector<?> quantized[] = adv.quantify();
			for(DataVector<?> dv: quantized) {
				vectors.add(dv);
			}
		}
		
		//allocate memory
		original_matrix = new double[vectors.size()][size];
		matrix = new double[vectors.size()][size];
		
		for(int vectNo = 0; vectNo < vectors.size(); ++vectNo) {
			for(int vectIndex = 0; vectIndex < size; ++vectIndex) {
				original_matrix[vectNo][vectIndex] = vectors.get(vectNo).get(vectIndex).doubleValue();
			}
		}
		
		//copy the data to the modifiable matrix
		for(int ii = 0; ii < original_matrix.length; ++ii) {
			for(int jj = 0; jj < original_matrix[ii].length; ++jj) {
				matrix[ii][jj] = original_matrix[ii][jj];
			}
		}
	}
}
