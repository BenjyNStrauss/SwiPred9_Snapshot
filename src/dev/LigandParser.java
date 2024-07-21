package dev;

import java.util.Arrays;

import assist.base.Assist;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.Pair;
import biology.molecule.MoleculeLookup;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class LigandParser extends LocalToolBase {
	private static final String AMINO_NAMES = "Alanine,Cysteine,Aspartic__Acid,Glutamic__Acid,Phenylalanine,Glycine,Histidine,Lysine,Leucine,Methionine,Asparagine,Proline,Glutamine,Arginine,Serine,Threonine,Valine,Tryptophan,Tyrosine,Ornithine";
	
	public static void main(String[] args) {
		String[] aminoNames = AMINO_NAMES.toLowerCase().split(",");
		
		String[] lines = getFileLines("new-pdb-codes.txt");
		LabeledHash<String, Pair<Integer, Integer>> counter = new LabeledHash<String, Pair<Integer, Integer>>();
		for(String line: lines) {
			if(counter.containsKey(line)) {
				Pair<Integer, Integer> p1 = counter.get(line);
				p1.x+=1;
				p1.y+=line.length();
			} else {
				counter.put(line, new Pair<Integer, Integer>(1, line.length()));
			}
		}

		String[] str_array = new String[counter.size()];
		counter.keySet().toArray(str_array);
		Arrays.sort(str_array);
		
		LabeledList<String> amino_names = new LabeledList<String>();
		
		/*for(String key: str_array) {
			Pair<Integer, Integer> p1 = counter.get(key);
			qp("["+p1.x+":"+p1.y+"] " + key);
		}
		qp("---------------------");*/
		for(String key: str_array) {
			if(key.trim().length() == 0) { continue; }
			int firstQuote = key.indexOf("\"");
			int secondQuote = Assist.getNthIndexOf(key, "\"", 1);
			String code = key.substring(firstQuote+1, secondQuote);
			
			if(Assist.containsOneOf(key.toLowerCase(), aminoNames)) {
				amino_names.add(key);
			} else if(!MoleculeLookup.recognized(code)) {
				qp(key);		
			}
		}
		qp("---------------------");
		for(String key: amino_names) {
			if(key.trim().length() == 0) { continue; }
			int firstQuote = key.indexOf("\"");
			int secondQuote = Assist.getNthIndexOf(key, "\"", 1);
			String code = key.substring(firstQuote+1, secondQuote);
			
			if(!MoleculeLookup.recognized(code)) {
				qp(key);	
			}
		}
	}
}
