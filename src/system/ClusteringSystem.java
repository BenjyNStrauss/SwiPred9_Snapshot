package system;

import java.io.File;
import java.util.List;

import assist.exceptions.FileNotFoundRuntimeException;
import install.DirectoryManager;
import modules.UserInputModule;
import modules.cluster.ClusterLoader;
import modules.descriptor.DescriptorAssignmentModule;
import project.ProteinDataset;
import tools.DataSource;
import view.ErrorView;

/**
 * Used to Parameterize the clustering
 * @author Benjamin Strauss
 *
 */

public final class ClusteringSystem extends SysTools {
	
	/**
	 * 
	 * @param instr
	 * @param projects
	 * @param defaultFastaSrc
	 */
	public static void readClusters(Instruction instr, List<ProteinDataset> projects,
			DataSource defaultFastaSrc) {
		
		if(projects.size() == 0) {
			error("Nowhere to save clusters to!");
			return;
		}
		
		//see if the file is explicitly labeled by the user as an argument
		String filename = instr.getFirstArgumentNamed(true, FILE_ARG);
		if(filename != null) {
			filename = validate(filename);
			if(filename == null) {
				error("Cluster file does not exist or is directory.");
				return;
			}
		}
		
		//no explicit file: if one of the values is a file's name, we'll use that
		else if(filename == null && instr.values.size() > 0) {
			for(String potentialInfile: instr.values) {
				filename = validate(potentialInfile);
				if(filename != null) { break; }
			}
		}
		
		while(filename == null) {
			filename = UserInputModule.getStringFromUser("Infile not found, please enter infile path, or \"q\" to quit: ");
			if(filename.equals("q")) {
				error("Aborted by User.");
				return;
			}
			filename = validate(filename);
		}
		
		int threads = 1;
		int minClusterSize = 2;
		int maxClusterSize = Integer.MAX_VALUE;
		int gc_at = 200;
		
		DataSource fastaSrc = defaultFastaSrc;
		String newFastaSrc = instr.getFirstArgumentNamed(true, FASTA_SRC_ARG);
		if(newFastaSrc != null) {
			fastaSrc = DataSource.parse(newFastaSrc);
		}
		
		String arg = instr.getFirstArgumentNamed(true, "-minsize");
		if(arg != null) { minClusterSize = getNumberFromArg(arg); }
		
		arg = instr.getFirstArgumentNamed(true, "-maxsize");
		if(arg != null) { maxClusterSize = getNumberFromArg(arg); }
		
		arg = instr.getFirstArgumentNamed(true, DescriptorAssignmentModule.getThreadSpecifiers());
		if(arg != null) { threads = getNumberFromArg(arg); }
		
		arg = instr.getFirstArgumentNamed(true, "-gc");
		if(arg != null) { gc_at = getNumberFromArg(arg); }
		
		if(threads < 1) { threads = 1; }
		
		ClusterLoader clusterer = new ClusterLoader(filename);
		clusterer.setMinSizeAllowed(minClusterSize);
		clusterer.setMaxSizeAllowed(maxClusterSize);
		clusterer.setNumThreads(threads);
		clusterer.setGC_Interval(gc_at);
		
		clusterer.setMulti(instr.hasArgumentNamed(ParamTable.MULTI));
		
		clusterer.setFastaSource(SwiPred.getShell().fastaSrc());
		clusterer.setFastaSource(fastaSrc);
		
		try {
			clusterer.cluster(projects);
		} catch (FileNotFoundRuntimeException FNFRE) {
			error("Error: " + FNFRE.getMessage());
			return;
		}
		
		ErrorView.displayErrors(clusterer);
	}
	
	private static String validate(String path) {
		File maybeValid = new File(path);
		if(maybeValid.exists() && !maybeValid.isDirectory()) {
			return path;
		}
		maybeValid = new File(DirectoryManager.INPUT + "/" + path);
		if(maybeValid.exists() && !maybeValid.isDirectory()) {
			return DirectoryManager.INPUT + "/" + path;
		}
		return null;
	}
}
