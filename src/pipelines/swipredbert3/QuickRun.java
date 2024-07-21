package pipelines.swipredbert3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import biology.protein.ChainID;
import biology.protein.ProteinChain;
import tools.reader.fasta.SequenceReader;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * Look for "Protein Taxon" : "protein	taxon:9755"
 * 
 * UniProtKB	P02185	MB	enables	GO:0005344	GO_REF:0000024	ISS	UniProtKB:P02144	F	Myoglobin	MB	protein	taxon:9755	20231027	UniProt		
 * 
 * @author Benjamin Strauss
 * 
 */

public class QuickRun extends CommonTools {
	private static final String GO_FILE = "input/swipredbert/base-files/goa_uniprot_all.gaf";
	private static final String PAGE_5 = "input/swipredbert/nr/stage5.txt";
	private static final String PDB_GO_FILE = "input/swipredbert/relevant_go.txt";
	
	public static void main(String[] args) throws Exception {
		final String nr_unp = "input/SwipredBERT/nr/s8token-unp.txt";
		final String wr_unp = "input/SwipredBERT/wr/s8token-unp.txt";
		
		qp(getFileLines(nr_unp).length);
		qp(getFileLines(wr_unp).length);
	}
	
	public static void read_stage5() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(PAGE_5));
		
		while(reader.ready()) {
			String[] sections = reader.readLine().split(":");
			if(!sections[3].equals("null")) {
				qp(sections);
			}
		}
		
		reader.close();
	}
	
	public static void re_process() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(PDB_GO_FILE));
		
		for(int i = 0; i < 100; ++i) {
			qp(reader.readLine().replaceAll("\t", " , "));
		}
		
		reader.close();
	}
	
	private static final void readGAF() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(GO_FILE));
		
		for(int i = 0; i < 100; ++i) {
			qp(reader.readLine().replaceAll("\t", " , "));
		}
		
		reader.close();
	}
	
}
