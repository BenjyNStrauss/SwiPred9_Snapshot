package tools.reader.cluster;

import java.io.File;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.util.LabeledList;
import biology.BioTools;
import tools.reader.schema.TSV_Column;
import tools.reader.schema.TSV_Schema;
import utilities.LocalToolBase;

/**
 * Designed to read clusters from a file, either text or PDF
 * Beware that each type of file has it's own specification
 * 
 * @author Benjy Strauss
 *
 */

public final class ClusterReader extends LocalToolBase {
	public static final String IGNORE = "*";
	
	/**
	 * Read cluster data from a file
	 * @param file: the file to read from
	 * @return array of string arrays, where each string array represents the chains
	 * 		in the cluster
	 */
	public static String[][] readClusters(File file) {
		return readClusters(file.getPath());
	}
	
	/**
	 * Read cluster data from a file
	 * @param filename: the name of the file to read from
	 * @return array of string arrays, where each string array represents the chains
	 * 		in the cluster
	 */
	public static String[][] readClusters(String filename) {
		if(filename.endsWith(".txt")) {
			return readClustersTXT(filename);
		} else if(filename.endsWith(".tsv")) {
			return readClustersTSV(filename);
		} else {
			return readClustersTXT(filename+".txt");
		}
		
	}
	
	/**
	 * Reads multiple clusters from a file.
	 * Each cluster is a line, protein names are separated by spaces
	 * 
	 * @param fileName: the name of the file to read
	 * @return: An array containing arrays of strings, with each array of strings
	 * representing the proteins in the cluster.
	 */
	public static String[][] readClustersPDF(String filename) {
		String[][] retVal = null;
		LabeledList<String[]> proteinNames = new LabeledList<String[]>();
		
		String[] fileLines = getFileLines(filename);
		
		for(String line: fileLines) {
			LabeledList<String> clusterNames = new LabeledList<String>();
			
			line = line.trim();
			String[] temp = line.split(" ");
			
			//this should remove the duplicates
			temp = removeDuplicates(temp);
			
			//this is just a check to make sure we're only reading proteins from the file
			for(String s: temp) {
				if(s.length() >= 4 && s.length() <= 5) { clusterNames.add(s); }
			}
			
			String[] meta = new String[clusterNames.size()];
			
			clusterNames.toArray(meta);
			proteinNames.add(meta);
		}
		
		
		retVal = new String[proteinNames.size()][];
		proteinNames.toArray(retVal);
		return retVal;
	}
	
	/**
	 * Reads multiple clusters from a file.
	 * Each chain is a line, protein names are separated by extra return
	 * 
	 * @param fileName: the name of the file to read
	 * @return: An array containing arrays of strings, with each array of strings
	 * representing the proteins in the cluster.
	 */
	public static String[][] readClustersTXT(String filename) {
		String[][] retVal = null;
		LabeledList<String[]> proteinNames = new LabeledList<String[]>();
		String fileLines[] = null;
		
		try {
			fileLines = getFileLines(filename);
		} catch (FileNotFoundRuntimeException FNFRE) {
			return null;
		}
		
		LabeledList<String> clusterNames = new LabeledList<String>();
		
		for(String line: fileLines) {
			line = line.trim();
			if(line.contains("%")) {
				if(line.startsWith("%")) { continue; }
				line = line.substring(0, line.indexOf("%"));
				line = line.trim();
			}
			
			if(!line.equals("")) {
				if(!line.startsWith(IGNORE)) {
					clusterNames.add(line);
				}
			} else {
				if(clusterNames.size() != 0) {
					String[] temp = new String[clusterNames.size()];
					clusterNames.toArray(temp);
					//this should remove the duplicates
					String temp2[] = removeDuplicates(temp);
					
					proteinNames.add(temp2);
					clusterNames.clear();
				}
			}
		}
		
		if(clusterNames.size() != 0) {
			String[] temp = new String[clusterNames.size()];
			clusterNames.toArray(temp);
		
			String[] temp2 = removeDuplicates(temp);
			proteinNames.add(temp2);
		}
		
		retVal = new String[proteinNames.size()][];
		proteinNames.toArray(retVal);
		
		return retVal;
	}
	
	/**
	 * Read cluster data from a .TSV file
	 * @param file: the file to read from
	 * @return array of string arrays, where each string array represents the chains
	 * 		in the cluster
	 */
	public static String[][] readClustersTSV(String filename) {
		String fileLines[] = null;
		
		try {
			fileLines = getFileLines(filename);
		} catch (FileNotFoundRuntimeException FNFRE) {
			return null;
		}
		
		TSV_Schema schema = TSV_Column.determineSchema(fileLines[0]);
		
		int clusterDominantSchemaIndex = schema.findIndexOf(TSV_Column.BONDUGULA_CHAINS);
		int clusterIDsSchemaIndex = schema.findIndexOf(TSV_Column.FULL_CLUSTER);
		
		if(clusterDominantSchemaIndex < 0 ){
			throw new MalformedTSVClusterException("TSV Cluster Reading: missing full cluster", schema);
		}
		
		return readClustersTSV(filename, clusterIDsSchemaIndex, clusterDominantSchemaIndex);
	}
	
	/**
	 * Read cluster data from a .TSV file
	 * @param file: the file to read from
	 * @param clusterCol
	 * @param domCol
	 * @return array of string arrays, where each string array represents the chains
	 * 		in the cluster
	 */
	public static String[][] readClustersTSV(String filename, int clusterCol, int domCol) {
		String[][] retVal = null;
		LabeledList<String[]> proteinNames = new LabeledList<String[]>();
		String fileLines[] = null;
		
		try {
			fileLines = getFileLines(filename);
		} catch (FileNotFoundRuntimeException FNFRE) {
			return null;
		}
		
		if(clusterCol < 0 ){
			throw new MalformedTSVClusterException("TSV Cluster Reading: missing full cluster");
		}

		for(int ii = 1; ii < fileLines.length; ++ii) {
			//skip accidental blank lines
			if(fileLines[ii].trim().length() == 0) {
				continue;
			}
			
			String[] fields = fileLines[ii].split("\t");
			String clusterDominant = null;
			
			if(domCol >= 0) {
				clusterDominant = parseClusterDominantPythonTSV(fields[domCol]);
			}
			
			fields[clusterCol] = fields[clusterCol].substring(1, fields[clusterCol].length()-1);
			fields[clusterCol] = fields[clusterCol].replaceAll("'", "");
			fields[clusterCol] = fields[clusterCol].replaceAll("_", ":");
			
			//fields[3] = convertProteinToUpperCase(fields[3]);
			
			String[] clustersIDs = fields[clusterCol].split(", ");
			for(int index = 0; index < clustersIDs.length; ++index) {
				clustersIDs[index] = BioTools.convertProteinToUpperCase(clustersIDs[index]);
			}
			
			if(clusterDominant != null) {
				//qp("clusterDominant = " + clusterDominant);
				int clusterDominantIndex = getIndexOf(clustersIDs, clusterDominant);
				//qp("clusterDominantIndex = " + clusterDominantIndex);
				//qp("Input IDs");
				//qp(clustersIDs);
				
				if(clusterDominantIndex != -1) {
					String temp = clustersIDs[clusterDominantIndex];
					clustersIDs[clusterDominantIndex] = clustersIDs[0];
					clustersIDs[0] = temp;
				} else {
					String[] newClusterIDs = new String[clustersIDs.length+1];
					newClusterIDs[0] = clusterDominant;
					for(int jj = 0; jj < clustersIDs.length; ++jj) {
						newClusterIDs[jj+1] = clustersIDs[jj];
					}
					clustersIDs = newClusterIDs;
				}
			}
			//qp("Output IDs");
			//qp(clustersIDs);
			
			//qp("dc: " + clustersIDs[0]);
			proteinNames.add(clustersIDs);
		}
		
		retVal = new String[proteinNames.size()][];
		proteinNames.toArray(retVal);
		return retVal;
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	private static String parseClusterDominantPythonTSV(String clusterDominant) {
		if(clusterDominant.contains(",")) {
			clusterDominant = clusterDominant.split(",")[0];
		}
		
		clusterDominant = clusterDominant.replaceAll("\\s+", "");
		clusterDominant = clusterDominant.replaceAll("\\[", "");
		clusterDominant = clusterDominant.replaceAll("\\]", "");
		clusterDominant = clusterDominant.replaceAll("'", "");
		
		clusterDominant = BioTools.convertProteinToUpperCase(clusterDominant);
		clusterDominant = clusterDominant.replaceAll("_", ":");
		return clusterDominant;
	}
	
	/**
	 * Takes a line in the form [PDB-ID][PDB-chain][space][uniprot-ID] and organizes it
	 * PDB-ID is length 4, PDB-chain length 1, 
	 * space is a singular space (or tab)
	 * uniprot-ID can be any length
	 * 
	 * @param line
	 * @return An array containing 3 data values, as follows:
	 * [0] = Protein PDB Name
	 * [1] = Protein PDB chainID
	 * [2] = Uniprot ID, or null if no ID was found
	 * 
	 */
	public static String[] parseCompositeLine(String line) {
		String retVal[] = new String[3];
		
		//remove all tabs
		line.replaceAll("\t", " ");
		
		String values[] = line.split(" ");
		
		retVal[0] = values[0].substring(0, 4);
		retVal[1] = "" + values[0].charAt(4);
		retVal[2] = values[1];

		return retVal;
	}
	
	/**
	 * Removes duplicate chains and cluster while preserving order
	 * Note that this is a brute-force algorithm and runs in O(n^2)
	 * 
	 * @param input: A list of strings
	 * @return: the list of strings with duplicates removed
	 */
	public static String[] removeDuplicates(String[] input) {
		String[] retVal;
		LabeledList<String> strList = new LabeledList<String>();
		
		for(int i = 0; i < input.length; ++i) {
			if(input[i] != null) {
				strList.add(input[i]);
				
				for(int j = i+1; j < input.length; ++j) {
					if(input[i].equals(input[j])) {
						input[j] = null;
					}
				}
			}
		}
		
		retVal = new String[strList.size()];
		strList.toArray(retVal);
		
		return retVal;
	}
}
