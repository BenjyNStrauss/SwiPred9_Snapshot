package biology.molecule.types;

import assist.base.ToolBeltLimited;
import biology.amino.UnknownResidueException;
import biology.molecule.MoleculeLookup;
import modules.descriptor.entropy.E6;
import utilities.LocalToolBase;

/**
 * Represents an Animo Acid residue type
 * Includes modified and non-proteinogenic residues
 * 
 * 3-Char codes:
 * 		• non-ASCII UTF-16 characters indicate that the 3-char code came from SwiPred
 * 		"ÆXX" implies acetylated
 * 
 * @author Benjamin Strauss
 * 
 * '?' = no sequence char in PubChem
 * '*' = no entry in PubChem
 * 
 * Issue(s): Perceived Redundancy
 * 		How are pubchem's "6657" and "80283" different?
 * 		How is 'BETA-L-ASPARTIC ACID' different from normal Aspartic Acid? 
 * 
 */

public enum AminoType implements MoleculeType, ToolBeltLimited {
	
	/**************************************************************************************** TODO
	 *										System Values									*
	 ****************************************************************************************/
	OTHER				("UNK", 		-1,		'X',	'X',	$DL,	E6.UNACCOUNTED_FOR),
	ANY					("XAA",		 	-1,		'X', 	'#',	$DL,	E6.UNACCOUNTED_FOR),
	INVALID				("INVALID",  	-2,		'_', 	'#',	$_,		E6.UNACCOUNTED_FOR),
	
	/**************************************************************************************** TODO
	 *										L-Amino Acids									*
	 ****************************************************************************************/
	
	Alanine				("ALA",		   5950,	'A',	'A',	$L,		E6.ALIPHATIC),
	Cysteine			("CYS",		   5862,	'C',	'C',	$L,		E6.ALIPHATIC),
	Aspartic__Acid		("ASP",		   5960,	'D',	'D',	$L,		E6.POSITIVE),
	Glutamic__Acid		("GLU", 	  33032,	'E',	'E',	$L,		E6.POSITIVE),
	Phenylalanine		("PHE", 	   6140,	'F',	'F',	$L,		E6.AROMATIC),
	Glycine				("GLY", 	    750,	'G',	'G',	$DL,	E6.SPECIAL),	
	Histidine			("HIS", 	   6274,	'H',	'H',	$L,		E6.AROMATIC),
	Isoleucine			("ILE", 	   6306,	'I',	'I',	$L,		E6.ALIPHATIC), 
	Lysine				("LYS", 	   5962,	'K',	'K',	$L,		E6.NEGATIVE),
	Leucine				("LEU", 	   6106,	'L',	'L',	$L,		E6.ALIPHATIC),
	Methionine			("MET", 	   6137,	'M',	'M',	$L,		E6.ALIPHATIC),
	Asparagine			("ASN", 	   6267,	'N',	'N',	$L,		E6.POLAR),
	Pyrrolysine			("PYL", 	5460671,	'O',	'O',	$L,		LYSINE),
	Proline				("PRO", 	 145742,	'P',	'P',	$L,		E6.SPECIAL),
	Glutamine			("GLN", 	   5961, 	'Q', 	'Q',	$L,		E6.POLAR),
	Arginine			("ARG", 	   6322,	'R',	'R',	$L,		E6.NEGATIVE),
	Serine				("SER", 	   5951,	'S',	'S',	$L,		E6.POLAR),
	Threonine			("THR", 	   6288,	'T',	'T',	$L,		E6.POLAR),
	Selenocysteine		("SEC", 	6326983,	'U',	'U', 	$L,		CYSTEINE),
	Valine				("VAL", 	   6287,	'V',	'V',	$L,		E6.ALIPHATIC),
	Tryptophan			("TRP", 	   6305,	'W',	'W',	$L,		E6.AROMATIC),
	Tyrosine			("TYR", 	   6057,	'Y',	'Y',	$L,		E6.AROMATIC), 
	
	/**************************************************************************************** TODO
	 *										D-Amino Acids									*
	 ****************************************************************************************/
	
	D_Alanine			("DAL",		  71080,	'A',	'a',	$D,		ALANINE,		"DALA"),
	D_Cysteine			("DCY", 	  92851,	'C',	'c',	$D,		CYSTEINE,		"DCYS"),
	D_Aspartic__Acid	("DAS", 	  83887,	'D',	'd',	$D,		ASPARTIC_ACID,	"DASP"),
	D_Glutamic__Acid	("DGL", 	  23327,	'E',	'e',	$D,		GLUTAMIC_ACID,	"DGLU"),
	D_Phenylalanine		("DPN", 	  71567,	'F',	'f',	$D,		PHENYLALANINE,	"DPHE"),
	D_Histidine			("DHI", 	  71083,	'H',	'h',	$D,		HISTIDINE,		"DHIS"),
	D_Isoleucine		("DIL",		  76551,	'I',	'i',	$D,		ISOLEUCINE,		"DILE"),
	D_Lysine			("DLY", 	  57449,	'K',	'k',	$D,		LYSINE,			"DLYS"),
	D_Leucine			("DLE",		 439524,	'L',	'l',	$D,		LEUCINE,		"DLEU"),
	D_Methionine		("MED",		  84815,	'M',	'm',	$D,		METHIONINE,		"DMET"),
	D_Asparagine		("DSG", 	 439600,	'N',	'n',	$D,		ASPARAGINE,		"DASN"),
	D_Proline			("DPR", 	   8988,	'P',	'p',	$D,		PROLINE,		"DPRO"),
	D_Glutamine			("DGN", 	 145815,	'Q',	'q',	$D,		GLUTAMINE,		"DGLN"),
	D_Arginine			("DAR", 	  71070,	'R',	'r',	$D,		ARGININE,		"DARG"),
	D_Serine			("DSN", 	  71077,	'S',	's',	$D,		SERINE,			"DSER"),
	D_Threonine			("DTH", 	  69435,	'T',	't',	$D,		THREONINE,		"DTHR"),
	D_Selenocysteine	("DƲC", 	6398619,	'U',	'u',	$D,		SELENOCYSTEINE),
	D_Valine			("DVA", 	  71563,	'V',	'v',	$D,		VALINE,			"DVAL"),
	D_Tryptophan		("DTR", 	   9060,	'W',	'w',	$D,		TRYPTOPHAN, 	"DTRP"),
	D_Tyrosine			("DTY", 	  71098,	'Y', 	'y',	$D,		TYROSINE,		"DTYR"),
	
	/**************************************************************************************** TODO
	 *								Non-Proteinogenic L-Amino Acids							*
	 ****************************************************************************************/
	
	Ornithine			("ORN",		   6262,	'X',	'Þ',	$L,		__OTHER__),
	Selenomethionine	("MSE", 	  15103,	'X', 	'µ',	$L,		METHIONINE),
	Thyronine			(THYRONINE, 5461103,	'X',			$L,		TYROSINE),
	
	/**************************************************************************************** TODO
	 *								Non-Proteinogenic D-Amino Acids							*
	 ****************************************************************************************/
	
	D_Ornithine			("ORD",		  71082,	'X',	'þ',	$D,		__OTHER__,		"DORN"),
	D_Selenomethionine	("???", 	5460538,	'X', 	'µ',	$D,		"MSE"),
	D_Thyronine			("???",		6951212,	'X',			$D,		THYRONINE),
	
	/**************************************************************************************** TODO
	 *								N-Acetylated Amino Acids								*
	 ****************************************************************************************/
	
	Acetyl_Alanine			("AYA", 	   7345,	'A',	'Ξ',	$L,		ALANINE),
	Acetyl_Aspartic__Acid	("KKD",		  65065,	'D',	'Δ',	$L,		ASPARTIC_ACID),
	Acetyl_Cysteine			("SC2",		  12035,	'C',			$P,		CYSTEINE),
	Acetyl_Phenylalanine	("5CR",		   2000,	'F',	'Φ',	$L,		PHENYLALANINE),
	Acetyl_Leucine			("LAY",		   1995,	'L',	'Λ',	$L,		LEUCINE),
	Acetyl_Methionine		("AME",		 448580,	'M',			PeptideLink.L_AMINO_TERMINUS,	METHIONINE),
	Acetyl_Proline			("N7P",		  66141,	'P',	'Π',	$L,		PROLINE),
	Acetyl_Glutamine		("NLQ",		  25561,	'Q',			$L,		GLUTAMINE),
	Acetyl_Serine			("SAC", 	  65249, 	'S',	'Σ',	$L,		SERINE),
	Acetyl_Threonine		("THC",		 152204,	'T',	'Θ',	$L,		THREONINE),
	Acetyl_D_Proline		("N8P",		  66141,	'P',			$D,		D_PROLINE),	
	
	N6_Acetyl_Lysine		("ALY",		  92832,	'X',			$L,		LYSINE),
	
	O_Acetyl_Serine			("OAS",		  99478,	'X',			$L,		SERINE), //see pubchem
	S_Acetyl_Cysteine		("SCY",    10130120,	'X',			$L,		CYSTEINE),
	
	Acetyl_Glutamyl_Phosphate("X2W",	 440236,	'X',			$L,		GLUTAMIC_ACID),
	
	/**************************************************************************************** TODO
	 *									Allo Amino Acids									*
	 ****************************************************************************************/
	Allo_Isoleucine			("IIL", 	  99288,	'X',		$L,	ISOLEUCINE,	 null,	"ALLE"),
	Allo_Threonine			("ALO", 	  99289,	'X',		$L,	THREONINE),
	
	D_Allo_Isoleucine		("28J", 	  94206,	'X',		$D,	ISOLEUCINE,	 null,	"dALLE"),
	D_Allo_Threonine		("2TL",		  90624,	'X',		$D,	THREONINE),
	
	/**************************************************************************************** TODO
	 *									Amino Acids Amide									*
	 ****************************************************************************************/
	Phenylalanine__Amide	("NFA",		 445694,	'F',			$L,		PHENYLALANINE),
	Proline__Amide			("LPD", 	 111306,	'P', 			$L,		PROLINE),
	
	D_Proline__Amide		("PR9", 	 447554,	'P', 	'p',	$D,		D_PROLINE),
	
	/**************************************************************************************** TODO
	 *									"Amino" Amino Acids									*
	 ****************************************************************************************/
	_2_Amino_Histidine				("B3U",		12071049,	'?',	$L,		HISTIDINE),
	
	_3_Amino_Alanine				("DPP",		  97328,	'X',	$L,		ALANINE,	 null,	"Dap"),
	_3_Amino_Tyrosine				("TY2", 	3084398,	'X',	$L,		TYROSINE),
	
	_3_Amino_D_Alanine				("2RA", 	 638152,	'X',	$D,		D_ALANINE),
	_3_Amino_6_Hydroxy_Tyrosine		("TYQ",	   17754207,	'?',	$L,		TYROSINE),
	
	_4_Amino_Phenylalanine			("HOX", 	 151001,	'X',	$L,		PHENYLALANINE),
	_4_Amino_Tryptophan				("4IN",	   17753790,	'X',	$L,		TRYPTOPHAN),
	
	Amino_Benzofurazan_O_Tyrosine	("TYN",	   49867773,	'?',	$L,		TYROSINE),
	
	/**************************************************************************************** TODO
	 *										Beta Amino Acids								*
	 ****************************************************************************************/
	
	Beta_Alanine					("BAL",			239, 	'X',	$P,		ALANINE,	"βALA"),
	Beta_2_Thienyl_Alanine			("TIH",		 146719,	'X',	$L,		BETA_ALANINE),
	//how is this different than Aspartic Acid?
	Beta_Aspartic__Acid				("IAS",  -381334852,	'?',	PeptideLink.Lβ,		ASPARTIC_ACID),
	
	/**************************************************************************************** TODO
	 *									Butyrylated Amino Acids								*
	 ****************************************************************************************/
	_2_Aminobenzoic_Acid			("BE2",			227,	'X',	$L,		__OTHER__),
	_2_Aminobutyric__Acid			("ABA",		  80283,	'X',	$L,		__OTHER__),
	_2$4_Diaminobutyric__Acid		("DAB",			470,	'X',	$L,		__OTHER__),
	
	_2_Benzamidoethanoic__Acid		("GZB",			464,	'X',	PeptideLink.L_AMINO_TERMINUS,	__OTHER__),
	S_Butyryl_Cysteine				("CY4",	   13817366,	'X',	$L,		 CYSTEINE),
	
	/**************************************************************************************** TODO
	 *								Carbamoylated Amino Acids								*
	 ****************************************************************************************/
	Carbamoyl_Alanine				("NCB", 	 426409,	'A',	$L,		ALANINE),
	
	/**************************************************************************************** TODO
	 *								Carboxylated Amino Acids								*
	 ****************************************************************************************/
	N_Carboxy_Methionine			("CXM",	   17753927,	'M',	$L,		METHIONINE),
	N6_Carboxy_Lysine				("KCX",	   17754054,	'X',	$L,		LYSINE),
	Gamma_Carboxy_Glutamic__Acid	("CGU", 	 104625,	'X',	$L,		GLUTAMIC_ACID,	"GLA"),
	Carboxymethylated__Cysteine		("CCS",   348284821,	'?',	$L,		CYSTEINE),

	/**************************************************************************************** TODO
	 *									Chloro Amino Acids									*
	 ****************************************************************************************/	
	_3_Chloro_Tyrosine				("3CT", 	 110992,	'X',	$L,		TYROSINE),
	_3_Chloro_D_Alanine				("C2N",		 439771,	'X',	$D,		ALANINE),
	
	/**************************************************************************************** TODO
	 *									Dansylated Amino Acids								*
	 ****************************************************************************************/
	Dansyl_Phenylalanine			("9NF",    13734199,	'F',	$L,		PHENYLALANINE),
	Dansyl_Asparagine				("9DN",		 102556,	'N',	$L,		ASPARAGINE),
	Dansyl_Arginine					("9NR",	   13734230,	'R',	$L,		ARGININE),
	Dansyl_Norvaline				("9NV",	   13743593,	'X',	$L,		VALINE),
	Dansyl_Sarcosine				("9DS", 	 122239,	'G',	$L,		"SAR"),

	/**************************************************************************************** TODO
	 *									Ethylated Amino Acids								*
	 ****************************************************************************************/
	Ethyl__Glycinate				("GEE",		  12176,	'G',	$L,		GLYCINE),
	N_Ethyl_Glycine					("2JC",		 316542,	'G',	$L,		GLYCINE),
	Monoethyl_Phosphoryl_Serine		("MIR",		3035913,	'?',	$L,		SERINE),
	
	/**************************************************************************************** TODO
	 *									Fluorolated Amino Acids								*
	 ****************************************************************************************/
	Fluoro_Tryptophan				("FTR",		 688357,	'X',	$L,		TRYPTOPHAN),		
	_2_Fluoro_Histidine				("2HF",    49866463,	'?',	$L,		HISTIDINE),
	_4_Fluoro_Phenylalanine			("PFF",		   4654,	'X',	$L,		PHENYLALANINE),
	_4_Fluoro_Tryptophan			("4FW", 	 101198,	'X',	$L,		TRYPTOPHAN),
	_6_Fluoro_Tryptophan			("FT6", 	  94937,	'X',	$L,		TRYPTOPHAN),
	_2$3_Difluoro_Tyrosine			("FY2",	   46863882,	'?',	$L,		TYROSINE),
	_3$5_Difluoro_Tyrosine			("F2Y",		 120656,	'X',	$L,		TYROSINE),
	Trifluoro_Alanine				("FLA",		  87123,	'X',	$L,		ALANINE),
	_2$3$5_Trifluoro_Tyrosine		("FY3",    71349658,	'?',	$L,		TYROSINE),
	_2$3$6_Trifluoro_Tyrosine		("51T",    71349657,	'?',	$L,		TYROSINE),
	N6_Trifluoro_Acetyl_Lysine		("FAK",		7009573,	'X',	$L,		LYSINE),
	Trans_4_Fluoro_Proline			("FP9",	   11902999,	'X',	$L,		PROLINE),
	Aspartate_Beryllium_Trifluoride	("BFD",	 -381328709,	'?',	$L,		__OTHER__),
	
	/**************************************************************************************** TODO
	 *									Homo Amino Acids									*
	 ****************************************************************************************/
	Homo_Cysteine					("HCS",		  91552,	'X',	$L,		CYSTEINE, null, "HCY"),
	Homo_Serine						("HSE",			779,	'X',	$L,		SERINE),
	Homo_Arginine					("HRG",		   9085,	'X',	$L,		ARGININE),
	_2_Aminoadipic__Acid			("UN1",		  92136,	'X',	$L,		GLUTAMIC_ACID, null, "AAD"),
	S_Adenosyl_Homo_Cysteine		("SAH",		 439155,	'?',	$L,		CYSTEINE),
	Canavanine						("GGB",		 439202,	'X',	$L,		"HSE"),
	
	/**************************************************************************************** TODO
	 *									Hydroxylated Amino Acids							*
	 ****************************************************************************************/
	Hydroxy_Lysine					("LYZ",		3032849,	'X',	$L,		LYSINE),
	Hydroxy_Methionine				("ME0",    49867299,	'X',	$L,		METHIONINE),
	Hydroxy_Proline					("HYP", 	   5810,	'X',	$L,		PROLINE),
	_2_Hydroxy_Tryptophan			("TRO", 	3382782,	'X',	$L,		TRYPTOPHAN),
	_3_Hydroxy_Proline				("HY3",		 150779,	'?',	$L,		PROLINE),

	_7_Hydroxy_Tryptophan			("0AF",		9859285,	'X',	$L,		TRYPTOPHAN),
	S_Hydroxy_Cysteine				("CSO", 	 165339,	'X',	$L,		CYSTEINE),
	_3$3_Dihydroxy_Alanine			("DDZ",	   49866897,	'?',	$L,		ALANINE),
	_3$4_Dihydroxy_Phenylalanine	("DAH", 	   6047,	'X',	$L,		PHENYLALANINE),
	_6$7_Dihydroxy_Tryptophan		("TOQ",	  137350056,	'?',	$L,		TRYPTOPHAN),
	Allo_Hydroxy_Lysine				("AHYL",	 439437,	'X',	null,	LYSINE),
	Hydroxy_Ethyl_Cysteine			("OCY",		 119224,	'X',	$L,		CYSTEINE),
	N_Omega_Hydroxy_Arginine		("HAR",		 123895,	'X',	$L,		ARGININE),
	S_Hydroperoxy_Cysteine			("2CO",    24978498,	'X',	$L,		CYSTEINE),
	Nor_N_Omega_Hydroxy_Arginine	("NNH", 	 446124,	'X',	$L,		ARGININE),
	Thiarsa_Hydroxy_Cysteine		("CZZ",    17753936,	'?',	$L,		CYSTEINE),
	
	/**************************************************************************************** TODO
	 *									Iodolated Amino Acids								*
	 ****************************************************************************************/
	Iodo_Phenylalanine				("PHI",		 134497,	'X',	$L,		PHENYLALANINE),
	_3_Iodo_Tyrosine				("IYR",		 439744,	'X',	$L,		TYROSINE),
	Diiodo_Tyrosine					("TYI", 	   9305,	'X',	$L,		TYROSINE),
	P_Iodo_D_Phenylalanine			("IOY",		2733279,	'X',	$D,		PHENYLALANINE),
	
	/**************************************************************************************** TODO
	 *									Meta Amino Acids									*
	 ****************************************************************************************/
	Meta_Tyrosine					("MTY",		6950578,	'X',	$L,		TYROSINE),
	Meta_Nitro_Tyrosine				("NIY",		  65124,	'?',	$L,		"MTY"),
	
	/**************************************************************************************** TODO
	 *									Methylated Amino Acids								*
	 *																						*
	 * Note[0] Sarcosine = N-Methyl-Glycine													*
	 ****************************************************************************************/
	Methyl_Isoleucine				("IML",		5288628,	'I',			$L,		ISOLEUCINE),
	
	N_Methyl_Alanine				("MAA",		5288725, 	'A',	'א',		$L,		ALANINE),
	N_Methyl_Cysteine				("NCY",		 452303, 	'C',	'כּ',		$L,		CYSTEINE),
	N_Methyl_Phenylalanine			("MEA", 	6951135,	'F',	'פּ',		$L,		PHENYLALANINE),	
	Sarcosine						("SAR",		   1088,	'G',	'ג',		$DL,	GLYCINE),
	N_Methyl_Leucine				("MLE", 	2777993,	'L',	'ל',		$L,		LEUCINE),
	N_Methyl_Methionine				("MME",		6451891,	'M',	'מ',		$L,		METHIONINE),
	N_Methyl_Asparagine				("MEN",		 100393,	'X',	'נ',		$L,		ASPARAGINE),
	
	N5_Methyl_Glutamine				("MEQ", 	 439925,	'X',	'ם',		$L,		GLUTAMINE),
	N6_Methyl_Lysine				("MLZ",		 164795,	'X',	'ך',		$L,		LYSINE),
	
	_2_Methyl_Alanine				("AIB",		   6119,	'X',			$L,		ALANINE),	
	_2_Methyl_Aspartic__Acid		("0A0",		2724884,	'X',			$L,		ASPARTIC_ACID),
	_2_Methyl_Leucine				("2ML",		 446181,	'X',			$L,		LEUCINE),
	
	_3_Methyl_Valine				("TBG",		 164608,	'X',			$L,		GLYCINE),
	_4_Methyl_Histidine				("HIC",		7020397,	'X',			$L,		HISTIDINE),
	_5_Methyl_Tryptophan			("D0Q",		 150990,	'X',			$L,		TRYPTOPHAN),
	
	L_3_Phenyllactic__Acid			("HFA", 	 444718,	'X',			$L,		__OTHER__),
	
	N_Methyl_D_Aspartic__Acid		("OEM",		  22880,	'D',	'd',	$D,		D_ASPARTIC_ACID),
		
	O_Methyl_Serine					("7C9",		  88250,	'X',			$L,		SERINE), 		//also 97963
	
	N_Dimethyl_Lysine				("MLY",		 193344,	'X',			$L,		LYSINE),
	Dimethyl_Alanine				("LAL",		5488191,	'A',			$L,		ALANINE),
	Dimethyl_Arginine				("DA2", 	 123831,	'X',			$L,		ARGININE),
	N_Trimethyl_Lysine				("M3L", 	 440121,	'X',			$L,		LYSINE),
	N3$__N4_Dimethyl_Arginine		("2MR",		 169148,	'X',			$L,		ARGININE),
	S_Methyl_Cysteine				("SMC",		  24417,	'X',			$L,		CYSTEINE),
	
	N$N$N_Trimethyl_Methionine		("4MM",	   49866512,	'X',			PeptideLink.L_AMINO_TERMINUS,	METHIONINE),
	
	Tetramethyl_Lysine				("DM0", 	 124784,	'M',			$L,		LYSINE),
	
	S_Methyl_Mercury_Cysteine		("CMH",    49866810,	'?',			$L,		CYSTEINE),
	S_Dimethyl_Arsenic_Cysteine		("CAS",    17753880,	'?',			$L,		CYSTEINE),		
	Carbocymethylated_Glutamic__Acid("CGA",    52941768,	'?',			$L,		GLUTAMIC_ACID),	
	N1_Methylated_Histidine			("MHS", 	  64969,	'X',			$L,		HISTIDINE),
	
	/**************************************************************************************** TODO
	 *										Nitro Amino Acids								*
	 ****************************************************************************************/
	N_Omega_Nitro_Arginine			("NRG",		 440005,	'X',			$L,		ARGININE),
	_6_Nitro_Norleucine				("6HN",	   11680096, 	'X',			$L,		NORLEUCINE),
	
	/**************************************************************************************** TODO
	 *										Nor Amino Acids									*
	 ****************************************************************************************/
	Norleucine						("NLE",		  21236,	'X',	'Ł',	$L,		LEUCINE),
	//Norvaline is an isomer of the more common amino acid valine
	Norvaline						("NVA",		  65098,	'X',	'Ʋ',	$L,		VALINE),
	Norleucyl_Phenylalanine			("0EK",   137347850,	'X',			$P,		__OTHER__),
	D_Norleucine					("DNE", 	 456468,	'X',	'ł',	$D,		D_LEUCINE),
	
	_6_Azido_Norleucine				("NOT",	   16666241,	'X',			$L,		NORLEUCINE),
	_5_Oxo_Norleucine				("ONL", 	5289072,	'X',			$L,		NORLEUCINE),
	
	/**************************************************************************************** TODO
	 *									O-Sulfo Amino Acids									*
	 ****************************************************************************************/
	O_Sulfo_Serine					("OSE",		 164701, 	'X',			$L,		SERINE),
	O_Sulfo_Tyrosine				("TYS", 	 514186,	'X',			$L,		TYROSINE),
	O_Benzylsulfonyl_Serine			("SEB",	   17754162,	'X',			$L,		SERINE),
	
	/**************************************************************************************** TODO
	 *									Phospholated Amino Acids							*
	 ****************************************************************************************/
	O_Phospho_Serine				("SEP",		  68841,	'X',			$L,		SERINE),
	O_Phospho_Threonine				("TPO",		3246323,	'X',			$L,		THREONINE),
	O_Phospho_Tyrosine				("PTR", 	  30819,	'X',			$L,		TYROSINE), 
	
	_4_Phospho_Aspartic__Acid		("PHD", 	 152441,	'X',			$L,		ASPARTIC_ACID),
	S_Phospho_Cysteine				("CSP",		3082729,	'X',			$L,		CYSTEINE),
	
	_1_Thio_Phosphono_Histidine		("PSH",    52945400,	'?',			$L,		HISTIDINE),
	N1_Phosphono_Histidine			("NEP",    15458486,	'X',			$L,		HISTIDINE),
	
	/**************************************************************************************** TODO
	 *							Amino Acids with Sulfinic/Sulfonic Acid						*
	 ****************************************************************************************/
	Cysteine_Sulfinic__Acid			("CSD",		1549098,	'X',			$L,		CYSTEINE),	
	Cysteine_Sulfonic__Acid			("OCS", 	  72886,	'X',			$L,		CYSTEINE),
	Cysteine_S_Sulfonic_Acid		("CSU", 	 115015,	'X',			$L,		CYSTEINE), 
	
	/**************************************************************************************** TODO
	 *									S-Oxy Amino Acids									*
	 ****************************************************************************************/
	S_Oxy_Cysteine					("CSX",		 165339,	'X',			$L,		CYSTEINE),
	S_Oxy_Methionine				("MHO",    10909908,	'X',			$L,		METHIONINE),
	S_Dioxy_Methionine				("OMT",		 445282,	'X',			$L,		METHIONINE),
	
	/**************************************************************************************** TODO
	 *								Succinyl Amino Acids									*
	 ****************************************************************************************/
	N6_Succinyl_Lysine				("SLL",	   46947876,	'X',			$L,		LYSINE),
	
	/**************************************************************************************** TODO
	 *									Thio Amino Acids									*
	 ****************************************************************************************/
	Thio_Cysteine					("CSS",		 165331,	'X',			$L,		CYSTEINE),
	S_Methyl_Thio_Cysteine			("SCH",		3080775,	'X',			$L,		CYSTEINE),
	S$S_Propyl_Thio_Cysteine		("PR3",	   17754128,	'X',			$L,		CYSTEINE),
	S$S_Pentyl_Thio_Cysteine		("PEC",	   49867483,	'?',			$L,		CYSTEINE),
	S$S_Butyl_Thio_Cysteine			("BUC",	   49866731,	'X',			$L,		CYSTEINE),
	S_Cyclopentyl__Thio_Cysteine	("C5C",	   49866754,	'?',			$L,		CYSTEINE),
	S_Cyclohexyl__Thio_Cysteine		("C6C",	   49866755,	'?',			$L,		CYSTEINE),
	
	/**************************************************************************************** TODO
	 *									Other Amino Acids									*
	 ****************************************************************************************/
  //.......|.......|.......|.......|.......|.......|.......|.......|.......|.......|
	//more uncommon residues - commented residues have bad codes…				
	
	Aminocaproic__Acid				("ACA",			564,	'X',			$DL,	__OTHER__),	
	
	_4_Cyano_Phenylalanine			("4CF",		1501865,	'X',			$L,		PHENYLALANINE),
	_7_Aza_Tryptophan				("TRN", 	7000165,	'?',			$L,		TRYPTOPHAN),

	Benzyl_Cysteine					("BCS", 	 193613,	'X',			$L,		CYSTEINE),
	Benzoyl_D_Alanine				("S2D",		  91514,	'A',	'a',	$D,		D_ALANINE),
	
	Cysteine_S_Acetamide			("YCM",    17754220,	'X',			$L,		CYSTEINE),
	
	D_Iso_Glutamine					("ZGL",		5288447,	'E',			$D,		D_GLUTAMINE),
	Diphthamide						("DDE",		6438375,	'?',			$L,		__OTHER__),
	
	Formyl_Methionine				("FME",		 439750,	'M',			$L,		METHIONINE),
	
	Histidinol						("HSO",			776,	'H',			$L,		HISTIDINE),
	
	Methionine__Sulfoxide			("SME",	   10062737,	'X',			$L,		METHIONINE),
	Mono_Isopropyl_Phosphoryl_Serine("MIS",	   17754078,	'?',			$L,		SERINE),
	
	N_6_Crotonyl_Lysine				("KCR",	   91811002,	'X',			$L,		LYSINE),
	N6_Myristoyl_Lysine				("MYK",		6454263,	'X',			$L,		LYSINE),
	Nepsilon						("6G4",	  101523583,	'?',			$DL,	LYSINE),	
	Nitrocefin_Acyl_Serine			("NC1",	   49867364,	'?',			$L,		SERINE),	
		
	Pyro_Glutamic__Acid				("PCA", 	   7405,	'X',			$L,		GLUTAMIC_ACID),
	
	S_Arsono_Cysteine				("CSR",	   17753924,	'?',			$L,		CYSTEINE),
	
	S_Nitroso_Cysteine				("SNC",		9793848,	'X',			$L,		CYSTEINE),		
	Selenomethionine__Selenoxide	("MSO",    17754089,	'?',			$L,		SELENOMETHIONINE),
		
	Thia_Lysine						("SLZ",		  20049,	'X',			$L,		LYSINE),
	Tyrosine__Derivative			("TYT",  -381342032,	'?',			$L,		TYROSINE), //compound ID doesn't connect to PDB
		
	//names are too long to encode as enum values
	__0AH__("0AH",		20711570,	'A',	$L,		SERINE,			"O-(Bromoacetyl)-Serine"),
	__0QL__("0QL",		11217818,	'?',	$L,		ALANINE,		"3-[(2-Aminoethyl)Disulfanyl]-Alanine"),
	__0PX__("0PX",	   137347880, 	'?', 	$P, 	__OTHER__, 		"N~1~-{(1s,2s,4s)-1-(Cyclohexylmethyl)-4-[(2,2-Dimethylpropyl)carbamoyl]-2-Hydroxy-5-Methylhexyl}-N~2~-(Quinolin-2-Ylcarbonyl)-Aspartamide"),
	__0RJ__("0RJ", 	   137347885,	'X',	$L,		ALANINE,		"3-(Formylamino)-Alanine"),
	__0TD__("0TD",	   137347888,	'?',	$L,		ASPARTIC_ACID,	"(3S)-3-(methylsulfanyl)-Aspartic Acid"),
	
	__19W__("19W",		15217865,	'X',	$L,		NORVALINE,		"5-(Aminooxy)-Norvaline"),
	__1IP__("1IP",		16750041,	'?',	$L,		ASPARAGINE,		"N~2~-(Phosphonoacetyl)-Asparagine"),
	__1TY__("1TY",	    49866428,	'?',	$L,		ALANINE,		"3-{(3E)-4-Hydroxy-6-Oxo-3-[(2-Phenylethyl)Imino]Cyclohexa-1,4-Dien-1-Yl}Alanine"),

	__2FM__("2FM",		  447827,	'?',	$L,		HOMOCYSTEINE,	"S-(Difluoromethyl)Homocysteine"),
	__2NC__("2NC",		49866429,	'?',	$P,		ORNITHINE,		"N-{(2S)-2-[(N-Acetyl-Threonyl-Isoleucyl)Amino]Hexyl}-Norleucyl-Glutaminyl-N~5~-[Amino(Iminio)Methyl]-Ornithinamide"),
	__2TY__("2TY", 	    46937041,	'?',	$L,		TYROSINE,		"2-Hydroxy-5-{[(1E)-2-Phenylethylidene]Amino}-Tyrosine"),
	
	__30V__("30V",	   137348112,	'?',	$L,		CYSTEINE,		"S-[(2-Carbamoylphenyl)Selanyl]-Cysteine"), //unsimplifiable
	__31Q__("31Q",	   137348117,	'?',	$L,		CYSTEINE,		"(4-Carboxyphenyl)(l-Cysteinato-Kappas~3~)Mercury"),
	__38X__("38X",	   137348133,	'?',	$P,		TRYPTOPHAN,		"N-[(3-Methyl-1H-Inden-2-Yl)Carbonyl]-D-Alanyl-N-[(2S,4R)-1-Cyclohexyl-5-Hydroxy-4-Methyl-3-Oxopentan-2-Yl]-Tryptophanamide"),
	__39V__("39V",	   137348136,	'?',	$P,		TRYPTOPHAN,		"N-[(3-Methyl-1H-Inden-2-Yl)Carbonyl]-D-Alanyl-N-[(2S,4R)-5-Hydroxy-4-Methyl-3-Oxo-1-Phenylpentan-2-Yl]-Tryptophanamide"),
	__3QN__("3QN",	   137348164,	'?',	$L,		LYSINE,			"(E)-N~6~-{2-Hydroxy-3-Methyl-6-[(Phosphonooxy)Methyl]Benzylidene}-Lysine"),
	__3TY__("3TY",	   137348170,	'?',	$L,		ALANINE,		"3-[(3E)-3-(Benzylhydrazono)-4-Hydroxy-6-Oxocyclohexa-1,4-Dien-1-Yl]-Alanine"),
	__3X9__("3X9",	   137348188,	'?',	$L,		ALANINE,		"3-{[(1-Hydroxy-2,2,5,5-Tetramethyl-2,5-Dihydro-1H-Pyrrol-3-Yl)Methyl]Disulfanyl}-Alanine"),
	__3ZL__("3ZL",	   137348197,	'?',	$L,		LYSINE,			"N~6~-[(1Z)-4-Amino-3-Oxopenta-1,4-Dien-1-Yl]-Lysine"),
	
	__4AK__("4AK",	   137348221,	'X',	$L,		LYSINE,			"N~6~-Sulfo-Lysine"),
	
	__6M6__("6M6",		 9860624,	'?',	$L,		CYSTEINE,		"S-[(2-Phenylethyl)Carbamothioyl]-Cysteine"),
	
	__7HA__("7HA", 		16725047,	'?',	$L,		GLYCINE,		"N-(3-{[2-(4-Chlorophenyl)-5-Methyl-1,3-Oxazol-4-Yl]Methoxy}Benzyl)-N-(Methoxycarbonyl)Glycine"),
	
	__AGT__("AGT",		49866622,	'?',	$L,		CYSTEINE,		"S-{(S)-Amino[(4-Aminobutyl)Amino]Methyl}-Cysteine"),
	
	__C1X__("C1X",	    49866741,	'?',	$L,		LYSINE,			"(Z)-N~6~-[(4R,5S)-5-(2-Carboxyethyl)-4-(Carboxymethyl)Dihydro-2H-Thiopyran-3(4H)-Ylidene]-Lysine"),
	__CGV__("CGV",	   137349095,	'?',	$L,		CYSTEINE,		"S-[(R)-Carboxy(Hydroxy)Methyl]-Cysteine"),
	__CME__("CME",		  170018,	'X',	$L,		CYSTEINE,		"S,S-(2-Hydroxyethyl)Thio-Cysteine"),
	__CML__("CML",		10263540,	'?',	$L,		CYSTEINE,		"S-[(1S)-1,2-Dicarboxyethyl]-Cysteine"),
	__CS3__("CS3",		49866834,	'?',	$L,		CYSTEINE,		"S-[3-Oxo-3-(2-Thienyl)Propyl]-Cysteine"),
	__CS4__("CS4",		46937082,	'?',	$L,		CYSTEINE,		"S-[3-(3,4-Dichlorophenyl)-3-Oxopropyl]-Cysteine"),
	__CTW__("CTW",		86306042,	'?',	$P,		NORVALINE,		"N-(4-Fluorobenzoyl)-L-Gamma-Glutamyl-5-{[(S)-{[(1S)-1,3-Dicarboxypropyl]Amino}(Hydroxy)Phosphoryl]Oxy}-Norvaline"),
	__CYG__("CYG",		49866856,	'?',	$L,		__OTHER__,		"2-Amino-4-(Amino-3-oxo-Propylsulfanylcarbonyl)-Butyric Acid"),
	__CYJ__("CYJ",	 	49866857,	'?',	$L,		LYSINE,			"(Z)-N~6~-[(4R,5S)-5-(2-Carboxyethyl)-4-(Carboxymethyl)Piperidin-3-Ylidene]-Lysine"),
	__CZ2__("CZ2",		49866861,	'?',	$L,		CYSTEINE,		"S-(Dihydroxyarsino)Cysteine"),

	__DC0__("DC0",		13996184,   '?',	$P,		PHENYLALANINE,	"N-[(2S)-2-Amino-4-Methylpentyl]-Phenylalanine"),
	__DYS__("DYS",		  122084,	'?',	$L,		CYSTEINE,		"S-[5-(2-Aminoethyl)-2,3-Dihydroxyphenyl]-Cysteine"),
	
	__EFC__("EFC",		49866941,	'?',	$L,		CYSTEINE,		"S,S-(2-Fluoroethyl)Thio-Cysteine"),
	__EJA__("EJA",	   137349257,	'?',	$L,		CYSTEINE,		"S-[(1Z)-2-Carboxy-N-Hydroxyethanimidoyl]-Cysteine"),
	__ESB__("ESB",		49866952,	'?',	$L,		ALANINE,		"3-[(3E)-3-(Ethylimino)-4-Hydroxy-6-Oxocyclohexa-1,4-Dien-1-Yl]-Alanine"),
	__EXY__("EXY",		24836829,	'?',	$L,		NORLEUCINE,		"6-[(2R)-Oxiran-2-Yl]-Norleucine"),
	
	__FDL__("FDL",		10091262,	'X',	$L,		LYSINE,			"N~6~-Acetyl-N-(4-Methyl-2-oxo-2H-chromen-7-yl)-Lysinamide"),
	__FHL__("FHL",	    49866985,	'?',	$L,		LYSINE,			"(E)-N~6~-[3-Carboxy-1-(Hydroxymethyl)Propylidene]-Lysine"),
	__FZN__("FZN",	    49867005,	'?',	$L,		__OTHER__,		"(2S)-2-amino-6-{[(1Z)-1-{[(2R,3R,4S,5R)-5-({[(R)-{[(R)-{[(2R,3S,4R,5R)-5-(6-amino-9H-purin-9-yl)-3,4-dihydroxytetrahydrofuran-2-yl]methoxy}(hydroxy)phosphoryl]oxy}(hydroxy)phosphoryl]oxy}methyl)-3,4-dihydroxytetrahydrofuran-2-yl]sulfanyl}ethylidene]amino}hexanoic acid"),
	
	__GPL__("GPL",	   137349444,	'?',	$L,		LYSINE,			"Lysine Guanosine-5'-Monophosphate"),
	
	__HLY__("HLY",	   137349518,	'?',	$L,		__OTHER__,		"(3~{R})-5-[[(5~{S})-5-azanyl-6-oxidanyl-6-oxidanylidene-hexyl]amino]-3-methyl-3-oxidanyl-5-oxidanylidene-Pentanoic Acid"),
	
	__KHB__("KHB",	   137349647,	'?',	$L,		LYSINE,			"N~6~-[(3S)-3-hydroxybutanoyl]-Lysine"), //<>
	__KST__("KST",		49867207,	'?',	$L,		LYSINE,			"N~6~-(5-Carboxy-3-Thienyl)-Lysine"),
	
	__LCK__("LCK",		49867232,	'?',	$L,		LYSINE,			"(Z)-N~6~-(2-Carboxy-1-Methylethylidene)-Lysine"),
	__LDH__("LDH",		36689968,	'X',	$L,		LYSINE,			"N~6~-Ethyl-Lysine"), //<>
	__LEF__("LEF",		15227560,	'?',	$L,		LEUCINE,		"(4S)-5-Fluoro-Leucine"), //<>
	__LEH__("LEH",		46937122,	'?',	$L,		LEUCINE,		"N-[12-(1H-Imidazol-1-Yl)Dodecanoyl]-Leucine"),
	__LET__("LET",		49867234,	'?',	$L,		LYSINE,			"(Z)-N^6-{3-Carboxy-1-[(4-Carboxy-2-Oxobutoxy)Methyl]Propylidene}-Lysine"),
	__LLP__("LLP",		17137182,	'?',	$L,		LYSINE,			"2-Lysine(3-Hydroxy-2-Methyl-5-Phosphonooxymethyl-Pyridin-4-Ylmethane)"),
	__LLY__("LLY",		17754065,	'?',	$L,		LYSINE,			"NZ-(Dicarboxymethyl)Lysine"),
	__LPS__("LPS",		 5288706,	'?',	$L,		SERINE,			"O-{Hydroxy[((2R)-2-Hydroxy-3-{[(1S)-1-Hydroxypentadecyl]Oxy}Propyl)Oxy]Phosphoryl}-Serine"),
	__LSO__("LSO",		49867256,	'?',	$L,		LYSINE,			"(Z)-N~6~-(3-Carboxy-1-{[(4-Carboxy-2-Oxobutyl)Sulfonyl]Methyl}Propylidene)-Lysine"),
	__LYR__("LYR",		52947668,	'?',	$L,		LYSINE,			"N~6~-[(2Z,4E,6E,8E)-3,7-Dimethyl-9-(2,6,6-Trimethylcyclohex-1-En-1-Yl)Nona-2,4,6,8-Tetraenyl]Lysine"),
	__LYX__("LYX",		49867265,	'?',	$L,		LYSINE,			"N''-(2-Coenzyme A)-Propanoyl-Lysine"),
	
	__M0H__("M0H",		45082019,	'?',	$L,		CYSTEINE,		"S-(Hydroxymethyl)-Cysteine"),
	__M2L__("M2L",		28212126,	'X',	$L,		__OTHER__,		"(2r)-2-Amino-3-(2-Dimethylaminoethylsulfanyl)Propanoicacid"),
	__MBQ__("MBQ",		52941757,	'?',	$L,		TYROSINE,		"2-Hydroxy-5-({1-[(4-Methylphenoxy)Methyl]-3-Oxoprop-1-Enyl}Amino)-Tyrosine"),
	__MCG__("MCG",		  446355,	'X',	$L,		GLYCINE,		"(S)-(Alpha)-Methyl-4-Carboxyphenyl-Glycine"),

	__MD3__("MD3",		56684141,	'?',	$L,		CYSTEINE,		"N-(Carboxycarbonyl)-S-(Naphthalen-2-Ylmethyl)-Cysteine"),
	__MD5__("MD5",		56684140,	'?',	$L,		CYSTEINE,		"N-(Carboxycarbonyl)-S-(3-Nitrobenzyl)-Cysteine"),
	__MD6__("MD6",		 9837038,	'?',	$DL,	GLYCINE,		"3-Hydroxypyridine-2-Carbonyl-Glycine"),
	__MKD__("MKD",		11332749,	'X',	$L,		__OTHER__,		"(2S)-2-Amino-2-Methyloctanoic Acid"), //<>
	__ML3__("ML3",		49867315,	'?',	$L,		__OTHER__,		"2-{[(2r)-2-Amino-2-Carboxyethyl]Sulfanyl}-n,n,n-Trimethylethanaminium"),
	
	__NBQ__("NBQ",		17754104,	'?',	$L,		TYROSINE,		"2-Hydroxy-5-({1-[(2-Naphthyloxy)Methyl]-3-Oxoprop-1-Enyl}Amino)Tyrosine"),
	__NDF__("NDF",		 5288998,	'?',	$D,		D_PHENYLALANINE,"N-(Carboxycarbonyl)-D-Phenylalanine"),
	__NKS__("NKS",		45254002,	'?',	$L,		GLYCINE,		"N-(3-{[2-(4-Chlorophenyl)-5-Methyl-1,3-Oxazol-4-Yl]Methoxy}Benzyl)-N-[(4-Methylphenoxy)Carbonyl]Glycine"),
	__NYS__("NYS",		49867417,	'?',	$L,		CYSTEINE,		"5-(S-Cysteinyl)Norepinephrine"),
	
	__OBS__("OBS",		49867426,	'?',	$L,		LYSINE,			"(Z)-N^6-[(4S,5R)-5-(2-Carboxyethyl)-4-(Carboxymethyl)-1-Hydroxydihydro-2H-Thiopyranium-3(4H)-Ylidene]-Lysine"),
	__OHI__("OHI",		49867436,	'?',	$L,		ALANINE,		"3-(2-Oxo-2H-Imidazol-4-Yl)-Alanine"),
	
	__P2Q__("P2Q",		52940540,	'?',	$L,		TYROSINE,		"2-Hydroxy-5-{[(1S,2E)-1-Formyl-4-Phenoxybut-2-En-1Yl]Amino}-Tyrosine"),
	__P3Q__("P3Q",		49867469,	'?',	$L,		TYROSINE,		"2-Hydroxy-5-{[(1E)-1-(2-Oxoethylidene)-4-Phenylbutyl]Amino}-Tyrosine"),
	__P4F__("P4F",		54754545,	'?',	$D,		"EE0",			"5,5-Difluoro-4-Oxo-5-Phosphono-D-Norvaline"), //<>
	__PAQ__("PAQ",		17754117,	'?',	$L,		PHENYLALANINE,	"2-Oxy-4-Hydroxy-5-(2-Hydrazinopyridine)Phenylalanine"),
	__PDD__("PDD",		  446862,	'?',	$D,		D_ALANINE,		"N-(5'-Phosphopyridoxyl)-D-Alanine"),
	
	__QCD__("QCD",	   155804491,	'?',	$L,		__OTHER__,		"{(4Z)-2-[(1R,2R)-1-amino-2-hydroxypropyl]-4-[(4-hydroxy-3-methoxyphenyl)methylidene]-5-oxo-4,5-dihydro-1H-imidazol-1-yl}acetic acid"),
	__QPA__("QPA",	   137349914,	'?',	$L,		CYSTEINE,		"S-[(1S)-1-Carboxy-1-(Phosphonooxy)Ethyl]-Cysteine"),
	
	__R1A__("R1A", 		49867559,	'?',	$L,		CYSTEINE,		"S-(Thiomethyl-3-[2,2,5,5-Tetramethyl Pyrroline-1-Oxyl]) Cysteine"),
	
	__S2C__("S2C",		  446122,	'?',	$L,		CYSTEINE,		"S-2-(Boronoethyl)-Cysteine"),
	__SCS__("SCS",		  282051,	'X',	$L,		CYSTEINE,		"3-(Ethyldisulfanyl)-Alanine"), //according to pubchem
	__SGB__("SGB",		49867646,	'?',	$L,		SERINE,			"O-[(s)-Methyl(1-Methylethoxy)Phosphoryl]-Serine"),
	__SLR__("SLR", 		25021189,	'?',	$D,		D_PROLINE,		"(3R,4R)-3-Hydroxy-2-[(1S)-1-Hydroxy-2-Methylpropyl]-4-Methyl-5-Oxo-D-Proline"),
	
	__TCK__("TCK",		   73094,	'K',	$P,		LYSINE,			"Tosyl-Lysine Chloromethyl Ketone"), //<OK>
	__TTS__("TTS",		49867765,	'?',	$L,		ALANINE,		"3-((3E)-4-Hydroxy-3-{[2-(4-Hydroxyphenyl)Ethyl]Imino}-6-Oxocyclohexa-1,4-Dien-1-Yl)Alanine"),
	__TY8__("TY8",		49867771,	'?',	$L,		PHENYLALANINE,	"2,4-Bis(Hydroperoxy)-5-Hydroxy-Phenylalanine"),
	__TY9__("TY9",		49867772,	'?',	$L,		PHENYLALANINE,	"3,4-Bis(Hydroperoxy)-5-Hydroxy-Phenylalanine"),
	__TYY__("TYY",		17754209,	'?',	$L,		ALANINE,		"3-(4-Hydroxy-3-Imino-6-Oxo-Cyclohexa-1,4-Dienyl)-Alanine"), //<>
	
	__UMA__("UMA",		 5496796,	'?',	$L,		ALANINE,		"Uridine-5'-Diphosphate-N-Acetylmuramoyl-Alanine"),
	
	__XPL__("XPL",	   137350170,	'?',	$L,		"PYL",			"4-amino reduced Pyrrolysine"),
	__XX1__("XX1",		49867870,	'?',	$L,		LYSINE,			"N~6~-7H-Purin-6-Yl-Lysine"),
	
	__YPZ__("YPZ",	   137350196,	'?',	$L,		ALANINE,		"3-[(3Z)-4-Hydroxy-6-Oxo-3-(2-Phenylhydrazinylidene)Cyclohexa-1,4-Dien-1-Yl]-Alanine"), //<>
	;
	
	//Compound name
	private final String name;
	//PDB(e) code
	public final String code;
	//positive is PubChem Compound ID, Negative is PubChem Substance ID
	public final int pubChem_id;
	//E6 class
	public final E6 clazz;
	//universally-accepted letter
	public final char letter;
	//SwiPred-assigned letter (using UTF-16)
	public final char utf16_letter;
	//
	public final PeptideLink type;
	//Base for of the amino acid (for simplification)
	public final String baseForm;
	//Alternate code
	public final String chemCode;
	
	private AminoType(String code, int pubChem_id, char letter, PeptideLink type) {
		this(code, pubChem_id, letter, NOT_PROVIDED, type, __OTHER__, null, null, code);
	}
	
	private AminoType(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type) {
		this(code, pubChem_id, letter, utf16_letter, type, (letter == utf16_letter) ? code : __OTHER__, null, null, code);
	}
	
	private AminoType(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type, String baseForm) {
		this(code, pubChem_id, letter, utf16_letter, type, baseForm, null, null, code);
	}
	
	private AminoType(String code, int pubChem_id, char letter, PeptideLink type, String baseForm, String name) {
		this(code, pubChem_id, letter, NOT_PROVIDED, type, baseForm, null, name, null);
	}
	
	private AminoType(String code, int pubChem_id, char letter, PeptideLink type, String baseForm, String name, String chemCode) {
		this(code, pubChem_id, letter, NOT_PROVIDED, type, baseForm, null, name, chemCode);
	}
	
	private AminoType(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type, String baseForm, String chemCode) {
		this(code, pubChem_id, letter, utf16_letter, type, baseForm, null, null, chemCode);
	}
	
	private AminoType(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type, E6 clazz) {
		this(code, pubChem_id, letter, utf16_letter, type, (letter == utf16_letter) ? code : __OTHER__, clazz, null, code);
	}
	
	private AminoType(String code, int pubChem_id, char letter, PeptideLink type, String baseForm) {
		this(code, pubChem_id, letter, NOT_PROVIDED, type, baseForm, null, null, code);
	}
	
	/**
	 * 
	 * @param code:			Amino Acid PDB code
	 * @param pubChem_id:	PubChem ID
	 * @param letter:		Official sequence letter
	 * @param utf16_letter:	SwiPred-assigned utf16 character
	 * @param baseForm:		base form of the amino acid (default=other)
	 * @param clazz:		Amino Acid E6 class
	 * @param name:			name (if it can't be encoded in the enum's value)
	 * @param chemCode:		chemical code (if not same as PDB code)
	 */
	private AminoType(String code, int pubChem_id, char letter, char utf16_letter,
			PeptideLink type, String baseForm, E6 clazz, String name, String chemCode) {
		this.code = code;
		this.pubChem_id = pubChem_id;
		this.name = name;
		this.letter = letter;
		this.utf16_letter = utf16_letter;
		this.type = type;
		this.clazz = clazz;
		this.baseForm = baseForm;
		this.chemCode = chemCode;
	}
	
	/**
	 * Note to coder: cases for duplicate return values are indented
	 * 
	 * Parses a 3-letter code into a residue type
	 * @param code: the string to parse
	 * @return: the residue type value
	 * @throws UnknownCodeException 
	 */
	public static AminoType parse(String code) throws UnknownResidueException {
		MoleculeType type = MoleculeLookup.parse(code);
		if(type == null) { throw new UnknownResidueException(code); }
		if(!(type instanceof AminoType))  { return OTHER; }
		
		return (AminoType) type;
	}
	
	/**
	 * Determines what Amino Acid of the Proteinogenic 22 the character represents
	 * So far, only the Proteinogenic 22 amino acids have one-character representations
	 * @param c: the character to look up
	 * 
	 * @return: the corresponding instance of the enum
	 * to any known type of animo acid
	 */
	public static AminoType parse(char c) {
		switch(c) {
		case 'B':
		case 'J':
		case 'Z':
		case 'X':	return ANY;
		case '_':	return null;
		default:	return MoleculeLookup.parseAmino(c);
		}
	}
	
	@Override public String toCode() { return code; }
	
	/**
	 * Compares this object with another residue type
	 * Two different derivatives of the same amino acid will be marked as the same
	 * if you want derivatives to be marked differently, use equals()
	 * 
	 * @param other: the other Residue type
	 * @return: Whether this residue type and the parameter are equal
	 */
	public boolean couldBe(AminoType other) { 
		return (letter == other.letter) || (standardize() == other.standardize());
	}
	
	public boolean proteinogenic() { return letter == utf16_letter; }
	
	public char toChar() {
		if(utf16_letter != NOT_PROVIDED) {
			return utf16_letter;
		} else if(letter != '?') {
			return letter;
		} else {
			return standardize().toChar();
		}
	}
	
	/** @return Standardized amino acid type */
	public AminoType standardize() { return MoleculeLookup.standardize(this); }
	
	public String toString() { return (name != null) ? name : fixString(super.toString()); }
	
	public String pubchemURL() {
		if(pubChem_id > 0) {
			return "https://pubchem.ncbi.nlm.nih.gov/compound/"+pubChem_id;
		} else {
			return "https://pubchem.ncbi.nlm.nih.gov/substance/"+Math.abs(pubChem_id);
		}
	}
	
	/**
	 * qp stands for quick-print
	 * @param arg0: the object to print
	 */
	private static final void qp(Object arg0) {
		LocalToolBase.qp(arg0);
	}
	
	public static void main(String[] args) {
		qp("N-[3-(8-SEC-BUTYL-7,10-DIOXO-2-OXA-6,9-DIAZA-BICYCLO[11.2.2] HEPTADECA-1(16),13(17),14-TRIEN-11-YAMINO)-2-HYDROXY-1-(4-HYDROXY-BENZYL) -PROPYL]-3-METHYL-2-PROPIONYLAMINO-BUTYRAMIDE".toLowerCase());
	}
}
