package examples.rScriptGen.limited;

import java.util.BitSet;

import assist.util.LabeledList;
import biology.descriptor.Metric;
import examples.rScriptGen.RTools;
import tools.writer.csv.CSVWriterFactory;

//import utilities.BiolToolBase;

/**
 * Writes a generic version of the R script that used to verify charles's data
 * This version includes combination descriptors
 * 
 * TODO unfinished
 * @author Benjamin Strauss
 *
 * Charles had no "isHOS", "isHOSU"
 * Nothing with both E6 AND E20
 */

public class RComboWriterLimited extends RTools {
	private static final int MAX = ((int) Math.pow(2, 9)) - 1;
	
	//column number, comma separated
	private static final String DESC = "#DES";
	private static final String SWI = "#SWI";
	//column name, plus separated?
	private static final String _DESC = "@DES";
	private static final String _SWI = "@SWI";
	//∑ = column number, period-separated
	private static final String _DESC_ = "∑DES";
	//private static final String _SWI_ = "∑SWI";
	
	public static final int DES_COL_START = 2;
	public static final int DES_COL_END = 10;
	public static final int SWI_COL_START = 12;
	public static final int SWI_COL_END = 43;
	
	private static final String TEST_TEMPLATE = "test_∑DES_#SWI = test[,c(#DES,#SWI)]";
	private static final String MODEL_TEMPLATE = "model_∑DES_#SWI = glm(formula = @SWI ~ @DES, data = learn, family = binomial)";
	private static final String PRED_TEMPLATE = "pred_∑DES_#SWI = predict(model_∑DES_#SWI, newdata = test_∑DES_#SWI, type=\"response\")";
	private static final String AUC_TEMPLATE = "= tryCatch(toString(auc(roc(test_∑DES_#SWI$@SWI, pred_∑DES_#SWI))),"
			+ " error = function(e) { print(e$message) })";
	private static final String AUC_TABLE_TEMPLATE = "aucs[1,META] = \"@DES\"";
	
	private static final LabeledList<Metric> COL_NAMES = CSVWriterFactory.getSwitchAndDescriptorWriter();
	
	public static void main(String[] args) {
		writeLogRegScript("logreg-c-lim.R", "acetyl-rcsb.csv", "sirt-rcsb.csv");
	}
	
	/*
	 * install.packages(c("Rcpp", "readr"))
	 * install.packages(c("ROCR"))
	 * install.packages(c("pROC"))
	 */
	
	/**
	 * Writes a logistic regression script designed to be compatible with the "SwitchAndDescriptorWriter"
	 * @param scriptName: name of the script
	 * @param learnPath: the path to the learning set
	 * @param testPath: the path to the testing set
	 */
	public static void writeLogRegScript(String scriptName, String learnPath, String testPath) {
		LabeledList<String> lines = new LabeledList<String>();
		RTools.addRScriptPreamble(lines);
		
		lines.add("\n#Load CSVs");
		lines.add("learn <- read_csv(\""+learnPath+"\")");
		lines.add("test <- read_csv(\""+testPath+"\")");
		
		writeTestLines(lines);
		writeModelLines(lines);
		writePredictionLines(lines);
		writeMakeTable(lines);
		
		lines.add("write.csv(aucs, file = \"aucs.csv\")");
		
		if(!scriptName.endsWith(".R")) { scriptName += ".R"; }
		
		limitLines(lines);
		
		if(scriptName.contains("/")) {
			writeFileLines(scriptName, lines);
		} else {
			writeFileLines("output/" + scriptName, lines);
		}
		
	}

	private static void limitLines(LabeledList<String> lines) {
		for(int index = lines.size()-1; index >= 0; --index) {
			if(lines.get(index).contains("5.6")) { // No E6 and E20
				lines.remove(index);
			} else if(lines.get(index).contains("8.9")) { // No 2 ambers
				lines.remove(index);
			} else if(lines.get(index).contains("10.11")) { //no 2 vk_pred_charges
				lines.remove(index);
			} else if(lines.get(index).contains("IS_HOSU")) {
				lines.remove(index);
			} else if(lines.get(index).contains("IS_HOS")) {
				lines.remove(index);
			} else if(lines.get(index).contains("_26")) {
				lines.remove(index);
			} else if(lines.get(index).contains("_30")) {
				lines.remove(index);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static boolean skipColumnInTable(String line) {
		return false;
	}

	/**
	 * 
	 * @param template: template string
	 * @param descs: bitset containing "1" for which descriptors to use
	 * @param swi
	 * @return
	 */
	private static String processStrCombo(String template, BitSet descs, int swi) {
		StringBuilder colNamesBuilder = new StringBuilder();
		StringBuilder colNumIdentBuilder = new StringBuilder();
		StringBuilder colNumBuilder = new StringBuilder();
		
		for(int bitIndex = 0; bitIndex <= descs.length(); ++bitIndex) {
			if(descs.get(bitIndex)) {
				colNamesBuilder.append(COL_NAMES.get(bitIndex+DES_COL_START).toString() + "+");
				colNumIdentBuilder.append((bitIndex+DES_COL_START+1) + ".");
				colNumBuilder.append((bitIndex+DES_COL_START+1) + ",");
			}
		}
		
		trimLastChar(colNamesBuilder);
		trimLastChar(colNumIdentBuilder);
		trimLastChar(colNumBuilder);
		
		//"test_#DES_#SWI = test[,c(#DES,#SWI)]";
		template = template.replaceAll(_DESC, colNamesBuilder.toString());
		//qp(_DESC_);
		template = template.replaceAll(_DESC_, colNumIdentBuilder.toString());
		//qp(template.contains(_DESC_));
		//System.exit(0);
		template = template.replaceAll(_SWI, COL_NAMES.get(swi).toString());
		template = template.replaceAll(DESC, colNumBuilder.toString());
		template = template.replaceAll(SWI, ""+(swi+1));
		return template;
	}
	
	/**
	 * Writes test lines
	 * TODO add combinations
	 * @param lines
	 * 
	 * 	public static final int DES_COL_START = 2;
	public static final int DES_COL_END = 10;
	public static final int SWI_COL_START = 12;
	public static final int SWI_COL_END = 43;
	 */
	private static void writeTestLines(LabeledList<String> lines) {
		lines.add("# Tests:");
		lines.add("# 3-11 are descriptor columns, 13-44 are switch columns");
		
		for (int desc = 1; desc <= MAX; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				BitSet metaSet = new BitSet();
				setBitSetToNumber(metaSet, desc);
				lines.add(processStrCombo(TEST_TEMPLATE, metaSet, swi));
			}
		}
		
		lines.add("print(\"tests complete\")");
		lines.add(DIVIDER);
	}
	
	/**
	 * Writes model; lines
	 * TODO add combinations
	 * @param lines
	 */
	private static void writeModelLines(LabeledList<String> lines) {
		lines.add("#Create Models");
		
		for (int desc = 1; desc <= MAX; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				BitSet metaSet = new BitSet();
				setBitSetToNumber(metaSet, desc);
				lines.add(processStrCombo(MODEL_TEMPLATE, metaSet, swi));
			}
		}
		
		lines.add("print(\"models complete\")");
		lines.add(DIVIDER);
	}
	
	/**
	 * Writes predict lines
	 * TODO add combinations
	 * @param lines
	 */
	private static void writePredictionLines(LabeledList<String> lines) {
		lines.add("#Make predictions");
		
		for (int desc = 1; desc <= MAX; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				BitSet metaSet = new BitSet();
				setBitSetToNumber(metaSet, desc);
				lines.add(processStrCombo(PRED_TEMPLATE, metaSet, swi));
			}
		}
		
		lines.add("print(\"predictions complete\")");
		lines.add(DIVIDER);
	}
	
	/**
	 * 32736-
	 * 
	 * Writes lines for making the CSV table
	 * TODO add combinations
	 * @param lines
	 */
	private static void writeMakeTable(LabeledList<String> lines) {
		lines.add("#create the array to hold everything");
		lines.add("aucs = array(dim = c(33, 550))\n");
		lines.add("aucs[1,1] = \"Table\"");
		for (int desc = 1; desc <= MAX; ++desc) {
			BitSet metaSet = new BitSet();
			setBitSetToNumber(metaSet, desc);
			String metaLine = processStrCombo(AUC_TABLE_TEMPLATE, metaSet, 0);
			metaLine = metaLine.replaceAll("META", ""+desc);	
			lines.add(metaLine);
		}
		
		for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
			lines.add("aucs[" + (swi-SWI_COL_START+2) + ",1] = \"" + COL_NAMES.get(swi).toString() + "\"");
		}
		
		lines.add("print(\"auc table complete\")");
		lines.add(DIVIDER);
		lines.add("#Get AUC Values");
		for (int desc = 1; desc <= MAX; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				String str = "aucs["+(swi-SWI_COL_START+2)+","+(desc-DES_COL_START+2)+"] ";
				BitSet metaSet = new BitSet();
				setBitSetToNumber(metaSet, desc);
				str += processStrCombo(AUC_TEMPLATE, metaSet, swi);
				//qp(str);
				lines.add(str);
			}
		}
		
		lines.add("print(\"aucs complete\")");
		lines.add(DIVIDER);
	}
	
	/**
	 * Sets the value of a BitSet equal to the number
	 * NOT FOR USE ELSEWHERE IN CODE! (outside of RComboWriter.java)
	 * 
	 * @param set
	 * @param number
	 */
	private static void setBitSetToNumber(BitSet set, int number) {
		for(int bitIndex = 0; bitIndex < set.size(); ++bitIndex) {
			int pos = (int) Math.pow(2, bitIndex);
			int meta = number / pos;
			if(meta % 2 == 1) { set.set(bitIndex); }
		}
	}
}
