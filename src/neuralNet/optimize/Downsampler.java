package neuralNet.optimize;

import java.util.List;

import assist.base.Assist;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class Downsampler extends LocalToolBase {

	public static void main(String[] args) {
		String[] lines = getFileLines("output/acetyl-sirt-dataset.csv");
		lines = downsampleCSVData(lines, 6);
		writeFileLines("output/acetyl-sirt-dataset-downsampled.csv", lines);
	}
	
	public static String[] downsampleCSVData(String[] lines, int column) {
		return downsample(lines, column, ",");
	}
	
	public static String[] downsample(String[] lines, int column, String delimiter) {
		qp(lines[0]);
		String[][] dataframe = new String[lines.length-1][];
		for(int index = 1; index < lines.length; ++index) {
			dataframe[index-1] = lines[index].split(delimiter);
		}
		
		LabeledHash<String, Integer> tally = new LabeledHash<String, Integer>();
		for(int index = 0; index < dataframe.length; ++index) {
			//qp(dataframe[index]);
			if(tally.containsKey(dataframe[index][column])) {
				tally.put(dataframe[index][column], tally.get(dataframe[index][column])+1);
			} else {
				tally.put(dataframe[index][column], 1);
			}
		}
		
		qp("tally.size(): " + tally.size());
		
		LabeledHash<String, LabeledList<String[]>> splitFrames = new LabeledHash<String, LabeledList<String[]>>();
		LabeledHash<String, LabeledList<String[]>> newSplitFrames = new LabeledHash<String, LabeledList<String[]>>();
		long min = Long.MAX_VALUE;
		
		for(String key: tally.keySet()) {
			min = min(min, tally.get(key));
			splitFrames.put(key, new LabeledList<String[]>());
			newSplitFrames.put(key, new LabeledList<String[]>());
		}
		qp("min: "+min);
		
		for(int index = 0; index < dataframe.length; ++index) {
			String key = dataframe[index][column];
			splitFrames.get(key).add(dataframe[index]);
		}
		
		for(String key: tally.keySet()) {
			while(newSplitFrames.get(key).size() < min) {
				double random = Math.random() * splitFrames.get(key).size();
				int randomIndex = (int) random;
				String[] randomlySelectedFields = splitFrames.get(key).remove(randomIndex);
				newSplitFrames.get(key).add(randomlySelectedFields);
			}
		}
		
		LabeledList<String> outLines = new LabeledList<String>();
		for(String key: tally.keySet()) {
			LabeledList<String[]> fieldList = newSplitFrames.get(key);
			for(String[] fields: fieldList) {
				outLines.add(Assist.joinFields(fields, ","));
			}
		}
		String[] retval = new String[outLines.size()+1];
		shuffle(outLines);
		
		//qp(outLines.get(0));
		outLines.add(0, lines[0]);
		//qp(outLines.get(0));
		outLines.toArray(retval);
		//qp(retval.length);
		return retval;
	}
	
	public static final void shuffle(List<String> list) {
		LabeledList<String> temp = new LabeledList<String>();
		
		while(list.size() > 0) {
			double random = Math.random() * list.size();
			int randomIndex = (int) random;
			temp.add(list.remove(randomIndex));
		}
		
		//make sure the list is clear
		list.clear();
		list.addAll(temp);
	}
}
