package biology.cluster;

import java.util.Iterator;

import assist.base.ToolBase;
import biology.protein.ProteinChain;

/**
 * An iterator for Clusters
 * @author Benjy Strauss
 *
 */

public class ClusterIterator extends ToolBase implements Iterator<ProteinChain> {
	
	private final ProteinChain[] cluster;
	private int index;
	
	public ClusterIterator(ChainCluster cluster) {
		this.cluster = cluster.toChainArray();
		index = 0;
	}

	@Override
	public boolean hasNext() {
		if(index < cluster.length) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public ProteinChain next() {
		if(hasNext()) {
			ProteinChain retVal = cluster[index];
			++index;
			return retVal;
		} else {
			return null;
		}
	}
	
	public void reset() { index = 0; }

}
