package biology.protein;

import java.util.Objects;

import assist.ActuallyCloneable;
import assist.util.LabeledSet;
import biology.BioObject;
import biology.descriptor.VKPred;
import tools.DataSource;

/**
 * Stores data about where a protein chain object came from, and what is inside it.
 * 
 * @author Benjy
 *
 */

public class ChainFlags extends BioObject implements ActuallyCloneable {
	private static final long serialVersionUID = 1L;
	
	private DataSource source;
	
	public boolean has_secondary_structure = false;
	public boolean has_isunstruct = false;
	public boolean has_entropy = false;
	public boolean has_flex = false;
	
	public boolean missing_dssp = false;
	public boolean missing_rcsb_ss_data = false;
	
	public final LabeledSet<VKPred> sspred;
	
	public ChainFlags(DataSource source) { 
		sspred = new LabeledSet<VKPred>();
		this.source = source;
	}
	
	public ChainFlags() { this(DataSource.UNSPECIFIED); }
	
	private ChainFlags(ChainFlags cloneFrom) {
		sspred = new LabeledSet<VKPred>();
		source = cloneFrom.source;
		
		has_secondary_structure = cloneFrom.has_secondary_structure;
		has_isunstruct = cloneFrom.has_isunstruct;
		has_entropy = cloneFrom.has_entropy;
		has_flex = cloneFrom.has_flex;
		
		missing_dssp = cloneFrom.missing_dssp;
		missing_rcsb_ss_data = cloneFrom.missing_rcsb_ss_data;
	}
	
	public void setSource(DataSource source) {
		Objects.requireNonNull(source, "DataSource cannot be null!");
		this.source = source;
	}
	public DataSource source() { return source; }
	
	public boolean equals(Object other) {
		if(other instanceof ChainFlags) {
			ChainFlags other_flags = (ChainFlags) other;
			if(other_flags.source != source) { return false; }
			if(other_flags.missing_dssp != missing_dssp) { return false; }
			if(other_flags.missing_rcsb_ss_data != missing_rcsb_ss_data) { return false; }
			if(other_flags.has_isunstruct != has_isunstruct) { return false; }
			if(other_flags.has_secondary_structure != has_secondary_structure) { return false; }
			if(other_flags.has_entropy != has_entropy) { return false; }
			if(other_flags.has_flex != has_flex) { return false; }
			return true;
		} else {
			return false;
		}
	}
	
	public ChainFlags clone() { return new ChainFlags(this); }
	
	public String toString() { return "ChainMetaData(" + source +")"; }
}
