package analysis;

import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class OutputParser extends LocalToolBase {

	public static void main(String[] args) {
		LabeledList<String> aucLines = new LabeledList<String>();
		
		for(int ii = 1; ii < 12; ++ii) {
			String[] lines = getFileLines("uhm-logs/preliminary/log-637-"+ii+".txt");
			
			for(String line: lines) {
				boolean AUC_line = line.contains("AUC");
				boolean noRand = !line.contains("AUC: 0.500") && !line.contains("AUC:, 0.500");
				boolean noNAN = !line.contains("AUC: nan") && !line.contains("AUC:, nan");
				boolean test = line.contains("test");
				
				if(AUC_line && noRand && noNAN && test) {
					aucLines.add(line);
				} else if(AUC_line && noRand && noNAN) {
					aucLines.add("[" + ii + "] " + line);
				}
			}
		}
		
		writeFileLines("uhm-logs/auc-refined.txt",aucLines);

	}

}
