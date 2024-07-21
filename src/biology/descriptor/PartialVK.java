package biology.descriptor;

import java.util.List;

import assist.ActuallyCloneable;
import assist.exceptions.NotYetImplementedError;
import assist.util.LabeledList;
import biology.amino.Aminoid;
import biology.protein.AminoChain;
import modules.descriptor.charge.Charge;
import utilities.exceptions.SwiPredRuntimeException;

/**
 * Used to calculate vkbat of a partial set of secondary structure predictions
 * @author Benjamin Strauss
 *
 */

public class PartialVK extends LabeledList<VKPred> implements ActuallyCloneable, Metric {
	private static final long serialVersionUID = 1L;
	
	private int windowSize = 0;
	public boolean normalize;
	public boolean flip;
	public DescriptorType chargeType = null;
	
	public PartialVK() { }
	public PartialVK(int initialCapacity) { super(initialCapacity); }
	public PartialVK(String label) { this.label = label; }
	
	public PartialVK(String label, int initialCapacity) {
		super(initialCapacity);
		this.label = label;
	}
	
	private PartialVK(PartialVK cloneFrom) {
		super(cloneFrom.label);
		windowSize = cloneFrom.windowSize;
		normalize = cloneFrom.normalize;
		flip = cloneFrom.flip;
		chargeType = cloneFrom.chargeType;
		
		for(VKPred vkp: cloneFrom) {
			add(vkp);
		}
	}
	
	@Override
	public void setName(String arg) { label = arg; }
	
	public void setWindowSize(int newSize) {
		if(newSize >= 0) { windowSize = newSize; }
	}
	
	public PartialVK clone() {
		return new PartialVK(this);
	}
	
	private double getSingleVKbat(Aminoid amino) {
		if(chargeType != null) {
			return getWeightedCharge(amino);
		}
		
		if(size() == 0) { return Double.NaN; }
		int H = 0, S = 0, O = 0, NULL = 0;
		//qp("Warning: VK-Predicted Amber95 is the same as normal Amber95");
		
		for(VKPred key: this) {
			
			//qp("key: "+ key + " :: " + chain.get(index).getVKPrediction(key));
			
			if(amino.getVKPrediction(key) == null) {
				++NULL;
			} else {
				switch(amino.getVKPrediction(key)) {
				case Helix:		++H;		break;
				case Sheet:		++S;		break;
				case Other:		++O;		break;
				default:	
				}
			}
		}
		//number of secondary structure categories = 3
		double k = 0; //max = 3
		
		if(H > 0) { ++k; }
		if(S > 0) { ++k; }
		if(O > 0) { ++k; }
		//number of algorithms
		double N = (size() - NULL);
		//most common types of secondary structure
		double n_1 = max(H, S, O);
		//qp(k + " : " + N + " : " + n_1);
		
		double retval = k * N / n_1;
		
		if(retval < 1) {
			throw new InternalError("VKbat is out of range!");
		}
		
		//qerr("returning: "+ retval);
		return retval;
	}
	
	public double getVKbat(AminoChain<?> chain, int index) {
		double retval = getVKbatWindow(chain, index);
		
		if(normalize) {
			throw new NotYetImplementedError("Vkbat normalization not yet supported");
		}
		
		if(flip) {
			throw new NotYetImplementedError("Vkbat flipping not yet supported");
		}
		
		return retval;
	}
	
	private double getVKbatWindow(AminoChain<?> chain, int index) {
		List<Aminoid> aminoWindow = new LabeledList<Aminoid>();
		
		for(int ii = index-windowSize; ii <= index+windowSize; ++ii) {
			if(chain.get(ii) != null && chain.get(ii) instanceof Aminoid) {
				aminoWindow.add((Aminoid) chain.get(ii));
			}
		}
		
		double sum = 0;
		for(Aminoid amino: aminoWindow) {
			sum += getSingleVKbat(amino);
		}
		
		double retval = sum / aminoWindow.size();
		
		if(chargeType == null && retval < 1) {
			throw new InternalError("VKbat is out of range!");
		}
		
		return retval;
	}
	
	/**
	 *
	 */
	private double getWeightedCharge(Aminoid amino) {
		switch(chargeType) {
		case CHARGE_N:
			return Charge.getVKPredictedCharge(amino, TMBRecordedAtom.N, false, this);
		case CHARGE_NH:
			return Charge.getVKPredictedCharge(amino, TMBRecordedAtom.HN, false, this);
		case CHARGE_Cα:
			return Charge.getVKPredictedCharge(amino, TMBRecordedAtom.Cα, false, this);
		case CHARGE_Cβ:
			return Charge.getVKPredictedCharge(amino, TMBRecordedAtom.Cβ, false, this);
		case CHARGE_CP:
			return Charge.getVKPredictedCharge(amino, TMBRecordedAtom.CP, false, this);
		case CHARGE_O:
			return Charge.getVKPredictedCharge(amino, TMBRecordedAtom.O, false, this);
		case NET_CHARGE:
			return Charge.getVKPredictedNetCharge(amino, false, false, this);
		case AVERAGE_CHARGE:
			return Charge.getVKPredictedNetCharge(amino, true, false, this);
		default:
			throw new SwiPredRuntimeException("Not a valid use of PartialVK");
		}
	}

}
