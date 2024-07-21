package dev.dssp_redo;

/**
 * Re-created without original
 * @translator Benjamin Strauss
 *
 */


public enum StructureType {
	Loop(' '),
	Alphahelix('H'),
	Betabridge('B'),
	Strand('E'),
	Helix_3('G'),
	Helix_5('I'),
	Helix_PPII('P'),
	Turn('T'),
	Bend('S');
	
	public final char letter;
	
	private StructureType(char ch) {
		letter = ch;
	}
}
