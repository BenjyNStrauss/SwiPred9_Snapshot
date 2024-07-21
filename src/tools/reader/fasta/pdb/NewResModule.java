package tools.reader.fasta.pdb;

import java.io.File;
import java.util.Hashtable;

//import assist.base.Assist;
import assist.util.LabeledHash;
import biology.molecule.MoleculeLookup;
import biology.protein.ChainID;
import tools.DataSource;
import tools.Lookup;
import tools.download.fasta.PDB_Downloader;

/**
 * Input a list of IDs into "TROUBLING_IDS" field, and see new AminoAcids+Ligands to add
 * @author Benjamin Strauss
 *
 */

final class NewResModule extends Lookup {
	
	private static final String[] TROUBLING_IDS = { "4QDO:A", "5KGF:F", "3C1B:A", "1Q3G:F", "3SWI:A" };

	public static void main(String[] args) {
		//new AminoAcid("M2L");
		//System.exit(0);
		
		ChainID ids[] = new ChainID[TROUBLING_IDS.length];
		for(int index = 0; index < TROUBLING_IDS.length; ++index) {
			String[] tokens = TROUBLING_IDS[index].split(":");
			ids[index] = new ChainID();
			ids[index].setProtein(tokens[0]);
			ids[index].setChain(tokens[1]);
			
			if(!fileExists(getFastaPath(ids[index], DataSource.RCSB_PDB))) {
				PDB_Downloader.quickDownload(ids[index]);
			}
		}
		
		LabeledHash<String, String> result = logNewResTypes(ids);
		printResults(result);
		qp("Results: " + result.size());
	}
	
	public static LabeledHash<String, String> logNewResTypes(ChainID... ids) {
		LabeledHash<String, String> result = new LabeledHash<String, String>();
		
		for(ChainID id: ids) {
			File fasta = new File(getFastaPath(id, DataSource.RCSB_PDB));
			String[] lines = getFileLines(fasta);
			for(String line: lines) {
				if(line.startsWith(PDB_Tools.HETNAM)) {
					line = line.substring(6).trim();
					String[] tokens = line.split("\\s+");
					
					int codeToken = (tokens[0].length() > 1 || tokens[0].charAt(0) >= 'A') ? 0 : 1;
					if(MoleculeLookup.recognized(tokens[codeToken])) {
						
					} else {
						if(tokens[0].length() > 1 || tokens[0].charAt(0) >= 'A') {
							//qp("put: " + tokens[0]);
							result.put(tokens[0], line.substring(3).trim());
						} else if(tokens.length > 2 && result.containsKey(tokens[1])) {
							String codeInProgress = result.get(tokens[1]) + line.substring(5).trim();
							result.put(tokens[1], codeInProgress);
						} else {
							//qp("**********");
							//qp(tokens);
							result.put(tokens[1], line.substring(3).trim());
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public static void printResults(Hashtable<String, String> table) {
		for(String str: table.keySet()) {
			qp(str.toLowerCase()+" :: "+table.get(str).toLowerCase());
		}
	}
}
