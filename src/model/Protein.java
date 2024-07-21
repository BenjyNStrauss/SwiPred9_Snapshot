package model;

import java.util.LinkedList;
import java.util.Objects;

import assist.util.ChaosList;
import assist.util.ChaosListIterator;
import assist.util.FloatingList;
import assist.util.LabeledLinkedList;
import biology.amino.InsertCode;
import biology.protein.GI_Number;
import utilities.DataObject;

/**
 * Important: This version of Protein does NOT use FloatingList because it is a 2D list 
 * indexed by InsertCodes
 * 
 * @author Benjamin Strauss
 * 
 */

public class Protein extends DataObject implements ChaosList<InsertCode, ResiduePosition> {
	private static final long serialVersionUID = 1L;
	
	private static final int NOT_SET = -1;
	private static final String UNKNOWN_PROTEIN = "Unknown Protein";
	
	private final FloatingList<LabeledLinkedList<ResiduePosition>> sequence;
	private boolean sizeChanged = false;
	private int lastKnownSize = 0;
	
	private String id_pfam;
	private String id_uniprot;
	private String id_genbank_wp;
	
	private GI_Number gi;
	
	private int id_family = NOT_SET;
	private int id_superfamily = NOT_SET;
	private int id_species = NOT_SET;
	private int id_membrane = NOT_SET;
	
	public Protein() { this(UNKNOWN_PROTEIN); }
	
	public Protein(String ukb) {
		set_uniprot(ukb != null ? ukb : UNKNOWN_PROTEIN);
		sequence = new FloatingList<LabeledLinkedList<ResiduePosition>>();
	}
	
	@Override
	public boolean add(ResiduePosition element) {
		sizeChanged = true;
		add(element.positionCode(), element);	
		return true;
	}

	@Override
	public void add(InsertCode index, ResiduePosition element) {
		add(index, element, false);
	}
	
	public void add(InsertCode index, ResiduePosition element, boolean override) {
		sizeChanged = true;
		if(sequence.get(index.index) == null) {
			sequence.set(index.index, new LabeledLinkedList<ResiduePosition>());
		}
		
		if(sequence.get(index.index).get(getSubindex(index.code)) == null) {
			sequence.get(index.index).set(getSubindex(index.code), element);
		} else {
			sequence.get(index.index).get(getSubindex(index.code)).merge(element);
		}
	}

	@Override
	public void clear() { sequence.clear(); }

	@Override
	public boolean contains(ResiduePosition element) {
		return get(element.positionCode()) != null;
	}

	@Override
	public ResiduePosition get(InsertCode index) {
		Objects.requireNonNull(index);
		LinkedList<ResiduePosition> pointer = sequence.get(index.index);
		if(pointer == null) { return null; }
		return pointer.get(getSubindex(index.code));
	}

	@Override
	public InsertCode indexOf(ResiduePosition element) {
		return element.positionCode();
	}

	@Override
	public InsertCode lastIndexOf(ResiduePosition element) {
		return element.positionCode();
	}

	@Override
	public ResiduePosition remove(InsertCode index) {
		if(get(index) == null) { return null; }
		
		ResiduePosition temp = sequence.get(index.index).get(getSubindex(index.code));
		
		sequence.get(index.index).set(getSubindex(index.code), null);
		sizeChanged = true;
		
		return temp;
	}

	@Override
	public boolean removeElement(ResiduePosition element) {
		if(get(element.positionCode()) == null) { return false; }
		remove(element.positionCode());
		sizeChanged = true;
		return true;
	}

	@Override
	public ResiduePosition set(InsertCode index, ResiduePosition element) {
		sizeChanged = true;
		
		if(sequence.get(index.index) == null) {
			sequence.set(index.index, new LabeledLinkedList<ResiduePosition>());
		}
		
		return sequence.get(index.index).set(getSubindex(index.code), element);
	}

	@Override
	public int size() {
		if(!sizeChanged) { return lastKnownSize; }
		
		lastKnownSize = 0;
		for(LinkedList<ResiduePosition> entry: sequence) {
			if(entry != null) {
				lastKnownSize += entry.size();
			}
		}
		
		return lastKnownSize;
	}
	
	@Override
	public ChaosListIterator<InsertCode, ResiduePosition> listIterator() {
		InsertCode default_code = new InsertCode(sequence.startsAt());
		return listIterator(default_code);
	}

	@Override
	public ChaosListIterator<InsertCode, ResiduePosition> listIterator(InsertCode index) {
		return new ProteinIterator(this, index);
	}
	
	@Override
	public Object[] toArray() {
		Object[] array = new Object[size()];
		
		int index = 0;
		for(ResiduePosition element: this) {
			array[index] = element;
			++index;
		}
		
		return array;
	}

	@Override
	public void toArray(ResiduePosition[] array) {
		int index = 0;
		for(ResiduePosition element: this) {
			if(index >= array.length) { break; }
			array[index] = element;
			++index;
		}
	}
	
	static int getSubindex(char ch) {
		if(ch == InsertCode.NO_CODE) { return 0; }
		else { return ch - 'A' + 1; }
	}
	
	FloatingList<LabeledLinkedList<ResiduePosition>> sequence() { return sequence; }
	
	public int family() { return id_family; }
	public int membrane() { return id_membrane; }
	public int species() { return id_species; }
	public int superfamily() { return id_superfamily; }
	
	public String genbankWP() { return id_genbank_wp; }
	public String pfam() { return id_pfam; }
	public String uniprot() { return id_uniprot; }
	public GI_Number gi() { return gi; }

	public void set_family(int family) { this.id_family = family; }
	public void set_membrane(int membrane) { this.id_membrane = membrane; }
	public void set_species(int species) { this.id_species = species; }
	public void set_superfamily(int superfamily) { this.id_superfamily = superfamily; }
	
	public void set_genbankWP(String id_genbank_wp) { this.id_genbank_wp = id_genbank_wp; }
	public void set_pfam(String id_pfam) { this.id_pfam = id_pfam; }
	public void set_uniprot(String id_uniprot) { this.id_uniprot = id_uniprot; }
	public void set_gi(GI_Number gi) { this.gi = gi; }
	
	/**
	 * Inserts and merges
	 * @param protein
	 * @param measure
	 */
	public void insert(String protein, String dsspLine) {
		try {
			insert(protein, new DSSP_Measure(dsspLine));
		} catch (DSSPBorderException e) {
			//Nothind to do, line does not represent a DSSP entry
		}
	}
	
	/**
	 * Inserts and merges
	 * @param protein
	 * @param measure
	 */
	public void insert(String protein, DSSP_Measure measure) {
		insert(new ResKey(protein, measure.chain), new DSSP_Entry(measure));
	}
	
	/**
	 * Inserts and merges
	 * @param protein
	 * @param measure
	 */
	public void insert(ResKey key, DSSP_Entry entry) {
		if(get(entry.insCode) == null) {
			set(entry.insCode, new ResiduePosition(entry.insCode));
		}
		
		ResiduePosition pos = get(entry.insCode);
		pos.put(key, entry);
	}
}
