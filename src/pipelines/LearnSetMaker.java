package pipelines;

import assist.exceptions.FileNotFoundRuntimeException;
import assist.exceptions.IORuntimeException;
import assist.util.LabeledList;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import modules.encode.tokens.AminoToken;
import modules.encode.tokens.MandatorilyInaccurateRepresentationException;
import modules.encode.tokens.TokenUtils;
import tools.reader.fasta.SequenceReader;
import tools.reader.fasta.pdb.PDBChecksumException;
import utilities.LocalToolBase;
import utilities.exceptions.DataRetrievalException;

/**
 * Debug on: 7XIM:D
 * @author Benjamin Strauss
 * 
 * 
 *
 */

public class LearnSetMaker extends LocalToolBase {
	
	private static final int BUFFER_CAP = 1024;
	private static final int GC_INTERVAL = 256;
	private static final String INFILE = "input/full_pdb_nr_usable_ids.txt";
	private static final String FLAG = "-no-good";
	
	private static final int START_HERE = 514;
	
	public static void main(String[] args) {
		String[] lines = getFileLines(INFILE);
		
		int inputFile = START_HERE;
		int fails = 0;
		LabeledList<AminoToken[]> buffer = new LabeledList<AminoToken[]>();
		
		AminoToken.displayCondensed = true;
		
		for(int index = START_HERE*1024; index < lines.length; ++index) {
			if(lines[index].endsWith(FLAG)) {
				++fails;
				continue;
			}
			
			String[] parts = lines[index].split("[:_]");
			ChainID id = new ChainID();
			id.setProtein(parts[0]);
			id.setChain(parts[1]);
			
			ProteinChain chain = null;
			try {
				while (true) {
					try {
						chain = SequenceReader.readChain_pdb(id, true);
						break;
					} catch (IORuntimeException IORE) {
						if(IORE instanceof FileNotFoundRuntimeException) {
							break;
						}
						//not connected to internet
						qp("Pausing 5s for internet connectivity: " + lines[index]);
						pause(5000);
					}
				}
				
				if(chain == null) { continue; }
				
				qp("Read Chain: " + chain.id());
			} catch (DataRetrievalException e) {
				qerr("DataRetrievalException for id: "+id);
				lines[index] += "-dre" + FLAG;
				writeFileLines(INFILE, lines);
				++fails;
				continue;
			} catch (PDBChecksumException e) {
				lines[index] += "-bad-checksum" + FLAG;
				writeFileLines(INFILE, lines);
				++fails;
				qerr("PDBChecksumException for id: "+id + " fails="+fails);
				continue;
			} catch (FileNotFoundRuntimeException FNFRE) {
				lines[index] += "-no-dssp" + FLAG;
				writeFileLines(INFILE, lines);
				++fails;
				qerr("No DSSP exists for id: "+id + " fails="+fails);
				continue;
			}
			
			try {
				AminoToken[] tokens = TokenUtils.fixLen(AminoToken.parse(chain), 1024);
				buffer.add(tokens);
			} catch (NullPointerException e) {
				lines[index] += "-no-ss" + FLAG;
				writeFileLines(INFILE, lines);
				++fails;
				qerr("Null Pointer Exception - no Secondary Structure for id: "+id + " fails="+fails);
				continue;
			} catch (MandatorilyInaccurateRepresentationException e) {
				lines[index] += "-length" + FLAG;
				writeFileLines(INFILE, lines);
				++fails;
				qerr("Length Issue for: "+id + " fails="+fails);
				continue;
			}
			
			if(buffer.size() % GC_INTERVAL == 0) {
				System.gc();
			}
			
			if(buffer.size() == BUFFER_CAP) {
				writeBuffer(buffer, inputFile);
				qp("Wrote File #"+inputFile);
				++inputFile;
			}
		}
		writeBuffer(buffer, inputFile);
	}

	private static void writeBuffer(LabeledList<AminoToken[]> buffer, int inputFile) {
		String[] lines = new String[BUFFER_CAP];
		for(int index = 0; index < buffer.size(); ++index) {
			lines[index] = TokenUtils.getString(buffer.get(index));
		}
		writeFileLines("input/tokens/pdb-encoded-"+inputFile+".txt", lines);
		buffer.clear();
	}
	
}
