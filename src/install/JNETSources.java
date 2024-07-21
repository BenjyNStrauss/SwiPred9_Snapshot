package install;

import java.util.ArrayList;

import assist.util.LabeledHash;
import utilities.LocalToolBase;

/**
 * Contains the weights for the JNET Secondary Structure Prediction Algorithm
 * Note that this class can only generat the "-sources" files, NOT the "-weights" and "-units"
 * @author Benjamin Strauss
 *
 */

public class JNETSources extends LocalToolBase {
	private static final LabeledHash<String, JNETSourceParamHolder> NETWORK_PARAMS = new LabeledHash<String, JNETSourceParamHolder>() {
		private static final long serialVersionUID = 1L;
		{
			put("consnet",	new JNETSourceParamHolder(204,25,229,3));
			put("hmm1",		new JNETSourceParamHolder(408,20,428,3));
			put("hmm2",		new JNETSourceParamHolder( 57,20, 77,3));
			put("hmmsol0",	new JNETSourceParamHolder(408,20,428,2));
			put("hmmsol5",	new JNETSourceParamHolder(408,20,428,2));
			put("hmmsol25",	new JNETSourceParamHolder(408,20,428,2));
			
			put("net1",		new JNETSourceParamHolder(425, 9,434,3));
			put("net1b",	new JNETSourceParamHolder(425, 9,434,3));
			put("net2",		new JNETSourceParamHolder( 76,15, 91,3));
			put("net2b",	new JNETSourceParamHolder( 76,15, 91,3));
			
			put("psinet",	new JNETSourceParamHolder( 57,20, 77,3));
			put("psinet1",	new JNETSourceParamHolder(340, 9,349,3));
			put("psinet1b", new JNETSourceParamHolder(340, 9,349,3));
			put("psinet2",  new JNETSourceParamHolder( 57, 9, 66,3));
			put("psinet2b", new JNETSourceParamHolder( 57, 9, 66,3));
			
			put("psisol0",	new JNETSourceParamHolder(340,20,360,2));
			put("psisol5",	new JNETSourceParamHolder(340,20,360,2));
			put("psisol25",	new JNETSourceParamHolder(340,20,360,2));
		}
	};
	
	public static void main(String[] args) {
		writeSources();
		//writeSource("consnet");
		//analyzeFile(DirectoryManager.FILES_PREDICT_JNET+"/consnet-sources.txt");
		//analyzeFile(DirectoryManager.FILES_PREDICT_JNET+"/consnet--sources.txt");
	}
	
	@SuppressWarnings("unused")
	private static void analyzeFile(String filename) {
		String[] lines = getFileLines(filename);
		int[] lineVals = new int[lines.length];
		for(int index = 0; index < lines.length; ++index) {
			lineVals[index] = Integer.parseInt(lines[index]);
		}
		
		int primaryRepeatTo = 0;
		int primaryRepeats = 1;
		int secondaryRepeatTo = 0;
		int secondaryRepeat = 1;
		
		for(int index = 1; index < lines.length; ++index) {
			if(lineVals[index] < lineVals[index-1]) {
				//qp("[" + index + "] decreases to: " + lineVals[index] + " from " + lineVals[index-1]);
				if(lineVals[index] == 1) {
					++primaryRepeats;
					primaryRepeatTo = lineVals[index-1];
				} else {
					++secondaryRepeat;
					secondaryRepeatTo = lineVals[index-1];
				}
			}
		}
		
		qp("File: " + filename);
		qp("primaryRepeatTo:   " + primaryRepeatTo);
		qp("primaryRepeats:    " + primaryRepeats);		
		qp("secondaryRepeatTo: " + secondaryRepeatTo);
		qp("secondaryRepeat:   " + secondaryRepeat);
	}
	
	public static void writeSources() {
		for(String file: NETWORK_PARAMS.keySet()) {
			writeSource(file);
		}
	}
	
	private static void writeSource(String filename) {
		JNETSourceParamHolder data = NETWORK_PARAMS.get(filename);
		ArrayList<String> consnet_lines = new ArrayList<String>();
		StringBuilder lineBuilder = new StringBuilder("1");
		for(int ii = 2; ii <= data.primaryRepeatTo; ++ii) {
			lineBuilder.append("\n" + ii);
		}
		
		for(int ii = 0; ii < data.primaryRepeats; ++ii) {
			consnet_lines.add(lineBuilder.toString());
		}
		
		lineBuilder.setLength(0);
		lineBuilder.append((data.primaryRepeatTo+1));
		for(int ii = (data.primaryRepeatTo+2); ii <= data.secondaryRepeatTo; ++ii) {
			lineBuilder.append("\n" + ii);
		}
		
		for(int ii = 0; ii < data.secondaryRepeats; ++ii) {
			consnet_lines.add(lineBuilder.toString());
		}
		
		writeFileLines(DirectoryManager.FILES_PREDICT_JNET+"/"+filename+"-sources.txt", consnet_lines);
	}
}
