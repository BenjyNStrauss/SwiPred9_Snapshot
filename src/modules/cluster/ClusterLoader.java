package modules.cluster;

import java.io.File;
import java.util.List;
import java.util.Objects;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.util.LabeledList;
import biology.protein.AminoChain;
import project.ProteinDataset;
import system.SwiPred;
import tools.DataSource;
import tools.reader.cluster.ClusterReader;
import utilities.LocalToolBase;

/**
 * Class to load clusters
 * @author Benjamin Strauss
 *
 */

public class ClusterLoader extends LocalToolBase {
	//method used to cluster
	private ClusterMethod method = ClusterMethod.DEFAULT;
	//where to get the fastas from, set to RCSB by default
	private DataSource fastaSource = SwiPred.getShell().fastaSrc();
	//the minimum cluster size
	private int minSizeAllowed = 1;
	//the maximum cluster size
	private int maxSizeAllowed = Integer.MAX_VALUE;
	//how often we run garbage collecting
	private int gcInterval = 200;
	//number of threads to use if doing multi-threaded clustering
	private int numThreads = 1;
	//display notifications during clustering
	private boolean notifications = true;
	//the file to read from
	private final File inFile;
	//is the clustering active?  Acts as a lock
	private boolean isClustering = false;
	//create multi-chains
	private boolean isMulti = false;
	
	private final LabeledList<String> errors = new LabeledList<String>("Clsutering Errors");
	
	public ClusterLoader(String inFile) { this.inFile = new File(inFile); }
	public ClusterLoader(File inFile) { this.inFile = inFile; }
	
	public void cluster(List<ProteinDataset> targets) {
		if(targets.size() == 0) {
			error("Error: no targets");
			return;
		}
		
		isClustering = true;
		String[][] clusterIDs = preProcess();
		
		ClusteringThread[] threads = new ClusteringThread[numThreads];
		if(numThreads == 1) {
			threads[0] = new ClusteringThread(0, this, clusterIDs);
		} else {
			String[][][] splitArray = split(clusterIDs, numThreads);
			for(int ii = 0; ii < numThreads; ++ii) {
				threads[ii] = new ClusteringThread(ii, this, splitArray[ii]);
			}
		}
		
		for(int ii = 0; ii < numThreads; ++ii) {
			threads[ii].start();
		}
		
		LabeledList<AminoChain<?>> allChains = new LabeledList<AminoChain<?>>();
		try {
			for(ClusteringThread thread: threads) {
				thread.join();
				if(notifications) { qp("Joined thread: " + thread.idNo()); }
			}
		} catch (InterruptedException e) {
			qerr("Error in joining threads!");
		}
		
		int failed = 0;
		for(ClusteringThread thread: threads) {
			for(AminoChain<?> chain: thread.getClustered()) {
				if(chain != null) { allChains.add(chain); } else { ++failed; }
			}
			errors.addAll(thread.errors());
		}
		qp("Failed at clustering " + failed + " chains out of " + (failed+allChains.size()));
		
		for(ProteinDataset project: targets) {
			project.addAll(allChains);
		}
		
		isClustering = false;
	}
	
	private String[][] preProcess() {
		Objects.requireNonNull(inFile, "No infile specified!");
		if(!inFile.exists()) { throw new FileNotFoundRuntimeException("File "+ inFile +" cannot be found."); }
		if(inFile.isDirectory()) { throw new FileNotFoundRuntimeException("File "+ inFile +" is a directory."); }
		
		String[][] clusterIDs = ClusterReader.readClusters(inFile);
		String label = "File=\""+inFile+"\", DataSource=\"" + fastaSource +"\"";
		
		if(notifications) {
			qpl("Making Clusters, "+label);
		} else {
			log("Making Clusters, "+label);
		}
		
		LabeledList<String[]> allowed = new LabeledList<String[]>();
		for(String[] ids: clusterIDs) {
			if(ids.length >= minSizeAllowed && ids.length <= maxSizeAllowed) {
				allowed.add(ids);
			}
		}
		
		clusterIDs = new String[allowed.size()][];
		allowed.toArray(clusterIDs);
		return clusterIDs;
	}
	
	public void setFastaSource(DataSource fastaSource) {
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.fastaSource = fastaSource;
	}
	
	public void setMethod(ClusterMethod method) {
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.method = method;
	}
	
	public void setMinSizeAllowed(int minSizeAllowed) {
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.minSizeAllowed = minSizeAllowed;
	}
	
	public void setMaxSizeAllowed(int maxSizeAllowed) { 
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.maxSizeAllowed = maxSizeAllowed;
	}
	
	public void setGC_Interval(int gcInterval) { 
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.gcInterval = gcInterval;
	}
	
	public void setNumThreads(int numThreads) { 
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.numThreads = max(numThreads, 1);
	}
	
	public void setNotifications(boolean notifications) { 
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.notifications = notifications;
	}
	
	public void setMulti(boolean multi) { 
		if(isClustering) { throw new ClusteringInProgressException(); }
		this.isMulti = multi;
	}
	
	public DataSource fastaSource() { return fastaSource; }
	public ClusterMethod method() { return method; }
	public int minSizeAllowed() { return minSizeAllowed; }
	public int maxSizeAllowed() { return maxSizeAllowed; }
	public int gcInterval() { return gcInterval; }
	public int numThreads() { return numThreads; }
	public boolean notifications() { return notifications; }
	public boolean isMulti() { return isMulti; }
	public boolean isClustering() { return isClustering; }
	
	public LabeledList<String> allErrors() { return errors; }
	
	private static final String[][][] split(String[][] array, int chunks) {
		if(chunks <= 0) { throw new IndexOutOfBoundsException(chunks); }
		
		String[][][] split = new String[chunks][][];
		double len = array.length;
		double parts = chunks;
		double part_len = len/parts;
		int part_len_floor = (int) part_len;
		int remainder = array.length - (part_len_floor * chunks);
		
		for(int ii = 0; ii < split.length; ++ii) {
			split[ii] = new String[(ii >= remainder) ? part_len_floor : (part_len_floor+1)][];
		}
		
		int startCopyHere = 0;
		for(int ii = 0; ii < chunks; ++ii) {
			System.arraycopy(array, startCopyHere, split[ii], 0, split[ii].length);
			startCopyHere += split[ii].length;
		}
		return split;
	}
}
