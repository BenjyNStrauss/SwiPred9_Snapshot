package tools.download.fasta;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import assist.exceptions.IORuntimeException;
import assist.script.Scripts;
import biology.protein.ChainID;
import tools.DataSource;
import tools.Lookup;

/**
 * Downloads a PDB file
 * @author Benjamin Strauss
 *
 */

public class PDB_Downloader extends FastaModule {
	private static final DataSource MY_SOURCE = DataSource.RCSB_PDB;
	@SuppressWarnings("unused")
	private static final String FILE_NOT_FOUND = "    <p>The requested URL was not found on this server.</p>";
	
	protected PDB_Downloader() {
		super(MY_SOURCE);
	}
	
	private static final String PDB_URL = "https://files.rcsb.org/view/";
	//5BTR.pdb
	
	public String download(ChainID id) {
		return quickDownload(id);
	}
	
	public static String quickDownload(ChainID id) {
		Objects.requireNonNull(id, "ID cannot be null!");
		Objects.requireNonNull(id.protein(), NULL_PROTEIN);
		
		String pdb_url = PDB_URL + id.protein() + PDB_EXT;
		
		String filename = Lookup.getFastaPath(id, MY_SOURCE);
		File file = new File(filename);
		
		URL url;
		try {
			url = new URL(pdb_url);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			if(huc.getResponseCode() != HttpURLConnection.HTTP_OK) { return null; }
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		
		Scripts.curl(pdb_url, filename);
		
		if(!fileExists(filename) || file.length() == 0) {
			qerr("Failed to download PDB file for " + id.standard());
			return null;
		} else if(!isValidPDB(file)) {
			qerr("No PDB file exists for " + id.standard());
			file.delete();
			return null;
		}
		
		return filename;
	}
	
	public static boolean isValidPDB(File pdbFile) {
		String[] lines = getFileLines(pdbFile);
		if(lines[0].startsWith("<")) {
			return false;
		} else {
			return true;
		}
	}
}
