package tools.writer.csv;

import java.io.Serializable;

import assist.util.LabeledList;
import biology.amino.*;
import biology.descriptor.*;
import biology.molecule.types.AminoType;
import biology.protein.AminoChain;
import biology.protein.MultiChain;
import chem.AminoAtom;
import modules.descriptor.charge.Charge;
import project.ProteinDataset;
import regression.vectors.DataVector;
import utilities.LocalToolBase;

/**
 * Designed to write CSVs of Residue Descriptors
 * @author Benjamin Strauss
 *
 */

//@SuppressWarnings("rawtypes")
public abstract class AbstractDescriptorCSVWriter extends LabeledList<Metric> implements CSVTools, Serializable {
	private static final long serialVersionUID = 1L;
	
	//how often do we run the garbage collector when dealing with the expanded data set
	protected static final int GC_INTERVAL = 50;
	
	protected static final String OUTPUT = "output/";
	protected static final String CSV = LocalToolBase.CSV;
	
	protected boolean repeatHeaderBetweenChains = false;
	
	public AbstractDescriptorCSVWriter() { }
	
	public AbstractDescriptorCSVWriter(String label) { super(label); }
	public abstract void writeData(String saveFileName, DataVector<?>... data);
	public abstract void writeData(String saveFileName, Iterable<AminoChain<?>> myChains);
	
	public void writeData(String saveFileName, ProteinDataset myChains) {
		writeData(saveFileName, (Iterable<AminoChain<?>>) myChains);
	}
	
	/**
	 * 
	 * @param saveFileName
	 * @param myChains
	 */
	public void writeData(String saveFileName, AminoChain<?>... myChains) {
		LabeledList<AminoChain<?>> chains = new LabeledList<AminoChain<?>>();
		chains.addAll(myChains);
		writeData(saveFileName, chains);
	}
	
	/**
	 * Retrieve's a residue's descriptor data
	 * @param chain: the chain containing the residue
	 * @param index: the residue's index in the chain
	 * @param desc: type of descriptor data to retrieve
	 * @return
	 */
	protected static Object retrieveDescriptorData(AminoChain<?> chain, int index, Descriptor desc) {
		if(chain.get(index) instanceof Aminoid) {
			Aminoid amino = (Aminoid) chain.get(index);
			
			switch(desc.type) {
			case AMBER_95:
			case FLEXIBILITY:
				return CSVTools.getWindowAverage(chain, index, desc);
			case AVERAGE_CHARGE:
				return amino.averageCharge();
			case CHARGE_Cα:
			case CHARGE_Cβ:
			case CHARGE_CP:
			case CHARGE_N:
			case CHARGE_NH:
			case CHARGE_O:
				if(amino.getAtom(desc.type.toString()) == null) {
					return Double.NaN;
				} else {
					return amino.getAtom(desc.type.toString()).charge();
				}
			case E20:
				double avg_20 = CSVTools.getWindowAverage(chain, index, desc);
				String val20_t = AminoTools.processEntropy(avg_20, 20, desc.flip, desc.normalize);
				return val20_t;
			case E22:
				double avg_22 = CSVTools.getWindowAverage(chain, index, desc);
				String val22_t = AminoTools.processEntropy(avg_22, 22, desc.flip, desc.normalize);
				return val22_t;
			case E6:
				double avg_6 = CSVTools.getWindowAverage(chain, index, desc);
				String val6_t = AminoTools.processEntropy(avg_6, 6, desc.flip, desc.normalize);
				return val6_t;
			case ISUNSTRUCT:
				double avg_isu = CSVTools.getWindowAverage(chain, index, desc);
				return (desc.flip) ? (1 - avg_isu) : avg_isu;
				
			case VKBAT:
				return (amino.vkbat() == -1) ? "Unknown" : amino.vkbat();
				
			case VKBAT_COMPLETION:
				return amino.vkKeys().size();
				
			case VK_PREDICTED_AVG_TMB:
				return Charge.getVKPredictedNetCharge(amino, true, false);
			case VK_PREDICTED_AVG_ABS_TMB: 
				return Charge.getVKPredictedNetCharge(amino, true, true);
			case VK_PREDICTED_NET_TMB:
				return Charge.getVKPredictedNetCharge(amino, false, false);
			case VK_PREDICTED_NET_ABS_TMB:
				return Charge.getVKPredictedNetCharge(amino, false, true);
			case GO_ID:
				return chain.function().hashCode();
			default:		throw new UnimplementedDescriptorException(desc.type);
			}	
		} else {
			return "N/A";
		}
	}
	
	/**
	 * 
	 * @param chain
	 * @param index
	 * @param mm
	 * @return
	 */
	protected static String retrieveIdentifierData(AminoChain<?> chain, int index, Identifier mm) {
		switch(mm) {
		case RCSB_ID:			return chain.id().protein();
		case CHAIN_ID:			return chain.id().chain();
		case RESIDUE_NUMBER:	return ""+(index);
		case UNIPROT_ID:		return chain.id().uniprot();
		case PFAM_ID:			return chain.id().pfam();
		
		case RESIDUE_CODE_CONSENSUS:
			return chain.get(index).toCode();
		case RESIDUE_CODE_DOMINANT:
			if(chain.get(index) instanceof AminoPosition) {
				return ((AminoPosition) chain.get(index)).dominantPrimary().toCode();
			} else {
				return chain.get(index).toCode();
			}
		case RESIDUE_LETTER_CONSENSUS:
			return ""+chain.get(index).toChar();
		case RESIDUE_LETTER_DOMINANT:
			if(chain.get(index) instanceof AminoPosition) {
				return ""+((AminoPosition) chain.get(index)).dominantPrimary().toChar();
			} else {
				return ""+chain.get(index).toChar();
			}
		case SECONDARY_SIMPLIFIED:
			if(chain.get(index) instanceof Aminoid) {
				return ""+((Aminoid) chain.get(index)).secSimple();
			} else { 
				return "N/A";
			}
		case SECONDARY_STRUCTURE:
			if(chain.get(index) instanceof Aminoid) {
				return ""+((Aminoid) chain.get(index)).secondary();
			} else { 
				return "N/A";
			}
		case NUM_HOMOLOGUES:	
			if(chain instanceof MultiChain) {
				AminoPosition ap = (AminoPosition) chain.get(index);
				ResidueConfig[] configs = ap.getConfigs();
				int total = 0;
				for(ResidueConfig config: configs) {
					total += config.occurrences();
				}
				return ""+total;
			} else {
				return ""+chain.knownHomologues();
			}
		case DATA_SOURCE:
			return ""+chain.getMetaData().source();
		default:	throw new UnimplementedDescriptorException(mm);
		}
		
	}
	
	/**
	 * Makes sure a specific atom charge is in the database for a given residue
	 * @param residueType
	 * @param atom
	 * @return
	 */
	protected static boolean skipCharge(AminoType residueType, TMBRecordedAtom atom) {
		if(residueType == AminoType.Glycine && atom.name().equals(AminoAtom.AMINO_Cβ)) {
			return true;
		} else if(residueType == AminoType.Proline && atom.name().equals(AminoAtom.AMINO_HN)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setRepeatHeaderBetweenChains(boolean b) {
		repeatHeaderBetweenChains = b;
	}
}
