package tools.reader.fasta.pdb.debug;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import assist.base.Assist;
import assist.exceptions.FileNotFoundRuntimeException;
import assist.util.LabeledList;
import assist.util.LabeledSet;
import assist.util.Pair;
import biology.molecule.MoleculeLookup;
import biology.protein.ChainID;
import tools.DataSource;
import tools.download.fasta.FastaDownloader;
import tools.reader.cluster.ClusterReader;
import tools.reader.fasta.pdb.PDBChecksumException;
import tools.reader.fasta.pdb.PDB_HashReader;
import tools.reader.fasta.pdb.PDB_LineParser;
import utilities.LocalToolBase;

/**
 * TODO get ids of chains in a PDB File
 * 
 * 
 * @author Benjamin Strauss
 *
 */

public class PDB_TestModule extends LocalToolBase {
	static int len = "HELIX   36  36 ASP A  559".length();
	static int compnd_len = "COMPND   3 CHAIN: ".length();
	private static float successes = 0;
	private static float fails = 0;
	private static float missing = 0;
	private static float missing_dssp = 0;
	static final LabeledSet<ChainID> FAILED_IDS = new LabeledSet<ChainID>();
	static final int START_HERE = -1;
	static final int HIGHEST_REACHED = 100000;
	private static final String FAILS_FILE = "fails.txt";
	
	private static final String[] KNOWN_FAILS = {
		//	"1EG0:B", "1EG0:J", "1EG0:E", "1EG0:G", "3T42:A",
		//	"1EG0:D", "1EG0:K", "2UWX:A", "2Y2L:B", "1EG0:F", "1EG0:N",
			"1JDB:B", "1JDB:E", "1JDB:H", "1JDB:K", //DBREF trim takes off first residue
			"1DKI:A", "1DKI:B", "1DKI:C", "1DKI:D",		
	};
	
	/* PDB ERRORS:
	 * 1JDB:B,E,H,K chain starts with 0, DBREF starts with 1
	 * 1K55:C error in PDB SEQRES/ATOM disconnect (Lysine vs mod Lysine
	 * 
	 */
	private static final String[] BON_FAILS = {
			"1DKI:A", "1DKI:D", "1DKI:C", "1DKI:B", "1QDM:A", "1QDM:C", 
			"1QDM:B", "2HIZ:A", "2HIZ:B", "2HIZ:C", "2P83:A", "2P83:B", "2P83:C", "2IQG:A", "2HM1:A", "1FKN:B", 
			"1M4H:B", "3UFL:A", "4B1D:A", "4B1E:A", "4B72:A", "4B77:A", "4B70:A", "3UQP:A", "3EXO:A", "2QZL:A", 
			"4ACU:A", "4ACX:A", "4AZY:A", "4B00:A", "4B05:A", "3K5D:A", "3K5D:B", "3K5D:C", "3DM6:A", "3DM6:B", 
			"3DM6:C", "3I25:A", "3I25:B", "3I25:C", "1TQF:A", "2IS0:A", "3H0B:B", "2NTR:A", "3H0B:C", "3KYR:A", 
			"3KYR:B", "3KYR:C", "4ZPE:A", "4ZPF:A", "4ZPG:A", "2B8V:A", "2P8H:A", "2ZJH:A", "2ZJI:A", "2ZJJ:A", 
			"2OAH:A", "2PH6:A", "2ZJK:A", "2ZJK:B", "2ZJK:C", "2PH8:A", "2ZJL:A", "2ZJM:A", "2B8L:A", "2ZJN:A", 
			"2IRZ:A", "3H0B:A", "3PI5:A", "3PI5:B", "3DUY:A", "3PI5:C", "3DUY:B", "3DUY:C", "3QBH:A", "3QBH:B", 
			"4LXK:A", "3QBH:C", "4LXK:B", "3K5C:A", "4LXK:C", "3K5C:B", "3K5C:C", "4LXM:A", "4LXM:B", "2F3E:A", 
			"3DV1:A", "4LXM:C", "2F3E:B", "3DV1:B", "2F3E:C", "3DV1:C", "2F3F:A", "2F3F:B", "3K5F:A", "2F3F:C", 
			"3K5F:B", "3K5F:C", "3K5G:A", "3K5G:B", "3K5G:C", "3DV5:A", "4LXA:A", "3DV5:B", "4LXA:B", "3DV5:C", 
			"4LXA:C", "2LIG:A", "1WAT:B", "1JM7:B", 
			"1YRB:A", "1YR7:A", "2OXR:A", "1YR8:A", "1YR9:A", "1YRA:A", "1YRA:B", "1YR6:A", "1YRB:B", "4YEF:G", 
			"1H24:B", "1H24:D", "1H25:B", "1H25:D", "1H26:B", "1H26:D", "1H27:B", "1H27:D", "1H28:B", "1H28:D", 
			"1EG0:J", "4AB3:N", "4AB3:G", "1W8Z:B", "1W8Z:A", "1NRI:A", "2VZL:A", "2DFY:C", "2BB3:B", "1W5C:B", 
			"1W5C:H", "1RO5:A", "3T42:A", "1Q6M:A", "1Q6J:A", "1G2W:B", 
			"1GKM:A", "1MSZ:A", "1MZK:A", "3AZ3:A", "2C24:B", "2Q0I:A", "3DH8:A", "2UWX:A", "2Y2L:B", "2Y2M:A", 
			"1RCS:A", "1RCS:B", "1K4J:A", "1CLW:A", "1TUD:A", "1E7U:A", "3RV3:B", "1DV2:B", "3RV4:A", "3RUP:A", 
			"3RUP:B", "1K47:A", "1K47:B", "1K47:C", "1K47:D", "1K47:E", "1K47:F", "1DPU:A", "1DEU:A", "1DEU:B", 
			"1BD7:B", "1JR3:B", "1JR3:C", "3PLV:A", "3H7S:A", "3H7S:B", "1PFZ:A", "1PFZ:B", "1PFZ:C", "1PFZ:D", 
			"1NV8:A", "1NV8:B", "1NV9:A", "1XT8:A", "1EJG:A", "3NIR:A", "1EG0:B", 
			"6DHM:A", "6DHM:B", "6DHM:C", "6DHM:D", "6DHM:E", 
			"5K12:B", "5K12:C", "5K12:D", "5K12:E", "5K12:F", "5K12:A", 
			"1IBI:A", "1K55:D", "1K55:C", "3RCK:X", "1ET6:B", "1EU3:B", "1FWX:A", "1FWX:B", 
			"1FWX:C", "1FWX:D", "1EF0:A", "1EF0:B", 
			//NEW(ish) fails 
			"2ICQ:A",
		};
	
	
	/**
	 * 2Y2L:B -> PDB inconsistency (moved 'I' residue)
	 * 1EG0:F -> M(1) not mentioned outside checksum
	 * 1EG0{B,D,E,J,G,K} -> Missing residues
	 * 3T42:A -> 4 Residues moved
	 * 2UWX:A -> PDB inconsistency (SEQRES, REMARK 465)
	 * 
	 * 
	 * @param args
	 * @throws StructureException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		//qp(BON_FAILS.length);
		//testBondugula();
		//testFixes();
		testExisting();
	}
	
	private static void testFixes() {
		qp(BON_FAILS.length);
		int fixed = 0;
		
		for(String str: BON_FAILS) {
			ChainID id = new ChainID();
			id.setProtein(str.split(":")[0]);
			id.setChain(str.split(":")[1]);
			
			PDB_HashReader testHash = new PDB_HashReader(id);
			testHash.readPDB();
			testHash.applyDSSP();
			
			try {
				qp("***"+testHash.toChain());
				
				if(testHash.atom_reorder_flag()) {
					qp("****"+testHash.toChain());
				}
				
				++fixed;
			} catch (PDBChecksumException PDBCE) {
				//PDBCE.printStackTrace();
				//qp(PDBCE.checksum());
				PDBCE.printDetails();
				qp();
			}
		}
		qp("fixed: "+ fixed);
		qp("fails: " + (BON_FAILS.length-fixed));
	}
	
	
	private static void compileFailList() {
		LabeledList<String> ids = new LabeledList<String>();
		String[] lines = LocalToolBase.getFileLines("_fails.txt");
		for(String line: lines) {
			if(line.startsWith("@")) { continue; }
			String str = line.substring(6);
			ids.add(str.split(":")[0]+":"+str.split(":")[1]);
		}
		
		for(int ii = 0; ii < ids.size(); ++ii) {
			System.out.print("\""+ids.get(ii)+"\", ");
			if((ii+1) % 10 == 0) {
				System.out.print("\n");
			}
		}
	}
	
	private static void testBondugula() throws Exception {
		File fails = new File(FAILS_FILE);
		if(fails.exists()) {fails.delete(); }
		fails.createNewFile();
		
		//testPipelineFails(KNOWN_FAILS);
		//testParseBioJava();
		
		String[][] ids;
		ids = ClusterReader.readClustersTSV("input/bon.tsv");
		//qp(ids);
		
		ArrayList<Pair<String,String>> downloaded_ids = new ArrayList<Pair<String,String>>();
		for(String[] strs: ids) {
			for(String str: strs) {
				downloaded_ids.add(new Pair<String, String>(str.split(":")[0],str.split(":")[1]));
			}
		}
		
		float success_percent = 0;
		qp("Load completed");
		try {
			success_percent = testPipeline(downloaded_ids);
			testExisting();
		} catch(RuntimeException RE) {
			RE.printStackTrace();
		}
		
		for(ChainID id: FAILED_IDS) {
			qp("Failed: " + id);
		}
		
		success_percent *= 100;
		
		for(String str: PDB_LineParser.NEEDED_UPGRADES.keySet()) {
			qp(str + ": " + PDB_LineParser.NEEDED_UPGRADES.get(str));
		}
		qp("Success Rate: " + success_percent + "%");
	}
	
	private static void testExisting() throws Exception {
		ArrayList<Pair<String,String>> downloaded_ids = getAllPDBIds();
		testPipeline(downloaded_ids);
		
		for(String str: PDB_LineParser.NEEDED_UPGRADES.keySet()) {
			qp(str + ": " + PDB_LineParser.NEEDED_UPGRADES.get(str));
		}
	}
	
	private static void searchDSSP() {
		String baseDirPath = "files/fasta/dssp";
		File baseDir = new File(baseDirPath);
		String[] files = baseDir.list();
		for(String file: files) {
			qp("@File: "+file);
			String filePath = baseDirPath+"/"+file;
			String[] lines = getFileLines(filePath);
			
			for(String line: lines) {
				if(line.charAt(10) != ' ') {
					qp(line);
				}
			}
		}
	}
	
	private static void test() {
		String baseDirPath = "files/fasta/pdb";
		File baseDir = new File(baseDirPath);
		String[] files = baseDir.list();
		for(String file: files) {
			//qp("@File: "+file);
			String filePath = baseDirPath+"/"+file;
			String[] lines = getFileLines(filePath);
			
			for(String line: lines) {
				if(line.startsWith("COMPND")) {
					String[] tokens = line.split("\\s+");
					if(tokens[2].equals("CHAIN:")) {
						String data = line.substring(compnd_len);
						data = data.replaceAll("[,;]", "");
						String[] chains = data.split("\\s+");
						for(String chain: chains) {
							qp(file.substring(0,4)+":"+chain);
						}
						
						//qp(line.substring(compnd_len));
					}
				}
			}
		}
	}
	
	private static float testPipeline(ArrayList<Pair<String,String>> idList) throws Exception {
		long start = System.currentTimeMillis();
		for(int index = START_HERE+1; index < idList.size(); ++index) {
			Pair<String,String> strPair = idList.get(index);
			
			ChainID id = new ChainID();
			id.setProtein(strPair.x);
			id.setChain(strPair.y);
			if(index % 100 == 0) { 
				qp("@"+index);
				Files.write(Paths.get(FAILS_FILE), ("@"+index+"\n").getBytes(), StandardOpenOption.APPEND);
			}
			if(index % 1000 == 0) { System.gc(); }
			PDB_HashReader hash = new PDB_HashReader(id);
			
			if(index < HIGHEST_REACHED && !FastaDownloader.exists(id, DataSource.RCSB_PDB)) {
				++missing;
				continue;
			} else {
				FastaDownloader.verify(id, DataSource.RCSB_PDB);
			}
			
			try {
				hash.readPDB();
			} catch (FileNotFoundRuntimeException FNFRE) {
				++missing;
				continue;
			}
			
			try {
				hash.applyDSSP();
			} catch (FileNotFoundRuntimeException FNFRE) {
				++missing_dssp;
			}
			
			try {
				if(hash.atom_reorder_flag()) {
					qp(">> reorder needed for: "+hash.toChain());
				} else {
					hash.toChain();
				}
				++successes;
			} catch (PDBChecksumException PCE) {
				String str = "fail: " + id + ": " + PCE.getMessage();
				qp(str);
				Files.write(Paths.get(FAILS_FILE), (str+"\n").getBytes(), StandardOpenOption.APPEND);
				FAILED_IDS.add(id);
				
				if(!Assist.stringArrayContains(BON_FAILS, PCE.chain().id().toString())) {
					qerr("Error - new Fail!" + PCE.chain().id());
				}
				
				++fails;
			}
		}
		
		qp("Missing Codes:");
		for(String key: MoleculeLookup.MISSED_CODES.keySet()) {
			qp("\t"+key + ": " + MoleculeLookup.MISSED_CODES.get(key));
		}
		
		qp("missing: "+missing);
		qp("missing_dssp: " + missing_dssp);
		qp("successes: "+successes);
		qp("fails: "+fails);
		qp(FAILED_IDS);
		long end = System.currentTimeMillis();
		qp("elapsed: " + (end-start));
		
		return successes / (successes+fails);
	}
	
	private static void testPipelineFails(String[] idList) {
		for(String strPair: idList) {
			ChainID id = new ChainID();
			id.setProtein(strPair.split(":")[0]);
			id.setChain(strPair.split(":")[1]);
			
			PDB_HashReader hash = new PDB_HashReader(id);
			hash.readPDB();
			hash.applyDSSP();
			
			try {
				hash.toChain();
				++successes;
			} catch (PDBChecksumException PCE) {
				qp("fail: " + id + ": " + PCE.getMessage());
				FAILED_IDS.add(id);
				++fails;
			}
			qp("");
		}
		
		for(String key: MoleculeLookup.MISSED_CODES.keySet()) {
			qp(key + ": " + MoleculeLookup.MISSED_CODES.get(key));
		}
		
		qp("successes: "+successes);
		qp("fails: "+fails);
		qp(FAILED_IDS);
	}
	
	private static ArrayList<Pair<String,String>> getAllPDBIds() {
		String baseDirPath = "files/fasta/pdb";
		File baseDir = new File(baseDirPath);
		String[] files = baseDir.list();
		ArrayList<Pair<String,String>> all_ids = new ArrayList<Pair<String,String>>();
		
		for(String file: files) {
			//qp("@File: "+file);
			String filePath = baseDirPath+"/"+file;
			String[] lines = getFileLines(filePath);
			
			for(String line: lines) {
				if(line.startsWith("COMPND")) {
					String[] tokens = line.split("\\s+");
					if(tokens[2].equals("CHAIN:")) {
						String data = line.substring(compnd_len);
						data = data.
								replaceAll("[,;]", "");
						String[] chains = data.split("\\s+");
						for(String chain: chains) {
							all_ids.add(new Pair<String,String>(file.substring(0,4), chain));
						}
						
						//qp(line.substring(compnd_len));
					}
				}
			}
		}
		return all_ids;
	}
}
