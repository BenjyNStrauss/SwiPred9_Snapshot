package tools.reader.fasta;

import java.io.File;
import java.io.FileNotFoundException;

import biology.amino.AminoAcid;
import biology.amino.UnknownResidueException;
import biology.molecule.types.AminoType;
import biology.protein.ChainFactory;
import biology.protein.ChainID;
import biology.protein.ProteinChain;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public class UniprotReader extends SequenceReaderBase {

	/**
	 * Reads a ProteinChain from a UniprotKB Fasta
	 * @param uniprotID
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static ProteinChain readSequence(ChainID id, File infile) {
		String lines[] = getFileLines(infile);
		StringBuilder sequenceCompactor = new StringBuilder();
		//skip the first line
		for(int index = 1; index < lines.length; ++index) {
			sequenceCompactor.append(lines[index]);
		}
		
		if(sequenceCompactor.toString().contains("<title>Error</title>")) {
			infile.delete();
			return null;
		}
		ProteinChain retval = ChainFactory.makeUniprot(id);
		//shift by 1 to make the list start at 1, not 0
		//done because bio-chemists number things differently than computer scientists
		retval.shiftList(-1);
		
		char[] seq = sequenceCompactor.toString().toCharArray();
		//qp(sequenceCompactor.toString());
		for(char ch: seq) {			
			try {
				retval.add(new AminoAcid(AminoType.parse(ch)));
			} catch (UnknownResidueException upe) {
				retval.add(new AminoAcid(AminoType.OTHER));
			}
		}
		
		return retval;
	}

}
