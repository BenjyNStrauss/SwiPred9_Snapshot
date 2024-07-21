package tools.download.blast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import assist.script.PythonScript;
import assist.script.PythonScriptException;
import biology.protein.AminoChain;
import install.DirectoryManager;
import install.PythonScriptManager;
import modules.UserInputModule;
import modules.descriptor.entropy.EntropyRetrievalException;
import system.SwiPred;
import utilities.LocalToolBase;

/**
 * Downloads BLASTs from NCBI
 * 
 * Python route works, direct route does not
 * @author Benjamin Strauss
 *
 */

public class BLAST_NCBI extends LocalToolBase {
	public static final String NCBI_BLAST_URL = "https://blast.ncbi.nlm.nih.gov/Blast.cgi";
	private static final int NCBI_BLAST_DOWNLOAD_TRIES = 3;
	
	//The python script to download NCBI blasts
	private static final File E6_FILE = new File(PythonScriptManager.NCBI_BLAST_FILENAME);
	
	private static final String RETRY_ENTROPY_PROMPT = "NCBI Blast failed "+ NCBI_BLAST_DOWNLOAD_TRIES + " times."
			+ "\nContinue trying to retrieve entropy?";
	
	/**
	 * TODO unfinished: supposed to download a BLAST without using the python script
	 * 
	 * Download a blast file from NCBI
	 * @param chain: the chain to download the blast for
	 * @param override: if true, download a new blast file even if the old one is already present
	 * @return name of new NCBI blast file
	 * @throws EntropyRetrievalException 
	 */
	static String downloadBlastNCBI(String purpose, AminoChain<?> chain, boolean override) throws EntropyRetrievalException {
		String seq = chain.toSequence();
		StringBuilder outfilenameBuilder = new StringBuilder();
		outfilenameBuilder.append(DirectoryManager.FILES_BLASTS + "/" + purpose);
		outfilenameBuilder.append("_" + purpose);
		outfilenameBuilder.append(chain.getMetaData().source() + "_" + chain.id().uniqueSaveID() + TXT);
		
		File blastFile = new File(outfilenameBuilder.toString());
		boolean needToDownload = override || !blastFile.exists() || blastFile.isDirectory();
		
		qp(!blastFile.exists());
		qp(override);
		qp(blastFile.isDirectory());
		
		String paramSeq = spaceSequenceNCBI(seq);
		
		URL url;
		
		if(needToDownload) {
			 try {
				url = new URL(NCBI_BLAST_URL);
				final HttpURLConnection http = (HttpURLConnection) url.openConnection();
				
				//put request
				
				//MTHHYPTDDIKIKEVKELLPPIAHLYELPISKEASGLVHRTRQEISDLVHGRDKRLLVIIGPCSIHDPKAALEYAERLLKLRKQYENELLIVMRVYFEKPRTTVGWKGLINDPHLDGTFDINFGLRQARSLLLSLNNMGMPASTEFLDMITPQYYADLISWGAIGARTTESQVHRELASGLSCPVGFKNGTDGNLKIAIDAIGAASHSHHFLSVTKAGHSAIVHTGGNPDCHVILRGGKEPNYDAEHVSEAAEQLRAAGVTDKLMIDCSHANSRKDYTRQMEVAQDIAAQLEQDGGNIMGVMVESHLVEGRQDKPEVYGKSITDACIGWGATEELLALLAGANKKRMARAS

				
				//<textarea id="seq" class="reset" rows="5" cols="80" name="QUERY" suggesthint></textarea>
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		File outFile = new File(outfilenameBuilder.toString());
		if(outFile.exists()) {
			return outfilenameBuilder.toString();
		} else {
			throw new EntropyRetrievalException(chain);
		}
	}
	
	/**
	 * Adds "%250A" every 70 characters
	 * @return
	 */
	private static String spaceSequenceNCBI(String seq) {
		char[] seqArr = seq.toCharArray();
		StringBuilder builder = new StringBuilder();
		
		for(int ii = 0; ii < seqArr.length; ++ii) {
			if(ii % 70 == 0) { builder.append("%250A"); }
			builder.append(seqArr[ii]);
		}
		
		if(!builder.toString().endsWith("%250A")) {
			builder.append("%250A");
		}
		
		return builder.toString();
	}
	
	/**
	 * Download a blast file from NCBI
	 * @param chain: the chain to download the blast for
	 * @param override: if true, download a new blast file even if the old one is already present
	 * @return name of new NCBI blast file
	 * @throws EntropyRetrievalException 
	 */
	public static String downloadBlastNCBI_py(String purpose, AminoChain<?> chain, boolean override) throws EntropyRetrievalException {
		String seq = chain.toSequence();
		seq = seq.replaceAll("_", "");
		
		StringBuilder outfilenameBuilder = new StringBuilder();
		outfilenameBuilder.append(DirectoryManager.FILES_BLASTS + "/" + purpose);
		outfilenameBuilder.append("_" + chain.getMetaData().source());
		outfilenameBuilder.append("_" + chain.id().uniqueSaveID() + TXT);
		
		String outfilename = outfilenameBuilder.toString();
				
		//qp(outfilename);
		File blastFile = new File(outfilename);
		
		//NCBI_BLAST_DOWNLOAD_TRIES
		boolean needToDownload = override || !blastFile.exists() || blastFile.isDirectory();
		int tryNo = 0;
		
		/*qp("-D: "+blastFile.exists());
		qp("-B: "+blastFile.isDirectory());
		qp("-O: "+override);
		qp("-+: "+needToDownload);
		if(true) throw new RuntimeException();*/
		
		//qp(outfilename);
		//qp(seq);
		//download the blast
		while(needToDownload) {
			qpl("Downloading NCBI Blast for: " + chain.name());
			try {
				PythonScript.runPythonScript(E6_FILE, outfilename, seq);
				needToDownload = false;
			} catch (PythonScriptException PE) {
				error("Failed to download NCBI Blast for: " + chain.name());
				if(blastFile.exists()) {
					blastFile.delete();
				}
				error(PE.getMessage());
			}
			++tryNo;
			
			if(tryNo >= NCBI_BLAST_DOWNLOAD_TRIES) {
				if(!SwiPred.askUserForHelp) {
					boolean userContinue = UserInputModule.getBooleanFromUser(RETRY_ENTROPY_PROMPT);
					if(userContinue) {
						tryNo = 0;
					} else {
						throw new EntropyRetrievalException();
					}
				} else {
					throw new EntropyRetrievalException();
				}
			}
		}
		
		return outfilename;
	}
}
