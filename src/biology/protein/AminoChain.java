package biology.protein;

import assist.util.LabeledList;
import assist.util.LabeledSet;
import biology.BioObject;
import biology.amino.Aminoid;
import biology.amino.ChainObject;
import biology.amino.InsertCode;
import biology.descriptor.SwitchClass;
import biology.descriptor.VKPred;
import biology.geneOntology.GO_AnnotationTerm;
import biology.molecule.FastaCrafter;

import java.util.Objects;

import assist.ActuallyCloneable;
import assist.base.DoNotPrintAsList;
import assist.util.FloatingList;
import modules.descriptor.vkbat.exceptions.VKAssignmentLengthException;
import tools.DataSource;
import tools.reader.mapping.AminoMapping;
import utilities.LocalToolBase;
import utilities.SwiPredObject;

/**
 * AminoChain represents a chain of Amino Acids
 * Implemented with an ArrayList
 * 
 * @author Benjy Strauss
 *
 */

public abstract class AminoChain<E extends ChainObject> extends FloatingList<E> implements ActuallyCloneable, CharSequence,
								Comparable<AminoChain<?>>, DoNotPrintAsList<E>, SwiPredObject {
	private static final long serialVersionUID = 1L;
	
	public static final char NULL_RESIDUE = '_';
	protected static final char MISSING_SECONDARY = ' ';
	private static final int MAX_FASTA_LINE_LENGTH = 80;
	
	private static final String NULL_WARNING = "Null Warning!";
	
	protected final ChainID id;
	protected final ChainFlags metaData;
	
	public String description;
	
	private int knownHomologues;
	private GO_AnnotationTerm function;
	
	private final LabeledSet<String> warnings;
	public final LabeledList<AminoMapping> mappings;
	
	protected AminoChain() { this(new ChainID(), new ChainFlags()); }
	
	protected AminoChain(ChainID idModule, ChainFlags sourceData) {
		Objects.requireNonNull(idModule);
		Objects.requireNonNull(sourceData);
		this.metaData = sourceData;
		this.id = idModule;
		warnings = new LabeledSet<String>();
		mappings = new LabeledList<AminoMapping>();
	}
	
	protected AminoChain(AminoChain<E> clonefrom) {
		this.metaData = clonefrom.metaData.clone();
		this.id = clonefrom.id.clone();
		description = clonefrom.description;
		knownHomologues = clonefrom.knownHomologues;
		modCount = clonefrom.modCount;
		warnings = new LabeledSet<String>();
		mappings = new LabeledList<AminoMapping>();
		
		for(String warning: clonefrom.warnings) {
			warnings.add(warning);
		}
		for(AminoMapping mapping: clonefrom.mappings) {
			mappings.add(mapping.clone());
		}
		
	}
	
	public ChainID id() { return id; }
	
	public E get(int index) {
		if(index < startsAt() || index >= size()) {
			return null;
		} else {
			return super.get(index);
		}
	}
	
	/**
	 * Get the protein's amino acid sequence
	 * @return the protein's amino acid sequence as a String,
	 * 		(using novel characters for (some) modified amino acids)
	 */
	public String toDetailSequence() { return FastaCrafter.textSequenceUTF16_equals_ON(this) ; }
	
	/**
	 * TODO - REWORK
	 * @param includeNull: include null residues
	 * @param includeLigand: include ligands
	 * @param useExpanded: use novel characters for modified amino acids
	 * @param includeInvalid: include invalid residues
	 * @return
	 */
	public String toSequence() { return FastaCrafter.textSequence(this); }
	
	/**
	 * Get a string representative of the ProteinChain's secondary structure
	 * Note that this does NOT include primary structure, just secondary structure if it is assigned
	 * @return: a string representative of the ProteinChain's secondary structure
	 */
	public String toSecondarySequence() { 
		StringBuilder builder = new StringBuilder();
		
		for(E aa: this) {
			if(aa != null) {
				if(aa.secondary() != null) {
					builder.append(aa.secondary().toChar());
				} else {
					builder.append(MISSING_SECONDARY);
				}
			} else {
				builder.append(NULL_RESIDUE);
			}
		}
		
		return builder.toString();
	}
	
	/** @return: string containing the chains secondary structures (simplified) */
	public String toSecondarySimple() { 
		StringBuilder builder = new StringBuilder();
		
		for(E aa: this) {
			if(aa != null) {
				if(aa.secondary() != null) {
					builder.append(aa.secondary().simpleClassify().toChar());
				} else {
					builder.append(MISSING_SECONDARY);
				}
			} else {
				builder.append(NULL_RESIDUE);
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * '*' = disordered
	 * '-' = not disordered
	 * '+' = superposition
	 * '?' = unknown/not assigned
	 * '!' = Error something was null that wasn't supposed to be!
	 * @return string containing disorder data
	 */
	public String toDisorder() {
		StringBuilder builder = new StringBuilder();
		
		for(E aa: this) {
			if(aa == null || aa.disordered() == null) {
				builder.append("!");
				continue;
			}
			
			switch(aa.disordered()) {
			case FALSE:				builder.append("-");		break;
			case SUPERPOSTION:		builder.append("+");		break;
			case TRUE:				builder.append("*");		break;
			case UNKNOWN:			builder.append("?");		break;
			}
		}
		
		return builder.toString();
	}
	
	public GO_AnnotationTerm function() { return function; }
	
	public void setFunction(GO_AnnotationTerm function) { 
		this.function = function;
		id.setGO(function.id());
	}
	
	/**
	 * 
	 * @param index
	 * @param flexibility
	 */
	public abstract void setFlexibility(int index, double flexibility);
	
	/**
	 * 
	 * @return number of null residues in the chain
	 * Runs in O(n) where n is chain size
	 */
	public int nullResidues() {
		int nulls = 0;
		for(E a: this) {
			if(a == null) { ++nulls; }
		}
		return nulls;
	}
	
	public ChainObject[] toArray() { 
		Object[] array = super.toArray();
		ChainObject[] newArray = new ChainObject[array.length];
		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}
	
	/**
	 * Assign a Vkbat prediction to the residues in the chain:
	 * @param predictor: the Vkbat prediction algorithm
	 * @param prediction: what the algorithm predicted
	 * @throws VKAssignmentLengthException 
	 * @throws NullPointerException: if the prediction is null
	 * 
	 * Important Notes:
	 * • If the prediction is all question marks: "?..." it will be rejected: this is used to denote
	 * 			when a java-implemented algorithm has failed (namely SSpro)
	 * • The prediction string MUST be the same length as the number of non-null residues in the chain,
	 * 			else it will be rejected
	 */
	public void setVK(VKPred predictor, String prediction) throws VKAssignmentLengthException {
		Objects.requireNonNull(prediction, "Prediction was null!");
		//no valid predictions to set
		if(prediction.matches("\\?*")) {
			LocalToolBase.qerrl("Error on prediction from [" + predictor + "] for " + id().standard());
			return;
		}
		
		if(predictor == null) { predictor = VKPred.UNKNOWN; }
		metaData.sspred.add(predictor);
		
		String vkSeq = FastaCrafter.textSequenceForVkbat(this);
		
		if(prediction.length() != vkSeq.length()) {
			if(prediction.length() == 0) { throw new NullPointerException("Prediction was empty!"); }
			throw new VKAssignmentLengthException(id, predictor, prediction, vkSeq);
		} else {
			char[] pred = prediction.toCharArray();
			int predIndex = 0;
			for(int assignIndex = 0; assignIndex < size(); ++assignIndex) {

				if(get(assignIndex) != null && get(assignIndex) instanceof Aminoid) {
					
					Aminoid aminoid = (Aminoid) get(assignIndex);
					//skip underscores in the prediction
					if(pred[predIndex] != '_') {
						aminoid.setVkbat(predictor, pred[predIndex]);
					};
					++predIndex;
				}
			}
		}
	}
	
	/** @return AminoChain's metadata object */
	public ChainFlags getMetaData() { return metaData; }
	
	public String toFasta() { return toFasta(metaData.source()); }
	
	/**
	 * Constructs a FASTA from the AminoChain's data
	 * @return: A string that can be written to a file as a FASTA representing this protein chain
	 */
	public String toFasta(DataSource src) {
		StringBuilder fastaBuilder = new StringBuilder();
		boolean fullLine = false;
		
		fastaBuilder.append(">" + id.relevant(src) + BioObject.STATIC_FASTA_HEADER + "\n");
			
		for(int i = 0; i < size(); ++i) {
			fullLine = false;
			
			if(get(i) == null) {
				fastaBuilder.append("-");
			} else {
				fastaBuilder.append(get(i).toChar());
			}
				
			if((i+1) % MAX_FASTA_LINE_LENGTH == 0) { fastaBuilder.append("\n"); fullLine = true; }
		}
		if(fullLine) { trimLastChar(fastaBuilder); }
		
		return fastaBuilder.toString();
	}
	
	/**
	 * Constructs a FASTA from the AminoChain's data without any null residues
	 * @return: A string that can be written to a file as a FASTA representing this protein chain
	 */
	public String toPurifiedFasta() {
		StringBuilder fastaBuilder = new StringBuilder();
		boolean fullLine = false;
		
		fastaBuilder.append(">" + id.standard() + BioObject.STATIC_FASTA_HEADER + "\n");
		int charsWritten = 0;
		
		for(int i = 0; i < size(); ++i) {
			fullLine = false;
				
			if(get(i) != null) {
				fastaBuilder.append(get(i).toChar());
				++charsWritten;
			}
				
			if((charsWritten+1) % 80 == 0) { fastaBuilder.append("\n"); fullLine = true; }
		}
		if(!fullLine) { fastaBuilder.append("\n"); }
		
		return fastaBuilder.toString();
	}
	
	/**
	 * 
	 * @param position
	 * @param amino
	 */
	public void setWithMapping(int position, E amino) {
		AminoMapping relevant = getRelevant(position);
		if(relevant != null) {
			set(relevant.map(position), amino);
		} else {
			set(position, amino);
		}
	}
	
	public void setWithMapping(InsertCode position, E amino) {
		AminoMapping relevant = getRelevant(position);
		if(relevant != null) {
			set(relevant.map(position), amino);
		} else {
			set(position.index, amino);
		}
	}
	
	public E getWithMapping(int position) {
		AminoMapping relevant = getRelevant(position);
		if(relevant != null) {
			return get(relevant.map(position));
		} else {
			return get(position);
		}
	}
	
	public E getWithMapping(InsertCode position) {
		AminoMapping relevant = getRelevant(position);
		if(relevant != null) {
			return get(relevant.map(position));
		} else {
			return get(position.index);
		}
	}
	
	private AminoMapping getRelevant(Object position) {
		for(AminoMapping am: mappings) {
			if(am.isValid(position)) {
				return am;
			}
		}
		return null;
	}
	
	public Integer map(InsertCode code) {
		AminoMapping relevant = getRelevant(code);
		if(relevant == null && code.code == InsertCode.NO_CODE) {
			return code.index;
		} else if(relevant == null) {
			throw new MappingNotFoundException(id, code);
		}
		return relevant.map(code);
	}
	
	public boolean equals(Object other) {
		if(this == other) { return true; }
		if(other instanceof ChainID) {
			return id.equals(other);
		} else if(other instanceof AminoChain<?>) {
			return id.equals(((AminoChain<?>) other).id);
		}
		return false;
	}
	
	public void setWarnings(Iterable<String> warnings) {
		for(String str: warnings) {
			this.warnings.add((str != null) ? str : NULL_WARNING);
		}
	}
	
	public void setWarnings(String... warnings) {
		for(String str: warnings) {
			this.warnings.add((str != null) ? str : NULL_WARNING);
		}
	}
	
	public boolean hasWarning(String warning) { return warnings.contains(warning); }
	
	public String[] allWarnings() {
		String allWarnings[] = new String[warnings.size()];
		warnings.toArray(allWarnings);
		return allWarnings;
	}
	
	public void setKnownHomologues(int val) { knownHomologues = val; }
	public int knownHomologues() { return knownHomologues; }
	
	public int length() { return size(); }
	public char charAt(int index) { return get(index).toChar(); }
	
	public int compareTo(AminoChain<?> other) {
		return id.compareTo(other.id);
	}
	
	public abstract AminoChain<?> subSequence(int start, int end);
	
	/**
	 * Returns a string containing the switch data of every residue in the chain:
	 * '-' = no switch, '|' = switch, '^' = possible switch
	 * '_' = null
	 * @return string containing switch data
	 */
	public String toSwitches() {
		StringBuilder builder = new StringBuilder();
		
		for(E aa: this) {
			if(aa instanceof Aminoid) {
				builder.append(SwitchClass.getSwitchCharNEW((Aminoid) aa));
			} else {
				builder.append("~");
			}
			
		}
		
		return builder.toString();
	}
	
	public String name() { return id.relevant(metaData.source()); }
	
	public abstract AminoChain<E> clone();
	
	/**
	 * Returns a String representing the ProteinChain object and it's sequence
	 * Designed for human viewing, not parsing!
	 */
	public String toString() {
		switch(metaData.source()) {
		case PFAM:				return "(" + id.pfam() + ") " + toDetailSequence();
		case UNIPROT:			return "[" + id.uniprot() + "] " + toDetailSequence();
		case RCSB_PDB:
		case RCSB_FASTA:		return id.standard() + " " + toDetailSequence();
		default:
			if(id.protein() != null) {
				return id.standard() + " " + toDetailSequence();
			} else if(id.uniprot() != null) {
				return "[" + id.uniprot() + "] " + toDetailSequence();
			} else if(id.pfam() != null) {
				return "(" + id.pfam() + ") " + toDetailSequence();
			} else {
				return "(Unknown Protien) " + toDetailSequence();
			}
		}
	}
}
