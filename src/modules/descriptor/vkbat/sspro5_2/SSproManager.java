package modules.descriptor.vkbat.sspro5_2;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import assist.exceptions.NotYetImplementedError;
import assist.script.Script;
import assist.script.UnixShellException;
import biology.amino.UnknownResidueException;
import biology.protein.ChainID;
import dev.setup.dev.SSproDebugModule;
import install.DirectoryManager;
import system.SwiPred;
import tools.Lookup;
import utilities.LocalToolBase;
import utilities.exceptions.LookupException;

/**
 * To Run SSpro, you can:
 * 	(0) use SSproManager.sspro("sequence") where the parameter is the amino acid sequence
 *  (1) make an SSproManager object using the chain's id.  Then run sspro() to use the appropriate fasta
 * 
 * @author Benjy Strauss
 * files/tools/blast-2.2.26/bin/blastpgp -d files/predict/sspro/uniref50/uniref50 -i files/blasts/sspro-blast.txt -o files/blasts/sspro-blast.aln -C files/blasts/sspro-blast.chk -Q files/blasts/sspro-blast.mat -j 3 -a 1 -e 0.001 -h 1e-10
 */

public class SSproManager extends SSproBase {
	private static final String BLAST_PGP_PATH = DirectoryManager.FILES_TOOLS_BLAST_BIN + "/blastpgp";
	public static final String BLAST_DB = DirectoryManager.FILES_PREDICT_URIREF50 + "/uniref50";
	
	//what we call the blast file when we don't know the PDB ID
	private static final String STATIC_BLAST_FILE = BLASTS_DIRECTORY + "/fasta"+LocalToolBase.systemID+".txt";
	private static final String ALN_FILE = BLASTS_DIRECTORY+"/fasta"+LocalToolBase.systemID+".aln";
	private static final String CHK_FILE = BLASTS_DIRECTORY+"/fasta"+LocalToolBase.systemID+".chk";
	private static final String MAT_FILE = BLASTS_DIRECTORY+"/fasta"+LocalToolBase.systemID+".mat";
	
	private static final DecimalFormat PROFILE_FORMAT = new DecimalFormat("0.00");
	
	/*
	 * From the blastpgp source code:
	 * 
	 * -d = Database
	 * -i = Query File (not needed if restarting from scoremat)
	 * -o = Output File for Alignment
	 * -C = Output File for PSI-BLAST Checkpointing
	 * -Q = Output File for PSI-BLAST Matrix in ASCII
	 * -j = Maximum number of passes to use in  multipass version
	 * -a = Number of processors to use
	 * -e = Expectation value (E)
	 * -h = e-value threshold for inclusion in multipass model
	 */
	private static final String[] BLAST_ARGS_TEMPLATE = { BLAST_PGP_PATH, "-d", BLAST_DB, "-i", null, "-o", null,
			"-C", null, "-Q", null, "-j", "3", "-a", "1", "-e", "0.001", "-h", "1e-10"};
	
	private static final String SSPRO_BLAST_SCRIPT = BLAST_PGP_PATH + " -d "+ BLAST_DB +" -i "+
			STATIC_BLAST_FILE+" -o "+ALN_FILE+" -C "+CHK_FILE+" -Q "+MAT_FILE+" -j 3 -a "+
			numberOfCores() + " -e 0.001 -h 1e-10";
	
	public ChainID pdb_id;
	
	public SSproManager(ChainID pdb_id) {
		this.pdb_id = pdb_id;
	}
	
	/**
	 * 
	 * @return
	 * @throws LookupException 
	 */
	public String sspro() throws LookupException {
		
		String source;
		try {
			source = Lookup.getFastaPath(pdb_id);
		} catch (NotYetImplementedError NYIE) {
			LocalToolBase.error("Fasta Path Lookup does not work yet from " + SwiPred.getShell().fastaSrc() + ".");
			source = getPathFromUser();
		}
		
		File fasta = new File(source);
		if(!fasta.exists() || fasta.isDirectory()) {
			LocalToolBase.error("Fasta was not found at expected location:");
			source = getPathFromUser();
		}
		
		cp(source, BLASTS_DIRECTORY +"/"+pdb_id+".txt");
		
		blast();
		String proFile = generateProfileFile(BLASTS_DIRECTORY+"/"+pdb_id+".mat");
		String pred = SSpro.profilesToSSAb(proFile);
		return pred;
	}
	
	private static String getPathFromUser() {
		qp("Please enter FULL path to FASTA");
		Scanner input = new Scanner(System.in);
		String location = input.next();
		File loc = new File(location);
		if(!loc.exists()) {
			qp("Error: could not find file "+location+"\nDid you enter the FULL path?");
		}
		input.close();
		return location;
	}
	
	
	
	
	/**
	 * Runs SSpro on a protein chain
	 * @param seq: the chain's sequence
	 * @return: SSpro 5.2 secondary structure predictions
	 */
	public static String sspro(String seq) {
		try {
			blast(seq);
		} catch (UnixShellException USE) {
			//did we have a full error (false) or just a warning (true)?
			boolean seqOK = printBlastErrors(USE, seq);
			if(!seqOK) {
				return null;
			}
		}
		
		String proFile = generateProfileFile(MAT_FILE);
		SSproDebugModule.record("proFile = " + proFile);
		String pred = SSpro.profilesToSSAb(proFile);
		return pred;
	}

	/**
	 * Runs blast
	 * 
	 */
	private void blast() {
		String args[] = BLAST_ARGS_TEMPLATE;
		args[4] = BLASTS_DIRECTORY+"/"+pdb_id+".txt"; //>p0\nfasta //thread_0.p0.fa
		args[6] = BLASTS_DIRECTORY+"/"+pdb_id+".aln"; //"thread_0.p0.aln";
		args[8] = BLASTS_DIRECTORY+"/"+pdb_id+".chk"; //"thread_0.p0.chk";
		args[10] = BLASTS_DIRECTORY+"/"+pdb_id+".mat"; //"thread_0.p0.mat";
		args[14] = ""+LocalToolBase.getNumberOfCores();
		//number_of_cores
		//args[19] = ">";
		//args[20] = "/dev/null";
		//args[21] = "2>";
		//args[22] = "/dev/null";
		
		fireBlast(args);
	}
	
	/**
	 * Run blast
	 * @param seq: the sequence to run blast on
	 */
	private static void blast(String seq) {
		String fileLines[] = new String[2];
		fileLines[0] = ">p0";
		fileLines[1] = seq;
		writeFileLines(STATIC_BLAST_FILE, (Object[]) fileLines);
		
		String args[] = BLAST_ARGS_TEMPLATE;
		args[4] = STATIC_BLAST_FILE; //>p0\nfasta //thread_0.p0.fa
		args[6] = ALN_FILE; //"thread_0.p0.aln";
		args[8] = CHK_FILE; //"thread_0.p0.chk";
		args[10] = MAT_FILE; //"thread_0.p0.mat";
		args[14] = ""+LocalToolBase.getNumberOfCores();
		
		File alnFile = new File(args[6]);
		if(alnFile.exists()) { alnFile.delete(); }
		
		//args[19] = ">";
		//args[20] = "/dev/null";
		//args[21] = "2>";
		//args[22] = "/dev/null";
		
		fireBlast(args);
	}
	
	/**
	 * Runs blast
	 * @param args
	 */
	private static void fireBlast(String[] args) {
		for(String arg: args) {
			Objects.requireNonNull(arg, "Null argument in SSpro blast call!");
		}
		
		//if we are in debug mode
		if(SSproDebugModule.DEBUG) {
			if(!SSproDebugModule.SKIP_BLAST) {
				SSproDebugModule.record("***Blast Starting!***");
				Script.runScript(args);
				SSproDebugModule.record("***Blast Complete!***");
			} else {
				SSproDebugModule.record("***Skipping Blast!***");
			}
			
			if(SSproDebugModule.ONLY_BLAST) { System.exit(0); }
		} else {
			Script.runScript(args);
		}
	}
	
	private static String generateProfileFile(String filename) {
		String outFileName = filename.substring(0, filename.length()-4) + ".pro";
		//qp(filename);
		String lines[] = getFileLines(filename);
		ArrayList<String> profileLines = new ArrayList<String>();
		profileLines.add(">p0");
		
		StringBuilder lineBuilder = new StringBuilder();
		
		//start on the third line!
		for(int i = 3; i < lines.length-6; ++i) {
			lineBuilder.setLength(0);
			double vals[] = new double[20];
			String profile[] = new String[20];
			
			String[] raw = lines[i].split("\\s+");
			char res = raw[2].charAt(0);
			double sum=0;
			
			for(int j=0; j < 20; j++){
				vals[j] = Double.parseDouble(raw[j+23]);
				sum += vals[j];
			}

			// Normalize & Print Sequence Profile
			if(sum == 0){
				if(res == 'X'){
					for(int j=0; j < 20; j++){
						profile[j] = "5";
					}
				} else{
					vals[getResIndex(res)] = 100;
				}
				sum = 100;
			}
			
			for(int j=0; j<20; j++){
				if(vals[j] != 0) {
					profile[j] = PROFILE_FORMAT.format(vals[j]/sum);
				} else {
					profile[j] = "0";
				}
			}
			
			lineBuilder.append(res+ " " + join(" ", profile));
			profileLines.add(lineBuilder.toString());
		}
		writeFileLines(outFileName, profileLines);
		return outFileName;
	}
	
	/**
	 * 
	 * @param USE
	 */
	private static boolean printBlastErrors(UnixShellException USE, String sequence) {
		String error = USE.getMessage();
		if(error.length() > Script.COMMAND_LINE_ERROR.length()) {
			 error = error.substring(Script.COMMAND_LINE_ERROR.length());
		}
		
		error = error.replaceAll("\n\n", "\n");
		//qp("Remove Debug Code");
		//qp(USE.getMessage());
		
		if(USE.getMessage().contains(BlastAssist.posPurgeMatches)) {
			qerr("SSpro warning on sequence: " + sequence);
			qerr(error);
			return true;
		} else if (USE.getMessage().contains(BlastAssist.seleno)) {
			qerr("SSpro warning on sequence: " + sequence);
			qerr(error);
			return true;
		} else if(USE.getMessage().toLowerCase().contains("warning")) {
			qerr("SSpro warning on sequence: " + sequence);
			qerr(error);
			return true;
		}else {
			qerr("SSpro error on sequence: " + sequence);
			qerr(error);
			qerr("Please make sure:");
			qerr("0:\t\"" + SSPRO_BLAST_SCRIPT + "\" works from the program directory");
			qerr("1:\tThe system can run 32-bit executables");
			return false;
		}
	}
	
	/**
	 * Changes the number of cores to 1 or 4
	 * @return
	 */
	private static int numberOfCores() {
		int cores = LocalToolBase.getNumberOfCores();
		if(cores >= 4) { cores = 4; } else { cores = 1; }
		return cores;
	}
	
	/**
	 * 
	 * @param res
	 * @return
	 */
	private static int getResIndex(char res) {
		switch(res) {
		case 'A': return 0;
		case 'R': return 1;
		case 'N': return 2;
		case 'D': return 3;
		case 'C': return 4;
		case 'Q': return 5;
		case 'E': return 6;
		case 'G': return 7;
		case 'H': return 8;
		case 'I': return 9;
		case 'L': return 10;
		case 'K': return 11;
		case 'M': return 12;
		case 'F': return 13;
		case 'P': return 14;
		case 'S': return 15;
		case 'T': return 16;
		case 'W': return 17;
		case 'Y': return 18;
		case 'V': return 19;
		default: throw new UnknownResidueException("" + res);
		}
	}
}
