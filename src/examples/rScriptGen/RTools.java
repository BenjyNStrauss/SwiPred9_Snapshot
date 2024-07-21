package examples.rScriptGen;

import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class RTools extends LocalToolBase {
	
	protected static final String DIVIDER = "#----------------------------------------\n";
	
	public static void addRScriptPreamble(LabeledList<String> lines) {
		lines.add("#Script by Benjamin Strauss's RWriter");
		
		lines.add("library(readr)");
		lines.add("library(ROCR)");
		lines.add("library(pROC)");
	}
}
