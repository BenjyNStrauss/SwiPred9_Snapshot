package modules.descriptor.vkbat.sympred;

import java.io.File;

import assist.base.Assist;
import assist.script.Script;
import assist.util.LabeledList;
import assist.util.Pair;
import biology.descriptor.VKPred;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import install.DirectoryManager;
import modules.descriptor.vkbat.exceptions.server.NotSupportedBySympredException;
import utilities.LocalToolBase;

/**
 * Class is used to connect to SymPred Server and obtain secondary structure prediction data
 * Algorithm run in this way are:
 * PHDpsi, PROFsec, SSPRO_2, PREDATOR_SP, YASPIN, JNET, PSIPred, SYMPRED_CONSENSUS
 * Sympred process sequences of up to 1023 amino acid residues, not more
 * 
 * @author ras (Benjamin's Father)
 * @editor Benjamin Strauss
 * 
 */

public class SymPred2 extends LocalToolBase {
	private static final int SYMPRED_MIN_INPUT_LENGTH = 6;
	private static final int SYMPRED_MAX_INPUT_LENGTH = 900;
	
	//default wait interval is 20 seconds (20,000 ms)
	private static final int DEFAULT_MAX_TRIES = 40;
	private static final long DEFAULT_WAIT_INTERVAL = 20000;
	
	private static final int JOB_DEFAULT_MAX_TRIES = 120;
	private static final long JOB_DEFAULT_WAIT_INTERVAL = 30000;
	
	//private static final String JOB_FINISHED = "<table><tr><td>You have run SYMPRED using the Dynamic Programming strategy with no weighting.<br><br>";
	
	private static int max_tries = DEFAULT_MAX_TRIES;
	private static long wait_interval = DEFAULT_WAIT_INTERVAL;
	private static int job_max_tries = JOB_DEFAULT_MAX_TRIES;
	private static long job_wait_interval = JOB_DEFAULT_WAIT_INTERVAL;
	
    final static String EMAIL_ADDR = "";  // we use an empty string for no email

    //Used for testing only!
	final static String FASTA_FILE =
            ">tr|Q6DNF2|Q6DNF2_9CYAN CurA OS=Lyngbya majuscula OX=158786 GN=curA PE=1 SV=1\n"
            + "MLELINRYEHGFVYIPVILACREKGLFELIKKKRITHRQIANTLGANTGNLQVALTMMES\n"
            + "LGWLSKNEADEYSLKDNCQAYLSIPEEILYLYNLPIESYLLGKEKPGIIKNWIELSQQQW\n";
    
	/** @param maxTries = max tries to attempt submitting a job before giving up */
	public static final void setMaxTries(int maxTries) { max_tries = maxTries; }
	/** @param millis = milliseconds to wait between attempts */
	public static final void setWaitInterval(long millis) { wait_interval = millis; }
	/** @param maxTries = max tries to attempt getting a result before giving up */
	public static final void setJobMaxTries(int maxTries) { job_max_tries = maxTries; }
	/** @param millis = milliseconds to wait between attempts */
	public static final void setJobWaitInterval(long millis) { job_wait_interval = millis; }
	
	public static final int maxTries() { return max_tries; }
	public static final long waitInterval() { return wait_interval; }
	public static final int jobMaxTries() { return job_max_tries; }
	public static final long jobWaitInterval() { return job_wait_interval; }
	
	public static boolean disableWaitingMessages = false;
	
    /**
     * 
     * @param chain
     * @param algorithm
     * @param override
     * @return
     * @throws SympredTimeoutException
     */
    public static String runSympred(AminoChain<?> chain, VKPred algorithm) throws SympredTimeoutException {
    	if(chain.length() < SYMPRED_MIN_INPUT_LENGTH) { return sympredFail(chain.toSequence()); }
    	
    	String signature = chain.id().mostUseful() + "-" + chain.getMetaData().source();
    	
    	return runSympred(FastaCrafter.textSequenceForVkbat(chain), algorithm, signature);
    	
    }
    
    public static String runSympred(String sequence, VKPred algorithm) throws SympredTimeoutException {
    	return runSympred(sequence, algorithm, "simpred-query");
    }
    
    private static String runSympred(String sequence, VKPred algorithm, String signature) throws SympredTimeoutException {
    	if(sequence.length() < SYMPRED_MIN_INPUT_LENGTH) { return sympredFail(sequence); }
    	sequence = ">" +signature + "\n" + sequence;
    	
    	String rawOutFileName = DirectoryManager.FILES_PREDICT_SYMPRED + "/" + signature + "-raw.txt";
    	rawOutFileName = rawOutFileName.replaceAll(":", "-");
		String outFileName = DirectoryManager.FILES_PREDICT_SYMPRED + "/"+signature+".txt";
		outFileName = outFileName.replaceAll(":", "-");
		File rawOut = new File(rawOutFileName);
		File out = new File(outFileName);
		
		//qp("Getting SymPred for: " + sequence);
		
		if(sequence.length() < SYMPRED_MAX_INPUT_LENGTH) {
			callSympred(sequence, rawOut, out);
		} else {
			LabeledList<String> sympredQueue = new LabeledList<String>();
	    	StringBuilder resultBuilder = new StringBuilder();
	    	
	    	while(sequence.length() > SYMPRED_MAX_INPUT_LENGTH) {
	    		String temp = sequence.substring(0,SYMPRED_MAX_INPUT_LENGTH);
	    		sequence = sequence.substring(SYMPRED_MAX_INPUT_LENGTH);
	    		sympredQueue.add(temp);
	    	}
	    	
	    	if(sequence.length() < SYMPRED_MIN_INPUT_LENGTH) {
	    		String lastFull = sympredQueue.remove(sympredQueue.size()-1);
	    		sequence += lastFull.substring(SYMPRED_MAX_INPUT_LENGTH-SYMPRED_MIN_INPUT_LENGTH);
	    		lastFull = lastFull.substring(0, SYMPRED_MAX_INPUT_LENGTH-SYMPRED_MIN_INPUT_LENGTH);
	    		sympredQueue.add(lastFull);
	    	}
	    	
	    	sympredQueue.add(sequence);
	    	
	    	for(int index = 0; index < sympredQueue.size(); ++index) {
	    		
	    		String _rawOutFileName = DirectoryManager.FILES_PREDICT_SYMPRED + "/" + signature;
	    		_rawOutFileName = _rawOutFileName + "-" + (index+1) + "of" + sympredQueue.size() + "-raw.txt";
	    		_rawOutFileName = _rawOutFileName.replaceAll(":", "-");
	        	
	    		String _outFileName = DirectoryManager.FILES_PREDICT_SYMPRED + "/" + signature;
	    		_outFileName = _outFileName + "-" + (index+1) + "of" + sympredQueue.size() + ".txt";
	    		_outFileName = _outFileName.replaceAll(":", "-");
	    		
	    		File _rawOut = new File(_rawOutFileName);
	    		File _out = new File(_outFileName);
	    		
	    		callSympred(sequence, _rawOut, _out);
	    		
	    		String[] rawLines = getFileLines(_rawOut);
	    		for(String line: rawLines) {
	    			resultBuilder.append(line + "\n");
	    		}
	    	}
	    	
	    	writeFileLines(rawOut, resultBuilder.toString());
		}
		
		refineSympredOutput(rawOut, out);
        
        return getSeq(out, algorithm);
    }
	
	/**
     * 
     * @param chain
     * @return
     * @throws SympredTimeoutException 
     */
	private static void callSympred(String sequence, File rawOut, File out) throws SympredTimeoutException {
		final SymPredInput input = new SymPredInput(">SymPred Query\n"+sequence);
        final SymPredSyncSubmit sympred = new SymPredSyncSubmit();
        
        Pair<HttpStatusCode, String> meta = null;
    	for (int tryno = 0; tryno < max_tries; tryno++) {
    		try {
    			meta = sympred.submitJob(input);
    	    	break;
    		} catch (Exception e) {
    			error("Sympred call failed for seq: " + sequence);
    			error("Trying again in: " + wait_interval + "ms");
    			//e.printStackTrace();
    		}
    		pause(wait_interval);
			if(tryno == job_max_tries-1) {
				throw new SympredTimeoutException("Could not submit Sympred job for sequence="+ sequence + "!");
			}
    	}
    	
    	if(meta == null) {
    		throw new SympredTimeoutException("Could not get results of Sympred job for sequence="+ sequence + "!");
    	}
        final HttpStatusCode code = meta.x;

        if (HttpStatusCode.FOUND.equals(code)) {  // expected
            qp("Job submitted: "+input.getJobName()+": "+code.value+"; "+sympred.returnMessage);
        } else {
            String s = sympred.returnMessage;
            if (s.length() > 1000) {
                s = s.substring(0,1000) + "... " + s.length() + " bytes total";
            }
            error("Job submission failed: "+input.getJobName()+" status: "+code+"; "+sympred.returnMessage);
        }
        final String result = sympred.returnMessage;
        
        final String jobUrl = fetchUrl(meta.y);
        if (jobUrl == null) {
            qerr("No URL was found in the returned message: "+result);
        }
        
        for(int tryno = 0; tryno < job_max_tries; tryno++) {
        	Script sympredDownload = new Script("curl", "-o", rawOut.getPath(), jobUrl);
			Script.runScript(sympredDownload);
			
			String[] lines = getFileLines(rawOut);
			if(lines.length > 230) {
				break;
			}
			
			if(!disableWaitingMessages) {
				qp("Waiting on Sympred job: " + tryno + " * " + job_wait_interval + "ms");
			}
    		pause(job_wait_interval);
			if(tryno == job_max_tries-1) {
				throw new SympredTimeoutException("Waiting timeout: Could not get results of Sympred job for sequence="+ sequence + "!");
			}
    	}
	}
    
	/**
	 * 
	 * @param inFile
	 * @param outFile
	 */
    private static void refineSympredOutput(File inFile, File outFile) {
    	String[] lines = getFileLines(inFile);
    	StringBuilder phd = new StringBuilder();
    	StringBuilder prof = new StringBuilder();
    	StringBuilder sspro = new StringBuilder();
    	StringBuilder yaspin = new StringBuilder();
    	StringBuilder jnet = new StringBuilder();
    	StringBuilder psipred = new StringBuilder();
    	StringBuilder predator = new StringBuilder();
    	StringBuilder sympred = new StringBuilder();
    	
    	for(String line: lines) {
    		if(line.startsWith("<a href='result")) { continue; }
    		line = line.toLowerCase();
    		line = Assist.removeHTML(line);
    		line = line.replaceAll("&nbsp;", "c");
    		
    		if(line.startsWith("phd")) {
    			phd.append(line.substring(3));
    		} else if(line.startsWith("prof")) {
    			prof.append(line.substring(4));
    		} else if(line.startsWith("sspro")) {
    			sspro.append(line.substring(5));
    		} else if(line.startsWith("yaspin")) {
    			yaspin.append(line.substring(6));
    		} else if(line.startsWith("jnet")) {
    			jnet.append(line.substring(4));
    		} else if(line.startsWith("psipred")) {
    			psipred.append(line.substring(7));
    		} else if(line.startsWith("predator")) {
    			predator.append(line.substring(8));
    		} else if(line.startsWith("sympred") && (!line.contains(" "))) {
    			sympred.append(line.substring(7));
    		}
    	}
    	
    	String[] outLines = new String[8];
    	outLines[0] = "PHD--" + phd.toString();
    	outLines[1] = "PROF--" + prof.toString();
    	outLines[2] = "SSPRO--" + sspro.toString();
    	outLines[3] = "PREDATOR--" + predator.toString();
    	outLines[4] = "YASPIN--" + yaspin.toString();
    	outLines[5] = "JNET--" + jnet.toString();
    	outLines[6] = "PSIPRED--" + psipred.toString();
    	outLines[7] = "SYMPRED--" + sympred.toString();
    	
    	
    	qp("outFile: " + outFile.getPath());
    	writeFileLines(outFile, (Object[]) outLines);
    }
    
    private static String fetchUrl(String result) {
        final int ix = result.indexOf("http");
        if (ix < 0) {
            qerr("No url starting with 'http' was found in return message: "+result);
        }
        final String urlStart = result.substring(ix-1);
        final char quote = urlStart.charAt(0);
        if ((quote != '"') && (quote != '\'')) {
            qerr("Char before 'http' wasn't a quote: "+urlStart);
        }
        final int endIx = urlStart.indexOf(quote, 1);
        if (endIx < 0) {
        	qerr("Quote char ("+quote+") wasn't found at URL end: "+urlStart);
        }
        final String url = urlStart.substring(1, endIx);
        //qp("Results page url was found in the return message: \n"+url);
        return url;
    }
    
    private static String sympredFail(String sequence) {
    	StringBuilder builder = new StringBuilder();
		for(@SuppressWarnings("unused") char aa: sequence.toCharArray()) {
			builder.append('.');
		}
		return builder.toString();
    }
    
	private static String getSeq(File out, VKPred algorithm) {
		String[] lines = getFileLines(out);
    	switch(algorithm) {
		case PHDpsi:				return lines[0].substring(5);
		case PROFsec:				return lines[1].substring(6);
		case SSPRO_2:				return lines[2].substring(7);
		case PREDATOR_SP:			return lines[3].substring(10);
		case YASPIN:				return lines[4].substring(8);
		case JNET:					return lines[5].substring(6);
		case PSIPred:				return lines[6].substring(9);
		case SYMPRED_CONSENSUS:		return lines[7].substring(9);
		default:					throw new NotSupportedBySympredException();
    	}
	}
    
    /**
     * Used for testing only!
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	ChainID id = new ChainID();
    	id.setProtein("2REE");
    	id.setChain("A");
    	id.setUniprot("Q6DNF2");
    	ProteinChain chain = ChainFactory.makeUniprot(id, "MLELINRYEHGFVYIPVILACREKGLFELIKKKRITHRQIANTLGANTGNLQVALTMMES\n"
    			+ "LGWLSKNEADEYSLKDNCQAYLSIPEEILYLYNLPIESYLLGKEKPGIIKNWIELSQQQW\n"
    			+ "NINDRTIADLLDGVLIIPIMLGLHKHNLLAVDEDKPLFYNVTPHIREELSRLFIAKGWAD\n"
    			+ "KSLIKVQKHSQKSNKNKNYLCLTDIGKCLGQRTIIKSISLNCFENNYYNLRHPKIEDLRD\n"
    			+ "LIALETLCWSENLQVDNEEIYRRIFKIPQGQFILELEDKIVGAIYSQRIDNPQLLDNKTC\n"
    			+ "TQVPLLHTESGVVVQLLAVNILPELQNQGLGDRLLEFMLQYCAQISGVEKVVAVTLCRNY\n"
    			+ "PDYSPMPMAEYIHQKNESGLLVDPLLRFHQIHGAKIEKLLPGYRPKDWENQTCGVLVSYD\n"
    			+ "IQHRQRFDGATVEKNRQTKVKLTEDIDEVVASCVRKVMHQKKSFDAQRALMEMGIESLEL\n"
    			+ "LELRYLLQKKLGVEIDPNLFFKYGTAAAIASYFKGETTERSGEVSEVLPKPEAIKTDSGF\n"
    			+ "STAQLENGVAIIGMACRFPGDADSPQQYWSLLHDGIDGITEVPPTRWDIEQYYHPEKNQP\n"
    			+ "GKIANRYGGFLTEVDKFEPDFFRISAREALYMDPQQRLLLEEHWKALEDAGINPESLSGT\n"
    			+ "ETGIFVGIAFHDYERLQDKYYQEQDLNIYFATGSSTAIGAGRLSYFFQLNGPSITVDTAC\n"
    			+ "SSSLSAVHLACQSIRNGECQLALASGVNLLLSPELSISFSQAGMLSPDGRCKTFDAAANG\n"
    			+ "YVRSEGCGVVVLKSLKQAIADNDRILAVVRGTAINQDGASNGLTAPNQSAQEAVLKRALS\n"
    			+ "VAGVSANQISYVEAHGTGTSLGDPVEIKAIEAVYGKDRSADRPLMIGSVKTNIGHTEAAA\n"
    			+ "GIAGLIKVVLSLQNQYIPPHLHWKQLNPYISLAGIPGVIPTEGKVWEQYEGDETRVAALS\n"
    			+ "SFGFSGTNSHAIVEEAPEGRSQEPGARSQEPGARSQEPPYLLTVSAKKEQALKELVSSYQ\n"
    			+ "HHLETNPELELADVCYTANTGRADFNHRLAIIATNPQQLTNKLRQYQAGEEESGVFSGQL\n"
    			+ "SHSSSPAKVAFLFTGQGSQYVQMGRQLYQTQPVFRQVLDQCDELLRPYLERPLLEVLYPQ\n"
    			+ "DTPNSNSYLLDQTAYTQPTLFALEYALCKLWESWGIKPQVVMGHSVGEYVAATIAGVLSL\n"
    			+ "EDGLKLIALRGRLMQQLPAGGEMVSVMASKSQVKDAIANHTKQVTIAAINGPESVVISGE\n"
    			+ "AGAIQAIVTKLESKLIKTKQLQVSHAFHSPLMTPMLAEFAAVAQQITYHQPRIPVISNVT\n"
    			+ "GTIADKSIATADYWVEHVVKPVRFVAGIKTLAEQDIRIFLEIGPKPVLLVMGRECLIGSK\n"
    			+ "KIWLPSLRPGKPDWLQMLQSLGQLYVQGVKVDWLGFYPDDAPQKVVLPTYPWQRKRYWIS\n"
    			+ "DLQQYKNKGKNGKVAPQVASDNNQSKNSVVTNNSSGSKMLPGIKESEQPRIKEREQPRIK\n"
    			+ "ESEQDARTTINIQKSDAPDIEQPQRRLSNPASLSLSKTTVAKQQPAKRELAQLRVPLTQV\n"
    			+ "EPAQITQQYLESEKITQTLGKALHQDILINQKTTKEPKTMNREQVEQLKQEYEEKGYCQI\n"
    			+ "KKIFDFSAIKTIQKTLDQAKQESQISKEKVTLKLGGIDDIDTNDHAYDLVKYDFVSSFIQ\n"
    			+ "EKLALLNYITGKNLMIMHNALFSVEPNHKGLPWHVGVGSFSFTKTEDFGASIWIPLDKIT\n"
    			+ "KEHRGGMQYVSTKIFPGQFYYSVFDLHLKNNIKWDESQGDLNEYVANANTIYNKITEDVI\n"
    			+ "DYTIKDGYEEDEYNLGDAFFFNKYVLHQSVPLKPGLHKLRRAFVIRLVDYDTRVDEERLG\n"
    			+ "LFSKYSQLHSRYYKTLPRYNKDSVLVMVSRAVQKGLKSPYLRDIPHVQQTLAARMAAGAI\n"
    			+ "SFEETPSISSAPQTQQPLKTLQPLRTPQVNQVNLSEIKQVLKQQLAEALYTEESEIAEDQ\n"
    			+ "KFVDLGLDSIVGVEWTTTINQTYNLNLKATKLYDYPTLLELSGYIAQILSSQGTKPISSS\n"
    			+ "SQTQQSLKTLQPLPTPQVNQVNLSEIKQVLKQQLAEALYTEESEIAEDQKFVDLGLDSIV\n"
    			+ "GVEWTTTINQTYNLNLKATKLYDYPTLLELAAYIAQTLASQGTKPQVSQQPLKTLQPLPQ\n"
    			+ "PQVNLSEIKQVLKQQLAEALYTEESEIAEDQKFVDLGLDSIVGVEWTTTINQTYNLNLKA\n"
    			+ "TKLYDYPTLLELAPYIAQEIAATGGSKLFQGNGNGGHPQESLPNNGSPVNSSEEMDPKLR\n"
    			+ "LRAILNKVAKKELTIQEANKLVQQIKKQVTV");
    	
    	String pred = runSympred(chain, VKPred.JNET);
    	qp(pred);
    	qp(chain.length());
    	qp(pred.length());
    	
    	System.exit(0);
    }
}
