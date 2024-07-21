package tools.download.fasta;

import java.io.File;

import assist.script.Script;
import biology.protein.ChainID;
import tools.DataSource;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 *
 */

class DSSPFasta extends FastaModule {
	private static final String LEGACY = "legacy";
	
	private static final String BARRIER_LINE = "  #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA";
	//don't put quotes around URL
	
	protected DSSPFasta() { super(DataSource.DSSP); }
	
	/**
	 * 
	 * @param id
	 * @throws DataRetrievalException
	 */
	public String download(ChainID id) throws DataRetrievalException {
		Script curl_script = new Script("curl", "https://pdb-redo.eu/dssp/db/"+id.protein().toLowerCase()+"/legacy");
		curl_script.run();
		
		String text = curl_script.getStdOut();
		String[] lines = text.split("\n");
		
		if(validDSSP(lines)) {
			String path = getFastaPath(id, DataSource.DSSP);
			writeFileLines(path, lines);
			return path;
		} else {
			throw new DataRetrievalException("DSSP for " + id.protein() + " does not exist.");
		}
	}

	public static final boolean bad_legacy_dssp() {
		String[] lines = getFileLines(LEGACY);
		for(int ii = lines.length-1; ii >= 0; --ii) {
			if(lines[ii].trim().length() == 0) {
				continue;
			}
			return !lines[ii].trim().toLowerCase().equals(BARRIER_LINE.trim().toLowerCase());
		}
		return false;
	}
	
	private static final boolean validDSSP(String[] lines) {
		for(int ii = lines.length-1; ii >= 0; --ii) {
			if(lines[ii].trim().length() == 0) {
				continue;
			}
			return !lines[ii].trim().toLowerCase().equals(BARRIER_LINE.trim().toLowerCase());
		}
		return false;
	}
}
