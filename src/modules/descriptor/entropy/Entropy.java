package modules.descriptor.entropy;

import java.util.List;

import assist.exceptions.NotYetImplementedError;
import assist.script.Script;
import assist.util.BitList;
import assist.util.LabeledList;
import assist.util.Pair;
import biology.amino.Aminoid;
import biology.amino.BioMolecule;
import biology.descriptor.DescriptorType;
import biology.exceptions.alignment.EntropyAssignmentAlignmentError;
import biology.protein.AminoChain;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import biology.tools.SequenceAligner;
import install.DirectoryManager;
import modules.descriptor.DescriptorAssignmentModule;
import project.ProteinDataset;
import system.Instruction;
import system.SwiPred;
import tools.DataSource;
import tools.Lookup;
import tools.download.blast.BlastDownloader;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;
import utilities.exceptions.SystemError;

/**
 * Module to assign sequence entropy/shannon entropy Descriptor
 * Run this module to obtain entropy values for a given chain
 * 
 * @author Benjy Strauss
 *
 */

public class Entropy extends DescriptorAssignmentModule {
	//private static final String FORMAT_TYPE = "Text";
	private static final String ALIGNMENTS = "1000";
	private static final String DESCRIPTIONS = "10000";
	private static final String HITLIST_SIZE = "10000";
	
	public static final String QUERY = "Query";
	public static final String SUBJECT = "Sbjct";
	
	private static final String[] DATABASE_SPECIFIER = {"-database", "-db"};
	private static final BlastDB DEFAULT_DATABASE = BlastDB.NCBI;
	
	private static final String[] ENTROPY_BLAST_ARGS = {
			Lookup.RUN_BLAST_ENTROPY, "-p", "blastp", "-d", null, "-i", null, "-f", HITLIST_SIZE, "-v", DESCRIPTIONS,
			"-b", ALIGNMENTS, "-o", null
	};
	
	public Entropy() { }
	
	/**
	 * Run entropy value lookup for a chain(s)
	 * @param args
	 */
	public static void main(String... args) {
		verifyRelevantFileSystem();
		args = parseArgs(args);
		
		LabeledList<String> sequences = new LabeledList<String>();
		for(String seq: args) {
			sequences.add(seq);
		}
		
		LabeledList<ProteinChain> dummyChains = new LabeledList<ProteinChain>();
		for(int index = 0; index < sequences.size(); ++index) {
			ChainID id = new ChainID();
			id.setProtein("###Chain### ");
			id.setChain(" " + index);
			dummyChains.add(ChainFactory.makeRCSB(id, sequences.get(index)));
		}
		
		for(ProteinChain chain: dummyChains) {
			assignEntropyNCBI(chain, true);	
		}
		
		StringBuilder csvBuilder = new StringBuilder();
		
		for(ProteinChain chain: dummyChains) {
			qp("" + chain);
			StringBuilder e6_builder = new StringBuilder();
			StringBuilder e20_builder = new StringBuilder();
			csvBuilder.append("\nSequence,");
			
			for(BioMolecule bMol: chain) {
				csvBuilder.append( bMol.toChar() + ",");
				if(bMol instanceof Aminoid) {
					Aminoid aa = (Aminoid) bMol;
					e6_builder.append( aa.getDescriptor(DescriptorType.E6.toString()) + ",");
					e20_builder.append( aa.getDescriptor(DescriptorType.E20.toString()) + ",");
				} else {
					e6_builder.append("N/A,");
					e20_builder.append("N/A,");
				}
			}
			trimLastChar(csvBuilder);
			trimLastChar(e6_builder);
			trimLastChar(e20_builder);
			csvBuilder.append("\nE6,");
			csvBuilder.append(e6_builder.toString());
			csvBuilder.append("\nE20,");
			csvBuilder.append(e20_builder.toString());
			
			qp("E6: " + e6_builder);
			qp("E20: " + e20_builder);
		}
		
		if(csvMode()) {
			writeFileLines("entropy.csv", csvBuilder.toString());
		}
	}
	
	/**
	 * Assign entropy to a project
	 * @param instr: instruction on how to assign entropy
	 * @param project: project to assign entropy to
	 */
	public static void assign(Instruction instr, ProteinDataset... projects) {
		LabeledList<ProteinDataset> dataList = new LabeledList<ProteinDataset>();
		dataList.addAll(projects);
		assign(instr, projects);
	}
	
	/**
	 * Assign entropy to a project
	 * @param instr: instruction on how to assign entropy
	 * @param project: project to assign entropy to
	 */
	public static void assign(Instruction instr, List<ProteinDataset> projects) {
		String database = null;
		
		database = instr.getFirstArgumentNamed(DATABASE_SPECIFIER);
		BlastDB _database = (database != null) ? BlastDB.parse(database) : DEFAULT_DATABASE;
		
		DataSource originalSource = SwiPred.getShell().fastaSrc();
		for(ProteinDataset pp: projects) {
			if(pp != null) {
				for(AminoChain<?> chain: pp) {
					if(!chain.getMetaData().has_entropy || instr.override) {
						SwiPred.getShell().setFastaSrc(chain.getMetaData().source());
						try {
							assign(chain, _database);
						} catch (EntropyAssignmentAlignmentError EAAE) {
							LocalToolBase.error("Cannot assign entropy for chain: " + chain.id().standard() + ".  (Alignment Error).");
						}
					}
				}
			}
		}
		
		SwiPred.getShell().setFastaSrc(originalSource);
	}
	
	/**
	 * Assigns Entropy values to a chain
	 * @param chain: The chain to calculate entropy for
	 * @param database: the database to use for the BLAST search
	 */
	public static void assign(AminoChain<?> chain, BlastDB database) {
		
		switch(database) {
		case NCBI:
		case ONLINE:		assignEntropyNCBI(chain);					break;
		case NEXTFLOW:		calculateEntropyNextflow();					break;
		default:			calculateEntropyLocal(chain, database);		break;
		}
	}

	/**
	 * Assigns Entropy values to a chain
	 * @param chain: The chain to calculate entropy for
	 */
	public static void calculateEntropyLocal(AminoChain<?> chain) {
		calculateEntropyLocal(chain, BlastDB.UNIREF50);
	}
	
	/**
	 * Assigns entropy values from NCBI database
	 * @param chain: the chain to assign values to
	 */
	public static void assignEntropyNCBI(AminoChain<?> chain) { assignEntropyNCBI(chain, false); }
	
	/**
	 * Assigns entropy values from NCBI database
	 * @param chain: the chain to assign values to
	 * @param redownload: re-download blast file
	 */
	public static void assignEntropyNCBI(AminoChain<?> chain, boolean redownload) {
		//DebugUtil.checkForFile("files/blasts/testfile.txt");
		
		ShannonList vectors = null;
		qp("Assigning entropy (NCBI) for: " + chain.name());
		BitList ignoreVectAtPos;
		
		boolean redo_download = false;
		do {
			//qp("*"+redo_download);
			try {
				String blastFile = BlastDownloader.downloadNCBI("entropy", chain, redownload, true);
				vectors = parseBlastFile(blastFile);
				ignoreVectAtPos = new BitList(vectors.size());
			} catch (DataRetrievalException e) {
				return;
			}
			//qp("-");
			redo_download = SequenceAligner.align(vectors, chain, ignoreVectAtPos);
		} while(redo_download);
		
		for(int index = chain.startsAt(); index < chain.size(); ++index) {
			int vecIndex = index - chain.startsAt();
			if(chain.get(index) == null || vectors.get(vecIndex) == null) { continue; }
			//Do we need this??
			//if(chain.get(index).toChar() == '_') { continue; }
			if(ignoreVectAtPos.get(vecIndex)) { error("as requested -- ignoring: " + index); continue; }
			
			if(chain.get(index) instanceof Aminoid) {
				Aminoid amino = (Aminoid) chain.get(index);
				amino.setDescriptor(DescriptorType.E6, vectors.get(vecIndex).calculateE6());
				amino.setDescriptor(DescriptorType.E20, vectors.get(vecIndex).calculateE20());
				amino.setDescriptor(DescriptorType.E22, vectors.get(vecIndex).calculateE22());
			}
			//qp(chain.get(index).getDescriptor(DescriptorType.E6));
		}
		
		chain.getMetaData().has_entropy = true;
	}
	
	/**
	 * Calculate entropy values using a local database
	 * @param chain: The chain to calculate entropy for
	 * @param database: the database to use for the BLAST search
	 */
	private static void calculateEntropyLocal(AminoChain<?> chain, BlastDB database) {
		String blastFile = JavaBlastLocal(chain, database);
		ShannonList vectors = null; 
		
		try {
			vectors = parseBlastFile(blastFile);
		} catch (DataRetrievalException e) {
			//This should literally never happen
			throw new SystemError("A bizarre error has occured, one which you should never see."
					+ "\nPlease contact Benjynstrauss@gmail.com for help debugging this program.");
		}
		
		//ShannonList vectors = parseBlastFile("files/blasts/entropy_4JLY_F.txt");
		SequenceAligner.align(vectors, chain, new BitList(vectors.size()));
		
		for(int index = 0; index < chain.size(); ++index) {
			if(chain.get(index).toChar() == vectors.get(index).queryLetter &&
					chain.get(index) instanceof Aminoid) {
				Aminoid amino = (Aminoid) chain.get(index);
				amino.setDescriptor(DescriptorType.E6, vectors.get(index).calculateE6());
				amino.setDescriptor(DescriptorType.E20, vectors.get(index).calculateE20());
				amino.setDescriptor(DescriptorType.E22, vectors.get(index).calculateE22());
			} else {
				throw new EntropyAssignmentAlignmentError(chain.name());
			}
		}
		chain.getMetaData().has_entropy = true;
	}
	
	private static void calculateEntropyNextflow() {
		throw new NotYetImplementedError();
		
	}
	
	/**
	 * Generate a blast file Using SSpro Blast Database Uriref50
	 * @param chain: the chain to run blast on
	 * @return: name of the generated blast file
	 */
	@SuppressWarnings("unused")
	private static String JavaBlastLocal(AminoChain<?> chain) {
		return JavaBlastLocal(chain, BlastDB.parse(ENTROPY_BLAST_ARGS[4]));
	}
	
	/**
	 * Obtain a blast file for a chain using a local database
	 * @param chain: The chain to generate a BLAST file for
	 * @param database: the database to use for the BLAST search
	 * @return: name of the generated BLAST file to use in calculating entropy
	 */
	private static String JavaBlastLocal(AminoChain<?> chain, BlastDB database) {
		String blastOut = DirectoryManager.FILES_BLASTS +"/entropy_" + chain.id().uniqueSaveID() + TXT;
		blastOut = blastOut.replaceAll(":", "_");
		String generatedFasta = DirectoryManager.FILES_BLASTS + "/" + chain.id().uniqueSaveID() + TXT;
		
		//colon can't be used in a file name on mac os x
		generatedFasta = generatedFasta.replaceAll(":", "");
		writeFileLines(generatedFasta, chain.toPurifiedFasta());
		
		switch(database) {
		case UNIREF50:
			qp("Running Blast: Using SSpro5.2 Blast Database Uniref50");	break;
		default:
			qp("Running Blast: Using Database: " + database);
		}
		
		String blastArgs[] = ENTROPY_BLAST_ARGS;
		blastArgs[4] = database.getLocation();
		blastArgs[6] = generatedFasta;
		blastArgs[14] = blastOut;
		
		Script.runScript(blastArgs);
		
		return blastOut;
	}
	
	/**
	 * Parses a Blast File into a ShannonList object
	 * query: original sequence
	 * sbjct: matched sequence
	 * 
	 * @param filename: name of the file to parse
	 * @return: ShannonList containing blast file data
	 * @throws DataRetrievalException 
	 */
	private static ShannonList parseBlastFile(String filename) throws DataRetrievalException {
		String lines[] = getFileLines(filename);
		ShannonList alignement = new ShannonList("Alignment");
		
		if(lines[0].startsWith("An error has occurred on the server")) {
			throw new DataRetrievalException("Bad Blast File: " + filename);
		}
		
		LabeledList<String> subjects = new LabeledList<String>(SUBJECT);
		LabeledList<String> queries = new LabeledList<String>(QUERY);
		
		for(String line: lines) {
			if(line.startsWith(SUBJECT)) {
				subjects.add(line);
			} else if(line.startsWith(QUERY)) {
				if(!line.startsWith("Query=")) {
					queries.add(line);
				}
			}
		}
		
		if(subjects.size() != queries.size()) {
			qp(subjects.size() + "," + queries.size());
			throw new MalformedBlastFileException("Subject and Query size don't match!");
		}
		
		//loop over each of the alignments
		for(int alignNo = 0; alignNo < subjects.size(); ++alignNo ) {
			String[] queryTokens = queries.get(alignNo).split("\\s+");
			
			int firstQueryPos, lastQueryPos;
			
			try {
				//sometimes it's just a line of dashes
				firstQueryPos = Integer.parseInt(queryTokens[1])-1;
			} catch (NumberFormatException NFE) {
				continue;
			}
			
			try {
				//sometimes it's just a line of dashes
				lastQueryPos = Integer.parseInt(queryTokens[3])-1;
			} catch (NumberFormatException NFE) {
				continue;
			}
			
			String sbjctSeq = subjects.get(alignNo).split("\\s+")[2];
			String querySeq = queryTokens[2];
			
			Pair<String,String> cleaned = cleanAllHyphens(querySeq, sbjctSeq);
			querySeq = cleaned.x;
			sbjctSeq = cleaned.y;
			
			//loop over the residues in the sequence
			for(int index = firstQueryPos; index <= lastQueryPos; ++index) {
				if(index >= alignement.size() || alignement.get(index) == null) {
					alignement.set(index, new ShannonVector(querySeq.charAt(index-firstQueryPos)));
				}
				alignement.get(index).record(sbjctSeq.charAt(index-firstQueryPos));
			}
		}
		
		//qp("alignement size: " + alignement.size());
		return alignement;
	}
	
	/**
	 * Cleans all irrelevant hyphens out of a pairwise blast alignment
	 * irrelevant in this case means "irrelevant to calculating sequence entropy"
	 * 
	 * Removes all hyphens from the querySeq and the corresponding residues from sbjctSeq
	 * 
	 * @param querySeq: the query sequence (the one we care about calculating entropy for)
	 * @param sbjctSeq: the similar sequence
	 * @return: Strings containing only the residues useful for calculating entropy
	 */
	private static Pair<String, String> cleanAllHyphens(String querySeq, String sbjctSeq) {
		Pair<String, String> retval = new Pair<String, String>();
		
		StringBuilder query = new StringBuilder();
		StringBuilder subject = new StringBuilder();
		
		for(int index = 0; index < sbjctSeq.length(); ++index) {
			char meta = querySeq.charAt(index);
			
			if(meta != '-') {
				query.append(querySeq.charAt(index));
				subject.append(sbjctSeq.charAt(index));
			}
		}
		
		retval.x = query.toString();
		retval.y = sbjctSeq.toString();
		return retval;
	}
}
