package modules.descriptor.vkbat.control;

import java.util.List;
import java.util.Objects;

import assist.exceptions.NotYetImplementedError;
import assist.util.LabeledList;
import biology.descriptor.VKPred;
import biology.protein.AminoChain;
import modules.descriptor.vkbat.Vkbat;
import utilities.SwiPredThread;

/**
 * Runs the selected vkbat algorithms on the selected chains
 * @author Benjy Strauss
 *
 */

public class VKThread extends SwiPredThread {
	protected final List<VKPred> algorithms;
	public final List<AminoChain<?>> chains;
	protected boolean replaceExisting;
	
	public VKThread(int idNo, List<VKPred> algorithms, List<AminoChain<?>> chains) {
		this(idNo, algorithms, chains, false);
	}
	
	public VKThread(int idNo, List<VKPred> algorithms, boolean replaceExisting) {
		this(idNo, algorithms, new LabeledList<AminoChain<?>>("chains"), replaceExisting);
	}
	
	public VKThread(int idNo, List<VKPred> algorithms, List<AminoChain<?>> chains, boolean replaceExisting) {
		super(idNo);
		Objects.requireNonNull(algorithms, "No algorithms specified!");
		Objects.requireNonNull(chains, "No chains specified!");
		this.algorithms = algorithms;
		this.chains = chains;
		this.replaceExisting = replaceExisting;
	}
	
	public void run() {
		for(AminoChain<?> chain: chains) {
			for(VKPred pred: algorithms) {
				try {
					Vkbat.assign(chain, pred, replaceExisting);
				} catch (NotYetImplementedError NYIE) {
					qerr("Error: Prediction Algorithm "+pred+" is not supported yet.");
				}
			}
		}
	}
}
