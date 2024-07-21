package biology.molecule.types;

/**
 * Contains combination Amino Acids
 * @author Benjamin Strauss
 *
 * Note: check anything that doesn't have "UNK"
 *
 */

public enum AminoCombination implements AminoComboConstants, MoleculeType {
	Beta_Aspartyl_Histidine			("BDH",		5287751,    "X", ASP, HIS),
	
	Cysteinyl_Glycine				("CYS-GLY",	 439498,   "CG", CYS, GLY),
	
	Desmosine						("???",	   15942890),
	Isodesmosine					("???",		  13811),
	
	Gamma_Glutamylcysteine			("3GC",		  123938,  "XC", GLU, CYS),
	Glutathione						("GSH",		  124886, "XCG", GLU, CYS, GLY),
	Hexylglutathione				("LEE",		   97536, "XXG", GLU, CYS, GLY),
	S_Hydroxy_Methyl_Glutathione	("AHE",		  447123, "XXG", GLU, CYS, GLY),
	
	Indole_3_Acetyl_Phenylalanine	("???",		 4255608,  "XF", PHENYLALANINE),
	Indole_3_Acetyl_Glycine			("IAG",		 6921768,  "XG", GLYCINE),
	Indole_3_Acetyl_Valine			("IAV",		 5165230,  "XV", VALINE),
	
	Norophthalamic_Acid				("BWS",		 5489007, "XAG", GLU, ALA, GLY),
	
	Etic__Acid						("KXV",	   138753301, $L),
	
	__002__("002",		 5487524,  "IL", "N-[(2R)-2-Benzyl-4-(Hydroxyamino)-4-Oxobutanoyl]-Isoleucyl-Leucine", ILE, LEU),
	__0EG__("0EG",		54415066,  "AP", "N-(Tert-Butoxycarbonyl)-L-Alanyl-N-[(1R)-1-(Dihydroxyboranyl)-2-Methylpropyl]-Prolinamide", ALA, PRO),
	__0FP__("0FP", 		23585980, "KXXXX", "N-(6-Aminohexanoyl)-3-Methyl-L-Valyl-3-Methyl-L-Valyl-N~1~-[(2s,3s)-3-Hydroxy-4-Oxo-4-{[(1r)-1-Phenylpropyl]amino}butan-2-Yl]-N~4~,N~4~-Dimethyl-L-Aspartamide", LYS, GLY, GLY, ASN, βALA ),
	__0HG__("0HG",	 	  444350, "XXG", "N-[(4S)-4-Ammonio-4-Carboxybutanoyl]-S-(4-Bromobenzyl)-Cysteinyl-Glycine", GLU, CYS, GLY),
	__0HH__("0HH",		  444057, "XXG", "L-Gamma-Glutamyl-S-Nonyl-Cysteinyl-Glycine", GLU, CYS, GLY),
	
	__143__("143",		17753762,	null,	"S-2,3-Dihydro-5-Glycin-2-Yl-Isoxazol-3-Yl-Cysteine"), //???
	__1JO__("1JO",		72376500,  "XXG", 	"D-Gamma-Glutamyl-S-(4-Phenylbutyl)-Cysteinyl-Glycine", GLU, CYS, GLY),
	__1R4__("1R4",		71304806,  "XXG",	"L-Gamma-Glutamyl-S-[2-(4-Nitrophenyl)-2-Oxoethyl]-Cysteinyl-Glycine", GLU, CYS, GLY),
	
	__2F7__("2F7",		91757941, 	null, 	"N-(4-Fluorobenzoyl)-L-Alloisoleucyl-O-[(S)-{[(1S)-1,3-Dicarboxypropyl]Amino}(Hydroxy)Phosphoryl]-Serine", ALLE, SER, UNK, GLU),
	__2F9__("2F9",		91757942,	null, 	"N-(4-Fluorobenzoyl)-L-Valyl-O-[(S)-{[(1S)-1,3-Dicarboxypropyl]Amino}(Hydroxy)Phosphoryl]-Serine", VAL, SER, UNK, GLU),
	__2G2__("2G2",		57394302,	null, 	"L-Gamma-Glutamyl-O-[(S)-{[(1S)-1,3-Dicarboxypropyl]Amino}(Hydroxy)Phosphoryl]-Serine", GLU, SER, GLU),
	
	__37Y__("37Y",	   137348131,	null,	"N-(Morpholin-4-Ylacetyl)-D-Alanyl-N-[(2S,4R)-1-Cyclohexyl-5-Hydroxy-4-Methyl-3-Oxopentan-2-Yl]-O-Methyl-L-Tyrosinamide"),
	__38N__("38N",	   137348132,	null, 	"N-(Morpholin-4-Ylacetyl)-L-Alanyl-N-[(2S,4R)-1-Cyclohexyl-5-Hydroxy-4-Methyl-3-Oxopentan-2-Yl]-O-Methyl-L-Tyrosinamide"),
	__39Q__("39Q",	   137348135,	null, 	"N-(Morpholin-4-Ylacetyl)-D-Alanyl-N-[(2S,4R)-5-Hydroxy-4-Methyl-3-Oxo-1-Phenylpentan-2-Yl]-O-Methyl-L-Tyrosinamide"),
	__3AI__("3AI",		 5497030,	"MAI",	"N-[(2S)-2-Amino-3-Phenylpropyl]-D-Methionyl-Alanyl-Isoleucine"),
	
	__56O__("56O",	   121493953,  "EE", "N-[(2R)-2-{[3-(3'-Chlorobiphenyl-4-Yl)-1,2-Oxazol-5-Yl]Methyl}-4-(Hydroxyamino)-4-Oxobutanoyl]-L-Alpha-Glutamyl-L-Alpha-Glutamine"),
	
	__9IN__("9IN",		 5287569, "VVL", "N-(3-Furoyl)-D-Valyl-L-Valyl-N~1~-((1R,2Z)-4-Ethoxy-4-Oxo-1-{[(3S)-2-Oxopyrrolidin-3-Yl]Methyl}But-2-Enyl)-D-Leucinamide"),
	
	__ABY__("ABY",		  447867, "XXG", "N-(4-Aminobutanoyl)-S-(4-Methoxybenzyl)-Cysteinyl-Glycine", CYS, GLY),
	__ACC__("ACC",		 5287601,  null, "N-[N-[2-Amino-6-Oxo-Hexanoic Acid-6-Yl]Cysteinyl]-S-Methyl-Cysteine"),
	__ACV__("ACV",		  448130,  null, "L-D-(A-Aminoadipoyl)-L-Cysteinyl-D-Valine"),
	__ACW__("ACW",		13891726,  null, "D-(L-A-Aminoadipoyl)-L-Cysteinyl-B-Methyl-D-Cyclopropyl-Glycine"),
	__ASV__("ASV",		 6398414,  null, "Delta-(L-Alpha-Aminoadipoyl)-L-Cysteinyl-D-Vinyl-Glycine", AAD, CYS, GLY),
	
	__BCV__("BCV",		16058631,  null, "D-(L-A-Aminoadipoyl)-L-Cysteinyl-D-Cyclopropyl-Glycine", AAD, CYS, GLY),
	__BGD__("BGD",		  446986,  null, "N-Benzoyl-L-Glutamyl-[4-Phosphono(Difluoromethyl)]-Phenylalanine-[4-Phosphono(Difluoro-Methyl)]-Phenylalanineamide"),
	__BLG__("BLG",		 5287797,  null, "4-O-(4-O-Sulfonyl-N-Acetylglucosamininyl)-5-Methylhydroxy-Proline-Taurine"),
	__BYG__("BYG",		49866735, "XXG", "L-Gamma-Glutamyl-S-{(4R)-4-[(6-Hydroxyhexyl)Sulfanyl]-7-Nitro-4,5-Dihydro-2,1,3-Benzoxadiazol-4-Yl}-Cysteinyl-Glycine", GLU, CYS, GLY),
	
	__CDH__("CDH",		 4369330,  null, "D-(L-A-Aminoadipoyl)-L-Cysteinyl-D-Isodehydro-Valine", AAD, CYS, dVAL),
	
	__DP1__("DP1",		  656910,  "XX", "L-N(Omega)-Nitro-Arginine-2,4-L-Diaminobutyric Amide"),
	__DP9__("DP9",		  656912,  null, "L-N(Omega)-Nitro-Arginine-(4R)-Amino-Proline Amide"),
	
	__EAL__("EAL",		 5462501,  "AP", "1-((2S)-2-{[(1S)-1-Carboxy-3-Phenylpropyl]Amino}Propanoyl)-Proline"),
	
	__GSF__("GSF",		 5326960, "XXG", "L-Gamma-Glutamyl-3-Sulfino-Alanyl-Glycine", GLU, CYS, GLY),
	__GSM__("GSM",		  115260, "XXG", "L-Gamma-Glutamyl-S-Methyl-Cysteinyl-Glycine", GLU, CYS, GLY),
	__GSO__("GSO",		 5288475, "XXG", "L-Gamma-Glutamyl-S-[(2S)-2-Hydroxy-2-Phenylethyl]-Cysteinyl-Glycine", GLU, CYS, GLY),
	__GVX__("GVX",	   101164969, "XXG", "L-Gamma-Glutamyl-S-[(2-Phenylethyl)Carbamothioyl]-Cysteinyl-Glycine", GLU, CYS, GLY),
	
	__HCG__("HCG",		 4369448,  null, "Delta-(L-Alpha-Aminoadipoyl)-Cysteinyl-Glycine", AAD, CYS, GLY),
	__HCV__("HCV",		14136481,  null, "N-[(5S)-5-Amino-5-Carboxypentanoyl]-Homocysteyl-D-Valine", AAD, HCY, dVAL),
	__HFV__("HFV",		 5288549,  null, "Delta-(L-Alpha-Aminoadipoyl)-Cysteinyl-L-3,3,3,3',3',3'-Hexafluoro-Valine"),
	__HKS__("HKS",		14178746,  null, "3-{[(2S)-2-Amino-2-Carboxyethyl]Sulfanyl}-5-Hydroxy-Tyrosine"),
	__HPI__("HPI",		  448836,  "FD", "N-(1-Carboxy-3-Phenylpropyl)Phenylalanyl-Alpha-Asparagine", PHE, ASP),
	
	__INN__("INN",		  444587,  "XA", "3,N(D,L-[2-(Hydroxyamino-Carbonyl)Methyl]-4-Methyl Pentanoyl)L-3-(Tert-Butyl)Glycyl-Alanine", GLY, ALA),
	__IYG__("IYG",		  447701,  "XG", "N-Alpha-Acetyl-3,5-Diiodotyrosyl-Glycine", TYR, GLY),
	__IYT__("IYT",		 5288651,  "XX", "N-Alpha-Acetyl-3,5-Diiodotyrosyl-D-Threonine", TYR, dTHR),
	
	__LSW__("LSW",		16007169,  "KW", "N~2~-[(1S)-1-Carboxy-3-Phenylpropyl]-Lysyl-Tryptophan", UNK, LYS, TRP),
	__LZ6__("LZ6",		24832039, "XXG", "Chlorambucil-Glutathione Conjugate", GLU, CYS, GLY),
	__LYB__("LYB",	   137349716, "XXXE", "O)-4-Carboxy-butyrylamino}-Pentanedioic Acid", GLU, GLU, GLU, GLU),
	
	__M2W__("M2W",		13652491,  null, "Delta-(L-Alpha-Aminoadipoyl)-L-Cysteinyl-O-Methyl-D-Threonine", AAD, CYS, dTHR),
	__M8F__("M8F",		56596539,  null, "N-[(5S)-5-Amino-5-Carboxypentanoyl]-L-Cysteinyl-D-Methionine", AAD, CYS, dMET),
	__MCJ__("MCJ",		49867288,  null, "N-[(2,3-Dihydroxyphenyl)Carbonyl]-O-[(2S)-2-{[(2,3-Dihydroxyphenyl)Carbonyl]Amino}-3-({N-[(2,3-Dihydroxyphenyl)Carbonyl]-L-Seryl}Oxy)Propanoyl]-D-Serine"),
	__MFN__("MFN",		49867302,  null, "N-[4,5,7-Tricarboxyheptanoyl]-L-Gamma-Glutamyl-N-{2-[4-({5-[(Formylamino)Methyl]-3-Furyl}Methoxy)Phenyl]Ethyl}-D-Glutamine"),
	
	__NQG__("NQG",		49867397, "XRW", "5-Azanylidyne-N-[(2S)-4-Ethoxy-2-Hydroxy-4-Oxobutanoyl]-L-Norvalyl-Arginyl-Tryptophanamide", UNK, ABU, ARG, TRP),
	__NQI__("NQI",		49867398, "XRW", "N~2~-[(2S)-2-{[(2R)-4-Ethoxy-2-Hydroxy-4-Oxobutanoyl]Amino}Pent-4-Enoyl]-L-Arginyl-Tryptophanamide", UNK, GLY, ARG, TRP),
	
	__OCV__("OCV",		 4369213,  null, "N6-[(1R)-2-{[(1R)-1-Carboxy-2-Methylpropyl]Oxy}-1-(Mercaptomethyl)-2-Oxoethyl]-6-Oxo-D-Lysine", AAD, CYS, dVAL),
	__OIR__("OIR",		  448249,  "FA", "N-(3-Phenyl-2-Sulfanylpropanoyl)Phenylalanyl-Alanine", PHE, ALA),
	__OQ4__("0Q4",	   137347882,  null, "N-[(2R)-2-({N~5~-[amino(iminio)methyl]-Ornithyl-Valyl}amino)-4-methylpentyl]-Phenylalanyl-alpha-Glutamyl-Alanyl-Norleucinamide"),
	
	__P0H__("P0H",		  439540,  "GP", "N-[(Benzyloxy)Carbonyl]Glycyl-Proline", GLY, PRO),
	
	__QEG__("QEG",		50991592,  "QE", "N~2~-{3-[4-(4-Phenylthiophen-2-Yl)Phenyl]Propanoyl}-Glutaminyl-Alpha-Glutamine", GLN, GLU),
	__QRG__("QRG",		53377507,  "DM", "N-Acetyl-L-Alpha-Aspartyl-Methionine", ASP, MET),
	
	__R45__("R45",		70698423,  "EE", "N-{(2S)-3-[(S)-(4-Bromophenyl)(Hydroxy)Phosphoryl]-2-[(3-Phenyl-1,2-Oxazol-5-Yl)Methyl]Propanoyl}-L-Alpha-Glutamyl-L-Alpha-Glutamine"),
	__R47__("R47",		44580458,  "EE", "N-[(2S)-3-[(S)-(4-Bromophenyl)(Hydroxy)Phosphoryl]-2-{[3-(3'-Chlorobiphenyl-4-Yl)-1,2-Oxazol-5-Yl]Methyl}Propanoyl]-L-Alpha-Glutamyl-L-Alpha-Glutamine"),
	__REX__("REX",		 5289280,  null, "Glycyl-L-Alpha-Amino-Epsilon-Pimelyl-D-Alanyl-D-Alanine", GLY, dALA, dALA),
	__REY__("REY",		 5289281,  null, "Glycyl-L-Alpha-Amino-Epsilon-Pimelyl-D-Alanine", GLY, dALA),
	__RS8__("RS8",	   135566777,  null, "N-[(2-Amino-4-Oxo-1,4-Dihydropteridin-7-Yl)Carbonyl]Glycyl-Phenylalanine", UNK, GLY, PHE),
	
	__SFK__("SFK",		28777137,  null, "N-(4-Methylpentanoyl)-Phenylalanine", LEU, PHE),
	
	__T01__("T01",		73386646,  null, "N-(4-Fluorobenzoyl)-L-Gamma-Glutamyl-O-[(S)-{[(1S)-1,3-Dicarboxypropyl]Amino}(Hydroxy)Phosphoryl]-Serine", GLU, SER, UNK, GLU),
	__T57__("T57",		73386645,  null, "N-{6-[(4-Fluorobenzoyl)Amino]Hexanoyl}-L-Gamma-Glutamyl-5-{[(S)-{[(1S)-1,3-Dicarboxypropyl]Amino}(Hydroxy)Phosphoryl]Oxy}-Norvaline"),
	__TGG__("TGG",		 6857725, "XXG", "Gamma-Glutamyl-S-(1,2-Dicarboxyethyl)Cysteinyl-Glycine", GLU, CYS, GLY),
	
	__UAG__("UAG",		  449538,  null, "Uridine-5'-Diphosphate-N-Acetylmuramoyl-Alanine-D-Glutamate"),
	
	__V10__("V10",		24901722,  null, "N^6^-[(1R)-2-[(1R)-1-Carboxy-2-(Methylsulfanyl)Ethoxy]-2-Oxo-1-(Sulfanylmethyl)Ethyl]-6-Oxo-Lysine"),
	__V20__("V20",		24860525,  null, "N6^-[(1R)-2-[(1S)-1-Carboxy-2-(Methylsulfanyl)Ethoxy]-2-Oxo-1-(Sulfanylmethyl)Ethyl]-6-Oxo-Lysine"),
	__VAZ__("VAZ",		42627549,  null, "N^6^-{(1R)-2-{[(1S,2R)-1-Carboxy-2-Hydroxy-2-(Methylsulfanyl)Ethyl]Oxy}-1-[(Oxidosulfanyl)Methyl]-2-Oxoethyl}-6-Oxo-Lysine"),
	__VB1__("VB1",		25021180,  null, "N^6^-[(1R)-2-{[(1S)-1-Carboxypropyl]Amino}-2-Oxo-1-(Sulfanylmethyl)Ethyl]-6-Oxo-Lysine"),

	__W05__("W05",		 6082085,  null, "Delta-(L-Alpha-Aminoadipoyl)-Cysteinyl-D-Alanine", AAD, CYS, dALA),
	__WT4__("WT4",		71295745,  null, "N-[(5S)-5-Amino-5-Carboxypentanoyl]-L-Homocysteyl-S-Methyl-D-Cysteine", AAD, HCY, dCYS),

	__ZRA__("ZRA",	   131704251,  "XA", "Benzoyl-Arginine-Alanine-Fluoro-Methyl Ketone", ORN, ALA),
	__ZYA__("ZYA",		15118984,  "YA", "Benzoyl-Tyrosine-Alanine-Fluoro-Methyl Ketone", TYR, ALA), //?
	;
	
	public final String code;
	public final int pubChem_id;
	public final String letter;
	public final String name;
	public final PeptideLink link;
	private final MoleculeType[] contents;
	
	private AminoCombination(String code, int id, MoleculeType... contents) { 
		this(code, id, "X", $_, null, contents);
	}
	
	private AminoCombination(String code, int id, PeptideLink link, MoleculeType... contents) { 
		this(code, id, "X", link, null, contents);
	}
	
	private AminoCombination(String code, int id, String name, MoleculeType... contents) { 
		this(code, id, "X", $_, name, contents);
	}
	
	private AminoCombination(String code, int id, String letter, String name, MoleculeType... contents) {
		this(code, id, letter, $_, name, contents);
	}
	
	/**
	 * 
	 * @param code
	 * @param name
	 * @param baseForm
	 * @param contents: Amino Acids that are part of the combination according to the PDB
	 */
	private AminoCombination(String code, int id, String letter, PeptideLink link,
			String name, MoleculeType... contents) {
		this.code = code;
		this.pubChem_id = id;
		this.link = link;
		this.name = name;
		this.letter = letter;
		this.contents = contents;
	}
	
	@Override
	public String toCode() { return code; }
	
	public MoleculeType[] contents() {
		AminoType[] temp = new AminoType[contents.length];
		System.arraycopy(contents, 0, temp, 0, contents.length);
		return contents;
	}
	
	public String toString() { 
		return (name != null) ? name : fixString(super.toString());
	}
}
