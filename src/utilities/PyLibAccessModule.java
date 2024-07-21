package utilities;

import assist.script.PythonScript;

/**
 * 
 * @author Benjamin Strauss 
 *
 */

public final class PyLibAccessModule extends LocalToolBase {
	private PyLibAccessModule() { }
	
	/**
	 * 
	 * @param function
	 * @param param_vec1
	 * @param param_vec2
	 * @return
	 */
	public static double accessPyFunct(String function, double[] param_vec1, double[] param_vec2) {
		StringBuilder vecEncoder = new StringBuilder();
		for(double val: param_vec1) {
			vecEncoder.append(val + ",");
		}
		trimLastChar(vecEncoder);
		String vec1 = vecEncoder.toString();
		vecEncoder.setLength(0);
		for(double val: param_vec2) {
			vecEncoder.append(val + ",");
		}
		trimLastChar(vecEncoder);
		String vec2 = vecEncoder.toString();
		
		PythonScript libAccess = new PythonScript("src-py/util/Access_v_v.py",function,vec1,vec2);
		libAccess.run();
		double retval = Double.NaN;
		
		try {
			retval = Double.parseDouble(libAccess.getStdOut());
		} catch (NumberFormatException NFE) {
			error("Python access failure for function: " + function);
		}
		
		return retval;
	}
	
	/*public static void main(String[] args) {
		//qp(accessPyFunct("cosine", new double[]{1,2,3}, new double[]{3,2,3}));
	}*/
}
