package model;

import assist.util.ChaosListIterator;
import assist.util.LabeledLinkedList;
import biology.amino.InsertCode;
import utilities.LocalToolBase;

/**
 * Iterates through a Protein
 * @author Benjamin Strauss
 * 
 */

public class ProteinIterator extends LocalToolBase implements ChaosListIterator<InsertCode, ResiduePosition> {
	
	private final Protein protein;
	private InsertCode prev_index;
	private InsertCode curr_index;
	private InsertCode next_index;
	
	public ProteinIterator(Protein protein, InsertCode index) {
		this.protein = protein;
		
		if(isValid(index)) {
			curr_index = index;
		} else {
			curr_index = new InsertCode(protein.sequence().startsAt());
			curr_index = findNext();
		}
		
		prev_index = findPrev();
		next_index = findNext();
	}

	@Override
	public final void add(ResiduePosition e) {
		if(protein.get(curr_index) != null) {
			protein.get(curr_index).merge(e);
		} else {
			protein.set(curr_index, e);
		}
	}

	@Override public boolean hasNext() { return next_index != null; }

	@Override
	public boolean hasPrevious() { return prev_index != null; }

	@Override
	public ResiduePosition next() {
		prev_index = curr_index;
		curr_index = next_index;
		next_index = findNext();
		return protein.sequence().get(curr_index.index).get(Protein.getSubindex(curr_index.code));
	}

	@Override public InsertCode nextIndex() { return next_index; }

	@Override
	public ResiduePosition previous() {
		next_index = curr_index;
		curr_index = prev_index;
		prev_index = findPrev();
		return protein.sequence().get(curr_index.index).get(Protein.getSubindex(curr_index.code));
	}

	@Override public InsertCode previousIndex() { return prev_index; }

	@Override public void remove() { protein.remove(curr_index); }

	@Override public final void set(ResiduePosition e) { set(e, false); }
	
	public final void set(ResiduePosition e, boolean merge) {
		if(merge && protein.get(curr_index) != null) {
			protein.get(curr_index).merge(e);
		} else {
			protein.set(curr_index, e);
		}
	}
	
	private InsertCode findNext() {
		int ins_index = Protein.getSubindex(curr_index.code)+1; 
		
		for(int seq_index = curr_index.index; seq_index < protein.sequence().size(); ++seq_index) {
			LabeledLinkedList<ResiduePosition> pointer = protein.sequence().get(seq_index);
			if(pointer == null) { continue; }
			
			for(; ins_index < pointer.size(); ++ins_index) {
				ResiduePosition resPos = pointer.get(ins_index);
				if(resPos != null) { return resPos.positionCode(); }
			}
			
			ins_index = 0;
		}
		
		return null;
	}

	private InsertCode findPrev() {
		int ins_index = Protein.getSubindex(curr_index.code)-1; 
		
		for(int seq_index = curr_index.index; seq_index >= protein.sequence().startsAt(); --seq_index) {
			LabeledLinkedList<ResiduePosition> pointer = protein.sequence().get(seq_index);
			if(pointer == null) { continue; }
			
			for(; ins_index >= 0; --ins_index) {
				ResiduePosition resPos = pointer.get(ins_index);
				if(resPos != null) { return resPos.positionCode(); }
			}
			
			ins_index = (protein.sequence().get(seq_index-1) != null) ? (protein.sequence().get(seq_index-1).size()-1) : 0 ;
		}
		
		return null;
	}
	
	private boolean isValid(InsertCode index) {
		return protein.get(index) != null;
	}
}
