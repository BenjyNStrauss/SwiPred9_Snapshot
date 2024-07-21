package biology.molecule.types;

import biology.amino.UnknownResidueException;
import biology.molecule.MoleculeLookup;
import modules.descriptor.entropy.E6;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public enum AminoNonPolymer implements MoleculeType {
	//NON-POLYMER
	Acetyl_Glutamic__Acid			("NLG",		  70914,	'E',	'η',	$_,		GLUTAMIC_ACID),
	Acetyl_Glycine					("AAC", 	  10972,	'G',	'Γ',	$_,		GLYCINE),
	Acetyl_Arginine					("AAG",		  67427,	'R',			$_,		ARGININE),
	Acetyl_Ornithine				("AOR", 	 439232,	'X',			$_,		ORNITHINE),
	Acetyl_Norvaline				("AN0", 	 306106,	'X',			$_,		NORVALINE),
	N5_Phosphonoacetyl_Ornithine	("PAO",		 124992, 	'?',			$_,		ORNITHINE),
	Tryptophan__Amide				("LTN", 	 439356,	'W', 			$_,		TRYPTOPHAN),
	Piperidinic__Acid				("ABU", 		119,	'?',			$_,		__OTHER__,	null, "4ABU"),
	_3_Aminoisobutyric__Acid		("BAIB",	  64956,	'X',			$_,		__OTHER__),
	Carbamoyl_Aspartic__Acid		("NCD", 	  93072,	'D',			$_,		ASPARTIC_ACID),
	Carbamoyl_Sarcosine				("CMS",		 439375,	'G',			$_,		SARCOSINE),
	Dansyl_Glutamate				("9DE",		 191888,	'E',			$_,		GLUTAMIC_ACID),
	Alpha_Difluoro_Methyl_Ornithine	("DMO", 	   3009,	'?',			$_,		ORNITHINE),
	Homo_Alanine					("???",		   6657,	'X',			$_,		ALANINE),
	Se_Adenosyl_Homo_Selenocysteine	("SAI",		 446317,	'?',			$_,		SELENOCYSTEINE),
	_6_Hydroxy_Norleucine			("LDO", 	  97725, 	'X',			$_,		NORLEUCINE),
	_6_Hydroxy_D_Norleucine			("DDO", 	5288041,	'X',			$_,		"DNE"),
	Triiodo_Thyronine				("T3",  	   5920,	'?',			$_,		THYRONINE),
	Methyl_Cysteine					("???", 	 225710,	'X',			$_,		CYSTEINE),
	N_Methyl_Aspartic__Acid			("???",	   21585102,	'D',	'ד',		$_,		ASPARTIC_ACID),
	N_Methyl_Glutamine				("???",			922,	'X',	'בּ',		$_,		GLUTAMINE),
	N_Methyl_Isoleucine				("???",		 560437,	'X',	'ע',		$_,		ISOLEUCINE),
	N_Methyl_Lysine					("???",		 541646,	'X',	'כ',		$_,		LYSINE),
	N_Methyl_Serine					("5JP",		7009640,	'S',	'ס',		$_,		SERINE),
	N_Methyl_Valine					("MEVAL", 	   4378,	'V',	'ו',		$_,		VALINE),
	MeTyrosine						("???",		 441350,	'X',			$_,		TYROSINE),
	Racemetirosine					("???",		   3125,	'X',			$_,		TYROSINE),
	O_Methyl_Tyrosine				("???",		  97118,	'X',			$_,		TYROSINE),
		
	SE_Methyl_Selenocysteine		("???0",	 147004,	'X',			$_,		SELENOCYSTEINE),
	Dimethyl_Glycine				("DMG", 		673,	'G',			$_,		GLYCINE),

	N$N_Dimethyl_Methionine			("2MM",		3246177,	'M',			$_,		METHIONINE),
	
	Trimethyl_Glycine				("BET",			248,	'?',			$_,		GLYCINE),
	//NON-POLYMER
	Creatine						("CRN", 		586, 	'G',			$_,		SARCOSINE),
	Norarginine						("???",		 435719,	'X',			$_,		ARGININE),
	
	N_Abietoyl_Tryptophan			("WAA",	  145997862, 	'X',			$_,		TRYPTOPHAN),
	
	/**************************************************************************************** TODO
	 *									Palmitoyl Amino Acids								*
	 ****************************************************************************************/
	N_Palmitoyl_Glycine				("140",		 151008,	'G',			$_,		GLYCINE),
	N_Palmitoyl_Methionine			("EPM",		 486207,	'M',			$_,		METHIONINE),
	
	Beta_Citryl_Glutamic__Acid		("BC8",		 189741,	'?',			$_,		GLUTAMIC_ACID),
	Ureidopropionate				("URP",			111,	'X',			$_,		BETA_ALANINE),
	
	D_Norvaline						("EE0",		 439575,	'X',	'ʋ',	$_,		D_VALINE),
	Norleucine__Boronic__Acid		("BNO",	   10820557,	'?',			$_,		NORLEUCINE),
	_3_Aminoadipic__Acid			("ǂAA",		 224389,	'X',			$_,		__OTHER__),
	Alanosine						("AL0",	  135409347,	'?',			$_,		ALANINE),
	
	_2$2_Diaminopimelic_Acid		("DǂA",	   19374013,	'?',			$_,		__OTHER__),//"DPM"
	_2$3_Diaminoproprionic_Acid		("DǂP", 		364,	'X',			$_,		ALANINE, null, "DAP"), //"DPR"
	Batimastat						("BAT",		5362422,	'?',			$_,		PHENYLALANINE),
	
	Carbox_S_AdenosylMethionine		("GEK",  NO_PUBCHEM,	'?',			$_,		METHIONINE),
	S_Adenodyl__Ethionine			("S7M",	 NO_PUBCHEM,	'?',			$_,		__OTHER__),
	
	N2_Succinyl_Ornithine			("SUO",		 127370,	'X',			$_,		ORNITHINE),
	N_Carboxy_Alanine				("NXA",		5496774,	'A',			$_,		ALANINE),
	D_Vinyl_Glycine					("A3B",		5287580,	'X',			$_,		GLYCINE),	
	Glutamine__Hydroxamate			("HGA",		 449178,	'X',			$_,		GLUTAMINE),
	Glyphosate						("GPF",		   3496,	'G',			$_,		GLYCINE),
	Kelatorphan						("KEL",		 123982,	'?',			$_,		ALANINE),
	Methionine_S_Sulfoximine__Phosphate("P3S", 24178104,	'?',			$_,		METHIONINE),
	N_Sulfamoyl_Glutamic__Acid		("3K0",	   73212792,	'E',			$_,		GLUTAMIC_ACID),
	N5_Iminoethyl_Ornithine			("ILO", 	 107984,	'?',			$_,		ORNITHINE),

	Oxalyl_Glycine					("OGA",		3080614,	'?',			$_,		GLYCINE),
	S_Adenosyl_Methionine			("SAM",		  34755,	'?',			$_,		METHIONINE),	
	Syringolin__B					("SY2",	   44624133,	'?',			$_,		VALINE),

	__17K__("17K",		78225893,	'?',		$_,		VALINE,			"(3R)-N-(2-Formylindolizin-3-Yl)-4-[(Phenylacetyl)Oxy]-3-Sulfino-D-Valine"),
	__1B7__("1B7",	   135566782,	'?',		$_,		ALANINE,		"N-(6-Chloro-3,3-Dimethyl-3,4-Dihydroisoquinolin-1-Yl)-3-(4-Propylthiophen-3-Yl)-Alanine"),
	__1B8__("1B8",	   135566783,	'?',		$_,		ALANINE,		"3-[2-Bromo-4-(1H-Pyrazol-4-Yl)Thiophen-3-Yl]-N-(6-Chloro-3,3-Dimethyl-3,4-Dihydroisoquinolin-1-Yl)-Alanine"),
	__1B9__("1B9",	   135566785,	'?',		$_,		ALANINE,		"3-(4-Bromothiophen-3-Yl)-N-(6-Chloro-3,3-Dimethyl-3,4-Dihydroisoquinolin-1-Yl)-Alanine"),
	__1BE__("1BE", 	   135566788,	'?',		$_,		ALANINE,		"N-(6-Chloro-3,3-Dimethyl-3,4-Dihydroisoquinolin-1-Yl)-3-[2-Propyl-4-(1H-Pyrazol-4-Yl)Thiophen-3-Yl]-Alanine"),
	__1BF__("1BF",	   135566784,	'?',		$_,		ALANINE,		"N-(6-Chloro-3,3-Dimethyl-3,4-Dihydroisoquinolin-1-Yl)-3-[4-(1H-Pyrazol-4-Yl)Thiophen-3-Yl]-Alanine"),
	__1CH__("1CH",	   135532136,	'?',		$_,		PHENYLALANINE,	"N-(3,3-Dimethyl-3,4-Dihydroisoquinolin-1-Yl)-Phenylalanine"),
	__1U3__("1U3", 		  426788,	'?',		$_,		GLYCINE,		"N-{[3-(2-Chlorophenyl)-5-Methyl-1,2-Oxazol-4-Yl]Carbonyl}Glycine"),
	
	__28P__("28P",		 9820434,	'?',		$_,		BETA_ALANINE,	"N-[3-(4-Benzylphenoxy)Propyl]-N-Methyl-Beta-Alanine"),
	__28T__("28T",		73167569,	'?',		$_,		PROLINE,		"1-{4-Oxo-4-[(2S)-Pyrrolidin-2-Yl]Butanoyl}-Proline"),
	__2DS__("2DS",		49866457,	'?',		$_,		SERINE,			"N-[(2,3-Dihydroxyphenyl)Carbonyl]-O-[(2R)-2-{[(2,3-Dihydroxyphenyl)Carbonyl]Amino}-3-Hydroxypropanoyl]-Serine"),
	__2P6__("2P6",		11198569,	'?',		$_,		PROLINE,		"(2S)-1-[1-(4-Phenylbutanoyl)-L-Prolyl]Pyrrolidine-2-Carbonitrile"),
	__2PX__("2PX",		73167574,	'?',		$_,		TRYPTOPHAN,		"N-{1-[(5-Methyl-1H-Indol-3-Yl)Methyl]Piperidin-4-Yl}-Tryptophanamide"), //<>
	__2PZ__("2PZ",	   117072009,	'?',		$_,		GLYCINE,		"N-[1-(1H-Indol-3-Ylmethyl)Piperidin-4-Yl]Glycinamide"),
	__2QM__("2QM",		46228334,	'?',		$_,		LYSINE,			"N~2~-{[(S)-Carboxy(4-Hydroxyphenyl)Methyl]Carbamoyl}-N~6~-(4-Iodobenzoyl)-Lysine"),
	__2QN__("2QN",		46228364,	'?',		$_,		LYSINE,			"N~2~-{[(1S)-1-Carboxy-2-(Pyridin-4-Yl)Ethyl]Carbamoyl}-N~6~-(4-Iodobenzoyl)-Lysine"),
	__2QP__("2QP",		46228341,	'?',		$_,		LYSINE,			"N~2~-{[(1S)-1-Carboxy-2-(Furan-2-Yl)Ethyl]Carbamoyl}-N~6~-(4-Iodobenzoyl)-Lysine"),
	__2QQ__("2QQ",		46228331,	'?',		$_,		LYSINE,			"N~2~-{[(1S)-1-Carboxybut-3-Yn-1-Yl]Carbamoyl}-N~6~-(4-Iodobenzoyl)-Lysine"),
	__2R7__("2R7",		46227613,	'?',		$_,		LYSINE,			"N~2~-[(1-Carboxycyclopropyl)Carbamoyl]-N~6~-(4-Iodobenzoyl)-Lysine"),
	
	__3EF__("3EF",		44626711,	'?',		$_,		TYROSINE,		"N-{(2S)-3-[(S)-[(1R)-1-{[(Benzyloxy)Carbonyl]Amino}-2-Phenylethyl](Hydroxy)Phosphoryl]-2-[(3-Phenyl-1,2-Oxazol-5-Yl)Methyl]Propanoyl}-Tyrosine"),
	__3ET__("3ET",		45489513,	'?',		$_,		SERINE,			"O-[(2R)-2-Amino-3-(D-Seryloxy)Propanoyl]-N-[(2,3-Dihydroxyphenyl)Carbonyl]-Serine"),

	__4C0__("4C0",		44517642,	'?',		$_,		D_ALANINE,		"3-(1H-Benzimidazol-2-Yl)-N-(3-Phenylpropanoyl)-D-Alanine"),
	__4D7__("4D7",		44517644,	'?',		$_,		D_ALANINE,		"3-(1H-Benzimidazol-2-Yl)-N-[(2-Methylfuran-3-Yl)Carbonyl]-D-Alanine"),
	__4D9__("4D9",		44517643,	'?',		$_,		D_ALANINE,		"3-(1H-Benzimidazol-2-Yl)-N-(1-Benzothiophen-2-Ylcarbonyl)-D-Alanine"),
	__4DH__("4DH",		44517645,	'?',		$_,		D_ALANINE,		"3-(1H-Benzimidazol-2-Yl)-N-[(1-Methyl-3-Phenyl-1H-Pyrazol-5-Yl)Carbonyl]-D-Alanine"),
	__4HF__("4HF",	   135430850,	'?',		$_,		GLUTAMIC_ACID,	"N-[(5-{2-[(6R)-2-Amino-4-oxo-3,4,5,6,7,8-hexahydropyrido[2,3-D]pyrimidin-6-YL]ethyl}-2-thienyl)carbonyl]-Glutamic Acid"),
	__4S4__("4S4",		91819622,	'?',		$_,		GLYCINE,		"N-(4-{[(4,5-Dibromo-1H-Pyrrol-2-Yl)Carbonyl]Amino}Benzoyl)Glycine"),
	__4TP__("4TP",		  440901,	'?',		$_,		THREONINE,		"4-Hydroxy-Threonine-5-Monophosphate"),
	
	__6L9__("6L9",	   137348482,	'X',		$_,		__OTHER__,		"[(2S)-2,3-Diamino-3-Oxopropyl]Propanedioic Acid"),

	__712__("712",		16094313,	'?',		$_,		__OTHER__,		"3-({[(1R)-1-(4-Fluorophenyl)Ethyl]Amino}Carbonyl)-5-[Methyl(Methylsulfonyl)Amino]Benzyl Alpha-Methyl-D-Phenylalaninate"),
	__739__("739",		 5287548,	'?',		$_,		METHIONINE,		"2(S)-{2(S)-[2(R)-Amino-3-Mercapto]Propylamino-3(S)-Methyl}Pentyloxy-3-Phenylpropionyl-Methionine Sulfone"),
	__792__("792", 		42627769,	'?',		$_,		TRYPTOPHAN,		"N-{[4-(But-2-Yn-1-Yloxy)Phenyl]Sulfonyl}-5-Methyl-D-Tryptophan"),

	__885__("885",		 6914644,	'?',		$_,		PROLINE,		"1-[(2-Amino-4-Chloro-5-Methylphenyl)Sulfonyl]-Proline"),
	
	__A1G__("A1G",	 	  759427,	'?',		$_,		__OTHER__,		"(2S)-2-(1-Adamantyl)-2-Aminoacetic Acid"),
	__A70__("A70", 		 5496932, 	'?',		$_,		NORLEUCINE,		"N-Ethyl-N-[(4-Methylpiperazin-1-Yl)carbonyl]-D-Phenylalanyl-N-[(1s,2s,4r)-4-(Butylcarbamoyl)-1-(Cyclohexylmethyl)-2-Hydroxy-5-Methylhexyl]-Norleucinamide"),
	__ABX__("ABX",		  449200,	'?',		$_,		PROLINE,		"5-[1-(Acetylamino)-3-Methylbutyl]-4-(Methoxycarbonyl)Proline"),
	__AQ3__("AQ3",		72376502,	'?',		$_,		D_ALANINE,		"N-({3-Hydroxy-2-Methyl-5-[(Phosphonooxy)Methyl]Pyridin-4-Yl}Methyl)-3-[(2-Hydroxyphenyl)Amino]-D-Alanine"),
	__AZV__("AZV",		50919286,	'?',		$_,		D_VALINE,		"N-(4-{[({(2R)-3-[(3R)-3-Amino-4-(2,4,5-Trifluorophenyl)Butanoyl]-1,3-Thiazolidin-2-Yl}Carbonyl)Amino]Methyl}Phenyl)-D-Valine"),
	
	__BBL__("BBL",		  736104,	'?',		$_,		ALANINE,		"N-[(Benzyloxy)Carbonyl]-Alanine"),
	__BDL__("BDL",		25271580,	'?',		$_,		D_LEUCINE,		"N-(Biphenyl-4-Ylsulfonyl)-D-Leucine"),
	__BIR__("BIR",		 5478838,	'?',		$_,		ALANINE,		"N-[3-[(1-Aminoethyl)(Hydroxy)Phosphoryl]-2-(1,1'-Biphenyl-4-Ylmethyl)Propanoyl]Alanine"),
	
	__CBH__("CBH", 		  446994,	'?',		$_,		HOMOCYSTEINE,	"S-(D-Carboxybutyl)-Homo-Cysteine"),
	__CHQ__("CHQ", 		  449094,	'?',		$_,		__OTHER__,		"Cyclo-(L-Histidine-L-Proline) Inhibitor"),
	__CMA__("CMA",		  446475,	'?',		$_,		ARGININE,		"N2-(Carboxyethyl)-Arginine"),
	__CZ9__("CZ9",		49866865,	'?',		$_,		D_TYROSINE,		"N-[(Dihydroxyboranyl)Methyl]-Nalpha-[(4-Ethyl-2,3-Dioxopiperazin-1-Yl)Carbonyl]-D-Tyrosinamide"),
	
	__DSV__("DSV",		24794392,	'?',		$_,		VALINE,			"N-(Dibenzo[B,D]Thiophen-3-Ylsulfonyl)-Valine"),
	
	__E37__("E37",		57336496,	'?',		$_,		GLUTAMINE,		"N~2~-[4-(4-Phenylthiophen-2-Yl)Benzoyl]-L-Alpha-Glutamine"),
	__EAS__("EAS",	   134821715,	'?',		$_,		TRYPTOPHAN,		"5-Chloro-N-{1-[(5-Chloro-1H-Indol-3-Yl)Methyl]Piperidin-4-Yl}-Tryptophanamide"),
	__EAV__("EAV",	   134821716,	'?',		$_,		TRYPTOPHAN,		"N-{1-[(5-Chloro-1H-Indol-3-Yl)Methyl]Piperidin-4-Yl}-6-Methyl-Tryptophanamide"),
	__EBY__("EBY",	   134821717,	'?',		$_,		TRYPTOPHAN,		"N-{1-[(5-Chloro-1H-Indol-3-Yl)Methyl]Piperidin-4-Yl}-5-Methyl-Tryptophanamide"),
	__EC4__("EC4",	   134821714,	'?',		$_,		TRYPTOPHAN,		"6-Chloro-N-{1-[(5-Chloro-1H-Indol-3-Yl)Methyl]Piperidin-4-Yl}-Tryptophanamide"),
	
	__HA0__("HA0",		52914843,	'?',		$_,		ALANINE,		"Heteroaryl-Alanine 5-Phenyl Oxazole"),
	
	__I84__("I84",			1944,	'?',		$_,		GLYCINE,		"[2,6-Dimethyl-4-(2-O-Tolyl-Acetylamino)-Benzenesulfonyl]-Glycine"),
	__IFA__("IFA",		25150855,	'?',		$_,		BETA_ALANINE,	"Methyl N-[(2',4'-Difluoro-4-Hydroxy-5-Iodobiphenyl-3-Yl)Carbonyl]-Beta-Alaninate"),
	__IFB__("IFB",		25150856,	'?',		$_,		BETA_ALANINE,	"N-[(2',4'-Difluoro-4-Hydroxy-5-Iodobiphenyl-3-Yl)Carbonyl]-Beta-Alanine"),
	__ILP__("ILP",		92282619,	'?',		$_,		ISOLEUCINE,		"N-[O-Phosphono-Pyridoxyl]-Isoleucine"),
	__IN0__("IN0", 		  445474,	'?',		$_,		ASPARTIC_ACID,	"N-{2-[Trans-7-Chloro-1-(2,2-Dimethyl-Propyl)-5-Naphthalen-1-Yl-2-oxo-1,2,3,5-Tetrahydro-Benzo[E] [1,4]Oxazepin-3-Yl]-Acetyl}-Aspartic Acid"),
	__INF__("INF",		  446049,	'?',		$_,		D_PHENYLALANINE,"D-[(N-Hydroxyamino)Carbonyl]Phenylalanine"),
	__IOT__("IOT",		  447122,	'?',		$_,		ARGININE,		"Arginine-N-Methylcarbonyl Phosphoric Acid 5'-Adenosine Ester"),
	__IPO__("IPO",		 5288643,	'X',		$_,		D_PHENYLALANINE,"Para-Iodo-D-Phenylalanine Hydroxamic Acid"),
	
	__JRG__("JRG",		46228323,	'?',		$_,		LYSINE,			"N~2~-{[(1S)-1-Carboxy-3-(Methylsulfanyl)Propyl]Carbamoyl}-N~6~-(4-Iodobenzoyl)-Lysine"),
	__JTP__("JTP",		11987832,	'?',		$_,		ALANINE,		"N-[(13-Cyclohexyl-6,7-Dihydroindolo[1,2-D][1,4]Benzoxazepin-10-Yl)Carbonyl]-2-Methyl-Alanine"),
	
	__KAF__("KAF",		24808492,	'?',		$_,		PHENYLALANINE,	"N-{(5S)-4,4-Dihydroxy-6-Phenyl-5-[(Phenylcarbonyl)Amino]Hexanoyl}-Phenylalanine"),
	__KAW__("KAW",		24808493,	'?',		$_,		TRYPTOPHAN,		"N-{(5S)-4,4-Dihydroxy-6-Phenyl-5-[(Phenylcarbonyl)Amino]Hexanoyl}-Tryptophan"),
	__KOU__("KOU",	   136246365,	'?',		$_,		SERINE,			"(E)-N-({3-Hydroxy-2-Methyl-5-[(Phosphonooxy)Methyl]Pyridin-4-Yl}Methylidene)-Serine"),
	
	__L34__("L34",    -381336339,	'?',		$_,		GLUTAMIC_ACID,	"4-(7-amino-9-hydroxy-1-oxo-3,3a,4,5-tetrahydro-2,5,6,8,9b-pentaaza-cyclopenta[a]naphthalen-2-yl)-phenylcarbonyl-Glutamic Acid"), //Compound ID doesn't connect
	__LN1__("LN1",		46937126,	'?',		$_,		D_VALINE,		"(3R)-4-{[(3,4-Dihydroxyphenyl)Acetyl]Oxy}-N-(2-Formylindolizin-3-Yl)-3-Sulfino-D-Valine"),
	
	__M11__("M11",		25021181,	'?',		$_,		LYSINE,			"N^6^-[(1R,2S)-1-({[(1R)-1-Carboxy-2-Methylpropyl]Oxy}Carbonyl)-2-Sulfanylpropyl]-6-Oxo-Lysine"),
	__MCK__("MCK",		46846244,	'?',		$_,		SERINE,			"N-[(2,3-Dihydroxyphenyl)Carbonyl]-O-[(2S)-2-{[(2,3-Dihydroxyphenyl)Carbonyl]Amino}-3-(L-Seryloxy)Propanoyl]-Serine"),
	__MDZ__("MDZ",		 5326860,	'?',		$_,		LYSINE,			"N~6~-Methyl-6-Oxo-Lysine - 2-[(3-Mercaptobutanoyl)Oxy]-3-Methylbutanoic Acid"),
	
	__NHO__("NHO",		 5289029,	'?',		$_,		VALINE,			"Nicotinamide-Adenine-Dinucleotide-5-Hydroxy-4-Oxonor-Valine"),
	__NZ2__("NZ2",		40554647,	'A',		$_,		ALANINE,		"N-(Methylsulfonyl)-N-Phenyl-Alanine"),
	__NZ3__("NZ3",		40554723,	'A',		$_,		D_ALANINE,		"N-(Methylsulfonyl)-N-Phenyl-D-Alanine"),
	
	__P28__("P28",		  449394,	'?',		$_,		THYRONINE,		"3',5'-Dinitro-N-Acetyl-Thyronine"),
	__PAZ__("PAZ",		   41635,	'?',		$_,		BETA_ALANINE,	"N-[(2R)-2-Hydroxy-3,3-Dimethyl-4-(Phosphonooxy)Butanoyl]-Beta-Alanine"),
	__PH0__("PH0",		 5289194,	'?',		$_,		PHENYLALANINE,	"N-{(2S)-3-[(R)-[(1R)-1-Amino-2-Phenylethyl](Hydroxy)Phosphoryl]-2-Benzylpropanoyl}-Phenylalanine"),
	__PLG__("PLG",		  445062,	'?',		$_,		GLYCINE,		"N-Glycine-[3-Hydroxy-2-Methyl-5-Phosphonooxymethyl-Pyridin-4-Yl-Methane]"),
	__PLS__("PLS",		  444550,	'?',		$_,		SERINE,			"[3-Hydroxy-2-Methyl-5-Phosphonooxymethyl-Pyridin-4-Ylmethyl]-Serine"),
	__PLT__("PLT",		 5289178,	'?',		$_,		TRYPTOPHAN,		"[3-Hydroxy-2-Methyl-5-Phosphonooxymethyl-Pyridin-4-Ylmethyl]-Tryptophan"),
	__PMX__("PMX",		11738933,	'?',		$_,		TYROSINE,		"3-Chloro-N-[(2E)-4-Methoxy-4-Oxobut-2-Enoyl]-Tyrosine"),
	__POI__("POI",		 5289186,	'?',		$_,		ORNITHINE,		"N~2~-Acetyl-N~5~-({3-Hydroxy-2-Methyl-5-[(Phosphonooxy)Methyl]Pyridin-4-Yl}Methyl)-Ornithine"),
	__PSQ__("PSQ",		 9543429,	'?',		$_,		ORNITHINE,		"Ndelta-(N'-Sulphodiaminophosphinyl)-Ornithine"),

	__REJ__("REJ",	   137349942,	'?',		$_,		SERINE,			"Tricarbonyl (Serine) Rhenium(I)"),
	__RND__("RND",		73167576,	'?',		$_,		TRYPTOPHAN,		"N-[1-(1H-Indol-3-Ylmethyl)Piperidin-4-Yl]-Tryptophanamide"),
	__RX3__("RX3",	  NO_PUBCHEM,	'?',		$_,		TRYPTOPHAN,		"N-({(1S,2R)-2-[(S)-[(1R)-1-{[(Benzyloxy)Carbonyl]Amino}-2-PhenyLethyl](Hydroxy)Phosphoryl]Cyclopentyl}Carbonyl)-Tryptophan"),
	
	__S48__("S48",		15942674,	'?',		$_,		GLYCINE,		"Methyl N-{(3S)-1-[(1-Methyl-1H-Imidazol-5-Yl)Methyl]-6-Phenyl-1,2,3,4-Tetrahydroquinolin-3-Yl}-N-[(1-Methyl-1H-Imidazol-4-Yl)Sulfonyl]Glycinate"),
	__SA2__("SA2",		49867632,	'?',		$_,		D_VALINE,		"(3R)-4-[(4-Carboxybutanoyl)Oxy]-N-[(1E)-3-Oxoprop-1-En-1-Yl]-3-Sulfino-D-Valine"),
	__SA8__("SA8",		  188380,	'?',		$_,		METHIONINE,		"S-5'-Azamethionine-5'-Deoxyadenosine"),
	__SCV__("SCV",		  446034,	'?',		$_,		LYSINE,			"N6-[(1S)-2-{[(1R)-1-Carboxy-2-Methylpropyl]Oxy}-1-(Mercaptocarbonyl)-2-Oxoethyl]-6-Oxo-Lysine"),
	__SHT__("SHT",		 6323503,	'?',		$_,		THREONINE,		"O-Phosphono-N-{(2E)-7-[(2-Sulfoethyl)Dithio]Hept-2-Enoyl}-Threonine"),
	__SMZ__("SMZ",		  107968,	'?',		$_,		ORNITHINE,		"N~5~-[(E)-Imino(Methylsulfanyl)Methyl]-Ornithine"),
	__SN0__("SN0",		 6852189,	'X',		$_,		NORVALINE,		"N-(3-Carboxypropanoyl)-Norvaline"),
	__SSC__("SSC",		25200992,	'?',		$_,		PROLINE,		"(5S-Carboxymethyl)-S-Proline"),
	__ST7__("ST7",		44129755,	'?',		$_,		ORNITHINE,		"N,N-Dibenzyl-N~5~-[N-(Methylcarbamoyl)Carbamimidoyl]-N~2~-{[5-({[(E)-(Quinolin-4-Ylmethylidene)Amino]Oxy}Methyl)-1H-1,2,3-Triazol-1-Yl]Acetyl}-L-Ornithinamide"),
	__SUG__("SUG",		  439968,	'R',		$_,		ARGININE,		"N~2~-(3-Carboxypropanoyl)-Arginine"),

	__TB1__("TB1",		 5311218,	'?',		$_,		ASPARTIC_ACID,	"(3S)-3-(Benzyloxy)-Aspartic Acid"),
	__TBL__("TBL",		  705712,	'A',		$_,		D_ALANINE,		"N-[(4-Methoxyphenyl)Sulfonyl]-D-Alanine"),
	__TD1__("TD1",		53313346,	'?',		$_,		SERINE,			"O-[(2S)-2-Amino-3-Hydroxypropanoyl]-N-(2,3-Dihydroxybenzoyl)-Serine"),
	__TPZ__("TPZ",		46870017,	'?',		$_,		THREONINE,		"O-Phosphono-N-(5-Sulfanylpentanoyl)-Threonine"), //<>
	__TT8__("TT8",		53377508,	'?',		$_,		"HCS",			"S-(N6-Methyladenosyl)-Homocysteine"),
	__TXZ__("TXZ",		46870018,	'?',		$_,		THREONINE,		"O-Phosphono-N-(6-Sulfanylhexanoyl)-Threonine"),
	__TYP__("TYP", 		  119404,	'?',		$_,		__OTHER__,		"Cyclo-(Tyrosine-Proline) Inhibitor"),
	
	__U89__("U89",	   135403834,	'?',		$_,		GLUTAMIC_ACID,	"N-[4-[[3-(2,4-Diamino-1,6-Dihydro-6-Oxo-4-Pyrimidinyl)-Propyl]-[2-((2-Oxo-2-((4-Phosphoriboxy)-Butyl)-Amino)-Ethyl)-Thio-Acetyl]-Amino]Benzoyl]-1-Glutamic Acid"), //<>
	
	__V24__("V24",	   121232413, 	'?',		$_,		D_VALINE,		"N-[([1,1'-Biphenyl]-4-Yl)Sulfonyl]-N-({1-[3,4,6-Tri-O-Acetyl-2-(Acetylamino)-2-Deoxy-Beta-D-Glucopyranosyl]-1H-1,2,3-Triazol-4-Yl}Methyl)-D-Valine"), //<>
	__V28__("V28",	   121232418, 	'?',		$_,		D_VALINE,		"N-({1-[2-(Acetylamino)-2-Deoxy-Beta-D-Glucopyranosyl]-1H-1,2,3-Triazol-4-Yl}Methyl)-N-[([1,1'-Biphenyl]-4-Yl)Sulfonyl]-D-Valine"),
	__V6F__("V6F",		10797632,  	'?', 		$_,		"HYP",			"(4R)-1-Acetyl-4-Hydroxy-N-Methyl-L-Prolinamide"),

	
	__W09__("W09",		86278550,	'?',		$_,		TRYPTOPHAN,		"N-(2,2,3,3,4,4,5,5,6,6,7,7,8,8,9,9,9-Heptadecafluorononanoyl)-Tryptophan"),
	__W2X__("W2X",		5459395,	'?',		$_,		LYSINE,			"N~6~-[(1R)-1-({[(1R,2R)-1-Carboxy-3-Hydroxy-2-Methylpropyl]Oxy}Carbonyl)-2-Mercaptoprop-2-En-1-Yl]-6-Oxo-Lysine"),
	
	__XP8__("XP8",		46870019,	'?',		$_,		THREONINE,		"O-Phosphono-N-(8-Sulfanyloctanoyl)-Threonine"),
	__XP9__("XP9",		46870020,	'?',		$_,		THREONINE,		"O-Phosphono-N-(9-Sulfanylnonanoyl)-Threonine"),
	__Y38__("Y38",		71304796,	'?',		$_,		D_VALINE,		"N-[2-(Benzoylamino)Ethyl]-N-(Biphenyl-4-Ylsulfonyl)-D-Valine"), //<>
	__YZE__("YZE",		44468183,	'?',		$_,		LYSINE,			"N~2~-{[(1S)-1-Carboxybut-3-En-1-Yl]Carbamoyl}-N~6~-[(4-Iodophenyl)Carbonyl]-Lysine"), //<>
	
	__Z99__("Z99",		 9819927,	'?',		$_,		D_ALANINE,		"2-[(1S,2S)-2-Carboxycyclopropyl]-3-(9H-Xanthen-9-Yl)-D-Alanine"), //Amino Acid Antagonist
	__ZXU__("ZXU",	   145915902,   '?',		$_,		TYROSINE,		"Prop-2-enoyl-Tyrosinamide"),
	
	//NULL
	Acetyl_Tryptophan_Methylamide	("ÆWM",	 	 151412,	'W',			null,	"ÆTR"),
	N_Ethyl_Asparagine				("ETASN",	 192839,	'N',			null,	ASPARAGINE),
	Ethyl_Proline					("???",	   28354718,	'P',			null,	PROLINE),
	Acetyl_Histidine				("ÆHI",		  75619,	'H',			null,	HISTIDINE),
	Acetyl_Isoleucine				("ÆIL", 	 306109,	'X',	'Ψ',	null,	ISOLEUCINE), //'X' is not a typo!
	Acetyl_Lysine					("???", 	  92907,	'K',	'Ϗ',		null,	LYSINE),
	Acetyl_Asparagine				("ÆSG", 	  99715,	'N',			null,	ASPARAGINE),
	Acetyl_Valine					("ÆVA", 	  66789,	'V',			null,	VALINE),
	Acetyl_Tryptophan				("ÆTR", 	   2002,	'W',	'Ω',	null,	TRYPTOPHAN),
	Acetyl_Tyrosine					("ÆTY", 	  89216,	'Y',	'Ϳ',		null,	TYROSINE),
	Acetyl_D_Aspartic__Acid			("ÆDD",		 774916,	'D',			null,	ASPARTIC_ACID),
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
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, PeptideLink type) {
		this(code, pubChem_id, letter, NOT_PROVIDED, type, __OTHER__, null, null, code);
	}
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type) {
		this(code, pubChem_id, letter, utf16_letter, type, (letter == utf16_letter) ? code : __OTHER__, null, null, code);
	}
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type, String baseForm) {
		this(code, pubChem_id, letter, utf16_letter, type, baseForm, null, null, code);
	}
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, PeptideLink type, String baseForm, String name) {
		this(code, pubChem_id, letter, NOT_PROVIDED, type, baseForm, null, name, null);
	}
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, PeptideLink type, String baseForm, String name, String chemCode) {
		this(code, pubChem_id, letter, NOT_PROVIDED, type, baseForm, null, name, chemCode);
	}
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type, String baseForm, String chemCode) {
		this(code, pubChem_id, letter, utf16_letter, type, baseForm, null, null, chemCode);
	}
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, char utf16_letter, PeptideLink type, E6 clazz) {
		this(code, pubChem_id, letter, utf16_letter, type, (letter == utf16_letter) ? code : __OTHER__, clazz, null, code);
	}
	
	private AminoNonPolymer(String code, int pubChem_id, char letter, PeptideLink type, String baseForm) {
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
	private AminoNonPolymer(String code, int pubChem_id, char letter, char utf16_letter,
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
	public static MoleculeType parse(String code) throws UnknownResidueException {
		MoleculeType type = MoleculeLookup.parse(code);
		if(type == null) { throw new UnknownResidueException(code); }
		if(!(type instanceof AminoType))  { return AminoType.OTHER; }
		
		return type;
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
	public MoleculeType standardize() { return MoleculeLookup.standardize(this); }
	
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
	@SuppressWarnings("unused")
	private static final void qp(Object arg0) {
		LocalToolBase.qp(arg0);
	}
}
