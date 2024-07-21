package modules.descriptor.vkbat.gor.gor3;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import utilities.LocalToolBase;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

class Gor3Util extends LocalToolBase {

	// Dictionary of the 20 natural amino acids
	static final LabeledHash<String, Character> ACIDS = new LabeledHash<String, Character>() {
		private static final long serialVersionUID = 1L;
		{
			put("ALA",'A');
			put("ARG",'R');
			put("ASN",'N');
			put("ASP",'D');
			put("CYS",'C');
			put("GLU",'E');
			put("GLN",'Q');
			put("GLY",'G');
			put("HIS",'H');
			put("ILE",'I');
			put("LEU",'L');
			put("LYS",'K');
			put("MET",'M');
			put("PHE",'F');
			put("PRO",'P');
			put("SER",'S');
			put("THR",'T');
			put("TRP",'W');
			put("TYR",'Y');
			put("VAL",'V');
			/*
			 * Added by Benjamin Strauss to prevent system crashes
			 * GOR-3 will still fail if there is an 'X' in the chain
			 */
			put("XAA",'X');
		}
	};
	
	static String[][] getDSSPInfo(String path) {
		String[] data_file = getFileLines(path);
		LabeledList<String[]> dataList = new LabeledList<String[]>();
		
		for(String line: data_file) {
	        String[] new_entry = line.split("\\s+");
	        if(new_entry[4].equals("Other")) {
	            new_entry[4] = "Coil";
	        }
	        if(new_entry[4].equals("Beta")) {
	            new_entry[4] = "Sheet";
	        }
	            
	        if(ACIDS.keySet().contains(new_entry[3].toUpperCase())) {
	            new_entry[3] = ""+ACIDS.get(new_entry[3]);
	            dataList.add(new_entry);
	        }
		}
		
		String[][] data_set = new String[dataList.size()][];
		dataList.toArray(data_set);
		
		return data_set;
	}
	
	static LabeledList<String> getPDBCode(String[][] dataset) {
		LabeledList<String> prot_list = new LabeledList<String>(); 
		for(String[] str: dataset) {
			prot_list.add(str[0]);
		}
		return prot_list;
	}

}
