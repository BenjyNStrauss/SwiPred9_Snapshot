package biology.molecule.types;

import system.SwiPred;

/**
 * [Nucleotide]_[Prime]_[#Phosphate]P
 * [Deoxynucleotide]_[Deoxy-Prime(s)]_[Prime]_[#Phosphate]P
 * 
 * IN BETA!
 * @author Benjamin Strauss
 *
 */

public enum NucleoType implements MoleculeType {
	ADENINE	("ADE", DNA_A, 		 190, "Adenine"),
	CYTOSINE("CYT", DNA_C, 		 597, "Cytosine"),
	GUANINE ("GUN", DNA_G, 135398634, "Guanine"),
	THYMINE ("TDR", DNA_T, 		1135, "Thymine"),
	URACIL  ("URA", DNA_U, 		1174, "Uracil"),
	INOSINE ("NOS", DNA_I, 135398641, "Inosine"),
	XANTHINE("XAN", DNA_X, 		1188, "Xanthine"),
	
	ADENOSINE					("ADN", DNA_A,		60961, "Adenosine"),
	ADENOSINE_1P				("AMP", DNA_A, 		 6083, "Adenosine Monophosphate"),
	ADENOSINE_2_1P				("2AM", DNA_A, 		94136, "Adenosine-2'-Monophosphate"),
	ADENOSINE_2_2P				("A2P", DNA_A,     440141, "Adenosine-2'-Diphosphate"),
	ADENOSINE_3_5_2P			("A3P", DNA_A, 	   159296, $RNA, "Adenosine-3'-5'-Diphosphate"),
	ADENOSINE_3_5_CYC_1P		("CMP", DNA_A, 		 6076, "Adenosine-3',5'-Cyclic-Monophosphate"),
	ADENOSINE_5_1P				("A",   DNA_A, 		 6083, $RNA, "Adenosine-5'-Monophosphate"),
	ADENOSINE_5_2P				("ADP", DNA_A, 		 6022, "Adenosine-5'-Diphosphate"),
	ADENOSINE_5_2P_GLUCOSE		("ADQ", DNA_A, 		16500, "Adenosine-5'-Diphosphate-Glucose"),
	ADENOSINE_5_2P_MVANA		("AD9", DNA_A, NO_PUBCHEM, "ADP Metavanadate"),
	ADENOSINE_5_2PR				("APR", DNA_A, 	   445794, "Adenosine-5'-Diphosphoribose"),
	ADENOSINE_5_3P				("ATP", DNA_A, 		 5957, "Adenosine-5'-Triphosphate"),
	ADENOSINE_5_4P				("AQP", DNA_A, 		14003, "Adenosine-5'-Tetraphosphate"),

	CYTIDINE_5_1P				("C5P", DNA_C, 		 6131, "Cytidine-5'-Monophosphate"), //AKA C
	METHYLCYTIDINE_5_1P			("5MC", DNA_C, 	   192785, $RNA, "5-Methylcytidine-5'-Monophosphate"),
	CYTIDINE_5_2P				("CDP", DNA_C,		 6132, "Cytidine-5'-Diphosphate"),
	CYTIDINE_5_3P				("CTP", DNA_C, 		 6176, "Cytidine-5'-Triphosphate"),
	
	GUANOSINE					("GMP", DNA_G,  135398635),
	GUANOSINE_2_1P				("2GP", DNA_G,  135460966, "Guanosine-2'-Monophosphate"),
	GUANOSINE_3_1P				("3GP", DNA_G,  135398727, "Guanosine-3'-Monophosphate"),
	GUANOSINE_5_1P				("5GP", DNA_G,  135398631, "Guanosine-5'-Monophosphate"),
	GUANOSINE_5_2P				("GDP", DNA_G,  135398619, $RNA, "Guanosine-5'-Diphosphate"),
	GUANOSINE_5_3P				("GTP", DNA_G, 	135398633, "Guanosine-5'-Triphosphate"),
	GUANOSINE_35_2P				("PGP", DNA_G, 	135509147, $RNA, "Guanosine-3',5'-Diphosphate"),
	GUANOSINE_53_4P				("G4P", DNA_G, 	135398637, $RNA, "Guanosine-5',3'-Tetraphosphate"),
	
	THYMIDINE					("THM", DNA_T, 		 5789, PeptideLink.DNA_5),
	THYMIDINE_3_1P				("T3P", DNA_T, 	   444920, $DNA, "Thymidine-3'-Phosphate"),
	THYMIDINE_5_1P				("DT",  DNA_T, 		 9700, $DNA, "Thymidine-5'-Monophosphate"), //AKA TMP
	THYMIDINE_5_2P				("TYD",	DNA_T, 	   164628, "Thymidine-5'-Diphosphate"),
	THYMIDINE_5_3P				("TTP", DNA_T, 		64968, "Thymidine-5'-Triphosphate"),
	THYMIDINE_3_5_2P			("THP", DNA_T, 	   121976, $DNA, "Thymidine-3',5'-Diphosphate"),
	
	URIDINE						("URI", DNA_U,		 6029),
	URIDINE_2P					("U2P", DNA_U, 	   101609, $RNA, "Uridine 2'-phosphate"),
	URIDINE_2_3CP				("JKM", DNA_U, 	   101609, $_, "Uridine 2',3'-cyclophosphate"),
	URIDINE_5_1P				("U5P", DNA_U, 		 6030, "Uridine-5'-Monophosphate"), //AKA U
	URIDINE_5_2P				("UDP", DNA_U, 		 6031, $RNA, "Uridine-5'-Diphosphate"),
	URIDINE_5_2P_GLUCO			("UPG", DNA_U, 		 8629, "Uridine-5'-Diphosphate-Glucose"),
	URIDINE_5_2P_GLUCU			("UPA", DNA_U, 	   444053, "Uridine-5'-Diphosphate-Glucuronic Acid"),
	URIDINE_5_2P_XYLO			("UPX", DNA_U,   72199642, "Uridine-5'-Diphosphate-Xylopyranose"),
	URIDINE_2P_N_ACE_GALAC		("UD2", DNA_U, 		23700, "Uridine-Diphosphate-N-Acetylgalactosamine"),
	URIDINE_5_3P				("UTP", DNA_U, 		 6133,"Uridine-5'-Triphosphate"),
	
	XANOSINE_5_1P				("XMP", DNA_X, NO_PUBCHEM, "Xanthosine-5'-Monophosphate"),
	
	FLAVIN_5_1P					("FMN", DNA_OTHER, 643976, "Flavin-5'-Monophosphate"),
	
	GALAC_URIDINE_5_2P			("GDU", DNA_U, 		18068, "Galactose-Uridine-5'-Diphosphate"),
	H2_URIDINE_56_5_1P			("H2U", DNA_U, "5,6-Dihydrouridine-5'-Monophosphate"),
	
	INOSINIC_ACID				("IMP", DNA_I, 135398640),
	INOSINE_5_3P				("ITT", DNA_I, 135398643, "Inosine 5'-Triphosphate"),
	
	DEOXYADENOSINE_2_5_1P		("DA",  DNA_A, "2'-DeoxyAdenosine-5'-Monophosphate"),
	DEOXYADENOSINE_2_5_2P		("DTP", DNA_A, "2'-Deoxyadenosine 5'-Triphosphate"),
	DEOXYADENOSINE_3			("3AD", DNA_A, 6303, "3'-Deoxyadenosine"),
	DEOXYADENOSINE_3_5_3P		("3AT", DNA_A, "3'-DeoxyAdenosine-5'-Triphosphate"),
	DEOXYADENOSINE_23_5_2P		("ADI", DNA_A, "2',3'-Dideoxyadenosine-5'-Diphosphate"),
	DEOXYADENOSINE_23_5_3P		("DAD", DNA_A, "2',3'-Dideoxyadenosine-5'-Triphosphate"),
	
	DEOXYCYTIDINE				("GNG", DNA_C, "2'-DeoxyCytidine"),
	DEOXYCYTIDINE_2P			("YYY", DNA_C, 150855, "Deoxycytidine Diphosphate"),
	DEOXYCYTIDINE_2_5_1P		("DC",  DNA_C, "2'-DeoxyCytidine-5'-Monophosphate"),
	DEOXYCYTIDINE_2_5_3P		("DCP", DNA_C, "2'-DeoxyCytidine-5'-Triphosphate"),
	
	DEOXYGUANOSINE_2			("GNG", DNA_G, 135398592, "2'-DeoxyGuanosine"),
	DEOXYGUANOSINE_2_5_1P		("DGP", DNA_G, 135398597, "2'-DeoxyGuanosine-5'-Monophosphate"), //AKA DG
	DEOXYGUANOSINE_2_5_3P		("DGT", DNA_G, "2'-DeoxyGuanosine-5'-Triphosphate"),
	
	DEOXYTHYMIDINE_5_1P			("2DT", DNA_T, "3'-Deoxythymidine-5'-Monophosphate"),
	AZIDO_DEOXYTHYMIDINE_5_1P	("ATM", DNA_T, "3'-Azido-3'-Deoxythymidine-5'-Monophosphate"),
	FLUORO_DEOXYTHYMIDINE_5_1P	("FDM", DNA_T, "3'-Fluoro-3'-Deoxythymidine Monophosphate"),
	AZIDO_DEOXYTHYMIDINE_5_3P	("AZT", DNA_T, "3'-Azido-3'-Deoxythymidine-5'-Triphosphate"),
	
	DEOXYURIDINE_5_2P			("DUD", DNA_U, "Deoxyuridine_5'_Diphosphate"),
	DEOXYURIDINE_5_3P			("DUT", DNA_U, "Deoxyuridine_5'_Triphosphate"),
	DEOXYURIDINE_2				("GNG", DNA_U, "2'-DeoxyUridine"),
	DEOXYURIDINE_2_3_1P			("UM3", DNA_U, "2'-DeoxyUridine-3'-Monophosphate"), 
										//  2'_Deoxyuridine_5'__Monophosphate("UMP"),
	DEOXYURIDINE_2_5_1P			("DU",  DNA_U, "2'-DeoxyUridine-5'-Monophosphate"),
	
	PROPY_DEOXYURIDINE_2_5_1P	("PSU", DNA_U, "5(1-Propynyl)-2'-Deoxyuridine-5-Monophosphate"),
	DEOXYURIDINE_3_5_3P			("U3H", DNA_U, "3'-DeoxyUridine-5'-Triphosphate"),
	
	PSEUDOURIDINE_5_1P			("PSU", DNA_U, 439424, "Pseudouridine-5'-Monophosphate"),
	
	ENTECAVIR_5_3P				("ET9", DNA_G, 135411098, "Entecavir Triphosphate"),
	
	DI2_FLAVIN_ADENINE			("FAD", DNA_A, "Flavin-Adenine Dinucleotide"),
	DI2_NICOTINAMIDE_GUANINE	("NGD", DNA_MULTI, 135404874, "Nicotinamide Guanine Dinucleotide"),
	DI2_NICOTINIC_ACID_ADENINE	("DND", DNA_MULTI, "Nicotinic Acid Adenine Dinucleotide"),
	DI2_NICOTINAMIDE_ADENINE_1P	("NAP", DNA_MULTI, "NADP Nicotinamide-Adenine-Dinucleotide Phosphate"),
	DI2_PTERIN_CYTOSINE			("MCN", DNA_MULTI, "Pterin Cytosine Dinucleotide"),
	
	_8_FORMYL_FLAVIN_ADENINE_DINUCLEOTIDE("FAY", DNA_MULTI, 21820032),
	
	_9_HYDROXYPROPYL_ADENINE__R ("ARP", DNA_A, "9-Hydroxypropyladenine, R-Isomer"),
	_9_HYDROXYPROPYL_ADENINE__S ("ARP", DNA_A, "9-Hydroxypropyladenine, S-Isomer"),
	
	__0OH__("0OH", DNA_A, "North-Methanocarba-2'-Deoxyadenosine Triphosphate"),
	__0OJ__("0OJ", DNA_A, "South-Methanocarba-2'-Deoxyadenosine Triphosphate"),
	__24G__("24G", DNA_U, "Uridine-5'-Diphosphate-3-O-(R-3-Hydroxymyristoyl)-Glucosamine"),
	__523__("523", DNA_U, "2'-Deoxy-5-Methylcytidine 5'-(Tetrahydrogen Triphosphate)"),
	__6FA__("6FA", DNA_MULTI, "6-Hydroxy-Flavin-Adenine Dinucleotide"),
	
	__A1R__("A1R", DNA_A, "5'-o-[(S)-{[(S)-{[(2R,3R,4S)-3,4-Dihydroxypyrrolidin-2-yl]Methoxy}(Hydroxy)Phosphoryl]Oxy}(Hydroxy)Phosphoryl]Adenosine"),
	__AAM__("AAM", DNA_A, "Alpha-Adenosine Monophosphate"),
	__ADJ__("ADJ", DNA_MULTI, "Nicotinamide-Adenine-Dinucleotide-Adenylate Intermediate"),
	__AMO__("AMO", DNA_A, "Aspartyl-Adenosine-5'-Monophosphate"),
	__AP5__("AP5", DNA_A, "Bis(Adenosine)-5'-Pentaphosphate"),
	__AVU__("AVU", DNA_A, 49866653, "Arabinosyl-2-Fluoro-Deoxy-Adenosine Diphosphate Ribose"),
	__AVV__("AVV", DNA_A, 44129639, "2-Fluoro-Adenosine Diphosphate Ribose"),
	__AZZ__("AZZ", DNA_T, "3'-Azido-3'-Deoxythymidine"),
	
	__CMK__("CMK", DNA_C, 445888, "Cytidine 5'-Monophosphate 3-Deoxy-Beta-D-Gulo-Oct-2-Ulo-Pyranosonic Acid"),
	__CNA__("CNA", DNA_MULTI, 163884, "Carba-Nicotinamide-Adenine-Dinucleotide"),
	__CV1__("CV1", DNA_A, 44457111, "8-Bromo-Cyclic-ADP-Ribose"),
	
	__DN4__("DN4", DNA_MULTI, 123952, "Nicotinate Adenine Dinucleotide Phosphate"),
	__DU2__("DU2", DNA_U, "2',5'-Dideoxy-5'-{[(R)-(1-Methyl-1H-Imidazol-2-Yl)(Phenyl)Methyl]Amino}Uridine"),
	__DU3__("DU3", DNA_U, "2',5'-Dideoxy-5'-[(Diphenylmethyl)Amino]Uridine"),
	__DU4__("DU4", DNA_U, "2',5'-Dideoxy-5'-[(Diphenylmethyl)(Methyl)Amino]Uridine"),
	__DUA__("DUA", DNA_U, "2',5'-Dideoxy-5'-(Tritylamino)Uridine"),	
	__DUX__("DUX", DNA_U, "2,3-Deoxy-3-Fluoro-5-O-Trityl-Uridine"),
	
	__EPU__("EPU", DNA_U, 172502, "Uridine-Diphosphate-2(N-Acetylglucosaminyl) Butyric Acid"),
	
	__FAS__("FAS", DNA_MULTI, "Arabino-Flavin-Adenine Dinucleotide"),
	__FCJ__("FCJ", DNA_A, "2'-Deoxy-5'-O-[(R)-Hydroxy{[(R)-Hydroxy(Phosphonomethyl)Phosphoryl]Oxy}Phosphoryl]Adenosine"),
	__FDJ__("FDJ", DNA_T, "5'-O-[(R)-{[(R)-[(R)-Chloro(Phosphono)Methyl](Hydroxy)Phosphoryl]Oxy}(Hydroxy)Phosphoryl]Thymidine"),
	__FDV__("FDV", DNA_T, "5'-O-[(R)-Hydroxy({(R)-Hydroxy[(1S)-1-Phosphonoethyl]Phosphoryl}Oxy)Phosphoryl]Thymidine"),
	__FDY__("FDY", DNA_T, "5'-O-[(R)-{[(R)-[(R)-Fluoro(Phosphono)Methyl](Hydroxy)Phosphoryl]Oxy}(Hydroxy)Phosphoryl]Thymidine"),
	__FSH__("FSH", DNA_A, 24944442, "(2r,3s,4s)-5-[(4r)-6',7'-Dimethyl-2,3',5-Trioxo-1'h-Spiro[imidazolidine-4,2'-Quinoxalin]-4'(3'h)-Yl]-2,3,4-Trihydroxypentyl-Adenosine Diphosphate"),
	
	__GAV__("GAV", DNA_G, "Guanosine-5'-Rp-Alpha-Thio-Triphosphate"),
	__GPG__("GPG", DNA_G, "Guanylyl-2',5'-Phosphoguanosine"),
	
	__MI3__("MI3", DNA_I, "3'-O-{[2-(Methylamino)Phenyl]Carbonyl}Inosine 5'-(Tetrahydrogen Triphosphate)"),
	__MGD__("MGD", DNA_MULTI, "2-Amino-5,6-Dimercapto-7-Methyl-3,7,8A,9-Tetrahydro-8-Oxa-1,3,9,10-Tetraaza-Anthracen-4-One Guanosine Dinucleotide"),
	__MNT__("MNT", DNA_A, "2'(3')-O-N-Methylanthraniloyl-Adenosine-5'-Diphosphate"),
	__MTA__("MTA", DNA_A, "5'-Deoxy-5'-Methylthioadenosine"),
	__MYD__("MYD", DNA_A, 477579, "C2-Mycophenolic Adenine Dinucleotide"),
	
	__N6T__("N6T", DNA_T, "2 '-Deoxy-5'-O-[(S)-Hydroxy{[(S)-Hydroxy(Phosphonoamino)Phosphoryl]Methyl}Phosphoryl]-3,4-Dihydrothymidine"),
	__NAI__("NAI", DNA_MULTI, "1,4-Dihydronicotinamide Adenine Dinucleotide"),
	__NBX__("NBX", DNA_OTHER, "N-{[(4-Aminophenyl)Carbonyl]Carbamoyl}-Beta-D-Glucopyranosylamine"),
	__NDA__("NDA", DNA_MULTI, "3-Aminomethyl-Pyridinium-Adenine-Dinucleotide"),
	__NDP__("NDP", DNA_MULTI, "Nadph Dihydro-Nicotinamide-Adenine-Dinucleotide Phosphate"),
	
	__OAD__("OAD", DNA_A, 447049, $RNA, "2'-O-Acetyl Adenosine-5-Diphosphoribose"),
	__ONA__("ONA", DNA_A, 123756, "3'-O-[2-(Methylamino)Benzoyl]Adenosine 5'-(Tetrahydrogen Triphosphate)"),
	__ONM__("ONM", DNA_G, 135509141, "3'-O-(N-Methylanthraniloyl)-Guanosine-5'-Triphosphate"),	
	
	__P1H__("P1H", DNA_MULTI, 49867455, "(4S)-4-(2-Propylisonicotinoyl)Nicotinamide Adenine Dinucleotide"),
	__P2G__("P2G", DNA_G, 135464488, "Guanosine-2',3'-O-methylidenephosphonate"),
	
	__RFL__("RFL", DNA_MULTI, 5289282, "8-Demethyl-8-Dimethylamino-Flavin-Adenine-Dinucleotide"),
	__RP2__("RP2", DNA_A, NO_PUBCHEM, "8-Bromoadenosine-3',5'-Cyclic Monophosphorothioate"),
	
	__SGP__("SGP", DNA_G, NO_PUBCHEM, "Guanosine-2',3'-Cyclophosphorothioate"),
	
	__TAD__("TAD", DNA_MULTI, "Beta-Methylene-Thiazole-4-Carboxyamide-Adenine Dinucleotide"),
	__TAT__("TAT", DNA_A, "Adenosine-5'-Rp-Alpha-Thio-Triphosphate"),
	__TXE__("TXE", DNA_MULTI, 57149546, "1,2,3,4-TetrahydroNicotinamide Adenine Dinucleotide"),
	
	__UDA__("UDA", DNA_U, 448003, "3'-1-Carboxy-1-Phosphonooxy-Ethoxy-Uridine-Diphosphate-N-Acetylglucosamine"),
	__URM__("URM", DNA_U, 46916280, "Uridine diphospho methylene galactopyranose"),
	
	__VA4__("VA4", DNA_A, "5'-O-[(R)-{[(R)-[(R)-Chloro(Phosphono)Methyl](Hydroxy)Phosphoryl]Oxy}(Hydroxy)Phosphoryl]-2'-Deoxyadenosine"),
	__VA7__("VA7", DNA_A, "2'-Deoxy-5'-O-[(R)-{[(R)-[Dichloro(Phosphono)Methyl](Hydroxy)Phosphoryl]Oxy}(Hydroxy)Phosphoryl]Adenosine"),
	
	UNKNOWN("N", DNA_OTHER, 445796, $RNA, "Any 5'-Monophosphate Nucleotide");
	
	public final String code;
	public final char letter;
	public final int pubChem_id;
	public final PeptideLink type;
	public final String name;
	
	private NucleoType(String code, char letter) {
		this(code, letter, NEED_PUBCHEM, $_, null);
	}
	
	private NucleoType(String code, char letter, int pubChem_id) {
		this(code, letter, pubChem_id, $_, null);
	}
	
	private NucleoType(String code, String name) {
		this(code, DNA_OTHER, NEED_PUBCHEM, $_, name);
	}

	private NucleoType(String code, char letter, String name) {
		this(code, letter, NEED_PUBCHEM, $_, name);
	}
	
	private NucleoType(String code, char letter, int pubChem_id, PeptideLink type) {
		this(code, letter, pubChem_id, type, null);
	}
	
	private NucleoType(String code, char letter, int pubChem_id, String name) {
		this(code, letter, pubChem_id, $_, name);
	}
	
	private NucleoType(String code, char letter, int pubChem_id, PeptideLink type, String name) {
		this.code = code;
		this.letter = letter;
		this.name = name;
		this.type = type;
		this.pubChem_id = pubChem_id;
	}
	
	@Override
	public char toChar() { return (SwiPred.showNonAscii) ? letter : 'X'; }
	
	@Override
	public String toCode() { return code; }

	public String toString() { 
		return (name != null) ? name : fixString(super.toString());
	}
}
