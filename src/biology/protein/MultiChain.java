package biology.protein;

import java.util.Objects;

import biology.amino.AminoPosition;
import biology.amino.Aminoid;
import biology.descriptor.DescriptorType;
import biology.descriptor.VKPred;
import biology.exceptions.EmptyChainException;
import biology.molecule.FastaCrafter;
import modules.descriptor.vkbat.exceptions.VKAssignmentLengthException;
import utilities.LocalToolBase;

/**
 * A PositionChain is a chain of residue positions that comprise a protein chain
 * PositionChain differs from ProteinChain in that each residue position can have
 * 	multiple primary/secondary structure configurations
 * 
 * @author Benjy Strauss
 *
 */

public class MultiChain extends AminoChain<AminoPosition> {
	private static final long serialVersionUID = 1L;
	
	private MultiChain(ChainID idModule, ChainFlags sourceData) {
		super(idModule, sourceData);
	}
	
	/**
	 * Constructs a new PositionChain instance from an existing AminoChain
	 * 
	 * Other configurations can then be added to the PositionChain
	 * @param chain
	 */
	public MultiChain(AminoChain<?> chain) {
		getMetaData().setSource(chain.getMetaData().source());
		id.copyFrom(chain.id);
		
		if(chain.size() == 0) { throw new EmptyChainException(chain); }
		startAt = chain.startsAt();
		
		for(int index = chain.startsAt(); index < chain.size(); ++index) {
			if(chain.get(index) != null) {
				set(index, new AminoPosition(chain.get(index).residueType(), chain.get(index).secondary()));
			} else {
				set(index, new AminoPosition());
			}
		}
	}
	
	public MultiChain(ChainID idModule) {
		super(idModule, new ChainFlags());
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
			//qp(pred);
			
			for(int assignIndex = 0; assignIndex < size(); ++assignIndex) {
				if(get(assignIndex) != null && get(assignIndex).vk_assignable()) {
					Aminoid aminoid = (Aminoid) get(assignIndex);
					
					try {
						aminoid.setVkbat(predictor, pred[predIndex]);
						//qp("Assigned: "+pred[predIndex]+" to "+aminoid.toCode());	
					} catch (RuntimeException re) {
						/*qp("aminoid        : "+aminoid.toCode());
						qp("assignIndex    : "+assignIndex);
						qp("pred[predIndex]: "+pred[predIndex]);*/
						
						qerr("MultiChain.setVK(...): Internal error for: "+id+" when assigning "+predictor);
						qerr("This:    " + this);
						qerr("Applicable seq: "+ vkSeq);
						qerr("Prediction:     " + prediction);
						//re.printStackTrace();
					}
					
					++predIndex;
				}/* else {
					qp("Unassignable: "+get(assignIndex));
				}*/
			}
		}
	}
	
	/**
	 * Make a deep copy of the protein chain
	 */
	public MultiChain clone() {
		MultiChain myClone = new MultiChain(id.clone(), metaData.clone());
		myClone.description = description;
		
		myClone.description = description;
		myClone.modCount = modCount;
		
		for(AminoPosition aa: this) {
			if(aa != null) {
				myClone.add(aa.clone());
			} else {
				myClone.pad();
			}
		}
		
		return myClone;
	}
	
	@Override
	public MultiChain subSequence(int start, int end) {
		MultiChain myClone = new MultiChain(this);
		
		for(int index = start; index < end; ++index) {
			if(index >= end || index < start || get(index) == null) {
				myClone.pad();
			} else {
				myClone.add(get(index).clone());
			}
		}
		
		return myClone;
	}
	
	@Override
	public void setFlexibility(int index, double flexibility) { 
		Objects.requireNonNull(get(index), "Residue at index " + index + " is null!");
		get(index).setDescriptor(DescriptorType.FLEXIBILITY, flexibility);
	}
	
	@Override
	public AminoPosition[] toArray() { return (AminoPosition[]) toArray(); }
	
	/**
	 * Gets the original sequence that this chain was constructed with
	 * @return a string representation of the original sequence used to construct this chain
	 */
	public String dominantSequence() {
		StringBuilder seqBuilder = new StringBuilder();
		
		for(int ii = 0; ii < size(); ++ii) {
			seqBuilder.append(get(ii).residueType().toChar());
		}
		
		return seqBuilder.toString();
	}
}
