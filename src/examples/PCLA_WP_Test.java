package examples;

import biology.cluster.ChainCluster;
import tools.reader.fasta.SequenceReader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author bns
 *
 */

public class PCLA_WP_Test extends LocalToolBase {
	
	public static void main(String[] args) throws DataRetrievalException {
		ChainCluster debugCluster = SequenceReader.readCluster_genbankWP("PCLA_122997");
		
		qp(debugCluster.listAll());
	}
}
