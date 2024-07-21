package pipelines.swipredbert3.analysis;

import assist.util.LabeledList;
import pipelines.swipredbert3.CommonTools;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public class BenchmarkAnalyzer extends CommonTools {

	public static void main(String[] args) {
		final String[] lines = getFileLines("output/pbert-log-wr__44535__2024_03_23-07-14-32.txt");
		final LabeledList<String> confusion_matrix_lines = new LabeledList<String>();
		final LabeledList<String> accuracies = new LabeledList<String>();
		
		for(String line: lines) {
			if(line.length() == 0) {
				confusion_matrix_lines.add(line);
				continue;
			} else if(Character.isDigit(line.trim().charAt(0))) {
				confusion_matrix_lines.add(line);
			} else if(line.contains("1  10  100  101  102  103  104  105  106  107  108  109  11  110  111")) {
				confusion_matrix_lines.add(line);
			} else if(line.toLowerCase().startsWith("all")) {
				accuracies.add(line);
			} else {
				qp(line);
			}
		}
		
		qp("––––––––––––––––––––––––––––––––");
		
		
		String[] arr = new String[3];
		int mod_val = 0;
		for(String str: accuracies) {
			arr[mod_val] = str.split("\\s+")[2];	
			++mod_val;
			
			if(mod_val % 3 == 0) {
				mod_val %= 3;
				qp(arr);
			}
		}
		
		writeFileLines("pbert_wr_conf_matr.txt", confusion_matrix_lines);
	}

}
