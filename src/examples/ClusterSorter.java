package examples;

import java.io.File;

import biology.cluster.ChainCluster;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author bns
 *
 */

public class ClusterSorter extends LocalToolBase {
	//WP_011712286.1
	private static final int START_HERE = 89;
	
	public static void main(String[] args) throws DataRetrievalException {
		String[] lines = getFileLines("files/fasta/ncbi-pcla/ncbi_clusters/PCLA_clusters.txt");
		for(int ii = START_HERE; ii < lines.length; ++ii) {
			qp("Reading cluster #"+ii);
			String clusterID = lines[ii].split("\t")[1];
			ChainCluster inCluster = SequenceReader.readCluster_genbankWP(clusterID);
			String dirName = "output/pcla/size"+inCluster.size();
			File dir = new File(dirName);
			if(!dir.exists()) { dir.mkdir(); }
			String filename = dirName+"/"+inCluster.clusterName;
			qp("wrote: "+filename);
			writeFileLines(filename, inCluster.toFasta());
		}
	}
}
