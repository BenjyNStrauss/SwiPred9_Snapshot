package dev.inProgress.sable;

import assist.translation.cplusplus.CTranslator;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public abstract class AbstractMain extends CTranslator {
	protected final NetworkBase network;
	protected float[][] data;
	protected int features;
	protected int vec_number;
	
	protected AbstractMain(NetworkBase network) {
		this.network = network;
	}
	
	protected abstract int SaveResults(String fileName);
	protected abstract int ReadFile(String fileName);
	public abstract String main(String[] args);
	
}
