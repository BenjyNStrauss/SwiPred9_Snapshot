package dev.special.transform;

import java.util.ArrayList;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import tools.Lookup;
import tools.reader.cluster.ClusterReader;
import tools.reader.fasta.SequenceReader;

/**
 * Used to aggregate sequences
 * @author Benjamin Strauss
 *
 * TODO: implement a classifier -> classifies into 2+ categories
 */

@SuppressWarnings("unused")
public class SeqAggPDB extends Lookup {
	private static final String INFILE = "input/acetyl-sirt-2022.txt";
	private static final String OUTFILE = "output/acetyl-sirt-seq-2022.txt";
	private static final String FASTA_FILE = "output/acetyl-sirt-fastas-for-encoding.txt";

	public static void main(String[] args) {
		/*ChainID id = new ChainID();
		id.setProtein("6DAU");
		id.setChain("F");
		FastaDownloader.download(id, DataSource.UNIPROT);
		ProteinChain chain = SequenceReader.readChain_uniprot(id);*/
		aggregateUnique(args);
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
					chain = SequenceReader.readChain_pdb(id, true);
				} catch (Exception e) { }
				if(chain != null) {
					protList.add(chain);
				}
			}
		}
		boolean sirt = false;
		ArrayList<String> fileLines = new ArrayList<String>();
		ArrayList<String> fileLines2 = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		for(ProteinChain chain: protList) {
			//mark the start of the sirtuins...
			if(chain.id().uniprot() != null) { 
				if(chain.id().uniprot().equals("Q9NXA8")) { sirt = true; }
			}
			
			if(chain.actualSize() > 0) {
				sb.setLength(0);
				sb.append(chain.id().protein()+",");
				sb.append(chain.id().chain()+",");
				sb.append((sirt) ? "s" : "a");
				sb.append(","+chain.toSequence());
				fileLines.add(sb.toString());
				
				sb.setLength(0);
				sb.append(">"+chain.id().protein()+":"+chain.id().chain());
				sb.append("\n"+chain.toSequence()+"\n");
				fileLines2.add(sb.toString());
			}
		}
		
		writeFileLines(OUTFILE, fileLines);
		writeFileLines(FASTA_FILE, fileLines2);
	}
	
	/**
	 * 
	 * @param args
	 */
	private static void aggregateUnique(String[] args) {
		final String acetylFile = "input/acetyl-extended.txt";
		final String sirtFile = "input/sirt-extended.txt";
		final String miscFile = "";
		
		String[][] acetyl_chains = ClusterReader.readClustersTXT(acetylFile);
		String[][] sirt_chains = ClusterReader.readClustersTXT(sirtFile);
		String[][] misc_chains = ClusterReader.readClustersTXT(miscFile);
		
		LabeledList<ProteinChain> acetylList = toProtList(acetyl_chains);
		LabeledList<ProteinChain> sirtList = toProtList(sirt_chains);
		LabeledList<ProteinChain> miscList = null;
		try {
			miscList = toProtList(misc_chains);
		} catch (NullPointerException NPE) {
			miscList = new LabeledList<ProteinChain>();
		}
		
		LabeledHash<String, String> uniqueSeqs = new LabeledHash<String, String>();
		
		appendUniqueSequences(uniqueSeqs, acetylList, 'a');
		appendUniqueSequences(uniqueSeqs, sirtList,   's');
		appendUniqueSequences(uniqueSeqs, miscList,   'm');
		
		for(ProteinChain chain: miscList) {
			String key = chain.toSequence();
			if(!uniqueSeqs.containsKey(key)) {
				String value = chain.id().protein()+","+chain.id().chain()+",m";
				uniqueSeqs.put(key, value);
			}
		}
		
		ArrayList<String> fileLines = new ArrayList<String>();
		for(String key: uniqueSeqs.keySet()) {
			fileLines.add(uniqueSeqs.get(key) + "," + key);
		}
		
		writeFileLines(OUTFILE, fileLines);
	}
	
	private static LabeledList<ProteinChain> toProtList(String[][] chains) {
		LabeledList<ProteinChain> protList = new LabeledList<ProteinChain>();
		for(String[] strArr: chains) {
			for(String str: strArr) {
				String[] tokens = str.split("[:_ ]");
				ChainID id = new ChainID();
				id.setProtein(tokens[0]);
				id.setChain(tokens[1]);
				ProteinChain chain = null;
				try {
					chain = SequenceReader.readChain_pdb(id, true);
				} catch (Exception e) { }
				if(chain != null) {
					protList.add(chain);
				}
			}
		}
		return protList;
	}
	
	private static void appendUniqueSequences(LabeledHash<String, String> hash, LabeledList<ProteinChain> chains, char category) {
		for(ProteinChain chain: chains) {
			String key = chain.toSequence();
			if(key.length() > 0 && (!hash.containsKey(key))) {
				String value = chain.id().protein()+","+chain.id().chain()+","+category;
				hash.put(key, value);
			}
		}
	}
}
