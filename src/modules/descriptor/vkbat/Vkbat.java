package modules.descriptor.vkbat;

import java.util.List;
import java.util.Objects;

import assist.base.Assist;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.descriptor.VKPred;
import biology.protein.AminoChain;
import modules.descriptor.DescriptorAssignmentModule;
import modules.descriptor.vkbat.choufasman.ChouFasmanValuesMissingException;
import modules.descriptor.vkbat.control.*;
import modules.descriptor.vkbat.exceptions.VKAssignmentLengthException;
import modules.descriptor.vkbat.exceptions.server.NotSupportedByPrabiException;
import modules.descriptor.vkbat.jpred.*;
import modules.descriptor.vkbat.sympred.SymPred2;
import modules.descriptor.vkbat.sympred.SympredTimeoutException;
import project.Project;
import project.ProteinDataset;
import system.Instruction;

/**
 * Module to assign VKbat Descriptor
 * @author Benjy Strauss
 *
 */

public class Vkbat extends DescriptorAssignmentModule {
	private static final String[] LOCAL = { "-l", "-local" };
	protected static final String[] ALG_ARG = {"-alg", "-algorithm"};
	
	/**
	 * Local SSpro implementation can cause problems or crash, set this to true to disable it
	 */
	public static boolean dissableLocalSSpro = false;
	
	public static boolean exitOnVKFail = false;
	
	public Vkbat() { }
	
	public static void assign(Instruction instr, Project project) {
		List<ProteinDataset> projects = new LabeledList<ProteinDataset>();
		for(String key: project.keySet()) {
			if(project.get(key) instanceof ProteinDataset) {
				projects.add((ProteinDataset) project.get(key));
			}
		}
		assign(instr, projects);
	}
	
	public static void assign(Instruction instr, List<ProteinDataset> projects) {
		String allAlgorithms = instr.getFirstArgumentNamed(true, ALG_ARG);
		if(allAlgorithms == null) {
			error("No prediction algorithms specified.");
			return;
		}
		
		String[] algorithms = allAlgorithms.trim().split(",");
		LabeledList<VKPred> preds = new LabeledList<VKPred>();
		for(String alg: algorithms) {
			VKPred pred = VKPred.parse(alg);
			if(pred != null) {
				preds.add(pred);
			} else {
				error("Unknown algorithm: " + alg);
			}
		}
		
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					for(VKPred pred: preds) {
						assign(chain, pred, instr.override || !chain.getMetaData().sspred.contains(pred));
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param dataSet
	 * @param algorithms
	 * @param replaceExisting
	 */
	public static void assign(ProteinDataset dataSet, List<VKPred> algorithms, boolean replaceExisting) {
		Objects.requireNonNull(dataSet, "No dataset to assign predictions to.");
		Objects.requireNonNull(algorithms, "No algorithms to assign.");
		for(AminoChain<?> chain: dataSet) {
			for(VKPred pred: algorithms) {
				assign(chain, pred, replaceExisting);
			}
		}
	}
	
	/**
	 * 
	 * @param chains
	 * @param algorithms
	 * @param replaceExisting
	 */
	public static void assign(Iterable<AminoChain<?>> chains, List<VKPred> algorithms, boolean replaceExisting) {
		for(AminoChain<?> chain: chains) {
			for(VKPred pred: algorithms) {
				assign(chain, pred, replaceExisting);
			}
		}
	}
	
	/**
	 * 
	 * @param chain
	 * @param algorithm
	 * @param replaceExisting
	 */
	public static void assign(AminoChain<?> chain, VKPred algorithm, boolean replaceExisting) {
		//qp("Vkbat.assign(): assigning " + algorithm + " to chain " + chain.id());
		switch(algorithm) {
		case dsc:				LocalVK.assign_DSC(chain, replaceExisting);					break;
		case jnet:				LocalVK.assign_JNET(chain, replaceExisting);				break;
		case psipred:			LocalVK.assign_PsiPred_single(chain, replaceExisting);		break;
		case gor3:				LocalVK.assign_gor3(chain, replaceExisting);				break;
		case gor4:				LocalVK.assign_gor4(chain, replaceExisting);				break;
		case SSPRO_5:			LocalVK.assign_SSpro(chain, replaceExisting);				break;
		case SSPRO_5_HOMOL:		LocalVK.assign_SSproHomol(chain, replaceExisting);			break;
		case CHOU_FASMAN:		LocalVK.assign_ChouFasman(chain, replaceExisting, PredOptions.preprocessChouFasman);	break;
		
		case GOR1:
		case GOR3:
		case GOR4:
		case DPM:
		case HNN:
		case MLRC:
		case PREDATOR_PR:
		case SOPM:
		case SOPMA:
		case SIMPA96:
		case PHD:
		case DSC:
			if((!chain.getMetaData().sspred.contains(algorithm)) || replaceExisting) {
				try {
					String predicted = PRABI_VK.getPRABIServer(chain, algorithm);
					if(predicted != null) {
						chain.setVK(algorithm, predicted);
					} else {
						error("Algorithm " + algorithm + " failed for chain " + chain.id());
					}
					
				} catch (NotSupportedByPrabiException e) {
					error("Internal Error -- check coding of: VK_Module.assignVK()");
				} catch (VKAssignmentLengthException e) {
					error("Prediction of wrong length: (VK_Module.assignVK()");
				}
			} break;
		case JPred:
			try {
				String predSeq = JPredConnect.runJpred(chain, replaceExisting);
				chain.setVK(algorithm, predSeq);
			}  catch (VKAssignmentLengthException e) {
				error("JPred Prediction of wrong length: (VK_Module.assignVK()");
			} catch (JPredJobIDNotFoundException e) {
				JPredConnect.handleJobIDNotFound(e);
			} catch (JPredPredictionNotFoundException e) {
				error("JPred failure for: " + chain.id());
			} catch (JPredException e1) {
				error("JPred failed for chain: " + chain.id() + ".  Sequence too short (min=20 residues)");
			} break;
		
		case PREDATOR_SP:
		case JNET:				
		case PHDpsi:
		case PROFsec:
		case PSIPred:
		case SSPRO_2:
		case YASPIN:
		case SYMPRED_CONSENSUS:
			try {
				String predSeq = SymPred2.runSympred(chain, algorithm);
				if(predSeq.contains(".")) {
					error("Sympred algorithm: " + algorithm + " failed for chain: " + chain.id());
					error("(When a Sympred algorithm fails in this way, additional attempts will be unsuccessful)");
				} else {
					chain.setVK(algorithm, predSeq);
				}
			} catch (SympredTimeoutException e) {
				error("Sympred Server failed retriving data for chain: " + chain.id());
			} catch (VKAssignmentLengthException e) {
				error(algorithm + " prediction of wrong length: (VK_Module.assignVK()");
			}
			break;
		
		default:
			error("VKbat not yet implemented for method: " + algorithm);
		}
	}
	
	/*@Override
	public void handle(ActionEvent arg0) {
		if(SwiPred.getProject() != null) {
			VKbatOptionForm form = new VKbatOptionForm();
			runPopup(form);
			if(form.isOK()) {
				boolean override = form.override();
				LabeledList<VKPred> algorithms = form.getAlgorithms();
				
				for(ProteinProject pp: SwiPred.getShell().getRelevantProjects(form)) {
					if(pp != null && pp.dataSet() != null) {
						for(AminoChain<?> chain: pp.dataSet()) {
							for(VKPred pred: algorithms) {
								assign(chain, pred, override || !chain.getMetaData().sspred.contains(pred));
							}
						}
					}
				}
			}
		} else {
			guiError("Error! No Project!");
		}
	}*/
	
	public static void main(String... args) {
		if(args.length < 1) { 
			qerr("Error, no arguments specified.");
			return;
		}
		
		boolean local = Assist.stringArrayContains(args, LOCAL, true);
		//DirectoryManager.makeFolders();
		
		String sequence = null;
		for(String arg: args) {
			if(!arg.startsWith("-")) {
				sequence = arg;
				break;
			}
		}
		
		if(sequence == null) { 
			qerr("Error, no sequence detected.");
			return;
		}
		
		LabeledHash<VKPred, String> predictions = new LabeledHash<VKPred, String>();
		
		if(local) {
			try {
				predictions.put(VKPred.CHOU_FASMAN, LocalVK.runChouFasman(sequence, false));
			} catch (ChouFasmanValuesMissingException e) {
				qerr("Prediction Error: Chou-Fasman Failed.");
				qerr("\t(This happens when anything other than a standard amino acid is in the sequence)");
			}
			
			VKReturn tmp = LocalVK.runDSC(sequence);
			if(tmp.DSC_remove_isolated_error) {
				qerr("A non-fatal error occured in removing isolated residues.");
			}
			
			predictions.put(VKPred.dsc,  tmp.prediction);
			
			predictions.put(VKPred.gor3, LocalVK.runGor3(sequence));
			predictions.put(VKPred.gor4, LocalVK.runGor4(sequence));
			predictions.put(VKPred.SSPRO_5, LocalVK.runSSpro(sequence));
			predictions.put(VKPred.psipred, LocalVK.runPsiPred_Single(sequence));
			predictions.put(VKPred.jnet, LocalVK.runJNet(sequence));
		} else {
			predictions.put(VKPred.GOR1, PRABI_VK.getPRABIServer(sequence, VKPred.GOR1));
			predictions.put(VKPred.GOR3, PRABI_VK.getPRABIServer(sequence, VKPred.GOR3));
			predictions.put(VKPred.DPM,  PRABI_VK.getPRABIServer(sequence, VKPred.DPM ));
			predictions.put(VKPred.DSC,  PRABI_VK.getPRABIServer(sequence, VKPred.DSC ));
			predictions.put(VKPred.HNN,  PRABI_VK.getPRABIServer(sequence, VKPred.HNN ));
			predictions.put(VKPred.MLRC, PRABI_VK.getPRABIServer(sequence, VKPred.MLRC));
			predictions.put(VKPred.SOPM, PRABI_VK.getPRABIServer(sequence, VKPred.SOPM));
			predictions.put(VKPred.PREDATOR_PR, PRABI_VK.getPRABIServer(sequence, VKPred.PREDATOR_PR));
			predictions.put(VKPred.JPred, PRABI_VK.getPRABIServer(sequence, VKPred.SOPM));
			
			try {
				String predSeq = JPredConnect.callJpred(sequence, true);
				predictions.put(VKPred.JPred, predSeq);
			} catch (JPredException e1) {
				error("JPred failed for sequence: " + sequence);
			} catch (JPredPredictionNotFoundException e) {
				error("JPred prediction not found");
			}
			
			for(VKPred algorithm: new VKPred[]{ VKPred.PHDpsi, VKPred.PROFsec, VKPred.YASPIN,
					VKPred.SSPRO_2, VKPred.PSIPred, VKPred.JNET}) {
				try {
					String pred = SymPred2.runSympred(sequence, algorithm);
					if(!pred.startsWith(".")) {
						predictions.put(algorithm, SymPred2.runSympred(sequence, algorithm));
					} else {
						qerr("Sympred failed for: "+algorithm);
					}
				} catch (SympredTimeoutException e) {
					qerr("Error assigning: "+algorithm);
				}
			}
		}
		
		for(VKPred key: predictions.keySet()) {
			if(predictions.get(key).length() != sequence.length()) {
				predictions.remove(key);
				qerr("Error: prediction " + key + " was of incorrect size.");
			}
		}
		
		LabeledList<String> lines = new LabeledList<String>();
		StringBuilder lineBuilder = new StringBuilder();
		lineBuilder.append("Seq,");
		for(VKPred key: predictions.keySet()) {
			lineBuilder.append(key+",");
		}
		lines.add(lineBuilder.toString());
		lineBuilder.setLength(0);
		
		for(int index = 0; index < sequence.length(); ++index) {
			lineBuilder.append(sequence.charAt(index)+",");
			for(VKPred key: predictions.keySet()) {
				lineBuilder.append(predictions.get(key).charAt(index)+",");
			}
			lines.add(lineBuilder.toString());
			lineBuilder.setLength(0);
		}
		writeFileLines("vk-output.txt", lines);
	}
}
