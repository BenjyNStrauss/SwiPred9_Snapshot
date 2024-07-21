package dev.inProgress.esmAligner;

import java.io.IOException;
import java.util.Objects;

import assist.translation.python.PythonTranslator;
import biology.descriptor.EncodingType;
import biology.protein.AminoChain;
import biology.tools.SequenceAligner;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class EmbeddingAligner extends PythonTranslator {
	private static final double DEFAULT_GAP_PENALTY = 0.445;
	
	private EmbeddingAligner() { }
	
	public static void align(AminoChain<?> seq1, AminoChain<?> seq2, EncodingType embeddingType) throws IOException {
		align(seq1, seq2, embeddingType, DEFAULT_GAP_PENALTY);
	}
	
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
	public static void align(AminoChain<?> seq1, AminoChain<?> seq2, EncodingType embeddingType, double gap_penalty) throws IOException {
		Objects.requireNonNull(seq1, "Cannot align a null chain - chain #1");
		Objects.requireNonNull(seq2, "Cannot align a null chain - chain #2");
		Objects.requireNonNull(embeddingType, "A type of embedding must be specified for this algorithm.");
		
		String sequence_1 = seq1.toSequence();
		String sequence_2 = seq2.toSequence();

		//Create Matrices
		double[][] main_matrix = new double[len(sequence_1)+1][len(sequence_2)+1];
		
		// redefinde this
		double[][] match_checker_matrix = makeMatchCheckerMatrix(seq1, seq2, sequence_1, sequence_2, embeddingType);
		

		
		//qp("\n");
		// Filling up the matrix using Needleman_Wunsch algorithm
		// STEP 1 : Initialisation
		for(int i = 0; i < len(sequence_1)+1; ++i) {
		    main_matrix[i][0] = i * gap_penalty;
		}
		for(int j = 0; j < len(sequence_2)+1; ++j) {
		    main_matrix[0][j] = j * gap_penalty;
		}	
		
		// STEP 2 : Matrix Filling
		for(int i = 1; i <= len(sequence_1); ++i) {
			for(int j = 1; j <= len(sequence_2); ++j) {
				double v1 = main_matrix[i-1][j-1] + match_checker_matrix[i-1][j-1];
				double v2 = main_matrix[i-1][j] + gap_penalty;
				double v3 = main_matrix[i][j-1] + gap_penalty;
		        main_matrix[i][j] = max(v1,v2,v3);
		    }
		}

		// STEP 3 : Traceback

		String aligned_1 = "";
		String aligned_2 = "";

		int ti = len(sequence_1);
		int tj = len(sequence_2);

		while(ti >0 && tj > 0) {

		    if (ti >0 && tj > 0 && main_matrix[ti][tj] == main_matrix[ti-1][tj-1]+ match_checker_matrix[ti-1][tj-1]) {

		        aligned_1 = sequence_1.charAt(ti-1) + aligned_1;
		        aligned_2 = sequence_2.charAt(tj-1) + aligned_2;

		        ti = ti - 1;
		        tj = tj - 1;
		    
			} else if(ti > 0 && main_matrix[ti][tj] == main_matrix[ti-1][tj] + gap_penalty) {
		        aligned_1 = sequence_1.charAt(ti-1) + aligned_1;
		        aligned_2 = "-" + aligned_2;

		        ti = ti -1;
			} else {
		        aligned_1 = "-" + aligned_1;
		        aligned_2 = sequence_2.charAt(tj-1) + aligned_2;

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
	static double[][] makeMatchCheckerMatrix(AminoChain<?> seq1, AminoChain<?> seq2,
			String sequence_1, String sequence_2, EncodingType embeddingType) {
		double[][] match_checker_matrix = new double[len(sequence_1)][len(sequence_2)];
		
		// Fill the match checker matrix according to match or mismatch
		for(int i = 0; i < len(sequence_1); ++i) {
		    for(int j = 0; j < len(sequence_2); ++j) {
		    	/*
		    	 * TODO: math is not being done correctly, or something...
		    	 * Note: make python pipe for "spatial.distance.cosine"
		    	 */
		    	
		    	double[] res1_embedding = seq1.get(i).getEncoding(embeddingType);
		    	double[] res2_embedding = seq2.get(j).getEncoding(embeddingType);
		    	
		    	//TODO: this pipeline is the bug?
		    	match_checker_matrix[i][j] = cosine(res1_embedding, res2_embedding);
		        //match_checker_matrix[i][j] = PyLibAccessModule.accessPyFunct("cosine", res1_embedding, res2_embedding);
		        //qp(match_checker_matrix[i][j]);
		        
		        //if(j == 3) { System.exit(0); }
		    }
		    qp("("+i+")");
		}
		
		/*for(double[] values: match_checker_matrix) {
			Assist.qpln(values);
		}*/
		
		return match_checker_matrix;
	}
	
	/**
	 * @author https://stackoverflow.com/questions/520241/how-do-i-calculate-the-cosine-similarity-of-two-vectors
	 * @param vectorA
	 * @param vectorB
	 * @return
	 */
	private static double cosine(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    } 
	    double val = (dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));
	    return val;
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
		return sum;
	}

	/**
	 * @author Bineet Kumar Mohanta - wrote original Python code
	 * @translator Benjamin Strauss - translated original code from Python to Java
	 * 
	 * @param seq1
	 * @param seq2
	 * @param embeddingType
	 */
	public static void align2(AminoChain<?> seq1, AminoChain<?> seq2) {
		//Create Matrices
		double[][] main_matrix = new double[len(seq1)+1][len(seq2)+1];
		double[][] match_checker_matrix = new double[len(seq1)][len(seq2)];

		// Providing the scores for match ,mismatch and gap
		int match_reward = 1;
		int mismatch_penalty = -1;
		int gap_penalty = -2;

		//Fill the match checker matrix accrording to match or mismatch
		for(int i = 0; i < len(seq1); ++i) {
		    for(int j = 0; j < len(seq2); ++j) {
		        if(seq1.get(i).residueType() == seq2.get(j).residueType()) {
		            match_checker_matrix[i][j] = match_reward;
		        } else {
		            match_checker_matrix[i][j] = mismatch_penalty;
		        }
		    }   
		}

		//print(match_checker_matrix)

		//Filling up the matrix using Needleman_Wunsch algorithm
		//STEP 1 : Initialisation
		for(int i = 0; i < len(seq1)+1; ++i) {
		    main_matrix[i][0] = i*gap_penalty;
		}
		for(int j = 0; j < len(seq2)+1; ++j) {
		    main_matrix[0][j] = j * gap_penalty;
		}

		//STEP 2 : Matrix Filling
		for (int i = 0; i < len(seq1)+1; ++i) {
		    for(int j = 0; j < len(seq2)+1; ++j) {
		        main_matrix[i][j] = max(main_matrix[i-1][j-1]+match_checker_matrix[i-1][j-1],
		                                main_matrix[i-1][j]+gap_penalty,
		                                main_matrix[i][j-1]+ gap_penalty);
		    }
		}

		//print(main_matrix)

		// STEP 3 : Traceback

		String aligned_1 = "";
		String aligned_2 = "";

		int ti = len(seq1);
		int tj = len(seq2);

		while(ti >0 && tj > 0) {

		    if (ti >0 && tj > 0 && main_matrix[ti][tj] == main_matrix[ti-1][tj-1]+ match_checker_matrix[ti-1][tj-1]) {

		        aligned_1 = seq1.get(ti-1).toChar() + aligned_1;
		        aligned_2 = seq2.get(tj-1).toChar() + aligned_2;

		        ti = ti - 1;
		        tj = tj - 1;
		    
		    } else if(ti > 0 && main_matrix[ti][tj] == main_matrix[ti-1][tj] + gap_penalty) {
		        aligned_1 = seq1.get(ti-1).toChar() + aligned_1;
		        aligned_2 = "-" + aligned_2;

		        ti = ti -1;
		    } else {
		        aligned_1 = "-" + aligned_1;
		        aligned_2 = seq2.get(tj-1).toChar() + aligned_2;

		        tj = tj - 1    ; 
		    }
		}
		SequenceAligner.modifyToMatchSequence(seq1, aligned_1);
		SequenceAligner.modifyToMatchSequence(seq2, aligned_2);
	}
}
