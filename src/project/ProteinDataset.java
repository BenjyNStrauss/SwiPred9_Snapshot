package project;

import java.util.Objects;

import assist.util.LabeledList;
import biology.protein.AminoChain;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class ProteinDataset extends LabeledList<AminoChain<?>> implements SwiPredDataset {
	private static final long serialVersionUID = 1L;

	public final boolean multi;
	
	public ProteinDataset(String label) { this(label, false); }
	
	public ProteinDataset(String label, boolean multi) {
		Objects.requireNonNull(label, "SwiPredDataset cannot have a null label!");
		this.label = label;
		this.multi = multi;
	}
}
