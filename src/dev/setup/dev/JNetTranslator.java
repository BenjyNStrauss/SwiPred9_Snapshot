package dev.setup.dev;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import utilities.LocalToolBase;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class JNetTranslator extends LocalToolBase {
	public static final String JNET_IN_PATH = "vkabat/jnetsrc/src/";
	public static final String JNET_OUT_PATH = "files/prediction/jnet/";	
	private static PrintWriter debugWriter = null;
	
	public static void debugLog(ArrayList<String> strs) {
		if(debugWriter == null) {
			try {
				debugWriter = new PrintWriter("DebugLog.txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		for(String str: strs) {
			debugWriter.write(str + '\n');
		}
	}
	
	public static void debugLog(String str) {
		if(debugWriter == null) {
			try {
				debugWriter = new PrintWriter("DebugLog.txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		debugWriter.write(str + '\n');
	}
	
	public static void assignIDsToStructUT(String filename) throws FileNotFoundException {
		String lines[] = getFileLines(JNET_IN_PATH + filename);
		
		boolean active = false;
		int id = 1;
		
		for(int index = 0; index < lines.length; ++index) {
			
			if(lines[index].contains("static UnitType Units")) {
				if(lines[index].contains("=")) {
					active = true;
				}
			}
			
			if(active && lines[index].startsWith("      0.0")) {
				lines[index] = id + "," + lines[index];
				++id;
			}
			
			if(lines[index].contains("};") && active) {
				break;
			}
		}
		
		PrintWriter writer = new PrintWriter(JNET_IN_PATH + filename);
		for(int ii = 0; ii < lines.length; ++ii){
			writer.write(lines[ii] + "\n");
		}
		writer.close();
	}
	
	public static void translateJNetCFile(String filename) throws FileNotFoundException {
		qp(filename + " Sources = " + translateJnetSources(filename));
		qp(filename + " Units = " + translateJnetUnits(filename));
		qp(filename + " Weights = " + translateJnetWeights(filename));
	}
	
	public static int translateJnetUnits(String filename) throws FileNotFoundException {
		String lines[] = getFileLines(JNET_IN_PATH + filename);
		ArrayList<String> unitList = new ArrayList<String>();
		StringBuilder lineBuilder = new StringBuilder();
		
		boolean active = false;
		
		for(int index = 0; index < lines.length; ++index) {
			
			if(lines[index].contains("static UnitType Units")) {
				if(lines[index].contains("=")) {
					active = true;
				}
			}
			
			//if(active) { qp("*"); }
			
			if(active && lines[index].contains("{ 0.0, 0.0, 0, NULL , NULL },")) {
				unitList.add("0.0,0.0,0,0,0");
			} else if(lines[index].equals("")) { 
				
			} else if(active && Character.isDigit(lines[index].trim().charAt(0))) {
				lineBuilder.setLength(0);
				lineBuilder.append(lines[index]);
				String next1 = lines[index+1].replaceAll("&Sources\\[", "");
				next1 = next1.replaceAll("] ,", ",");
				lineBuilder.append(next1.trim());
				String next2 = lines[index+2].replaceAll("&Weights\\[", "");
				next2 = next2.replaceAll("] ,", "");
				lineBuilder.append(next2.trim());
				unitList.add(lineBuilder.toString().trim());
			}
			
			if(lines[index].contains("};") && active) {
				break;
			}
		}
		
		filename = filename.replaceAll(".c", "");
		PrintWriter writer = new PrintWriter(JNET_OUT_PATH + filename + "-units.txt");
		for(int ii = 0; ii < unitList.size(); ++ii){
			writer.write(unitList.get(ii) + "\n");
		}
		writer.close();
		
		return unitList.size();
	}
	
	public static int translateJnetWeights(String filename) throws FileNotFoundException {
		String lines[] = getFileLines(JNET_IN_PATH + filename);
		ArrayList<String> weightList = new ArrayList<String>();
		
		boolean active = false;
		
		for(String line: lines) {
			if(line.contains("static float Weights")) {
				active = true;
				continue;
			}
			
			if(line.contains("}") && active) {
				break;
			}
			
			if(active) {
				weightList.add(line.trim());
			}
		}
		
		filename = filename.replaceAll(".c", "");
		PrintWriter writer = new PrintWriter(JNET_OUT_PATH + filename + "-weights.txt");
		for(int ii = 0; ii < weightList.size(); ++ii){
			writer.write(weightList.get(ii) + "\n");
		}
		writer.close();
		return weightList.size() + 1;
	}
	
	public static int translateJnetSources(String filename) throws FileNotFoundException {
		String lines[] = getFileLines(JNET_IN_PATH + filename);
		ArrayList<Integer> unitList = new ArrayList<Integer>();
		
		boolean active = false;
		
		for(String line: lines) {
			if(line.contains("static pUnit Sources")) {
				active = true;
				continue;
			}
			
			if(line.contains("}") && active) {
				break;
			}
			
			if(active) {
				String line_units[] = line.split(",");
				
				for(String line_unit: line_units) {
					line_unit = line_unit.trim();
					line_unit = line_unit.replaceAll("Units", "");
					line_unit = line_unit.replaceAll("\\+", "");
					if(line_unit.length() != 0) {
						unitList.add(Integer.parseInt(line_unit.trim()));
					}
				}
			}
		}
		
		filename = filename.replaceAll(".c", "");
		PrintWriter writer = new PrintWriter(JNET_OUT_PATH + filename + "-sources.txt");
		for(int ii = 0; ii < unitList.size(); ++ii){
			writer.write(unitList.get(ii) + "\n");
		}
		writer.close();
		return unitList.size() + 1;
	}
}
