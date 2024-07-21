package modules.descriptor.vkbat.control;

import java.util.List;

import assist.util.LabeledList;
import biology.descriptor.VKPred;
import biology.protein.AminoChain;
import modules.descriptor.vkbat.Vkbat;

/**
 * 
 * @author Benjy Strauss
 *
 */
public class VKThreadClassic extends VKThread {
	private static final List<VKPred> CLASSIC_ALGORITHMS = new LabeledList<VKPred>() {
		private static final long serialVersionUID = 1L;
		{
			add(VKPred.GOR1);
			add(VKPred.DPM);
			add(VKPred.GOR3);
			add(VKPred.PREDATOR_PR);
			add(VKPred.HNN);
			add(VKPred.MLRC);
			add(VKPred.SOPM);
			add(VKPred.JPred);
			add(VKPred.PSIPred);
			add(VKPred.JNET);
			add(VKPred.YASPIN);
			add(VKPred.SSPRO_2);
			add(VKPred.PROFsec);
			add(VKPred.PHDpsi);
			add(VKPred.DSC);
		}
	};
	
	private static final List<VKPred> CLASSIC_ONLY_ALGORITHMS = new LabeledList<VKPred>() {
		private static final long serialVersionUID = 1L;
		{
			add(VKPred.JPred);
			add(VKPred.PSIPred);
			add(VKPred.JNET);
			add(VKPred.YASPIN);
			add(VKPred.SSPRO_2);
			add(VKPred.PROFsec);
			add(VKPred.PHDpsi);
		}
	};
	
	public VKThreadClassic(int idNo, List<AminoChain<?>> chains, boolean replaceExisting) {
		super(idNo, CLASSIC_ALGORITHMS, chains, replaceExisting);
	}
	
	public void run() {
		for(AminoChain<?> chain: chains) {
			for(VKPred algorithm: algorithms) {
				Vkbat.assign(chain, algorithm, replaceExisting);
			}
		}
	}
	
	public static final List<VKPred> getClassicAlgorithms() { return CLASSIC_ALGORITHMS; }
	public static final List<VKPred> getClassicOnlyAlgorithms() { return CLASSIC_ONLY_ALGORITHMS; }
}
