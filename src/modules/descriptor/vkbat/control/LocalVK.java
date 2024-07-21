package modules.descriptor.vkbat.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import biology.descriptor.VKPred;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import install.FileManager;
import modules.descriptor.vkbat.choufasman.*;
import modules.descriptor.vkbat.dsc.DSC;
import modules.descriptor.vkbat.exceptions.VKAssignmentLengthException;
import modules.descriptor.vkbat.gor.gor3.Gor3;
import modules.descriptor.vkbat.gor.gor4.Gor4;
import modules.descriptor.vkbat.jnet.JNet;
import modules.descriptor.vkbat.psipred.PsiPredException;
import modules.descriptor.vkbat.psipred.seq2mtx;
import modules.descriptor.vkbat.psipred.sspred_avpred;
import modules.descriptor.vkbat.sspro5_2.SSproHomology;
import modules.descriptor.vkbat.sspro5_2.SSproManager;
import utilities.LocalToolBase;

/**
 * Contains methods for generating secondary structure prediction data locally with Java
 * 		for use in vkbat calculations.
 * 
 * @author Benjy Strauss
 *
 * Note that this class should not be visible from outside the package!
 */

public final class LocalVK extends LocalToolBase {
	private static final VKPred[] ALL_LOCAL_ALGORTHIMS = { 
			VKPred.psipred, VKPred.gor3, VKPred.gor4, VKPred.SSPRO_5, VKPred.jnet, VKPred.dsc,
			VKPred.CHOU_FASMAN
	};
	
	
	private static final String VK_LOG_PREF = "-> Running";
	private static boolean helpRequest_gor3 = false;
	
	/**
	 * Assigns the PsiPred single (non-homology) prediction to an AminoChain, if it doesn't already exist
	 * @param chain: chain to assign PsiPred Single prediction to
	 */
	public static void assign_PsiPred_single(AminoChain<?> chain) {
		assign_PsiPred_single(chain, false);
	}
	
	/**
	 * Assigns the PsiPred single (non-homology) prediction to an AminoChain
	 * @param chain: chain to assign PsiPred Single prediction to
	 * @param replaceExisting: replace existing algorithm prediction
	 */
	public static void assign_PsiPred_single(AminoChain<?> chain, boolean replaceExisting) {
		if((!chain.getMetaData().sspred.contains(VKPred.psipred)) || replaceExisting) {
			try {
				log("PsiPred Single (Java Translation) starting for chain: " + chain.id().standard());
				String seq = FastaCrafter.textSequenceForVkbat(chain);
				chain.setVK(VKPred.psipred, runPsiPred_Single(seq));
				log("PsiPred Single (Java Translation) complete for chain: " + chain.id().standard());
			} catch (VKAssignmentLengthException e) {
				qerr(e.generateReport());
			} catch (PsiPredException PPE) {
				qerr("Nonfatal Psipred Error for: " + chain.id().standard());
				qerr(PPE.getMessage());
				PPE.printStackTrace();
				qerr("\n");
			}
		}
	}
	
	/**
	 * Assigns the PsiPred single (non-homology) prediction to an AminoChain, if it doesn't already exist
	 * @param chain: chain to assign PsiPred Single prediction to
	 */
	public static void assign_gor3(AminoChain<?> chain) {
		assign_gor3(chain, false);
	}
	
	/**
	 * Assigns the Gor3 prediction to a protein chain
	 * @param chain: chain to assign Gor3 prediction to
	 * @param replaceExisting: replace existing algorithm prediction
	 */
	public static void assign_gor3(AminoChain<?> chain, boolean replaceExisting) {
		if((!chain.getMetaData().sspred.contains(VKPred.gor3)) || replaceExisting) {
			try {
				log("Gor3 (Java Translation) starting for chain: " + chain.id().standard());
				String seq = FastaCrafter.textSequenceForVkbat(chain);
				chain.setVK(VKPred.gor3, runGor3(seq));
				log("Gor3 (Java Translation) complete for chain: " + chain.id().standard());
			} catch (VKAssignmentLengthException e) {
				qerr(e.generateReport());
				qerr("(This will happen if there is an 'X' in the amino acid chain)");
				if(!helpRequest_gor3) {
					qerr("If you know how to fix this, please contact Benjynstrauss@gmail.com");
					helpRequest_gor3 = true;
				}
			} catch (Exception e) {
				qerr("Error assigning Gor3 to "+chain.id());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Assigns the DSC prediction to an AminoChain, if it doesn't already exist
	 * @param chain: chain to assign DSC prediction to
	 */
	public static void assign_DSC(AminoChain<?> chain) {
		assign_DSC(chain, false);
	}
	
	/**
	 * Assigns the DSC prediction to an AminoChain
	 * @param chain: chain to assign DSC prediction to
	 * @param replaceExisting: replace existing algorithm prediction
	 */
	public static void assign_DSC(AminoChain<?> chain, boolean replaceExisting) {
		if((!chain.getMetaData().sspred.contains(VKPred.dsc)) || replaceExisting) {
			String seq = FastaCrafter.textSequenceForVkbat(chain);
			VKReturn prediction = runDSC(seq);
			try {
				log("DSC (Java Translation) starting for chain: " + chain.id().standard());
				chain.setVK(VKPred.dsc, prediction.prediction);
				log("DSC (Java Translation) complete for chain: " + chain.id().standard());
			} catch (VKAssignmentLengthException e) {
				qerr(e.generateReport());
			}
			if(prediction.DSC_remove_isolated_error) {
				qp("Error: DSC failed in removing isolated residues for: " + chain.id().standard());
				pause(2);
				qerrl("Error: DSC failed in removing isolated residues for: " + chain.id().standard());
			}
		}
	}
	
	/**
	 * Assigns the JNET prediction to an AminoChain, if it doesn't already exist
	 * @param chain: chain to assign JNET prediction to
	 */
	public static void assign_JNET(AminoChain<?> chain) {
		assign_JNET(chain, false);
	}
	
	/**
	 * Assigns the JNET prediction to an AminoChain
	 * @param chain: chain to assign JNET prediction to
	 * @param replaceExisting: replace existing algorithm prediction
	 */
	public static void assign_JNET(AminoChain<?> chain, boolean replaceExisting) {
		if((!chain.getMetaData().sspred.contains(VKPred.jnet)) || replaceExisting) {
			try {
				log("JNET (Java Translation) starting for chain: " + chain.id().standard());
				String seq = FastaCrafter.textSequenceForVkbat(chain);
				chain.setVK(VKPred.jnet, runJNet(seq));
				log("JNET (Java Translation) complete for chain: " + chain.id().standard());
			} catch (VKAssignmentLengthException e) {
				qerr(e.generateReport());
			}
		}
	}
	
	/**
	 * Assigns the SSpro5.2 prediction (no homology) to an AminoChain, if it doesn't already exist
	 * @param chain: chain to assign SSpro prediction to
	 */
	public static void assign_SSpro(AminoChain<?> chain) {
		assign_SSpro(chain, false);
	}
	
	/**
	 * Assigns the SSpro5.2 prediction (without homology) to an AminoChain
	 * @param chain: chain to assign SSpro prediction to
	 * @param replaceExisting: replace existing algorithm prediction
	 */
	public static void assign_SSpro(AminoChain<?> chain, boolean replaceExisting) {
		if((!chain.getMetaData().sspred.contains(VKPred.SSPRO_5)) || replaceExisting) {
			String seq = FastaCrafter.textSequenceForVkbat(chain);
			String sspro_result = runSSpro(seq);
			if(sspro_result != null) {
				try {
					log("SSPRO v5.2 (Java Translation) starting for chain: " + chain.id().standard());
					chain.setVK(VKPred.SSPRO_5, sspro_result);
					log("SSPRO v5.2 (Java Translation) complete for chain: " + chain.id().standard());
				} catch (VKAssignmentLengthException e) {
					qerr(e.generateReport());
				}
			}
		}
	}
	
	/**
	 * Assigns the SSpro5.2 prediction (with homology) to an AminoChain, if it doesn't already exist
	 * @param chain: chain to assign SSpro prediction to
	 */
	public static void assign_SSproHomol(AminoChain<?> chain) {
		assign_SSproHomol(chain, false);
	}
	
	/**
	 * Assigns the SSpro5.2 prediction (with homology) to an AminoChain
	 * @param chain: chain to assign SSpro (homology) prediction to
	 * @param replaceExisting: replace existing algorithm prediction
	 */
	public static void assign_SSproHomol(AminoChain<?> chain, boolean replaceExisting) {
		if((!chain.getMetaData().sspred.contains(VKPred.SSPRO_5_HOMOL)) || replaceExisting) {
			String seq = FastaCrafter.textSequenceForVkbat(chain);
			String sspro_result = runSSpro(seq);
			if(sspro_result != null) {
				try {
					chain.setVK(VKPred.SSPRO_5, sspro_result);
				} catch (VKAssignmentLengthException e) {
					qerr(e.generateReport());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param chain
	 */
	public static void assign_gor4(AminoChain<?> chain) {
		assign_gor4(chain, false);
	}
	
	/**
	 * 
	 * @param chain
	 * @param replaceExisting
	 */
	public static void assign_gor4(AminoChain<?> chain, boolean replaceExisting) {
		if((!chain.getMetaData().sspred.contains(VKPred.gor4)) || replaceExisting) {
			String seq = FastaCrafter.textSequenceForVkbat(chain);
			String gor4_result = runGor4(seq);
			if(gor4_result != null) {
				try {
					chain.setVK(VKPred.gor4, gor4_result);
				} catch (VKAssignmentLengthException e) {
					qerr(e.generateReport());
				}
			}
		}
	}
	
	/**
	 * Assigns the Chou-Fasman prediction to an AminoChain, if it doesn't already exist
	 * @param chain: chain to assign Chou-Fasman prediction to
	 */
	public static void assign_ChouFasman(AminoChain<?> chain) {
		assign_ChouFasman(chain, false, false);
	}
	
	/**
	 * Assigns the Chou-Fasman (non-homology) prediction to an AminoChain
	 * @param chain: chain to assign Chou-Fasman prediction to
	 * @param replaceExisting: replace existing algorithm prediction
	 */
	public static void assign_ChouFasman(AminoChain<?> chain, boolean replaceExisting, boolean preprocess) {
		if((!chain.getMetaData().sspred.contains(VKPred.CHOU_FASMAN)) || replaceExisting) {
			try {
				String seq = FastaCrafter.textSequenceForVkbat(chain);
				String pred = runChouFasman(seq, preprocess);
				chain.setVK(VKPred.CHOU_FASMAN, pred);
			} catch (VKAssignmentLengthException e) {
				qerr(e.generateReport());
			} catch (ChouFasmanValuesMissingException e) {
				qerr(e.getMessage());
				qerr("Chain causing error: " + chain + "\n");	
			}
		}
	}
	
	public static String runGor3(String sequence) {
		Objects.requireNonNull(sequence, "Cannot run Gor3 on null sequence!");
		log("Running Gor3 on: " + sequence);
		
		Gor3 module = new Gor3();
		String prediction = module.sec_struc_prediction(sequence);
		if(module.nullPointerIssue()) {
			qerrl("There was an issue involving null values for GOR3 with sequence:\n\t"+sequence);
		}
		
		return prediction;
	}
	
	/**
	 * Runs PsiPred Single on the sequence given as input
	 * @return: secondary structure predictions as given by psipred
	 */
	public static String runPsiPred_Single(String sequence) {
		Objects.requireNonNull(sequence, "Cannot run PSIPred on null sequence!");
		log("Running PSIPred (single) on: " + sequence);
		
		String mtx_file_name = "meta-fasta.mtx";
		File mtxFile = new File(mtx_file_name);
		String prediction = null;
		
		seq2mtx translator = new seq2mtx();
		
		sspred_avpred psipredModule = new sspred_avpred(sequence.length());
		
		String lines[] = translator.getMTXFromString(sequence).split("\n");
		
		writeFileLines(mtx_file_name, lines);
		
		String[] avpred_args = new String[5];
		avpred_args[1] = mtx_file_name;
		avpred_args[2] = FileManager.getPsiPredWeights(1);
		avpred_args[3] = FileManager.getPsiPredWeights(2);
		avpred_args[4] = FileManager.getPsiPredWeights(3);
		
		try {
			prediction = psipredModule.javaMain(avpred_args);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mtxFile.delete();
		
		return prediction;
	}
	
	/**
	 * Runs JNET on a Protein Chain
	 * @param sequence: the protein chain's primary sequence
	 * @return: Final secondary structure prediction by JNet (NOT alignment!)
	 */
	public static String runJNet(String sequence) {
		Objects.requireNonNull(sequence, "Cannot run JNET on null sequence!");
		log(VK_LOG_PREF + "JNET on: " + sequence);
		
		JNet jnetModule = new JNet();
		String retval = jnetModule.main(sequence);
		//qp((int) retval.charAt(50));
		retval = retval.trim();
		return retval;
	}
	
	/**
	 * 
	 * 1CJWA gives output:
	 * @param sequence
	 * @return
	 */
	public static String runSSpro(String sequence) {
		//if(SSproDebugModule.DEBUG)
		Objects.requireNonNull(sequence, "Cannot run SSpro v5.2 on null sequence!");
		log(VK_LOG_PREF + "SSpro v5.2 on: " + sequence);
		
		return SSproManager.sspro(sequence);
	}
	
	public static String runSSpro_homology(String sequence) {
		Objects.requireNonNull(sequence, "Cannot run SSpro v5.2+homology on null sequence!");
		log(VK_LOG_PREF + "SSpro v5.2 (+homology) on: " + sequence);
		
		String DB_blast = "vkabat/SCRATCH-1D_1.2/pkg/HOMOLpro_1.2/data/pdb_full/pdb_full"; 
		String homol = null;
		
		try {
			//bio.tools.vkabat.sspro.raw.SSproHomology.
			homol = SSproHomology.addHomologyPredictions(DB_blast, sequence);
		} catch (IOException e) {
			e.printStackTrace();
		}
		homol = homol.replaceAll("_", "-");
		homol = homol.replaceAll("C", "-");
		
		return homol;
	}

	/**
	 * Runs DSC on the amino acid sequence
	 * @param seq: the sequence
	 * @return: DSC secondary structure prediction
	 * NOTE: not the same as online DSC!
	 */
	public static VKReturn runDSC(String sequence) {
		Objects.requireNonNull(sequence, "Cannot run DSC on null sequence!");
		log(VK_LOG_PREF + "Gor IV on: " + sequence);
		
		DSC dscModule = new DSC(sequence);
		/* Main prediction routine */
		dscModule.predict_sequence();
		
		VKReturn retval = new VKReturn(VKPred.dsc);
		retval.prediction = dscModule.prediction();
		if(dscModule.errorRemovingIsolated()) {
			retval.DSC_remove_isolated_error = true;
		}
		
		return retval;
	}
	
	public static String runGor4(String sequence) {
		Objects.requireNonNull(sequence, "Cannot run Gor4 on null sequence!");
		log(VK_LOG_PREF + "Gor IV on: " + sequence);
		
		Gor4 module = new Gor4();
		String prediction = null;
		
		try {
			prediction = module.internalMain(sequence);
		} catch (FileNotFoundException e) {
			error("Critical database files missing: GOR-IV cannot run.");
		}
		
		return prediction;
	}
	
	public static String runChouFasman(String sequence, boolean preprocess) throws ChouFasmanValuesMissingException {
		Objects.requireNonNull(sequence, "Cannot run ChouFasman on null sequence!");
		log(VK_LOG_PREF + "Chou-Fasman on: " + sequence);
		log("\tpre-processing: " + ((preprocess) ? "on" : "off"));
		
		ChouFasman module = new ChouFasman();
		String prediction = module.runChouFasman(sequence, preprocess);
		return prediction;
	}
	
	public static VKPred[] getLocal() {
		VKPred[] array = new VKPred[ALL_LOCAL_ALGORTHIMS.length];
		System.arraycopy(ALL_LOCAL_ALGORTHIMS, 0, array, 0, ALL_LOCAL_ALGORTHIMS.length);
		return array;
	}
	
	/*public static void main(String... args) {
		String seq = "GSRDNLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVPRCPRCPADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV";
		String pred = runGor4(seq);
		qp("*" + pred);
	}*/
}
