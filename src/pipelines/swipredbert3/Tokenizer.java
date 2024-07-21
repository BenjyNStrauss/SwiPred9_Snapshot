package pipelines.swipredbert3;

import java.text.DecimalFormat;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.util.LabeledHash;
import assist.util.LabeledSet;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import biology.tools.ChainRepair;
import modules.encode.tokens.AminoSToken;
import modules.encode.tokens.AminoToken;
import modules.encode.tokens.PrimaryToken;
import modules.encode.tokens.ProteinBERT_Token;
import modules.encode.tokens.SecondarySimpleToken;
import modules.encode.tokens.SecondaryToken;
import tools.reader.fasta.SequenceReader;
import tools.reader.fasta.pdb.PDBChecksumException;
import utilities.exceptions.DataRetrievalException;

/**
 * 
 * @author Benjamin Strauss
 * 
 */

public final class Tokenizer extends CommonTools {
	private static final DecimalFormat FORMAT = new DecimalFormat("00.00");
	
	private static final String[] KEYS = { "ps8token", "ps3token", "ptoken", "s8token", "s3token", "pberttoken" };
	
	public static final String CHECKSUM_ERRORS = "Checksum Errors";
	
	private Tokenizer() { }
	
	public static final LabeledHash<String, LabeledSet<String>> tokenize(String[] lines) {
		return tokenize(lines, false);
	}
	
	/**
	 * 
	 * @param lines
	 * @param repair
	 * @return
	 */
	public static final LabeledHash<String, LabeledSet<String>> tokenize(String[] lines, boolean repair) {
		AminoToken.displayCondensed = true;
		AminoSToken.displayCondensed = true;
		PrimaryToken.displayCondensed = true;
		ProteinBERT_Token.displayCondensed = true;
		SecondaryToken.displayCondensed = true;
		SecondarySimpleToken.displayCondensed = true;
		
		LabeledHash<String, LabeledSet<String>> data = new LabeledHash<String, LabeledSet<String>>();
		data.put(CHECKSUM_ERRORS, new LabeledSet<String>(CHECKSUM_ERRORS));
		
		for(String str: KEYS) {
			data.put(str, new LabeledSet<String>(str));
		}
		
		int successes = 0;
		int dre_fail = 0;
		int no_dssp = 0;
		int checksum_error = 0;
		
		for(int index = 0; index < lines.length; ++index) {
			String[] fields = lines[index].split(":");
			
			ChainID id = new ChainID();
			id.setProtein(fields[0]);
			id.setChain(fields[1]);
			id.setUniprot(fields[2]);
			if(!fields[3].equals("null")) {
				id.setGO(Integer.parseInt(fields[3]));
			}
			String pref = fields[0]+":"+fields[1]+":"+fields[2]+":"+fields[3]+":";
			
			//Run the garbage collector at regular intervals
			if((index+1)%1000 == 0) { System.gc(); }
			
			ProteinChain chain = null;
			try {
				chain = SequenceReader.readChain_pdb(id, true);
				if(repair && chain.id().uniprot() != null) { 
					chain = ChainRepair.repair(chain);
				}
				++successes;
			} catch (FileNotFoundRuntimeException FNFRE) {
				qerr("No DSSP file: " + lines[index]);
				data.get(CHECKSUM_ERRORS).add(lines[index]);
				++no_dssp;
				continue;
			} catch (DataRetrievalException e) {
				qerr(e.getMessage());
				++dre_fail;
				continue;
			}  catch (PDBChecksumException PDBCSE) {
				qerr("PDB Checksum Failure: " + lines[index]);
				++checksum_error;
				data.get(CHECKSUM_ERRORS).add(lines[index]);
				continue;
			}
			
			AminoToken[] tokens = AminoToken.parse(chain);
			AminoSToken[] s_tokens = AminoSToken.parse(chain);
			PrimaryToken[] p_tokens = PrimaryToken.parse(chain);
			SecondaryToken[] ss_tokens = SecondaryToken.parse(chain);
			SecondarySimpleToken[] sss_tokens = SecondarySimpleToken.parse(chain);
			ProteinBERT_Token[] pb_tokens = ProteinBERT_Token.parse(chain);		

			data.get(KEYS[0]).add(pref+getStr(tokens));
			data.get(KEYS[1]).add(pref+getStr(s_tokens));
			data.get(KEYS[2]).add(pref+getStr(p_tokens));
			data.get(KEYS[3]).add(pref+getStr(ss_tokens));
			data.get(KEYS[4]).add(pref+getStr(sss_tokens));
			data.get(KEYS[5]).add(pref+getStr(pb_tokens));
			
			if(index % 100 == 0) {
				double tmp = successes*100;
				tmp/= (index+1);
				qp("Successes: " + successes +"/"+index+" ("+FORMAT.format(tmp)+"%)"+ 
						" | DREs: " + dre_fail +
						" | Missing DSSP: " + no_dssp +
						" | CheckSum Error: " + checksum_error);
			}
		}
		
		return data;
	}
	
	public static final String[] getKeys() {
		final String[] keys = new String[KEYS.length];
		System.arraycopy(KEYS, 0, keys, 0, KEYS.length);
		return keys;
	}
	
}
