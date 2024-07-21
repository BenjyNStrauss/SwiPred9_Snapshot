package biology.tools;

import assist.script.PythonScript;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import biology.protein.ChainFactory;
import biology.protein.ProteinChain;
import modules.encode.esm.ESM_Compare;
import modules.encode.esm.ESM_Model;
import modules.encode.esm.FacebookESM;
import utilities.LocalToolBase;

/**
 * Designed to align sequences with NN embeddings instead of by residue
 * @author Benjamin Strauss
 *
 */

public class SequenceEmbeddingAligner extends LocalToolBase {
	
	/**
	 * Aligns 2 chains pairwise based on esm encodings
	 * @param chain1
	 * @param chain2
	 * @param model
	 * @param gapWeight
	 */
	public static void alignPairwise(AminoChain<?> chain1, AminoChain<?> chain2, ESM_Model model, double gapWeight) {		
		double[][] matrix = getRMSD_Matrix(chain1, chain2, model, gapWeight);
		//Assist.printAlignedMatrix(matrix);
		
		int[][] path = new int[matrix.length][matrix[0].length];
		
		/*
		 * climb through the matrix, looking for the smallest option
		 * matrix [y][x]
		 * y: chain1, x: chain2
		 */
		int x = matrix[0].length-1;
		int y = matrix.length-1;
		path[y][x] = 1;
		
		while(x > 0 && y > 0) {
			double v1 = matrix[y-1][x-1];
			double v2 = matrix[y][x-1];
			double v3 = matrix[y-1][x];
			if(v1 == min(v1, v2, v3)) {
				path[y-1][x-1] = 1;
				--x;
				--y;
			} else if(v2 == min(v1, v2, v3)) {
				path[y][x-1] = 1;
				--x;
			} else if(v3 == min(v1, v2, v3)) {
				path[y-1][x] = 1;
				--y;
			}
		}
		
		//Assist.printAlignedMatrix(path);
		
		String aln_seq1 = extractAlignmentFromMatrix(path, chain1, true);
		String aln_seq2 = extractAlignmentFromMatrix(path, chain2, false);
		
		SequenceAligner.trimCommonBlanks(aln_seq1, aln_seq2);
		
		char[] aln1 = aln_seq1.toCharArray();
		char[] aln2 = aln_seq2.toCharArray();
		int minLen = min(aln1.length, aln2.length);
		
		qp(new String(aln1));
		qp(new String(aln2));
		
		for(int index = 0; index < minLen; ++index){
			if(aln1[index] == '_' && aln2[index] == '_') {
				aln1[index] = '•';
				aln2[index] = '•';
			}
		}
		
		aln_seq1 = new String(aln1).replaceAll("•", "");
		aln_seq2 = new String(aln2).replaceAll("•", "");
		
		SequenceAligner.modifyToMatchSequence(chain1, aln_seq1);
		SequenceAligner.modifyToMatchSequence(chain2, aln_seq2);
	}
	
	private static String extractAlignmentFromMatrix(int[][] path, AminoChain<?> chain, boolean isVertical) {
		int x = 0; //path[0].length-1
		int y = 0; //path.length-1
		
		while(x < path[0].length-1 && path[y][x] != 1) {
			++x;
		}
		
		if(path[y][x] != 1) {
			x = 0;
		}
		
		while(y < path.length-1 && path[y][x] != 1) {
			++y;
		}
		
		String originalSeq = "_"+FastaCrafter.textSequenceForVkbat(chain);
		StringBuilder alignedSeqBuilder = new StringBuilder();
		
		//Assist.printAlignedMatrix(path);
		
		if(isVertical) {
			while(y < path.length-1) {
				if(path[y+1][x+1] == 1) {
					alignedSeqBuilder.append(originalSeq.charAt(y+1));
					++x;
					++y;
				} else if(path[y][x+1] == 1) {
					alignedSeqBuilder.append('_');
					++x;
				} else if(path[y+1][x] == 1) {
					++y;
				} else {
					/*qp(x + "," + y);
					qp(path[y-1][x-1] + "," + path[y-1][x] + "," + path[y-1][x+1]);
					qp(path[y][x-1] + "," + path[y][x] + "," + path[y][x+1]);
					qp(path[y+1][x-1] + "," + path[y+1][x] + "," + path[y+1][x+1]);*/
					
					throw new InternalError();
				}
			}
		} else {
			while(x < path[0].length-1) {
				if(path[y+1][x+1] == 1) {
					alignedSeqBuilder.append(originalSeq.charAt(x+1));
					++x;
					++y;
				} else if(path[y][x+1] == 1) {
					++x;
				} else if(path[y+1][x] == 1) {
					alignedSeqBuilder.append('_');
					++y;
				} else {
					throw new InternalError();
				}
			}
		}
		
		return alignedSeqBuilder.toString();
	}

	/**
	 * 
	 * @param chain1
	 * @param chain2
	 * @param model
	 * @return
	 */
	private static double[][] getRMSD_Matrix(AminoChain<?> chain1, AminoChain<?> chain2, ESM_Model model, double gap) {
		String seq1 = FastaCrafter.textSequenceForVkbat(chain1);
		String seq2 = FastaCrafter.textSequenceForVkbat(chain2);
		double[][] matrix = new double[seq1.length()+1][seq2.length()+1];
		
		gap = -Math.abs(gap);
		matrix[0][0] = gap;
		
		for(int ii = 1; ii < matrix[0].length; ++ii) {
			matrix[0][ii] = gap + matrix[0][ii-1];
		}
		
		for(int ii = 1; ii < matrix.length; ++ii) {
			matrix[ii][0] = gap + matrix[ii-1][0];
		}
		
		for(int index1 = chain1.startsAt(); index1 < chain1.size(); ++index1) {
			//ensure the residue isn't null (or virtually null)
			if(!isValid(chain1.get(index1))) { continue; }
			
			for(int index2 = chain2.startsAt(); index2 < chain2.size(); ++index2) {
				//ensure the residue isn't null (or virtually null)
				if(!isValid(chain2.get(index2))) { continue; }
				
				matrix[index1 - chain1.startsAt()+1][index2 - chain2.startsAt()+1] = 
						ESM_Compare.getRMSD(chain1.get(index1), chain2.get(index2), model);
			}
		}
		
		return matrix;
	}
	
	@SuppressWarnings("unused")
	private static void test1() {
		PythonScript.setPythonPath("/Users/bns/miniconda3/bin/python");
		
		ProteinChain test5BTR_A    = ChainFactory.makeRCSB("5BTR", "A", "GSRDNLLFGDE");
		ProteinChain test5BTR_B    = ChainFactory.makeRCSB("5BTR", "B", "GSRDNLFGFE");
		
		FacebookESM.assignESM(test5BTR_A, ESM_Model.esm1b_t33_650M_UR50S, true);
		FacebookESM.assignESM(test5BTR_B, ESM_Model.esm1b_t33_650M_UR50S, true);
		
		alignPairwise(test5BTR_A, test5BTR_B, ESM_Model.esm1b_t33_650M_UR50S, -1);
	}
	
	@SuppressWarnings("unused")
	private static void test2() {
		PythonScript.setPythonPath("/Users/bns/miniconda3/bin/python");
		
		ProteinChain test4JJX_A    = ChainFactory.makeRCSB("4JJX", "A", "SNAMNSQLTLRASYWFEEPYESFDELEELYNKHIHDNAER");
		ProteinChain test4JJX_B    = ChainFactory.makeRCSB("4JJX", "B", "SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAER");
		
		FacebookESM.assignESM(test4JJX_A, ESM_Model.esm1b_t33_650M_UR50S, true);
		FacebookESM.assignESM(test4JJX_B, ESM_Model.esm1b_t33_650M_UR50S, true);
		
		alignPairwise(test4JJX_A, test4JJX_B, ESM_Model.esm1b_t33_650M_UR50S, -1);
		
		qp(""+test4JJX_A);
		qp(""+test4JJX_B);
	}
	
	private static void test3() {
		PythonScript.setPythonPath("/Users/bns/miniconda3/bin/python");
		
		ProteinChain test4JJX_A    = ChainFactory.makeRCSB("2HJH", "A", "EDPLAKKQTVRLIKDLQRAINKVLCTRLRLSNFFTIDHFIQKLHTARKILVLTGAGVSTSLGIPDFRSSEGFYSKIKHLGLDDPQDVFNYNIFMHDPSVFYNIANMVLPPEKIYSPLHSFIKMLQMKGKLLRNYTQNIDNLESYAGISTDKLVQCHGSFATATCVTCHWNLPGERIFNKIRNLELPLCPYCYKKRREYFPEGYNNKVGVAASQGSMSERPPYILNSYGVLKPDITFFGEALPNKFHKSIREDILECDLLICIGTSLKVAPVSEIVNMVPSHVPQVLINRDPVKHAEFDLSLLGYCDDIAAMVAQKCGWTIPHKKWNDLKNKNFKCQEKDKGVYVVTSDEHPKTL");
		ProteinChain test4JJX_B    = ChainFactory.makeRCSB("2QQQ", "A", "MDIKELHVKTVKRGENVTMECSMSKVTNKNNLAWYRQSFGKVPQYFVRYYSSNSGYKFAEGFKDSRFSMTVNDQKFDLNIIGAREDDGGEYFCGEVEGIIIKFTSGTRLQF");
		
		FacebookESM.assignESM(test4JJX_A, ESM_Model.esm1b_t33_650M_UR50S, true);
		FacebookESM.assignESM(test4JJX_B, ESM_Model.esm1b_t33_650M_UR50S, true);
		
		alignPairwise(test4JJX_A, test4JJX_B, ESM_Model.esm1b_t33_650M_UR50S, -1);
		
		qp(""+test4JJX_A);
		qp(""+test4JJX_B);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		test3();
		System.exit(0);
	}
}
