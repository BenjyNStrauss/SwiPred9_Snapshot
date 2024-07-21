package tools.writer.csv.secStrComp;

import assist.exceptions.NotYetImplementedError;
import assist.util.LabeledList;
import biology.amino.AminoAcid;
import biology.amino.AminoPosition;
import biology.amino.BioMolecule;
import biology.amino.ChainObject;
import biology.amino.SecondaryStructure;
import biology.cluster.ChainCluster;
import biology.exceptions.alignment.ResidueAlignmentException;
import biology.protein.AminoChain;
import system.Instruction;
import utilities.LocalToolBase;

/**
 * Writes CSV files that compare secondary structure
 * Usable from both the command line and the GUI
 * @author Benjy Strauss
 *
 */

public class SecStrCompWriter extends LocalToolBase {
	private static final String[] FULL_STRUCTURE = {"-f", "-full", "-no-simple", "-8"};
	private static final String[] OUTFILE = {"-file", "-out", "-outfile"};
	
	public static void write(Instruction instr, Iterable<AminoChain<?>> alignedChains) {
		write(instr, 0, alignedChains);
	}
	
	/**
	 * Writes a secondary structure comparison csv(s) of each ProteinDataset
	 * 
	 * Designed to write secondary structure comparison of near-homologous chains
	 * 
	 * @param instr
	 */
	public static String[] write(Instruction instr, int reference, Iterable<AminoChain<?>> alignedChains) {
		//use simplified secondary structure
		boolean simplify = !instr.hasArgumentNamed(FULL_STRUCTURE);
		
		LabeledList<AminoChain<?>> usable = new LabeledList<AminoChain<?>>();
		int maxLen = 0;
		int index = 0;
		
		int nulls = 0;
		//do error-checking
		for(AminoChain<?> chain: alignedChains) {
			if(chain == null) {
				++nulls;
				if(reference == index) {
					qerr("Non-Fatal Error: Reference chain was null, a new reference will be automatically selected.");
					reference = 0;
				}
			} else if(chain.length() == 0) {
				qerr("Non-Fatal Error: Chain: "+ chain + " has length zero");
				if(reference == index) {
					qerr("Non-Fatal Error: Reference chain has length zero, a new reference will be automatically selected.");
					reference = 0;
				} 
			} else {
				usable.add(chain);
				maxLen = max(maxLen, chain.size());
				//update reference
				if(reference == index) { reference = usable.size()-1; }
			}
			++index;
		}
		if(nulls > 0) {
			qerr("Non-Fatal Error: "+nulls+" null chains were found");
		}
		
		LabeledList<String> outLines = new LabeledList<String>();
		//start building the csv
		StringBuilder builder = new StringBuilder();
		builder.append("Index,Residue,");
		for(AminoChain<?> chain: usable) { builder.append(chain.name() + ","); }
		trimLastChar(builder);
		outLines.add(builder.toString());
		
		for(index = 0; index < maxLen; ++index) {
			builder.setLength(0);
			
			BioMolecule aa0 = getBMol(usable.get(reference).get(index));
			//append the index and residue type of the reference
			builder.append(index + ",");
			if(aa0 == null || aa0.residueType() == null) {
				builder.append("-,");
			} else if(!aa0.residueType().proteinogenic()) {
				builder.append(aa0.toChar()+" ["+aa0.toCode()+"], ");
			} else {
				builder.append(aa0.toChar() + ",");
			}
			
			for(AminoChain<?> chain: usable) {
				BioMolecule aa = getBMol(chain.get(index));
				if(aa0 == null) {
					if(aa == null || aa.residueType() == null) {
						builder.append("-,");
					} else if(!aa.residueType().proteinogenic()) {
						builder.append(getChar(aa.secondary(), simplify)+" ["+aa.toCode()+"],");
					} else {
						builder.append(getChar(aa.secondary(), simplify)+ " ("+aa.toChar() + "),");
					}
				} else {
					if(aa == null || aa.residueType() == null) {
						builder.append("-,");
					} else {
						if(aa0.residueType() != aa.residueType()) {
							if(aa.residueType().proteinogenic()) {
								builder.append(getChar(aa.secondary(), simplify) + "(" + aa.toChar() + "),");
							} else {
								builder.append(getChar(aa.secondary(), simplify)+" ["+aa.toCode()+"],");
							}
							builder.append("(" + aa.toChar() + "),");
						} else {
							builder.append(getChar(aa.secondary(), simplify)+",");
						}
					}
				}
			}
			trimLastChar(builder);
			outLines.add(builder.toString());
		}
		
		String[] fileLines = new String[outLines.size()];
		outLines.toArray(fileLines);
		
		String outFile = instr.getFirstArgumentNamed(true, OUTFILE);
		if(outFile != null) {
			writeFileLines(outFile, fileLines);
		}
		
		return fileLines;
	}
	
	private static char getChar(SecondaryStructure secStr, boolean simplify) {
		if(simplify) {
			return (secStr != null) ? secStr.simpleClassify().toChar() : '*';
		} else {
			return (secStr != null) ? secStr.toChar() : '*';
		}
	}
	
	private static BioMolecule getBMol(ChainObject co) {
		if(co instanceof BioMolecule) {
			return (BioMolecule) co;
		} else if (co instanceof AminoPosition) {
			AminoPosition ap = (AminoPosition) co;
			AminoAcid aa = new AminoAcid(ap.residueType());
			aa.setSecondaryStructure(ap.secondary());
			return aa;
		} else {
			throw new NotYetImplementedError();
		}
	}
	
	/**
	 * Should write a CSV file of just chain secondary structures, along with the indexes and residue types
	 * 
	 * @param fileName: name of the output file
	 * @param cluster: cluster to write
	 */
	public static void writeSecondaryStructureComparisonCSV(String filename, ChainCluster cluster, boolean simplify) {
		if(!filename.startsWith(OUTPUT)) { filename = OUTPUT + filename; }
		if(!filename.endsWith(CSV)) { filename += CSV; }
		
		try {
			writeFileLines(filename, cluster.makeSecStrCSV(simplify));
		} catch (ResidueAlignmentException e) {
			e.printStackTrace();
		}
	}
}
