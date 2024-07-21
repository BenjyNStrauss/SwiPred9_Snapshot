package modules.blast;

import assist.script.Script;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import install.DirectoryManager;
import tools.writer.fasta.FastaWriter;
import utilities.LocalToolBase;

/**
 * Interface for using BlastP
 * @author Benjamin Strauss
 *
 */

public final class BlastP extends LocalToolBase {
	private static final String[] BLAST_SCRIPT = { 
			DirectoryManager.FILES_TOOLS_BLAST_PLUS_BIN+"/blastp", "-query", "", "-subject",
			"", "-out", DirectoryManager.FILES_TMP + "/blastp-out.txt" };
	
	private BlastP() { }
	
	/**
	 * 
	 * @param chain1
	 * @param chain2
	 * @return
	 */
	public static synchronized int getBitScore(AminoChain<?> chain1, AminoChain<?> chain2) {
		runBlastP(chain1, chain2);
		
		String[] lines = getFileLines(BLAST_SCRIPT[6]);
		int index = 0;
		for(; index < lines.length; ++index) {
			if(lines[index].startsWith("Sequences producing significant alignments:")) {
				break;
			}
		}
		
		String targetLine = lines[index+2];
		String bitscore = targetLine.substring(70).trim();
		return Integer.parseInt(bitscore);
	}
	
	/**
	 * 
	 * @param chain1
	 * @param chain2
	 * @return
	 */
	public static synchronized double getEValue(AminoChain<?> chain1, AminoChain<?> chain2) {
		runBlastP(chain1, chain2);
		
		String[] lines = getFileLines(BLAST_SCRIPT[6]);
		int index = 0;
		for(; index < lines.length; ++index) {
			if(lines[index].startsWith("Sequences producing significant alignments:")) {
				break;
			}
		}
		
		String targetLine = lines[index+2];
		String evalue = targetLine.substring(78).trim();
		return Double.parseDouble(evalue);
	}
	
	/**
	 * 
	 * @param chain1
	 * @param chain2
	 */
	private static synchronized void runBlastP(AminoChain<?> chain1, AminoChain<?> chain2) {
		String fasta1 = DirectoryManager.FILES_TMP + "/" + chain1.id().standard();
		String fasta2 = DirectoryManager.FILES_TMP + "/" + chain2.id().standard();
		deleteFile(BLAST_SCRIPT[6]);
		
		if(fasta1.equals(fasta2)) {
			fasta2 += "+";
		}
		
		boolean filter_gap = FastaCrafter.filter_gap;
		FastaCrafter.filter_gap = true;
		FastaWriter.write(fasta1, chain1);
		FastaWriter.write(fasta2, chain2);
		FastaCrafter.filter_gap = filter_gap;
		
		String[] blast_args = BLAST_SCRIPT;
		blast_args[2] = fasta1;
		blast_args[4] = fasta2;
		Script.runScript(blast_args);
		
		//give the blast time to complete
		while(!fileExists(BLAST_SCRIPT[6])) {
			//pause for 0.1 seconds?
			pause(100);
		}
	}
	
	public static void main(String[] args ) {
		qp("4ZZH_1|Chain A|NAD-dependent protein deacetylase sirtuin-1|Homo s...  ".length());
	}
}
