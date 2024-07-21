package patching;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class PatchBase extends LocalToolBase {
	
	protected static final int RES_INDEX = 2;
	protected static final int E6_CSV = 4;
	protected static final int E20_CSV = 5;
	protected static final int E6_PATCH = 4;
	protected static final int E20_PATCH = 6;
	
	protected static final double[] CSVs = { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 
										   6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 7.0};
	
	public static String[][] readSwipredOutput(double section) {
		String infile = "output/bon8/sp8-bon-out+"+section+".csv";
		String[] lines = getFileLines(infile);
		String[][] fields = new String[lines.length][];
		for(int ii = 0; ii < lines.length; ++ii) {
			fields[ii] = lines[ii].split(",");
		}
		return fields;
	}
}
