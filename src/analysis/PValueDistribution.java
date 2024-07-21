package analysis;

import java.text.DecimalFormat;
import java.util.ArrayList;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class PValueDistribution extends LocalToolBase {

	public static void main(String[] args) {
		String[] lines = getFileLines("esm-reg-data/esm2/results-logistic.txt");
		
		int[] categories = new int[20];
		int counter = 0;
		int counter2 = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		
		boolean reached_divider = false;
		
		for(int ii = 0; ii < lines.length; ++ ii) {
			if(!reached_divider || lines[ii].isEmpty()) {
				if(lines[ii].startsWith("–")) { reached_divider = true; }
				continue;
			}
			
			for(String num: lines[ii].split(",\\s+")) {
				double val = Double.parseDouble(num);
				double index = val*20;
				++categories[(int) index];
				
				if(val > 0.01) {
					//qp("("+ii+":"+val+")");
					list.add(ii);
					++counter;
				}
				if(val > 0.05) { ++counter2; }
			}
		}
		
		for(Integer val: list) {
			System.out.print(val+",");
		}
		
		qp("\n"+counter+" bad p-values @ 0.01");
		qp(counter2+" bad p-values @ 0.05\n");
		DecimalFormat form = new DecimalFormat("#0.00");  
		
		for(double ii = 0; ii < categories.length; ++ii) {
			qp("[" +form.format(ii/20) + ", " +form.format((ii+1)/20) + ") : " + categories[(int) ii]);
		}

	}
	
	private static String format(double d) {
		return (d % 0.1 < 0.001) ? ""+d+"0" : ""+d; 
	}

}
