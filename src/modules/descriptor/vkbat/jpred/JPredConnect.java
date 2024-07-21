package modules.descriptor.vkbat.jpred;

import assist.base.Assist;
import assist.script.Script;
import assist.util.LabeledList;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import install.DirectoryManager;
import utilities.LocalToolBase;

/**
 * Under development
 * @author Benjamin Strauss
 *
 */

public class JPredConnect extends LocalToolBase {
	private static final int JPRED_MIN_SEQ_LENGTH = 20;
	private static final int JPRED_MAX_SEQ_LENGTH = 800;
	//private static final int JPRED_SEQ_SPLIT_LENGTH = 760;
	
	private final static int DEFAULT_MAX_TRIES = 90;
	//90 seconds
	private final static long DEFAULT_WAIT_INTERVAL = 9000;
	
	private static final String PERL = "perl";
	private static final String JPREDAPI = DirectoryManager.FILES_PREDICT_JPRED + "/jpredapi";
	private static final String SUBMIT = "submit";
	private static final String MODE = "mode=single";
	private static final String FORMAT = "format=raw";
	private static final String SEQ = "seq=";
	private static final String STATUS = "status";
	private static final String GET_RESULTS = "getResults=no";
	private static final String SILENT = "silent";
	
	private static int max_tries = DEFAULT_MAX_TRIES;
	private static long wait_interval = DEFAULT_WAIT_INTERVAL;
	
	/** @param maxTries = max tries to attempt submitting a job before giving up */
	public static final void setMaxTries(int maxTries) { max_tries = maxTries; }
	/** @param millis = milliseconds to wait between attempts */
	public static final void setWaitInterval(long millis) { wait_interval = millis; }
	
	public static final int maxTries() { return max_tries; }
	public static final long waitInterval() { return wait_interval; }
	
    /**
     * Used for testing only!
     * >>jp_IuKuLv2
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	ChainID id = new ChainID();
    	id.setProtein("4JJX");
    	id.setChain("A");
    	ProteinChain _4jjx = ChainFactory.makeRCSB(id, "SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRALDYSFTILNLHKIYLHVAVENPKAVHLYEECGFVEEGHLVEEFFINGRYQDVKRMYILQSKYLNRSE");
    	String pred = runJpred(_4jjx, false);
    	qp(pred);
    	System.exit(0);
    }
	
    public static String runJpred(AminoChain<?> chain, boolean override)
    		throws JPredException, JPredJobIDNotFoundException {
    	//don't worry if it's too big – we split up the sequence
    	if(chain.length() < JPRED_MIN_SEQ_LENGTH) {
    		throw new JPredException("Sequence too short: min 20 residues!");
    	}
    	
    	String seq = SEQ + FastaCrafter.textSequenceForVkbat(chain);
    	qp("Getting JPred for: " + chain.id().mostUseful());
    	if(seq.length() <= JPRED_MAX_SEQ_LENGTH) { 
    		return callJpred(seq, override);		
    	}
    	
    	LabeledList<String> jPredQueue = new LabeledList<String>();
    	StringBuilder resultBuilder = new StringBuilder();
    	
    	//split the chain into a number of roughly equal segments
    	if(seq.length() > JPRED_MAX_SEQ_LENGTH) {
    		int segments = 2;
    		while(seq.length() / segments > JPRED_MAX_SEQ_LENGTH-2) {
    			++segments;
    		}
    		
    		int segment_size = seq.length()/segments;
    		
    		while(seq.length() > segment_size) {
        		String temp = seq.substring(0,segment_size+1);
        		seq = seq.substring(segment_size+1);
        		jPredQueue.add(temp);
        	}
        	jPredQueue.add(seq);
        	for(String subseq: jPredQueue) {
        		resultBuilder.append(callJpred(subseq, override));
        	}
    	} else {
    		resultBuilder.append(callJpred(seq, override));
    	}
    	
    	return resultBuilder.toString();
    }
    
	/**
	 * 
	 * @param chain
	 * @param override
	 * @return
	 * @throws JPredJobIDNotFoundException 
	 */
	public static String callJpred(String seq, boolean override) throws JPredJobIDNotFoundException {
		Script jpredCall = new Script(PERL, JPREDAPI, SUBMIT, MODE, FORMAT, seq);
		Script.runScript(jpredCall);
		
		String[] submission_output = jpredCall.getStdOut().split("\n");
		
		String jobid;
		try {
			jobid = submission_output[7];
		} catch (ArrayIndexOutOfBoundsException AIOOBE) {
			throw new JPredJobIDNotFoundException(submission_output);
		}
		jobid = jobid.substring(30);
		//qp("jobid: " + jobid); //--> this works
		
		String jobidParam = "jobid="+jobid;
		String checkEvery = "checkEvery" + wait_interval;
		
		Script jpredCheck = new Script(PERL, JPREDAPI, STATUS, jobidParam, GET_RESULTS, checkEvery, SILENT);
		Script.runScript(jpredCheck);
		String[] status_output = jpredCheck.getStdOut().split("\n");
		String url = "";
		for(String str: status_output) {
			if(str.startsWith("http://www.compbio.dundee.ac.uk/")) {
				url = str;
			}
		}
		
		url = url.replaceAll("\\.results\\.", "\\.simple\\.");
		
		//qp(url);
		Script jpredCurl = new Script("curl", url);
		Script.runScript(jpredCurl);
		
		String[] url_output = jpredCurl.getStdOut().split("\n");
		
		String prediction;
		try {
			prediction = url_output[6];
		} catch (ArrayIndexOutOfBoundsException AIOOBE) {
			qerr("Error: Jpred Prediction Not found: url="+url_output);
			throw new JPredPredictionNotFoundException(jpredCurl.getStdOut());
		}
		prediction = Assist.removeHTML(prediction);
		prediction = prediction.replaceAll("-", "C");
		return prediction;
	}
	
	public static void handleJobIDNotFound(JPredJobIDNotFoundException e) {
		error("JPred Error: Job ID Not Found");
		error("If the problem persists, please contact " + BMAIL);
		error("And include the following for debugging purposes");
		error("----------------------------------------------------------------");
		error(e);
		e.printStackTrace();
		error("----------------------------------------------------------------\n");
	}

}
