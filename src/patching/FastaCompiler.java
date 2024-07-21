package patching;

import assist.util.LabeledList;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class FastaCompiler extends PatchBase {

	public static void main(String[] args) {
		
		LabeledList<String> fastaLines = new LabeledList<String>();
		
		for(int outputIndex = 0; outputIndex < CSVs.length; ++outputIndex) {
			String[][] csvTable = readSwipredOutput(CSVs[outputIndex]);
			StringBuilder lineBuilder = new StringBuilder();
			String protID = "";
			
			for(String[] resData: csvTable) {
				if(!protID.equals(resData[0])) {
					if(protID.length() > 0) {
						fastaLines.add(">"+protID);
						fastaLines.add(lineBuilder.toString());
						fastaLines.add("");
						lineBuilder.setLength(0);
					}
					protID = resData[0];
				}
				lineBuilder.append(resData[2]);
			}
			writeFileLines("output/bon8-fasta/combo-fasta-"+CSVs[outputIndex]+".fasta", fastaLines);
			fastaLines.clear();
		}
	}
}
