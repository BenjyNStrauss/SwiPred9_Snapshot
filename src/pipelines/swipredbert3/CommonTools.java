package pipelines.swipredbert3;

import java.util.Set;

import assist.ml.DataSplit;
import assist.ml.ML_Base;
import assist.util.LabeledHash;
import biology.protein.ChainID;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;

/**
 * TODO make a list of excluded IDs
 * 
 * @author Benjamin Strauss
 * 
 */

public abstract class CommonTools extends LocalToolBase {
	protected static final String NR_DIR = "input/swipredbert/nr/";
	
	protected CommonTools() { }
	
	public static String[] filterRedundancy(RedundancyFilter criteria, String[] lines, int min_length,
			Set<String> known_checksum_errors) {
		final LabeledHash<String, String> table = new LabeledHash<String, String>();
		
		for(String line: lines) {
			String[] fields = line.split(":");
			
			String key;
			
			switch(criteria) {
			case UNIPROT_ID:		key = fields[2];		break;
			case GO_ID:				key = fields[3];		break;
			case SEQUENCE_100:		key = fields[4];		break;
			//case SEQUENCE_75:		return RedundancyFilter75.filterHighRedundancy(lines, 0.75);
			case NONE:				
			default:				return lines;
			}
			
			if(known_checksum_errors.contains(line)) {
				continue;
			}
			
			String seq = fields[4];
			
			if(!table.containsKey(key) && seq.length() >= min_length && verifyLoadable(fields)) {
				table.put(key, line);
			} else {
				known_checksum_errors.add(line);
			}
		}
		
		String[] out_lines = new String[table.size()];
		
		int index = 0;
		for(String key: table.keySet()) {
			out_lines[index] = table.get(key);
			++index;
		}
		
		return out_lines;
	}
	
	private static boolean verifyLoadable(String[] fields) {
		ChainID id = new ChainID();
		id.setProtein(fields[0]);
		id.setChain(fields[1]);
		
		try {
			SequenceReader.readChain_pdb(id, true);
			return true;
		} catch (Exception e) {
			qerr("Failed: "+id);
			return false;
		}
	}

	/**
	 * 
	 * @param infile
	 * @param test_percent
	 * @return
	 */
	public static String makeTestSetDesignatorBinaryString(String infile, double test_percent) {
		StringBuilder builder = new StringBuilder();
		
		String[] lines = getFileLines(infile);
		DataSplit<String> sets = ML_Base.train_test_val_split(lines, test_percent, 0.0, true);
		
		for(String str: lines) {
			builder.append(sets.train().contains(str) ? 0 : 1);
		}
		
		return builder.toString();
	}
}
