package dev.special.transform;

import java.util.ArrayList;

import biology.protein.ChainID;
import biology.protein.ProteinChain;
import tools.DataSource;
import tools.Lookup;
import tools.download.fasta.FastaDownloader;
import tools.reader.cluster.ClusterReader;
import tools.reader.fasta.SequenceReader;
import utilities.exceptions.DataRetrievalException;
import utilities.exceptions.LookupException;

/**
 * Used to aggregate sequences
 * @author Benjamin Strauss
 *
 * TODO: implement a classifier -> classifies into 2+ categories
 */

@SuppressWarnings("unused")
public class SeqAggUniprot extends Lookup {
	private static final String INFILE = "input/acetyl-sirt-2022.txt";
	private static final String OUTFILE = "acetyl-sirt-seq-2022.txt";

	public static void main(String[] args) {
		/*ChainID id = new ChainID();
		id.setProtein("6DAU");
		id.setChain("F");
		FastaDownloader.download(id, DataSource.UNIPROT);
		ProteinChain chain = SequenceReader.readChain_uniprot(id);*/
		aggregate(args);
	}
	
	private static void aggregate(String[] args) {
		String filename = (args.length > 0) ? args[0] : INFILE;
		String[][] chains = ClusterReader.readClustersTXT(filename);
		ArrayList<ProteinChain> protList = new ArrayList<ProteinChain>();
		for(String[] strArr: chains) {
			for(String str: strArr) {
				String[] tokens = str.split("[:_ ]");
				ChainID id = new ChainID();
				id.setProtein(tokens[0]);
				id.setChain(tokens[1]);
				ProteinChain chain = null;
				try {
					chain = SequenceReader.readChain_uniprot(id);
				} catch (DataRetrievalException | LookupException e) { }
				if(chain != null) {
					protList.add(chain);
				}
			}
		}
		boolean sirt = false;
		ArrayList<String> fileLines = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		for(ProteinChain chain: protList) {
			if(chain.id().uniprot().equals("Q9NXA8")) { sirt = true; }
			if(chain.actualSize() > 0) {
				sb.setLength(0);
				sb.append(chain.id().protein()+",");
				sb.append(chain.id().chain()+",");
				sb.append((sirt) ? "s" : "a");
				sb.append(","+chain.toSequence());
				fileLines.add(sb.toString());
			}
		}
		
		writeFileLines(OUTFILE, fileLines);
	}
}
