package tools.writer.fasta;

import java.io.File;

import biology.protein.AminoChain;
import biology.protein.MultiChain;
import biology.protein.ProteinChain;
import modules.UserInputModule;
import system.Instruction;
import tools.DataSource;
import utilities.LocalToolBase;

/**
 * Module to write Fastas
 * 
 * @author Benjamin Strauss
 *
 */

public final class FastaWriter extends LocalToolBase {
	private static FastaWizard wizard = new FastaWizard();
	
	private FastaWriter() { }
	
	/**
	 * 
	 * @param outfile
	 * @param header
	 * @param sequence
	 */
	public static void write(String outfile, String sequence) {
		String[] lines = new String[2];
		lines[0] = ">Untitled Protein";
		lines[1] = sequence;
		writeFileLines(outfile, lines);
	}
	
	/**
	 * 
	 * @param outfile
	 * @param header
	 * @param sequence
	 */
	public static void write(String outfile, String header, String sequence) {
		String[] lines = new String[2];
		lines[0] = header;
		lines[1] = sequence;
		writeFileLines(outfile, lines);
	}
	
	/**
	 * 
	 * @param outfile
	 * @param chain
	 * @param includeBlanks
	 */
	public static synchronized void write(File outfile, ProteinChain chain) {
		StringBuilder headerBuilder = new StringBuilder(">");
		
		String sequence = chain.toSequence();
		
		if(chain.id().protein() != null) {
			headerBuilder.append(chain.id().protein());
			if(chain.id().chain() != null) {
				headerBuilder.append("_"+chain.id().chain());
			}
			headerBuilder.append("|");
			if(chain.id().uniprot() != null) {
				headerBuilder.append(chain.id().uniprot() + "|");
			}
		} else if(chain.id().uniprot() != null) {
			headerBuilder.append(chain.id().uniprot() + "|");
		} else {
			headerBuilder.append("Unknown Protein|");
		}
		
		if(chain.id().giNumber() != null) {
			headerBuilder.append("GI: " + chain.id().giNumber() + "|");
		}
		
		headerBuilder.append("Source = " + chain.getMetaData().source() + "\n");
		headerBuilder.append(sequence);
		writeFileLines(outfile, sequence);
	}
	
	/**
	 * 
	 * @param outfile
	 * @param chain
	 * @param includeBlanks
	 */
	public static synchronized void write(String outfile, MultiChain chain) {
		write(new File(outfile), chain);
	}
	
	/**
	 * 
	 * @param outfile
	 * @param chain
	 * @param includeBlanks
	 */
	public static synchronized void write(File outfile, MultiChain chain) {
		StringBuilder headerBuilder = new StringBuilder(">");
		
		String sequence = chain.toSequence();
		
		if(chain.id().protein() != null) {
			headerBuilder.append(chain.id().protein());
			if(chain.id().chain() != null) {
				headerBuilder.append("_"+chain.id().chain());
			}
			headerBuilder.append("|");
			if(chain.id().uniprot() != null) {
				headerBuilder.append(chain.id().uniprot() + "|");
			}
		} else if(chain.id().uniprot() != null) {
			headerBuilder.append(chain.id().uniprot() + "|");
		} else {
			headerBuilder.append("Unknown Protein|");
		}
		
		if(chain.id().giNumber() != null) {
			headerBuilder.append("GI: " + chain.id().giNumber() + "|");
		}
		
		headerBuilder.append("Source = " + chain.getMetaData().source() + "\n");
		headerBuilder.append(sequence);
		writeFileLines(outfile, sequence);
	}
	
	/**
	 * 
	 * @param outfile
	 * @param chain
	 * @param includeBlanks
	 */
	public static synchronized void write(String outfile, AminoChain<?> chain) {
		write(new File(outfile), chain);
	}
	
	/**
	 * 
	 * @param outfile
	 * @param chain
	 * @param includeBlanks
	 */
	public static synchronized void write(File outfile, AminoChain<?> chain) {
		StringBuilder headerBuilder = new StringBuilder(">");
		
		String sequence = chain.toSequence();
		
		if(chain.id().protein() != null) {
			headerBuilder.append(chain.id().protein());
			if(chain.id().chain() != null) {
				headerBuilder.append("_"+chain.id().chain());
			}
			headerBuilder.append("|");
			if(chain.id().uniprot() != null) {
				headerBuilder.append(chain.id().uniprot() + "|");
			}
		} else if(chain.id().uniprot() != null) {
			headerBuilder.append(chain.id().uniprot() + "|");
		} else {
			headerBuilder.append("Unknown Protein|");
		}
		
		if(chain.id().giNumber() != null) {
			headerBuilder.append("GI: " + chain.id().giNumber() + "|");
		}
		
		headerBuilder.append("Source = " + chain.getMetaData().source() + "\n");
		headerBuilder.append(sequence);
		writeFileLines(outfile, headerBuilder.toString());
	}
	
	/**
	 * create a new FASTA from the SwiPred Shell
	 * @param instr
	 */
	public static void newFASTA(Instruction instr) {
		String saveName = instr.getFirstArgumentNamed(true, "-save", "-file");
		if(saveName == null) {
			saveName = UserInputModule.getStringFromUser("Please enter file name (no extension!)");
		}
		
		String header = instr.getFirstArgumentNamed(true, "-header", "-headr");
		if(header == null) {
			header = UserInputModule.getStringFromUser("Please enter FASTA header:");
		}
		
		String sequence = instr.getFirstArgumentNamed(true, "-seq", "-sequence");
		if(sequence == null) {
			sequence = UserInputModule.getStringFromUser("Please enter FASTA sequence:");
		}
		
		/*String rawSource = instr.getArgumentNamed(true, "-src", "-fastasrc");
		if(rawSource == null) {
			rawSource = UserInputModule.getStringFromUser("Please enter FASTA source:");
		}
		
		DataSource src = DataSource.parse(rawSource);*/
		write(saveName, header, sequence);
	}
	
	/**
	 * TODO: DOES NOT WORK!
	 * create a new FASTA with a GUI wizard
	 */
	public synchronized static void newFASTA_GUI() {
		makeFasta(wizard.getSaveName(), wizard.getHeader(), wizard.getSequence(), wizard.getSrc());
	}
	
	/**
	 * 
	 * @param saveName
	 * @param header
	 * @param sequence
	 * @param src
	 */
	private static void makeFasta(String saveName, String header, String sequence, DataSource src) {
		if(!saveName.endsWith(TXT) && !saveName.endsWith(".fasta")) { saveName += TXT; }
		File saveThisAs = new File(src.fastaFolderPref()+saveName);
		StringBuilder fastaBuilder = new StringBuilder();
		fastaBuilder.append(header + "\n" + sequence);
		
		if(saveThisAs.exists()) {
			boolean choice = UserInputModule.getBooleanFromUser("File " + saveName + " already exists.  Replace it?");
			if(!choice) { return; }
		}
		
		writeFileLines(saveThisAs, fastaBuilder);
	}
}
