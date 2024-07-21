package dev.inProgress.sable;

import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class SableTester extends LocalToolBase {
	
	public static void main(String[] args) {
		NetworkBase base = null;
		base = NetworkLoader.getInstance().getNetwork("complexSA", "Approx_1");
		qp(base.getClass());
	}
	
	private static void debugSable(String[] args) {
		final String seq = "SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRALDYSFTILNLHKIYLHVAVENPKAVHLYEECGFVEEGHLVEEFFINGRYQDVKRMYILQSKYLNRSE";
		//SableENV environment = new SableENV();
		ChainID id = new ChainID();
		id.setProtein("4JJX");
		id.setChain("1");
		ProteinChain _4jjx = ChainFactory.makeRCSB(id, seq);		
		SableHelper.getPsiBlast(_4jjx);
		writeFileLines("data.seq", seq);
		
		Sable sable = new Sable(getTestingInstance());
		sable.debug_main(args);
	}
	
	/**
	 * TODO
	 * @return get a dummy SableENV for testing
	 */
	public static SableENV getTestingInstance() {
		SableENV env = new SableENV();
		env.put("SABLE_VERSION", "sable2");
		env.put("SA_ACTION",     "Approximator");
		env.put("BLAST_FILE",    "sable_rcsb-fasta_4JJX_1.txt");
		env.put("NR_DIR",        "files/predict/sable/nr");
		return env;
	}
}
