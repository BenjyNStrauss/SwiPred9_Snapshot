package examples.rScriptGen;

import assist.util.LabeledList;
import biology.descriptor.Metric;
import tools.writer.csv.CSVWriterFactory;

//import utilities.BiolToolBase;

/**
 * Writes a generic version of the R script that used to verify charles's data
 * @author Benjamin Strauss
 *
 */

public class RWriter extends RTools {
	private static final String DESC = "#DES";
	private static final String SWI = "#SWI";
	private static final String _DESC = "@DES";
	private static final String _SWI = "@SWI";
	
	public static final int DES_COL_START = 2;
	public static final int DES_COL_END = 10;
	public static final int SWI_COL_START = 12;
	public static final int SWI_COL_END = 43;
	
	private static final String TEST_TEMPLATE = "test_#DES_#SWI = test[,c(#DES,#SWI)]";
	private static final String MODEL_TEMPLATE = "model_#DES_#SWI = glm(formula = @SWI ~ @DES, data = learn, family = binomial)";
	private static final String PRED_TEMPLATE = "pred_#DES_#SWI = predict(model_#DES_#SWI, newdata = test_#DES_#SWI, type=\"response\")";
	private static final String AUC_TEMPLATE = "= tryCatch(toString(auc(roc(test_#DES_#SWI$@SWI, pred_#DES_#SWI))),"
			+ " error = function(e) { print(e$message) })";
	
	private static final LabeledList<Metric> COL_NAMES = CSVWriterFactory.getSwitchAndDescriptorWriter();
	
	public static void main(String[] args) {
		writeLogRegScript("log-reg.R", "acetyl-rcsb.csv", "sirt-rcsb.csv");
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
		
		lines.add("# Tests:");
		lines.add("# 3-11? are descriptor columns, 13-44? are switch columns");
		
		for (int desc = DES_COL_START; desc <= DES_COL_END; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				lines.add(processStr(TEST_TEMPLATE, desc, swi));
			}
		}
		lines.add("print(\"tests complete\")");
		lines.add(DIVIDER);
		
		lines.add("#Create Models");
		for (int desc = DES_COL_START; desc <= DES_COL_END; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				lines.add(processStr(MODEL_TEMPLATE, desc, swi));
			}
		}
		lines.add("print(\"models complete\")");
		lines.add(DIVIDER);
		
		lines.add("#Make predictions");
		for (int desc = DES_COL_START; desc <= DES_COL_END; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				lines.add(processStr(PRED_TEMPLATE, desc, swi));
			}
		}
		lines.add("print(\"predictions complete\")");
		lines.add(DIVIDER);
		
		lines.add("#create the array to hold everything");
		lines.add("aucs = array(dim = c(33, 10))\n");
		lines.add("aucs[1,1] = \"Table\"");
		for (int desc = DES_COL_START; desc <= DES_COL_END; ++desc) {
			//qp("aucs[1," + desc + "] = \"" + COL_NAMES[desc+DES_COL_START-3] + "\"");
			lines.add("aucs[1," + (desc) + "] = \"" + COL_NAMES.get(desc).toString() + "\"");
		}
		for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
			lines.add("aucs[" + (swi-SWI_COL_START+2) + ",1] = \"" + COL_NAMES.get(swi).toString() + "\"");
		}
		
		lines.add("print(\"auc table complete\")");
		lines.add(DIVIDER);
		lines.add("#Get AUC Values");
		for (int desc = DES_COL_START; desc <= DES_COL_END; ++desc) {
			for (int swi = SWI_COL_START; swi <= SWI_COL_END; ++swi) {
				String str = "aucs["+(swi-SWI_COL_START+2)+","+(desc-DES_COL_START+2)+"] ";
				str += processStr(AUC_TEMPLATE, desc, swi);
				//qp(str);
				lines.add(str);
			}
		}
		
		lines.add("print(\"aucs complete\")");
		lines.add(DIVIDER);
		lines.add("write.csv(aucs, file = \"aucs.csv\")");
		
		if(!scriptName.endsWith(".R")) { scriptName += ".R"; }
		
		if(scriptName.contains("/")) {
			writeFileLines(scriptName, lines);
		} else {
			writeFileLines("output/" + scriptName, lines);
		}
		
	}
	
	private static String processStr(String template, int desc, int swi) {
		template = template.replaceAll(_DESC, COL_NAMES.get(desc).toString());
		template = template.replaceAll(_SWI, COL_NAMES.get(swi).toString());
		template = template.replaceAll(DESC, ""+(desc+1));
		template = template.replaceAll(SWI, ""+(swi+1));
		return template;
	}
}
