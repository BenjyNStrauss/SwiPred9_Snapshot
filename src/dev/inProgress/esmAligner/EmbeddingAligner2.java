package dev.inProgress.esmAligner;

import java.io.IOException;
import java.util.Objects;

import assist.base.Assist;
import assist.translation.python.PythonTranslator;
import biology.descriptor.EncodingType;
import biology.protein.AminoChain;
import biology.tools.SequenceAligner;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class EmbeddingAligner2 extends PythonTranslator {
	private static final double CONSTANT = 16;
	
	private EmbeddingAligner2() { }
	
	/**
	 * @author Bineet Kumar Mohanta - wrote original Python code
	 * @modifier Mahdi Belcaid - modified original Python code
	 * @translator Benjamin Strauss - translated modified code from Python to Java
	 * 
	 * @param seq1
	 * @param seq2
	 * @param embeddingType
	 * @throws IOException 
	 */
	public static void align(AminoChain<?> seq1, AminoChain<?> seq2, EncodingType embeddingType) {
		Objects.requireNonNull(seq1, "Cannot align a null chain - chain #1");
		Objects.requireNonNull(seq2, "Cannot align a null chain - chain #2");
		Objects.requireNonNull(embeddingType, "A type of embedding must be specified for this algorithm.");
		for(int index = seq1.startsAt(); index < seq1.size(); ++index) {
			Objects.requireNonNull(seq1.get(index), "Error: null residue.");
			Objects.requireNonNull(seq1.get(index).getEncoding(embeddingType), "Error: null residue encoding.");
		}
		for(int index = seq2.startsAt(); index < seq2.size(); ++index) {
			Objects.requireNonNull(seq2.get(index), "Error: null residue.");
			Objects.requireNonNull(seq2.get(index).getEncoding(embeddingType), "Error: null residue encoding.");
		}

		//Create Matrices
		double[][] main_matrix = new double[seq1.actualSize()+1][seq2.actualSize()+1];
		
		// redefinde this
		double[][] match_checker_matrix = makeMatchCheckerMatrix(seq1, seq2, embeddingType);
		
		// Filling up the matrix using Needleman_Wunsch algorithm
		// STEP 1 : Initialisation
		double netPenalty = 0;
		for(int i = seq1.startsAt()+1; i < seq1.size()+1; ++i) {
			netPenalty += gap_penalty(seq1.get(i-1).getEncoding(embeddingType));
		    main_matrix[i-seq1.startsAt()][0] = netPenalty;
		}
		
		netPenalty = 0;
		for(int j = seq2.startsAt()+1; j < seq2.size()+1; ++j) {
			netPenalty += gap_penalty(seq2.get(j-1).getEncoding(embeddingType));
		    main_matrix[0][j-seq2.startsAt()] = netPenalty;
		}
		
		Assist.qp(main_matrix[0]);
		
		// STEP 2 : Matrix Filling
		for(int i = 1; i <= seq1.size(); ++i) {
			for(int j = 1; j <= seq2.size(); ++j) {
				double v1 = main_matrix[i-1][j-1] + match_checker_matrix[i-1][j-1];
				//TODO double check this...
				double v2 = main_matrix[i-1][j]   + gap_penalty(seq1.get(i-1).getEncoding(embeddingType));
				double v3 = main_matrix[i][j-1]   + gap_penalty(seq2.get(j-1).getEncoding(embeddingType));
		        main_matrix[i][j] = max(v1,v2,v3);
		    }
		}

		// STEP 3 : Traceback

		String aligned_1 = "";
		String aligned_2 = "";

		int ti = seq1.actualSize();
		int tj = seq2.actualSize();

		while(ti >0 && tj > 0) {

		    if (ti >0 && tj > 0 && main_matrix[ti][tj] == main_matrix[ti-1][tj-1]+ match_checker_matrix[ti-1][tj-1]) {

		        aligned_1 = seq1.charAt(ti-1) + aligned_1;
		        aligned_2 = seq2.charAt(tj-1) + aligned_2;

		        ti = ti - 1;
		        tj = tj - 1;
		    
			} else if(ti > 0 && main_matrix[ti][tj] == main_matrix[ti-1][tj] + gap_penalty(seq1.get(ti-1).getEncoding(embeddingType))) {
		        aligned_1 = seq1.charAt(ti-1) + aligned_1;
		        aligned_2 = "-" + aligned_2;

		        ti = ti - 1;
			} else {
		        aligned_1 = "-" + aligned_1;
		        aligned_2 = seq2.charAt(tj-1) + aligned_2;

		        tj = tj - 1;
			}
		}
		
		qp(aligned_1);
		qp(aligned_2);
		//test
		SequenceAligner.modifyToMatchSequence(seq1, aligned_1);
		SequenceAligner.modifyToMatchSequence(seq2, aligned_2);
	}
	
	/**
	 * 
	 * @param seq1
	 * @param seq2
	 * @param embeddingType
	 * @return
	 */
	static double[][] makeMatchCheckerMatrix(AminoChain<?> seq1, AminoChain<?> seq2, EncodingType embeddingType) {
		double[][] match_checker_matrix = new double[seq1.size()][seq2.size()];
		
		int offset1 = seq1.startsAt();
		int offset2 = seq2.startsAt();
		
		// Fill the match checker matrix according to match or mismatch
		for(int i = seq1.startsAt(); i < seq1.size(); ++i) {
		    for(int j = seq2.startsAt(); j < seq2.size(); ++j) {
		    	double[] res1_embedding = seq1.get(i).getEncoding(embeddingType);
		    	double[] res2_embedding = seq2.get(j).getEncoding(embeddingType);
		    	
		    	match_checker_matrix[i-offset1][j-offset2] = squared_error(res1_embedding, res2_embedding);
		    }
		}
		return match_checker_matrix;
	}
	
	private static double squared_error(double[] vectorA, double[] vectorB) {
		double sum = 0;
		for (int i = 0; i < vectorA.length; i++) {
			sum += Math.pow(vectorA[i] - vectorB[i], 2);
		}
		sum /= vectorA.length;
		sum = Math.pow(sum, 0.5);
		return sum;
	}
	
	private static double gap_penalty(double[] vectorA) {
		double sum = 0;
		for (int i = 0; i < vectorA.length; i++) {
			sum += Math.pow(vectorA[i], 2);
		}
		sum /= vectorA.length;
		sum = Math.pow(sum, 0.5);
		return sum/CONSTANT;
	}
}
