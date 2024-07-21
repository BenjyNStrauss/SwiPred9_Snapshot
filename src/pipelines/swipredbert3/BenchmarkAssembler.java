package pipelines.swipredbert3;

import java.io.File;

import assist.base.ParsingBase;
import assist.exceptions.FileNotFoundRuntimeException;
import assist.exceptions.IncompleteLineException;
import assist.ml.DataSplit;
import assist.ml.ML_Base;
import assist.sheets.CSVTools;
import assist.sheets.Spreadsheet;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import biology.amino.BioMolecule;
import biology.amino.SecondaryStructure;
//import assist.util.LabeledSet;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import biology.tools.ChainRepair;
import modules.encode.tokens.*;
import tools.reader.fasta.SequenceReader;
import tools.reader.fasta.pdb.PDBChecksumException;
import utilities.exceptions.DataRetrievalException;

/**
 * Complete filter for PDB ids
 * @author Benjamin Strauss
 * 
 * 350 sequences
 * 60 unique
 * 
 * TODO
 * 1QLE:A Weird
 * 1AMT:A XXPXAXAQXVXGLXPVXXEQX breaks
 * 
 * Data from ?
 * filename = proteins-2024-03-05.csv
 * url? = https://cgopm.cc.lehigh.edu:3000/opm-backend//primary_structures?fileFormat=csv
 * 
 * 0 id
1 ordering
2 family_name_cache
3 species_name_cache
4 membrane_name_cache
5 name
6 description
7 comments
8 pdbid
9 resolution
10 topology_subunit
11 topology_show_in
12 thickness
13 thicknesserror
14 subunit_segments
15 tilt
16 tilterror
17 gibbs
18 tau
19 verification
20 membrane_id
21 species_id
22 family_id
23 superfamily_id
24 classtype_id
25 type_id
26 secondary_representations_count
27 structure_subunits_count
28 citations_count
29 created_at
30 updated_at
31 uniprotcode
32 interpro
 */

public class BenchmarkAssembler extends CommonTools implements ParsingBase {
	private static final int PDBID = 8;
	private static final int CHAIN = 10;
	private static final int SPECIES_ID = 21;
	private static final int FAMILY_ID = 22;
	private static final int SUPERFAMILY_ID = 23;
	
	/**
	 * 
	 * @param csvFile
	 * @param outFile
	 */
	public static void extract_ids(String csvFile, String outFile) {
		final Spreadsheet<String> datasheet = CSVTools.readCSV(csvFile, true, false);
		
		final LabeledList<String> extracted = new LabeledList<String>();
		
		for(Object[] entry: datasheet) {
			
			String pdbid = entry[PDBID].toString().substring(2, entry[PDBID].toString().length()-1).toUpperCase();
			String chain = entry[CHAIN].toString();
			if(chain.length() == 0) { chain = "A"; }
			
			String species_id = entry[SPECIES_ID].toString();
			extracted.add(pdbid+":"+chain+":"+species_id+":"+entry[FAMILY_ID]+":"+entry[SUPERFAMILY_ID]);
		}
		
		writeFileLines(outFile, extracted);
	}
	
	public static int family_id_pipeline(String infile, String outfile_pref, boolean useUnpSeq) {
		final String[] lines = getFileLines(infile);
		
		AminoToken.displayCondensed = true;
		AminoSToken.displayCondensed = true;
		PrimaryToken.displayCondensed = true;
		ProteinBERT_Token.displayCondensed = true;
		SecondaryToken.displayCondensed = true;
		SecondarySimpleToken.displayCondensed = true;
		
		//final LabeledHash<String, LabeledList<String>> benchmarks = new LabeledHash<>();
		
		final LabeledHash<String, LabeledList<String>> benchmarks_species     = new LabeledHash<>();
		final LabeledHash<String, LabeledList<String>> benchmarks_family      = new LabeledHash<>();
		final LabeledHash<String, LabeledList<String>> benchmarks_superfamily = new LabeledHash<>();
		
		final String[] keys = Tokenizer.getKeys();
		
		for(String key: keys) {
			benchmarks_species.put(key, new LabeledList<String>());
			benchmarks_family.put(key, new LabeledList<String>());
			benchmarks_superfamily.put(key, new LabeledList<String>());
		}
		
		int total = 0;
		int successes = 0;
		//1QJP:A:9:34:26 - protein:chain:species:family:superfamily 
		for(String line: lines) {
			String[] fields = line.split(":");
			++total;
			ChainID id = new ChainID();
			id.setProtein(fields[0]);
			id.setChain(fields[1]);
			
			ProteinChain chain = null;
			
			try {
				chain = SequenceReader.readChain_pdb(id, true);
				if(useUnpSeq && chain.id().uniprot() != null) {
					chain = ChainRepair.repair(chain);
				}				
			} catch (DataRetrievalException e) {
				continue;
			} catch (PDBChecksumException pdbcse) {
				qerr("Checksum Error for: "+id);
				continue;
			} catch (FileNotFoundRuntimeException fnfre) {
				if(!useUnpSeq) {
					qerr("No PDB file for:    "+id);
				}
				continue;
			} 
			
			if(chain.size() < ProtIDAssembler_v3.DEFAULT_MIN_SIZE) {
				continue;
			}
			
			int index = 0;
			
			for(BioMolecule bMol: chain) {
				if(bMol.secondary() == null) {
					bMol.setSecondaryStructure(SecondaryStructure.DISORDERED);
				}
			}
			
			for(String key: keys) {
				String tokenized = "";
				
				switch(key) {
				case "ps8token":	tokenized = getStr(AminoToken.parse(chain));			break;
				case "ps3token":	tokenized = getStr(AminoSToken.parse(chain));			break;
				case "ptoken":		tokenized = getStr(PrimaryToken.parse(chain));			break;
				case "s8token":		tokenized = getStr(SecondaryToken.parse(chain));		break;
				case "s3token":		tokenized = getStr(SecondarySimpleToken.parse(chain));	break;
				case "pberttoken":	tokenized = getStr(ProteinBERT_Token.parse(chain));		break;
				}
				
				benchmarks_species.get(key).add(makeCSVLine(tokenized, fields[2]));
				benchmarks_family.get(key).add(makeCSVLine(tokenized, fields[3]));
				benchmarks_superfamily.get(key).add(makeCSVLine(tokenized, fields[4]));
				++index;
				
				if(index % 100 == 0) {
					qp("Completed: " + index);
					System.gc();
				}
			}
			++successes;
		}
		
		qp("Total Entries: " + benchmarks_species.get("ptoken").size());
		
		for(String key: keys) {
			writeFileLines(outfile_pref+key+"-species.txt", benchmarks_species.get(key));
			writeFileLines(outfile_pref+key+"-family.txt", benchmarks_family.get(key));
			writeFileLines(outfile_pref+key+"-superfamily.txt", benchmarks_superfamily.get(key));
		}
		return total - successes;
	}
	
	/**
	 * 
	 * @param tokenized
	 * @param target
	 * @return
	 */
	private static String makeCSVLine(String tokenized, String target) {
		return tokenized.replaceAll(",", "-")+","+target;
	}
	
	public static String getTrainTestValString(int len) {
		final StringBuilder builder = new StringBuilder();
		
		for(int ii = 0; ii < len; ++ii) {
			double randval = Math.random();
			if(randval < 0.1) {
				builder.append(2);
			} else if (randval < 0.6) {
				builder.append(1);
			} else {
				builder.append(0);
			}
		}
		
		return builder.toString();
	}
	
	private static String getBenchmarksDir(boolean redundant, boolean useUnpSeq) {
		String redundancy = (redundant) ? "wr" : "nr";
		String database = (useUnpSeq) ? "unp" : "pdb";
		String dir = "input/swipredbert/benchmarks/" + redundancy + "-" + database ;
		File file = new File(dir);
		
		if(!file.exists()) { file.mkdir(); }
		
		return dir+"/";
	}
	
	/**
	 * 
	 * @param redundant
	 * @param useUNP
	 */
	public static void make_benchmarks(boolean redundant, boolean useUNP) {
		//final String csvFile = "input/swipredbert/benchmark/proteins-2024-03-05.csv";
		final String benchmarks_file = "input/swipredbert/benchmark/proteins-benchmarks.txt";
		
		int num_ids = getFileLines(benchmarks_file).length;
		
		final String sorter = getTrainTestValString(num_ids);
		
		final String path_start = getBenchmarksDir(redundant, useUNP);
		
		final int fails = family_id_pipeline(benchmarks_file, path_start, useUNP);
		qp("# fails: "+fails+" for "+path_start);
		
		final String[] keys = { "ps8token", "ps3token", "ptoken", "s8token", "s3token", "pberttoken" };
		final String[] types = { "family", "species", "superfamily" };
		
		for(String key: keys) {
			for(String type: types) {
				final String path = path_start+key+"-"+type;
				
				final DataSplit<String> split = ML_Base.train_test_val_split(getFileLines(path+TXT), sorter);
				writeFileLines(path+".train"+CSV, "seq,label");
				writeFileLines(path+ ".test"+CSV, "seq,label");
				writeFileLines(path+".valid"+CSV, "seq,label");
				appendFileLines(path+".train"+CSV, split.train());
				appendFileLines(path+ ".test"+CSV, split. test());
				appendFileLines(path+".valid"+CSV, split.valid());
			}
		}
	}
	
	/**
	 * 
	 * 
	 * Train on: 52991
	 * Benchmark on: 8154 (pdb) / 7773 (unp)
	 * 		TODO does 8915 become 8311 after bad chains get removed?
	 * 
	 * example
	 * test  = 27218 = 0.503775819945214 = 50%
	 * train = 21447 = 0.396960835122529 = 40%
	 * valid = 5363  = 0.099263344932257 = 10%
	 * total = 54028
	 * 
	 * @param args: unused
	 * @throws IncompleteLineException 
	 */
	public static void main(String[] args) throws IncompleteLineException {
		make_benchmarks(false, true );
		make_benchmarks(true,  true );
		make_benchmarks(false, false);
		make_benchmarks(true,  false);
	}
}
