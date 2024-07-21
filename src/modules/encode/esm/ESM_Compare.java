package modules.encode.esm;

import java.util.Objects;

import assist.script.PythonScript;
import biology.amino.BioMolecule;
import biology.amino.ChainObject;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.protein.ChainFactory;
import biology.protein.ProteinChain;
import biology.tools.SequenceAligner;
import modules.blast.BlastP;
import utilities.LocalToolBase;

/**
 * TODO
 * recode aligner to use embeddings
 * Global vs local alignment
 * 		Mahdi suggests global alignment (Needleman–Wunsch)
 * 		Local is Smith-Waterman
 * 
 * Bit Score = # of positions that match
 * 
 * @author Benjamin Strauss
 *
 * 
 */

public class ESM_Compare extends LocalToolBase {
	
	/**
	 * 
	 * @param a1
	 * @param a2
	 * @param model
	 * @return
	 */
	public static double getRMSD(ChainObject a1, ChainObject a2, ESM_Model model) {
		Objects.requireNonNull(a1, "No amino specified (param #1)!");
		Objects.requireNonNull(a2, "No amino specified (param #2)!");
		Objects.requireNonNull(model, "No model specified.");
		
		double[] a1_vals = a1.getEncoding(model);
		double[] a2_vals = a2.getEncoding(model);
		
		double sum = 0;
		for(int index2 = 0; index2 < a1_vals.length; ++index2) {
			double temp = a1_vals[index2] - a2_vals[index2];
			temp = temp * temp;
			sum += temp;
		}
		sum /= a1_vals.length;
		sum = Math.sqrt(sum);
		
		return sum;
	}
	
	/**
	 * 
	 * @param chain1
	 * @param chain2
	 * @param model
	 * @return
	 */
	public static double getSimilarity(ProteinChain chain1, ProteinChain chain2, ESM_Model model) {
		try {
			SequenceAligner.align(chain1, chain2);
		} catch (ResidueAlignmentException e) {
			error(e.getMessage());
			return Double.POSITIVE_INFINITY;
		}
		
		//verify chain1 has the encoding assigned
		for(BioMolecule aa: chain1) {
			if(isValid(aa) && aa.getEncoding(model) == null) {
				FacebookESM.assignESM(chain1, model);
			}
		}
		
		//verify chain2 has the encoding assigned
		for(BioMolecule aa: chain2) {
			if(isValid(aa) && aa.getEncoding(model) == null) {
				FacebookESM.assignESM(chain2, model);
			}
		}
		
		//LabeledList<Double> rmsds = new LabeledList<Double>("Per-Amino RMSDs");
		double super_sum = 0;
		
		for(int index = 0; index < chain1.actualSize(); ++index) {
			ChainObject a1 = chain1.get(index + chain1.startsAt());
			ChainObject a2 = chain2.get(index + chain2.startsAt());
			if(!isValid(a1) || !isValid(a2)) { continue; }
			super_sum += getRMSD(a1, a2, model);
		}
		
		super_sum /= chain1.actualSize();
		return super_sum;
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		test1();
	}
	
	private static void test1() {
		PythonScript.setPythonPath("/Users/bns/miniconda3/bin/python");
		
		ProteinChain test5BTR_A    = ChainFactory.makeRCSB("5BTR", "A", "GS_RDNLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVPRCPRCPADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV");
		ProteinChain test5BTR_B    = ChainFactory.makeRCSB("5BTR", "B", "GSRDNLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVPRCPRCPADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV");

		ProteinChain test5BTR_mod1 = ChainFactory.makeRCSB("5BTR", "A+", "GSSDNLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVPRCPRCPADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV");
		ProteinChain test5BTR_mod2 = ChainFactory.makeRCSB("5BTR", "A-", "GSSDNLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVSSSSSSSADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV");
		ProteinChain test5BTR_mod3 = ChainFactory.makeRCSB("5BTR", "A*", "GSSDLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVSSSSSSSADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV");
		
		FacebookESM.assignESM(test5BTR_A,    ESM_Model.esm1b_t33_650M_UR50S, true);
		FacebookESM.assignESM(test5BTR_B,    ESM_Model.esm1b_t33_650M_UR50S, true);
		FacebookESM.assignESM(test5BTR_mod1, ESM_Model.esm1b_t33_650M_UR50S, true);
		FacebookESM.assignESM(test5BTR_mod2, ESM_Model.esm1b_t33_650M_UR50S, true);
		FacebookESM.assignESM(test5BTR_mod3, ESM_Model.esm1b_t33_650M_UR50S, true);
		
		double val = getSimilarity(test5BTR_A, test5BTR_B, ESM_Model.esm1b_t33_650M_UR50S);
		qp(val);
		val = BlastP.getEValue(test5BTR_A, test5BTR_B);
		qp(val + "\n");
		
		val = getSimilarity(test5BTR_A, test5BTR_mod1, ESM_Model.esm1b_t33_650M_UR50S);
		qp(val);
		val = BlastP.getEValue(test5BTR_A, test5BTR_mod1);
		qp(val + "\n");
		
		val = getSimilarity(test5BTR_A, test5BTR_mod2, ESM_Model.esm1b_t33_650M_UR50S);
		qp(val);
		val = BlastP.getEValue(test5BTR_A, test5BTR_mod2);
		qp(val + "\n");
		
		val = getSimilarity(test5BTR_A, test5BTR_mod3, ESM_Model.esm1b_t33_650M_UR50S);
		qp(val);
		val = BlastP.getEValue(test5BTR_A, test5BTR_mod3);
		qp(val + "\n");
		
		val = getSimilarity(test5BTR_mod2, test5BTR_mod3, ESM_Model.esm1b_t33_650M_UR50S);
		qp(val);
		val = BlastP.getEValue(test5BTR_mod2, test5BTR_mod3);
		qp(val + "\n");
		
		/* Output:
		 * 0.0
		 * 0.00449766105721142
		 * 0.05066393042670531
		 * 0.05060179082589574
		 * 0.007196017385096361
		 */
		System.exit(0);
	}
}
