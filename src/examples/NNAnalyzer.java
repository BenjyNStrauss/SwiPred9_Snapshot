package examples;

import java.io.File;

import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class NNAnalyzer extends LocalToolBase {

	public static void main(String[] args) {
		File uhmLogs = new File("uhm-logs");
		LabeledList<String> testSetLines = new LabeledList<String>();
		
		//int _index_ = 0;
		for(String file: uhmLogs.list()) {
			
			if(file.contains("log435-")) {
				testSetLines.add(file);
				String[] lines = getFileLines("uhm-logs/"+file);
				for(String line: lines) {
					if(line.startsWith("@Test Set:")) {
						testSetLines.add(line);
						//qp(line);
					}
				}
			}
		}
		
		//qp(testSetLines.size());
		for(int index = 0; index < testSetLines.size(); ++index) {
			qp(testSetLines.get(index));
			/*if(index % 5 == 4) {
				qp("------");
			}*/
		}
		
	}
}
