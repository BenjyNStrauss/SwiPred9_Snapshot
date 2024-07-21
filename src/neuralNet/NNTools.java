package neuralNet;

import assist.base.Assist;
import assist.exceptions.ArrayLengthMismatchException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class NNTools extends Assist {

	public static void verifyArraySize(double[]... arrays) {
		int size = arrays[0].length;
		for(double[] array: arrays) {
			if(array.length != size) {
				throw new ArrayLengthMismatchException();
			}
		}
	}
}
