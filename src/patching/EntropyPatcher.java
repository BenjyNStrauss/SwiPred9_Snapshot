package patching;

/**
 * 
 * @author bns
 *
 */

public class EntropyPatcher extends PatchBase {
	
	private static final StringBuilder DEBUG_OUTPUT_RECORDER = new StringBuilder();
	private static final StringBuilder DEBUG_ENTROPY_RECORDER = new StringBuilder();
	
	public static void main(String[] args) {
		int entropyIndex = 0;
		//cause this to be re-initialized in the first loop;
		String[][] entropyPatch = new String[0][0];
		//the line in the entropy patch
		int entropyPatchIndex = 1;
		
		//iterate over all of the output files
		for(int outputIndex = 0; outputIndex < CSVs.length; ++outputIndex) {
			String[][] csvTable = readSwipredOutput(CSVs[outputIndex]);
			for(int csvTableLine = 1; csvTableLine < csvTable.length; ++csvTableLine) {
				//if out of range, load the next protein
				if(entropyPatchIndex >= entropyPatch.length) {
					qerr(">"+DEBUG_OUTPUT_RECORDER);
					qerr(">"+DEBUG_ENTROPY_RECORDER);
					entropyPatch = readEntropyData(entropyIndex);
					++entropyIndex;
					entropyPatchIndex = 1;
					DEBUG_OUTPUT_RECORDER.setLength(0);
					DEBUG_ENTROPY_RECORDER.setLength(0);
				}
				//apply the patch
				try {
					applyEntropyPatch(csvTable[csvTableLine], entropyPatch[entropyPatchIndex]);
				} catch (Exception e) {
					qerr(e.getMessage() + "[csv = " + CSVs[outputIndex] + ", line = " + csvTableLine +
							"] [prot = " + (entropyIndex-1) + ", line = " + entropyPatchIndex + "]");
				}
				//increment entropyPatchIndex 
				++entropyPatchIndex;
			}
			
			writePatchedOutput(CSVs[outputIndex], csvTable);
		}
	}
	
	private static void applyEntropyPatch(String[] csv_line, String[] patch_line) {
		DEBUG_OUTPUT_RECORDER.append(csv_line[RES_INDEX].toUpperCase().charAt(0));
		DEBUG_ENTROPY_RECORDER.append(patch_line[RES_INDEX].toUpperCase().charAt(0));
		if(csv_line[RES_INDEX].toUpperCase().charAt(0) == patch_line[RES_INDEX].toUpperCase().charAt(0)) {
			csv_line[E6_CSV] = patch_line[E6_PATCH];
			csv_line[E20_CSV] = patch_line[E20_PATCH];
		} else {
			throw new RuntimeException(csv_line[RES_INDEX] + " vs " + patch_line[RES_INDEX]);
		}
	}
	
	public static void writePatchedOutput(double section, String[][] fields) {
		String outfile = "output/bon8-patched/sp8-bon-"+section+".csv";
		String[] lines = new String[fields.length];
		
		for(int ii = 0; ii < fields.length; ++ii) {
			lines[ii] = String.join(",", fields[ii]);
			while(lines[ii].endsWith(",")) {
				lines[ii] = lines[ii].substring(0, lines[ii].length()-1);
			}
		}
		
		writeFileLines(outfile, lines);
	}
	
	public static String[][] readEntropyData(int prot) {
		String infile = "input/entropy-tables/Protein_"+prot+"_entropy.csv";
		String[] lines = getFileLines(infile);
		String[][] fields = new String[lines.length][];
		for(int ii = 0; ii < lines.length; ++ii) {
			fields[ii] = lines[ii].split(",");
		}
		return fields;
	}
}
