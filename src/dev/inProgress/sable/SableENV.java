package dev.inProgress.sable;

import assist.util.LabeledHash;
import install.DirectoryManager;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class SableENV extends LabeledHash<String, String> {
	private static final long serialVersionUID = 1L;

	protected static final String WAPPROX = "wApproximator";
	
	public SableENV() {
		put("BLAST_DIR", DirectoryManager.FILES_BLASTS);
		put("SABLE_DIR", DirectoryManager.FILES_PREDICT_SABLE);
	}
	
	public void setBlastDir(String blastDir) { put("BLAST_DIR", blastDir); }
	public void setNRdir(String nrDir) { put("NR_DIR", nrDir); }
	public void setOSType(String osType) { put("OSTYPE", osType); }
	public void setPBSJobID(String pbsJobid) { put("PBS_JOBID", pbsJobid); }
	public void setSableVersion(String version) { put("SABLE_VERSION", version); }
	public void setSAAction(String action) { put("SA_ACTION", action); }
	public void setSableAction(String action) { put("SABLE_ACTION", action); }
	public void setSableSA(String sa) { put("SABLE_SA", sa); }
	public void setSableDir(String dir) { put("SABLE_DIR", dir); }
	public void setSecondaryDatabase(String secDB) { put("SECONDARY_DATABASE", secDB); }
}
