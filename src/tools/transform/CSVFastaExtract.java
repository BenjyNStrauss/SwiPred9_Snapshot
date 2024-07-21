package tools.transform;

import java.util.List;

import assist.util.LabeledList;
import tools.reader.csv.CSVReaderTools;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class CSVFastaExtract extends CSVReaderTools {
	private static final int MAX_LINE_LENGTH = 80;
	
	private static final String[] FILES = {
			"output/bondugula2-0-learn-adjusted.csv",
			"output/bondugula2-1-learn-adjusted.csv",
			"output/bondugula2-2-learn-adjusted.csv",
			"output/bondugula2-3-learn-adjusted.csv",
			"output/bondugula2-4-learn-adjusted.csv"
	};
	
	public static void main(String[] args) {
		/*if(args.length == 0) { 
			qp("Error: no files specified");
			System.exit(0);
		}*/
		
		LabeledList<String> fastas = new LabeledList<String>();
		
		List<Integer> detectChangeCols = new LabeledList<Integer>();
		detectChangeCols.add(0);
		//detectChangeCols.add(1);
		
		for(String file: FILES) { //replace "FILES" with "args"
			fastas.addAll(extractFastaSeq(getFileLines(file), 2, detectChangeCols, true));
		}
		writeFileLines("output/chains.fasta", fastas);
	}
	
	/**
	 * 
	 * @param lines
	 * @param resCol
	 * @param detectChange
	 * @param hasHeader
	 * @return
	 */
	private static LabeledList<String> extractFastaSeq(String[] lines, int resCol,
			List<Integer> detectChange, boolean hasHeader) {
		int start = (hasHeader) ? 1 : 0;
		
		LabeledList<String> outLines = new LabeledList<String>();
		outLines.add(">Protein");
		StringBuilder seqBuilder = new StringBuilder();
		seqBuilder.setLength(0);
		
		for(int index = start; index < lines.length-1; ++index) {
			if(lines[index].split(",").length <= resCol) { break; }
			String res = lines[index].split(",")[resCol];
			
			if(!res.equals("_")) {
				seqBuilder.append(res);
			}
			
			if(detectChange(lines[index], lines[index+1], detectChange)) {
				//qp(seqBuilder.toString());
				outLines.add(seqBuilder.toString());
				seqBuilder.setLength(0);
				outLines.add("\n>Protein");
				continue;
			}
			
			if((seqBuilder.length()+1) % (MAX_LINE_LENGTH+1) == 0) {
				outLines.add(seqBuilder.toString());
				seqBuilder.setLength(0);
			}
		}
		
		outLines.add(seqBuilder.toString());
		return outLines;
	}
	
	/**
	 * 
	 * @param s1
	 * @param s2
	 * @param detectChange
	 * @return
	 */
	private static boolean detectChange(String s1, String s2, List<Integer> detectChange) {
		String[] tokens1 = s1.split(",");
		String[] tokens2 = s2.split(",");
		for(Integer val: detectChange) {
			if(!tokens1[val].equals(tokens2[val])) {
				return true;
			}
		}
		return false;
	}
}
