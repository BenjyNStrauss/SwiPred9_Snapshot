package modules.encode.swipred;

import java.io.File;
import java.util.Arrays;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import install.DirectoryManager;
import modules.encode.tokens.AminoToken;
import utilities.LocalToolBase;

/**
 * Matrix encoder for converting AminoToken arrays to matrices (for use in a transformer)
 * @author Benjamin Strauss
 *
 */

public class MatrixEncoder extends LabeledHash<AminoToken, double[]> {
	private static final long serialVersionUID = 1L;
	private static final String DIVIDER = ":";
	private final File infile;
	private int valuesLen;
	
	/**
	 * 
	 * @param infile: name of file to read the matrix encoding from
	 */
	public MatrixEncoder(String infile) { this(new File(infile)); }
	
	/**
	 * 
	 * @param infile: file to read the matrix encoding from
	 */
	public MatrixEncoder(File infile) {
		super(AminoToken.values().length, 1);
		this.infile = infile;
		this.label = "Naive Encoder using file: " + infile.getPath();
		
		valuesLen = -1;
		
		String[] lines = LocalToolBase.getFileLines(infile);
		for(String line: lines) {
			String[] parts = line.split(DIVIDER);
			
			AminoToken token = AminoToken.parse(parts[0]);
			parts[1] = parts[1].trim();
			parts[1] = parts[1].substring(1, parts[1].length()-1);
			parts[1] = parts[1].replaceAll("\\s+", "");
			String[] values = parts[1].split(",");
			double[] array = new double[values.length];
			if(valuesLen == -1) { valuesLen = values.length; }
			if(valuesLen != values.length) { 
				throw new InvalidEncodingException("Encoding lengths are not consistent.");
			}
			for(int index = 0; index < values.length; ++index) {
				try {
					array[index] = Double.parseDouble(values[index]);
				} catch (NumberFormatException NFE) {
					throw new InvalidEncodingException("Not a number: " + values[index] +
							"\n\tfor token \""+token+"\" position #"+index);
				}
			}
			
			put(token, array);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String encodingFile() { return infile.getPath(); }
	
	/**
	 * 
	 * @param tokens
	 * @return
	 */
	public double[][] encode(AminoToken[] tokens) {
		double[][] matrix = new double[tokens.length][];
		for(int index = 0; index < tokens.length; ++index) {
			matrix[index] = get(tokens[index]);
		}
		return matrix;
	}
	
	public int getInputDimension() {
		for(AminoToken key: keySet()) {
			return get(key).length;
		}
		return -1;
	}
	
	/**
	 * 
	 * @param name
	 * @param embeddingLen
	 */
	public static void generateNaive(String name, int embeddingLen) {
		DirectoryManager.verifyFolder(DirectoryManager.FILES_ENCODE);
		DirectoryManager.verifyFolder(DirectoryManager.FILES_ENCODE_NAIVE);
		
		LabeledList<String> random_encodings = new LabeledList<String>();
		for(AminoToken at: AminoToken.values()) {
			double[] generated = new double[embeddingLen];
			for(int ii = 0; ii < embeddingLen; ++ii) {
				generated[ii] = Math.random();
			}
			random_encodings.add(at+DIVIDER+Arrays.toString(generated));
		}
		LocalToolBase.writeFileLines(DirectoryManager.FILES_ENCODE_NAIVE+"/"+name, random_encodings);
	}
	
	public static void main(String[] args) {
		generateNaive("attempt-01.txt", 1024);
		/*double[][] matrix = new double[5][5];
		for(int index = 0; index < 5; ++index) {
			matrix[index] = new double[]{5,5,5,5,5};
		}
		
		LocalToolBase.qp(Arrays.toString(matrix)); // << doesn't work!
		qp("-------");
		LocalToolBase.qp(matrix);*/
	}

}
