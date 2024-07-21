package examples.v7.tests;

import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import modules.descriptor.entropy.BlastDB;
import modules.descriptor.entropy.Entropy;
import utilities.LocalToolBase;

/**
 * Contains tests for marking switches
 * @author Benjy Strauss
 *
 */

public final class EntropyDownloadTest extends LocalToolBase {
	private EntropyDownloadTest() { }
	
	public static void test1() {
		ChainID id = new ChainID();
		id.setProtein("1CJW");
		id.setChain("A");
		String seq = "HTLPANEFRCLTPEDAAGVFEIEREAFISVSGNCPLNLDEVQHFLTLCPELSLGWFVEGRLVAFIIGSLWDEERLTQESLALHRPRGHSAHLHALAVHRSFRQQGKGSVLLWRYLHHVGAQPAVRRAVLMCEDALVPFYQRFGFHPAGPCAIVVGSLTFTEMHCSL";
		ProteinChain _1CJW_A = ChainFactory.makeRCSB(id, seq);
		Entropy.assign(_1CJW_A, BlastDB.NCBI);
		qp("success");
	}
	
	public static void test2() {
		
	}
}
