package pipelines.functpred;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import utilities.LocalToolBase;

/**
 * @ScriptNo 1
 * 
 * @purpose extract a random subset of goa_uniprot_all.gaf that's easier for my computer to handle
 * 				of ~20m-25m proteins, down from 1,377,959,095
 * 
 * @author Benjamin Strauss
 * 
 */

public class GOSubset extends LocalToolBase {
	private static final String GAF_PATH_OR = "input/goa_uniprot_all.gaf";
	
	/**
	 * 
	 * @param infile
	 * @param outfile
	 * @param desiredSampleSize
	 * @throws IOException
	 */
	public static final void make_subset(String infile, String outfile, double desiredSampleSize) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(infile));
		PrintWriter writer = new PrintWriter(outfile);
		
		int written = 0;
		
		final double chance = desiredSampleSize/numUsableLines(infile);
		
		String str;
		while(reader.ready()) {
			str = reader.readLine();
			
			if(!str.split("\t")[0].equals("UniProtKB")) {
				continue;
			}
			
			double randomselect = Math.random();
			if(randomselect < chance) {
				writer.write(str+"\n");
				++written;
			}
			
			if(written >= desiredSampleSize) {
				writer.close();
				reader.close();
				break;
			}
		}
		
		qp("Created Sample Size of " + written);
		writer.close();
		reader.close();
	}
	
	public static final int numUsableLines(String infile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(infile));
		int lines = 0;
		
		while(reader.ready()) {
			String str = reader.readLine();
			if(str.split("\t")[0].equals("UniProtKB")) {
				++lines;
			}
		}
		
		reader.close();
		return lines;
	}
	
	public static void main(String[] args) throws Exception {
		final String outfile = "input/goa_uniprot_random_sample_5.txt";
		final int sample_size = 24000000;
		make_subset(GAF_PATH_OR, outfile, sample_size);
	}

}
