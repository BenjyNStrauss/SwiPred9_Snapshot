package tools.reader.fasta.pdb;

import java.io.File;

import assist.base.Assist;
import assist.base.FileToolBase;
import assist.util.LabeledBitSet;
import assist.util.LabeledList;
import biology.amino.AminoAcid;
import biology.amino.InsertCode;
import biology.amino.SecondaryStructure;
import biology.descriptor.ResAnnotation;
import biology.molecule.types.AminoType;
import biology.molecule.types.MoleculeType;
import biology.protein.ChainID;
import tools.reader.fasta.DSSP_Reader;
import utilities.LocalToolBase;

/**
 * TODO: read from DSSP directly; check against checksum
 * @author Benjamin Strauss
 *
 */

@SuppressWarnings("unused")
public class PDB_Seqres extends LabeledList<MoleculeType> {
	private static final long serialVersionUID = 1L;
	private SecondaryStructure[] dssp_structure;
	private LabeledBitSet sulfurBonds;
	
	public PDB_Seqres() { }
	public PDB_Seqres(String label) { super(label); }
}
