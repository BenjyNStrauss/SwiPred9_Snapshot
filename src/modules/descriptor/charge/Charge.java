package modules.descriptor.charge;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import biology.amino.*;
import biology.amino.atoms.AtomTable;
import biology.descriptor.DescriptorType;
import biology.descriptor.TMBRecordedAtom;
import biology.descriptor.VKPred;
import biology.molecule.types.AminoType;
import biology.protein.AminoChain;
import chem.AminoAtom;
import modules.descriptor.DescriptorAssignmentModule;
import project.ProteinDataset;
import system.Instruction;
import tools.Lookup;

/**
 * Module to residue charge Descriptor
 * @author Benjy Strauss
 *
 */

public class Charge extends DescriptorAssignmentModule {
	//list of all the atom types in the paper
	private static final TMBRecordedAtom[] ATOM_LIST = {
			TMBRecordedAtom.N,  TMBRecordedAtom.HN,
			TMBRecordedAtom.Cα, TMBRecordedAtom.Cβ,
			TMBRecordedAtom.CP, TMBRecordedAtom.O
	};
	
	public Charge() { }
	
	/**
	 * Assign Charges to a project
	 * @param instr: instruction on how to assign charges
	 * @param project: project to assign charges to
	 */
	public static void assign(Instruction instr, Iterable<ProteinDataset> projects) {
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					assign(chain);
				}
			}
		}
	}
	
	/**
	 * Assign Charges to a project
	 * @param instr: instruction on how to assign charges
	 * @param project: project to assign charges to
	 */
	public static void assign(Instruction instr, ProteinDataset... projects) {
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					assign(chain);
				}
			}
		}
	}
	
	public static void assign(AminoChain<?> chain) {
		qp("Assigning charges based on primary and secondary structure to: " + chain.id().relevant(chain.getMetaData().source()));
		for(ChainObject amino: chain) {
			if(amino != null && amino instanceof Aminoid) { 
				assignTMBCharge((Aminoid) amino);
			}
		}
	}
	
	public static void assignTMBCharge(Aminoid amino) {
		Objects.requireNonNull(amino, "Need an animo to assign charge to!");
		if(amino instanceof AminoAcid) {
			double netAmber95 = 0;
			
			Set<TMBRecordedAtom> chargeHashKeys = ChargeTable.getKeys(amino.residueType());
			for(TMBRecordedAtom tmbAtom: chargeHashKeys) {
				double value = ChargeTable.getAtomCharge(amino.residueType(), amino.secondary(), tmbAtom);
				double atomAmber = ChargeTable.getAmber95(amino.residueType(), tmbAtom);
				netAmber95 += atomAmber;
				
				AminoAtom aatom = amino.getAtom(tmbAtom.toString());
				if(aatom == null) {
					aatom = AminoAtom.parse(tmbAtom.toString());
					amino.setAtom(aatom);
				}
				
				aatom.setCharge(value);
				aatom.setAmber(atomAmber);
			}
			amino.setDescriptor(DescriptorType.AMBER_95, netAmber95 / ((double) chargeHashKeys.size()));
		} else if(amino instanceof AminoPosition) {
			AminoPosition ap = (AminoPosition) amino;
			
			double netAmber95 = 0;
			AtomTable table = new AtomTable();
			for(String str: Lookup.ATOM_CODES) {
				TMBRecordedAtom tmbAtom = TMBRecordedAtom.parse(str);
				//qp(ap.getAtom(str));
				AminoAtom atom = (ap.getAtom(str) != null) ? ap.getAtom(str) : ChargeTable.generateTMB(tmbAtom);
				
				table.put(atom);
			}
			
			for(String str: Lookup.ATOM_CODES) {
				AminoAtom atom = table.get(str);
				
				//qp(amino);
				//qp(atom);
				
				atom.setCharge(0);
				atom.setAmber(0);
				int chargeNANs = 0;
				int amberNANs = 0;
				
				for(ResidueConfig config: ap) {
					//qp(config.primary() + ":" + config.secondary() + ":"+ TMBRecordedAtom.parse(str));
					double nextAtomCharge = ChargeTable.getAtomCharge(config.primary(), config.secondary(), TMBRecordedAtom.parse(str));
					double nextAtomAmber = ChargeTable.getAmber95(config.primary(), TMBRecordedAtom.parse(str));
					
					if(Double.isNaN(nextAtomCharge)) {
						//qp("!!!!");
						++chargeNANs;
					} else {
						atom.setCharge(atom.charge() + nextAtomCharge);
					}
					
					if(Double.isNaN(nextAtomAmber)) {
						++amberNANs;
					} else {
						atom.setAmber(atom.amber() + nextAtomAmber);
					}
				}
				atom.setCharge(atom.charge() / ((double) ap.getConfigs().length) - chargeNANs);
				atom.setAmber(atom.amber() / ((double) ap.getConfigs().length) - amberNANs);
				
				/*qp("configs   : "+ap.getConfigs().length);
				qp("chargeNANs: "+chargeNANs);
				qp("charges   : "+(ap.getConfigs().length-chargeNANs));
				qp("amberNANs : "+amberNANs);*/
				
				netAmber95 += atom.amber();
				ap.setAtom(atom);
			}
			
			//System.exit(0);
			netAmber95 = netAmber95 / ((double) Lookup.ATOM_CODES.length);
			ap.setDescriptor(DescriptorType.AMBER_95, netAmber95);
		}
	}
	
	/**
	 * Gets the vkbat-predicted charge for a single type of atom;
	 * @param amino
	 * @param atom
	 * @return
	 */
	
	/**
	 * Get the charge for a single atom of a given amino acid
	 * @param aminoid
	 * @param atom
	 * @param absoluteValue: get the absolute value
	 * @return
	 */
	public static double getVKPredictedCharge(Aminoid aminoid, TMBRecordedAtom atom, boolean absoluteValue) {
		Set<VKPred> vkKeys = aminoid.vkKeys();
		if(vkKeys.size() == 0) { return Double.NaN; }
		int H = 0, E = 0, O = 0;
		
		for(VKPred key: vkKeys) {
			switch(aminoid.getVKPrediction(key)) {
			case Helix:		++H;		break;
			case Sheet:		++E;		break;
			case Other:		++O;		break;
			default:	
			}
		}
		
		double chargeH = H * ChargeTable.getAtomCharge(aminoid.residueType(), SecondarySimple.Helix, atom);
		double chargeE = E * ChargeTable.getAtomCharge(aminoid.residueType(), SecondarySimple.Sheet, atom);
		double chargeO = O * ChargeTable.getAtomCharge(aminoid.residueType(), SecondarySimple.Other, atom);
		
		double numPreds = vkKeys.size();
		double total = 0;
		if(!Double.isNaN(chargeH)) { total += chargeH; } else { numPreds -= H; }
		if(!Double.isNaN(chargeE)) { total += chargeE; } else { numPreds -= E; }
		if(!Double.isNaN(chargeO)) { total += chargeO; } else { numPreds -= O; }
		
		if(absoluteValue) { total = Math.abs(total); }
		total = total/numPreds;
		
		//qp(total);
		//System.exit(3);
		return total;
	}
	
	/**
	 * Get the charge for a single atom of a given amino acid
	 * @param amino
	 * @param atom
	 * @param absoluteValue: get the absolute value
	 * @param predictions
	 * @return
	 */
	public static double getVKPredictedCharge(Aminoid amino, TMBRecordedAtom atom, boolean absoluteValue, Collection<VKPred> predictions) {
		Objects.requireNonNull(amino, "Cannot get predicted charge for residue of null type.");
		if(predictions.size() == 0) { return Double.NaN; }
		int H = 0, E = 0, O = 0;
		
		for(VKPred key: predictions) {
			SecondarySimple value = amino.getVKPrediction(key);
			//key would be null if vkbat assignment failed
			if(value != null) {
				switch(value) {
				case Helix:		++H;		break;
				case Sheet:		++E;		break;
				case Other:		++O;		break;
				default:	
				}
			}
		}
		
		double chargeH = H * ChargeTable.getAtomCharge(amino.residueType(), SecondarySimple.Helix, atom);
		double chargeE = E * ChargeTable.getAtomCharge(amino.residueType(), SecondarySimple.Sheet, atom);
		double chargeO = O * ChargeTable.getAtomCharge(amino.residueType(), SecondarySimple.Other, atom);
		//qp("###: "+chargeE);
		
		/*if(Double.isNaN(chargeH) && Double.isNaN(chargeE) && Double.isNaN(chargeO)) {
			qp("["+atom+"]--------------------------------");
			qp("NAN-H: " + H + " ("+amino.residueType()+")");
			qp("NAN-E: " + E + " ("+amino.residueType()+")");
			qp("NAN-O: " + O + " ("+amino.residueType()+")");
		}*/
		
		double numPreds = predictions.size();
		double total = 0;
		if(!Double.isNaN(chargeH)) { total += chargeH; } else { numPreds -= H; }
		if(!Double.isNaN(chargeE)) { total += chargeE; } else { numPreds -= E; }
		if(!Double.isNaN(chargeO)) { total += chargeO; } else { numPreds -= O; }
		
		if(absoluteValue) { total = Math.abs(total); }
		total = total/numPreds;
		
		/*if(Double.isNaN(total)) {
			qp("["+atom + " : " + amino.residueType().toChar() + "]---------------");
			qp("H: " + H + " : " + chargeH);
			qp("E: " + E + " : " + chargeE);
			qp("O: " + O + " : " + chargeO);
		}*/
		
		//qp(total);
		//System.exit(3);
		return total;
	}
	
	/**
	 * Get vkbat-predicted charge for all atoms in the amino object
	 * @param amino: amino to calculate the charge for
	 * @param average: compute an average of the atoms
	 * @param absoluteValue: use the absolute value of the atom charges
	 * @return
	 */
	public static double getVKPredictedNetCharge(Aminoid amino, boolean average, boolean absoluteValue) {
		return getVKPredictedNetCharge(amino, average, absoluteValue, amino.vkKeys());
	}
	
	/**
	 * 
	 * Get vkbat-predicted charge for all atoms in the amino object
	 * @param amino: amino to calculate the charge for
	 * @param average: compute an average of the atoms
	 * @param absoluteValue: use the absolute value of the atom charges
	 * @param predictions
	 * @return
	 */
	public static double getVKPredictedNetCharge(Aminoid amino, boolean average, boolean absoluteValue, Collection<VKPred> predictions) {
		int numAtoms = ATOM_LIST.length;
		double total = 0;
		for(TMBRecordedAtom tmbAtom: ATOM_LIST) {
			double atomCharge = (predictions != null) ? 
					getVKPredictedCharge(amino, tmbAtom, absoluteValue, predictions) : 
					getVKPredictedCharge(amino, tmbAtom, absoluteValue);
			if(!Double.isNaN(atomCharge)) { total += atomCharge; } else {
				//qp("Lookup: " + amino.residueType() + " : " + tmbAtom);
				
				--numAtoms;
			}
		}
		
		if(numAtoms == 0) {
			return Double.NaN;
		}
		
		if(average) {
			return (total/numAtoms);
		} else {
			return total;
		}
	}
	
	/**
	 * 
	 * @param resType
	 * @return
	 */
	public static double getAvgAmber95(AminoType resType) {
		return ChargeTable.getAvgAmber95(resType);
	}
	
	public static final TMBRecordedAtom[] atom_list() {
		TMBRecordedAtom[] atoms = new TMBRecordedAtom[ATOM_LIST.length];
		System.arraycopy(ATOM_LIST, 0, atoms, 0, ATOM_LIST.length);
		return atoms;
	}
	
	/*public static void main(String[] args) {
		//AminoAcid aa = new AminoAcid(SideChain.Cysteine);
		qp(getAvgAmber95(SideChain.Cysteine));
		double d = -0.416 + 0.272 +0.021+ 0.597 -0.568 -0.123;
		qp(d/6);
		
	}*/
}
