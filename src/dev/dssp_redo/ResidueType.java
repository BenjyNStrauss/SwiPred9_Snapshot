package dev.dssp_redo;

public enum ResidueType {
	kUnknownResidue('X', "UNK"),

	kAlanine		('A', "ALA"),       //	ala
	kArginine		('R', "ARG"),      //	arg
	kAsparagine		('N', "ASN"),    //	asn
	kAsparticAcid	('D', "ASP"),  //	asp
	kCysteine		('C', "CYS"),      //	cys
	kGlutamicAcid	('E', "GLU"),  //	glu
	kGlutamine		('Q', "GLN"),     //	gln
	kGlycine		('G', "GLN"),       //	gly
	kHistidine		('H', "HIS"),     //	his
	kIsoleucine		('I', "ILE"),    //	ile
	kLeucine		('L', "LEU"),       //	leu
	kLysine			('K', "LYS"),        //	lys
	kMethionine		('M', "MET"),    //	met
	kPhenylalanine	('F', "PHE"), //	phe
	kProline		('P', "PRO"),       //	pro
	kSerine			('S', "SER"),        //	ser
	kThreonine		('T', "THR"),     //	thr
	kTryptophan		('W', "TRP"),    //	trp
	kTyrosine		('Y', "TYR"),      //	tyr
	kValine			('V', "VAL");        //	val
	
	public final char letter;
	public final String name;
	
	private ResidueType(char letter, String name) {
		this.letter = letter;
		this.name = name;
	}
}
