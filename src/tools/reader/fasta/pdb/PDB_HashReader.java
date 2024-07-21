package tools.reader.fasta.pdb;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import assist.base.Assist;
import assist.exceptions.FileNotFoundRuntimeException;
import assist.numerical.Int_Interval;
import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.LabeledSet;
import biology.amino.AminoAcid;
import biology.amino.BioMolecule;
import biology.amino.InsertCode;
import biology.amino.SecondaryStructure;
import biology.descriptor.ResAnnotation;
import biology.molecule.types.AminoType;
import biology.molecule.types.MoleculeType;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import system.SwiPred;
import tools.DataSource;
import tools.Lookup;
import tools.download.fasta.FastaDownloader;
import tools.reader.fasta.DSSP_Reader;
import tools.reader.fasta.HashReader;
import tools.reader.fasta.exceptions.BrokenPDBFileException;
import utilities.LocalToolBase;

/**
 * PDB_Hash is a hashtable-based parser where
 * 		key = residue number + insertion code
 * 		value = residue
 * 
 * PDB_Hash looks at the fields described in the PDB_LineParser class and records the 	
 * 	amino acids at those given positions.  It then can read DSSP secondary structure	
 * 	into the hash as DSSP positions *usually* line up with PDB positions. Conflicts	will
 * 	be noted (to standard out)
 * 
 * SEQRES fields are not used in reading amino acid residues, as they have no position
 * 	numbers, however the SEQRES fields are employed as a sort of checksum.				
 * 
 * Once the chain is read, and the DSSP is parsed, the parser will employ several tricks
 * 	(should those tricks be necessary) to get the chain to match the sequence specified
 * 	in the SEQRES fields.  These include removing residues not specified in the DBREF
 *  fields, reordering residues that appear in the ATOM section (instead of what their
 *  indices+insertion codes would otherwise indicate, and adding ligands to the chain
 *  that are mentioned in the checksum.  A last resort (to get a chain to fit) is to
 *  attempt to add the SEQRES residues to the checksum, using their SEQRES position as 
 *  their index (no code)
 * 
 * Note: a protein can fail to match its checksum due to residue types unknown to
 * 	SwiPred.  If this occurs, there will be an 'X' in the chain or checksum.  However
 * 	the converse is not necessarily true.
 * 
 * The benefit of this approach is that no MSA has to be conducted to match DSSP with
 *  PDB, and the parser (as far as we know) does not have any soft fails.
 * 
 * @author Benjamin Strauss
 * @version 1.1.1
 *
 */

public class PDB_HashReader extends HashReader {
	private static final long serialVersionUID = 1L;
	
	private static final boolean DEBUG_CHECKSUM = false;
	
	private boolean missingDSSP = false;
	private boolean secStrAssignSuccess = false;
	
	//residues that were excluded using DBREFs
	private final LabeledHash<InsertCode, AminoAcid> excluded;
	//insertcodes that were referencing multiple amino acids
	//private final LabeledSet<InsertCode> conflicts;
	//SEQRES field: used as a checksum
	final PDB_Seqres seqres_checksum;
	//Order of which atoms were assigned in the file, used as a checksum
	final LabeledList<InsertCode> atom_checksum;
	final LabeledList<DBReference> dbrefs = new LabeledList<DBReference>();
	
	//Set of all ranges in the DBREF 
	final LabeledSet<Int_Interval> validRanges = new LabeledSet<Int_Interval>();
	private InsertCode ter;
	
	//buffer for PDB_LineParser to use
	String DBREF1_buffer;
	//protein's description as described by the HEADER in the PDB file
	String description;
	
	private File dssp_file = null;
	private boolean reapply_dssp = false;
	
	public PDB_HashReader(ChainID id) {
		super(id.toString(), id);
		excluded = new LabeledHash<InsertCode, AminoAcid>();
		//conflicts = new LabeledSet<InsertCode>();
		seqres_checksum = new PDB_Seqres("Seqres checksum for "+id);
		atom_checksum = new LabeledList<InsertCode>("Atom checksum for "+id);
	}
	
	/**
	 * Reads a PDB File using the default fasta path given the chain's ID
	 */
	public void readPDB() { readPDB(new File(Lookup.getFastaPath(id, PDB_Tools.MY_SOURCE))); }
	
	/**
	 * Reads the PDB File specified
	 * @param fasta: specified pdb file
	 */
	public void readPDB(File fasta) {
		if(isLoaded) { throw new DuplicateLoadException(); }
		
		String[] lines = LocalToolBase.getFileLines(fasta);
		
		for(String line: lines) {
			PDB_LineParser.parse(line, this);
		}
		
		isLoaded = true;
	}
	
	public void applyDSSP() { applyDSSP(new File(Lookup.getFastaPath(id, DataSource.DSSP))); }
	
	public void applyDSSP(File dssp) {
		dssp_file = dssp;
		if(!dssp.exists() || dssp.isDirectory()) {
			missingDSSP = true;
			throw new FileNotFoundRuntimeException(dssp);
		}
		
		String[] lines = LocalToolBase.getFileLines(dssp);
		lines = DSSP_Reader.filterLines(lines, id);
		
		for(String line: lines) {
			//qp(line);
			String[] fields = Assist.splitStringOnIndicies(line, DSSP_Reader.DSSP_INDICES_BETA);
			InsertCode insCode = new InsertCode(fields[1].trim());
			
			char aminoChar = fields[3].trim().charAt(0);
			
			boolean sulfur_bond = false;
			char secStrChar;
			if(fields[4].trim().length() != 0) {
				secStrChar = fields[4].trim().charAt(0);
			} else {
				secStrChar = ' ';
			}
			
			//b denotes a sulfur-bonded cysteine
			if(Character.isLowerCase(aminoChar)) { 
				aminoChar = 'C';
				sulfur_bond = true;
			}
			AminoType type = AminoType.parse(aminoChar);
			
			switch(aminoChar) {
			case 'B':
			case 'J':
			case 'Z':
				qp("Warning for "+id+": DSSP amino char "+aminoChar+" is converted to X");
				aminoChar = 'X';
				break;
			default:
			}
			
			AminoAcid aa = get(insCode);
			//qp(aa);
			if(aa == null) { put(insCode, new AminoAcid(aminoChar)); }
			
			aa = get(insCode);
			
			if(!(type.couldBe(aa.residueType())) && aminoChar != 'X') {
				LocalToolBase.error("WARNING: Index["+insCode+"] "+id+"(rcsb-pdb/dssp) {"+aa.residueType().toChar()+" vs "+aminoChar+"}");
			}
			
			aa.setSecondaryStructure(SecondaryStructure.parse(secStrChar));
			if(sulfur_bond && (!aa.hasAnnotation(ResAnnotation.SULFUR_BOND))) {
				aa.addAnnotation(ResAnnotation.SULFUR_BOND);
			}
		}
		
		for(InsertCode key: keySet()) {
			if(get(key).secondary() == null) {
				get(key).setSecondaryStructure(SecondaryStructure.DISORDERED);
			}
		}
		
		secStrAssignSuccess = true;
	}
	
	public AminoAcid put(InsertCode code, AminoAcid aa) {
		aa.setPostition(code);
		
		if(get(code) == null) {
			return super.put(code, aa);
		} else if(get(code).residueType() != aa.residueType()) {
			throw new BrokenPDBFileException("Conflicting data for code: "+code);
		} else {
			return get(code);
		}
	}
	
	public boolean knownConflict(InsertCode code) {
		boolean preexisting = get(code).conflict;
		get(code).conflict = true;
		return preexisting;
	} 
	
	public ProteinChain toChain() {
		ProteinChain chain = new ProteinChain(id);
		chain.getMetaData().setSource(DataSource.RCSB_PDB);
		chain.getMetaData().missing_dssp = missingDSSP;
		chain.getMetaData().has_secondary_structure = secStrAssignSuccess;
		chain.description = description;
		
		InsertCode[] codes = new InsertCode[keySet().size()];
		keySet().toArray(codes);
		Arrays.sort(codes);
		
		for(InsertCode code: codes) {
			chain.add(get(code));
		}
		
		boolean x_in_checksum = modifyChainToXsInChecksum(chain);
		
		printChainAndChecksum(chain);
		
		/* double-check, is the chain *still* too big?
		 * could trimming leading/trailing 'X's help?
		 */
		int or_chainSize = chain.size();
		if(chain.size() > seqres_checksum.size() && (!x_in_checksum)) {
			trimXs(chain);
		}
		
		if(!validate(chain)) {
			reorderToAtomOrder(chain, codes);
		} else { return chain; }
		
		//boolean alwaysReorderToATOM = false;
		if(!validate(chain)) {
			//ProteinChain clone = chain.clone();
			regenerate(chain, true);
			/*if(clone.size() == seqres_checksum.size() && chain.size() != seqres_checksum.size()) {
				chain = clone;
				alwaysReorderToATOM = true;
			}*/
		} else { return chain; }
		
		/* double-check, is the chain *still* too big?
		 * If the chain is too large for the checksum
		 * remove everything not in DBREFs
		 */
		if(or_chainSize > seqres_checksum.size() && chain.size() != seqres_checksum.size()) {
			trimWithDBREFs(chain, false);
			if(x_in_checksum) { modifyChainToXsInChecksum(chain); }
		}
		
		printChainAndChecksum(chain);
		
		//This is a LAST RESORT
		if(chain.size() < seqres_checksum.size()) {
			regenerateWithChecksum(chain, false);
			atom_reorder_flag = false;
			//if(alwaysReorderToATOM) { reorderToAtomOrder(chain, codes); }
		}
		if(chain.size() > seqres_checksum.size()) {
			regenerate(chain, true);
			atom_reorder_flag = false;
			//if(alwaysReorderToATOM) { reorderToAtomOrder(chain, codes); }
		}
		if(chain.size() > seqres_checksum.size() && (!x_in_checksum)) {
			trimXs(chain);
		}
		
		if(dssp_file != null && reapply_dssp) { applyDSSP(dssp_file); }
		printChainAndChecksum(chain);
		
		if(!validate(chain)) {
			throw new PDBChecksumException("Chain seqres doesn't match pdb seqres!",chain,seqres_checksum );
		}
		
		Objects.requireNonNull(chain);
		return chain;
	}

	public void setTER(InsertCode terCode) { ter = terCode; }
	
	private boolean modifyChainToXsInChecksum(ProteinChain chain) {
		if(DEBUG_CHECKSUM) { qp("Adding Xs in SEQRES checksum"); }
		boolean x_in_checksum = false;
		
		for(int index = 0; index < seqres_checksum.size(); ++index) {
			
			if(!(seqres_checksum.get(index) instanceof AminoType)) {
				if(chain.get(index) == null) {
					BioMolecule bMol = new BioMolecule(seqres_checksum.get(index));
					//bMol.setPostition(new InsertCode(index));
					chain.add(index, bMol);
					continue;
				}
				
				AminoType at = chain.get(index).residueType();
				
				if(at == AminoType.OTHER || at == AminoType.ANY) {
					chain.set(index, new BioMolecule(seqres_checksum.get(index)));
					//chain.get(index).setPostition(new InsertCode(index));
				} else {
					chain.add(index, new BioMolecule(seqres_checksum.get(index)));
					//chain.get(index).setPostition(new InsertCode(index));
					x_in_checksum = true;
				}
			}
		}
		
		reapply_dssp = true;
		return x_in_checksum;
	}
	
	private void reorderToAtomOrder(ProteinChain chain, InsertCode[] codes) {
		if(DEBUG_CHECKSUM) { qp("Attempting reorder based on \"ATOM\" order"); }
		printChainAndChecksum(chain);
		
		for(int index = 0; index < codes.length; ++index) {
			//qp("+"+codes[index]);
			if(atom_checksum.contains(codes[index])) {
				//qp("--"+codes[index]);
				codes[index] = null;
			}	
		}
		
		//ii = arrayIndex; jj = list index
		for(int ii = 0, jj = 0; ii < codes.length; ++ii) {
			if(codes[ii] == null) {
				codes[ii] = atom_checksum.get(jj);
				++jj;
			}
		}
		
		/*
		 * Do NOT replace this with 'regenerate()'
		 * No changes will occur to the chain if that is done
		 * Why: it needs to use the re-ordered InsertCodes
		 */
		chain.clear();
		for(InsertCode code: codes) {
			chain.add(get(code));
		}
		
		printChainAndChecksum(chain);
		//qp("--------------------");
	}
	
	private void trimWithDBREFs(ProteinChain chain, boolean applyTER) {
		if(DEBUG_CHECKSUM) { qp("Trimming to DBREF spec"); }
		for(InsertCode key: keySet()) {
			boolean inDBREF = false;
			for(Int_Interval interval: validRanges) {
				if(key.index >= interval.start && key.index <= interval.end) {
					inDBREF = true;
				}
			}
			if(!inDBREF) {
				excluded.put(key, get(key));
			}
		}
		
		for(InsertCode code: excluded.keySet()) {
			remove(code);
		}
		
		regenerate(chain, false);
		
	}
	
	private void trimXs(ProteinChain chain) {
		if(DEBUG_CHECKSUM) { qp("Trimming Leading/Trailing 'X's"); }
		
		int seqres_leading_x = 0;
		int seqres_trailing_x = 0;
		
		int chain_leading_x = 0;
		int chain_trailing_x = 0;
		
		int chain_excess_leading_x;
		int chain_excess_trailing_x;
		
		for(BioMolecule bMol: chain) {
			if(bMol.toChar() == 'X') {
				++chain_leading_x;
			} else {
				break;
			}
		}
		
		for(int index = chain.size()-1; index >= 0; index--) {
			if(chain.get(index).toChar() == 'X') {
				++chain_trailing_x;
			} else {
				break;
			}
		}
		
		//no trimming if the chain is all Xs!
		if(chain_leading_x == chain_trailing_x && chain_leading_x == chain.size()) {
			return;
		}
		
		for(MoleculeType mt: seqres_checksum) {
			if(mt.toChar() == 'X') {
				++seqres_leading_x;
			} else {
				break;
			}
		}
		
		for(int index = seqres_checksum.size()-1; index >= 0; index--) {
			if(seqres_checksum.get(index).toChar() == 'X') {
				++seqres_trailing_x;
			} else {
				break;
			}
		}
		
		//no trimming if seqres is all Xs!
		if(seqres_leading_x == seqres_trailing_x && seqres_leading_x == seqres_checksum.size()) {
			return;
		}
		
		chain_excess_leading_x = chain_leading_x - seqres_leading_x;
		chain_excess_trailing_x = chain_trailing_x - seqres_trailing_x;
		for(int ii = 0; ii < chain_excess_leading_x; ++ii) {
			chain.remove(0);
		}
		for(int ii = 0; ii < chain_excess_trailing_x; ++ii) {
			chain.remove(chain.size()-1);
		}
	}
	
	/**
	 * Choose a checksum and validate
	 * checksum may or may not include ligands, depending on the chain's size
	 * @param chain
	 * @return
	 */
	private boolean validate(ProteinChain chain) {
		boolean checksumOK = true;
		
		LabeledList<AminoType> seqres_checksum2 = new LabeledList<AminoType>();
		for(MoleculeType molType: seqres_checksum) {
			if(molType instanceof AminoType) {
				seqres_checksum2.add((AminoType) molType);
			}
		}
		
		LabeledList<?> seqres_checksum_in_use;
		
		//Do proofreading
		if(chain.size() == seqres_checksum.size()) {
			seqres_checksum_in_use = seqres_checksum;
		} else if(chain.size() == seqres_checksum2.size()) {
			seqres_checksum_in_use = seqres_checksum2;
		} else {
			
			return false;
		}
		
		for(int index = 0; index < chain.size(); ++index) {
			
			AminoType at = chain.get(index).residueType();
			MoleculeType check = (MoleculeType) seqres_checksum_in_use.get(index);
			
			if(at != check && !(!(check instanceof AminoAcid) && at == AminoType.INVALID)) {
				checksumOK = false;
				break;
			}
		}
		
		return checksumOK;
	}
	
	/**
	 * LAST RESORT!
	 * @param chain
	 * @param applyTER
	 */
	private void regenerateWithChecksum(ProteinChain chain, boolean applyTER) {
		//if all else fails, attempt to apply the checksum to the hash
		for(int index = 0; index < seqres_checksum.size(); ++index) {
			InsertCode temp = new InsertCode(index+1);
			if(get(temp) == null && seqres_checksum.get(index) instanceof AminoType) {
				put(temp, new AminoAcid((AminoType) seqres_checksum.get(index)));
				get(temp).setSecondaryStructure(SecondaryStructure.DISORDERED);
			}
		}
		regenerate(chain, applyTER);
		
		reapply_dssp = true;
	}
	
	private void regenerate(ProteinChain chain, boolean apply_ter_limit) {
		chain.clear();
		InsertCode[] codes = new InsertCode[keySet().size()];
		keySet().toArray(codes);
		Arrays.sort(codes);
		
		for(InsertCode code: codes) {
			if(code != null) {
				if(ter == null || !(apply_ter_limit && code.compareTo(ter) >= 1)) {
					chain.add(get(code));	
				}
			}
		}
		
		reapply_dssp = true;
	}
	
	public DBReference[] dbrefs() {
		DBReference[] refs = new DBReference[dbrefs.size()];
		for(int ii = 0; ii < dbrefs.size(); ++ii) {
			refs[ii] = dbrefs.get(ii).clone();
		}
		return refs;
	}
	
	private void printChainAndChecksum(ProteinChain chain) {
		if(DEBUG_CHECKSUM) {
			qp("   "+chain);
			System.out.print("checksum: ");
			for(MoleculeType m: seqres_checksum) {
				System.out.print(m.toChar());
			}
			qp("");
		}
	}
	
	public String toString() { return label; }
	
	/**
	 * 
	 * 
	 * Checksum = 1x, chain = 2x
	 * @param args
	 */
	public static void main(String[] args) {
		SwiPred.getShell().setFastaSrc(DataSource.RCSB_PDB);
		ChainID id = new ChainID();
		id.setProtein("5BTR");
		id.setChain("A");
		FastaDownloader.verify(id);
		
		PDB_HashReader testHash = new PDB_HashReader(id);
		testHash.readPDB();
		
		try {
			testHash.applyDSSP();
		} catch (FileNotFoundRuntimeException FNFRE) { }
		
		try {
			ProteinChain chain = testHash.toChain();
			qp(chain);
			qp(chain.toSecondarySequence());
		} catch (PDBChecksumException PDBCE) {
			qerr("Error!");
			PDBCE.printDetails();
		}
	}
}
