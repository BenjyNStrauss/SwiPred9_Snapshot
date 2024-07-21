import enum

'''
Created on Jun 29, 2022

@author: Benjamin Strauss
'''

class ResType(enum.Enum):
    Alanine = 1;
    Asparagine = 2;
    AsparticAcid = 3;
    Arginine = 4;
    Cysteine = 5;
    Glutamine = 6;
    Glycine = 7;
    GlutamicAcid = 8;
    Histidine = 9;
    Isoleucine = 10;
    Leucine = 11;
    Lysine = 12;
    Methionine = 13;
    Phenylalanine = 14;
    Serine = 15;
    Proline = 16;
    Tryptophan = 17;
    Threonine = 18;
    Tyrosine = 19;
    Valine = 20;
    
    Pyrrolysine = 21;
    Selenocysteine = 22;
    
    _2_Aminoadipic_Acid = 23;
    _3_Aminoadipic_Acid = 24;
    BetaAlanine = 25;
    _2_Aminobutyric_Acid = 26;
    Piperidinic_Acid = 27;
    _6_Aminocaproic_Acid = 28;
    _2_Aminoheptanoic_Acid = 29;
    _2_Aminoisobutyric_Acid = 30
    _3_Aminoisobutyric_Acid = 31;
    _2_Aminopimelic_Acid = 32;
    _2_4_Diaminobutyric_Acid = 33;
    Desmosine = 34;
    _2_2_Diaminopimelic_Acid = 35;
    _2_3_Diaminoproprionic_Acid = 36;
    N_Ethylglycine = 37;
    N_Ethylasparagine = 38;
    Hydroxylysine = 39;
    Allo_Hydroxylysine = 40;
    _3_Hydroxyproline = 41;
    _4_Hydroxyproline = 42;
    Isodesmosine = 43;
    Allo_Isoleucine = 44;
    N_Methylglycine = 45;
    N_Methylisoleucine = 46;
    _6_N_Methyllysine = 47;
    N_Methylvaline = 48;
    Norvaline = 49;
    Norleucine = 50;
    Ornithine = 51;
    
    Hydroxy_L_Methionine = 52;
    S_MethylMercury_L_Cysteine = 53;
    S_Acetyl_Cysteine = 54;

    N_Acetyl_Serine = 55;
    S_MercaptoCysteine = 56;
    Fluorotryptophane = 57;
    N_Dimethyl_Lysine = 58;
    S_DimethylArsenic_Cysteine = 59;
    
    D_AsparticAcid = 60;
    D_Arginine = 61;
    D_Methionine = 62;
    D_Serine = 63;
    D_Lysine = 64;
    D_Glutamine = 65;
    
    Selenomethionine = 66;
    N6_Accetyllysine = 67;
    Unknown = 999999;
    
    def __str__(self):
        return abbrevDict[self]
        
    def toChar(self):
        return letterDict[standardizationDict[self]]

parsingDict = { "A": ResType.Alanine,       "ALA": ResType.Alanine,
                "C": ResType.Cysteine,      "CYS": ResType.Cysteine,
                "D": ResType.AsparticAcid,  "ASP": ResType.AsparticAcid,
                "E": ResType.GlutamicAcid,  "GLU": ResType.GlutamicAcid,
                "F": ResType.Phenylalanine, "PHE": ResType.Phenylalanine,
                "G": ResType.Glycine,       "GLY": ResType.Glycine,
                "H": ResType.Histidine,     "HIS": ResType.Histidine,
                "I": ResType.Isoleucine,    "ILE": ResType.Isoleucine,
                "L": ResType.Leucine,       "LEU": ResType.Leucine,
                "K": ResType.Lysine,        "LYS": ResType.Lysine,
                "M": ResType.Methionine,    "MET": ResType.Methionine,                
                "N": ResType.Asparagine,    "ASN": ResType.Asparagine,
                "O": ResType.Pyrrolysine,   "PYL": ResType.Pyrrolysine,
                "P": ResType.Proline,       "PRO": ResType.Proline,
                "Q": ResType.Glutamine,     "GLN": ResType.Glutamine,
                "R": ResType.Arginine,      "ARG": ResType.Arginine,
                "S": ResType.Serine,        "SER": ResType.Serine,
                "T": ResType.Threonine,     "THR": ResType.Threonine,
                "U": ResType.Selenocysteine,"SEC": ResType.Selenocysteine,
                "V": ResType.Valine,        "VAL": ResType.Valine,
                "W": ResType.Tryptophan,    "TRP": ResType.Tryptophan,
                "X": ResType.Unknown,       "UNK": ResType.Unknown,
                                            "XAA": ResType.Unknown,
                "Y": ResType.Tyrosine,      "TYR": ResType.Tyrosine,
                "_": None,
                
                "4ABU" : ResType.Piperidinic_Acid,
                "AAD"  : ResType._2_Aminoadipic_Acid,
                "ABU"  : ResType._2_Aminobutyric_Acid,
                "ACP"  : ResType._6_Aminocaproic_Acid,
                "AHE"  : ResType._2_Aminoheptanoic_Acid,
                "AHYL" : ResType.Allo_Hydroxylysine,
                "AIB"  : ResType._2_Aminoisobutyric_Acid,
                "AILE" : ResType.Allo_Isoleucine,
                "ALY"  : ResType.N6_Accetyllysine,
                "APM"  : ResType._2_Aminopimelic_Acid,
                "BAAD" : ResType._3_Aminoadipic_Acid,
                "BAIB" : ResType._3_Aminoisobutyric_Acid,
                "BALA" : ResType.BetaAlanine,
                "CAS"  : ResType.S_DimethylArsenic_Cysteine,
                "CMH"  : ResType.S_MethylMercury_L_Cysteine,
                "CSS"  : ResType.S_MercaptoCysteine,
                "DAR"  : ResType.D_Arginine,
                "DAS"  : ResType.D_AsparticAcid,
                "DBU"  : ResType._2_4_Diaminobutyric_Acid,
                "DES"  : ResType.Desmosine,
                "DGN"  : ResType.D_Glutamine,
                "DLY"  : ResType.D_Lysine,
                "DPM"  : ResType._2_2_Diaminopimelic_Acid,
                "DPR"  : ResType._2_3_Diaminoproprionic_Acid,
                "DSN"  : ResType.D_Serine,
                "ETGLY": ResType.N_Ethylglycine,
                "ETASN": ResType.N_Ethylasparagine,
                "FTR"  : ResType.Fluorotryptophane,
                "HYL"  : ResType.Hydroxylysine,
                "HY3"  : ResType._3_Hydroxyproline,
                "3HYP" : ResType._3_Hydroxyproline,
                "HYP"  : ResType._4_Hydroxyproline,
                "4HYP" : ResType._4_Hydroxyproline,
                "IDE"  : ResType.Isodesmosine,
                "ME0"  : ResType.Hydroxy_L_Methionine,
                "MED"  : ResType.D_Methionine,
                "MEGLY": ResType.N_Methylglycine,
                "MEILE": ResType.N_Methylisoleucine,
                "MELYS": ResType._6_N_Methyllysine,
                "MEVAL": ResType.N_Methylvaline,
                "MLY"  : ResType.N_Dimethyl_Lysine,
                "MSE"  : ResType.Selenomethionine,
                "NVA"  : ResType.Norvaline,
                "NLE"  : ResType.Norleucine,
                "ORN"  : ResType.Ornithine,
                "SAC"  : ResType.N_Acetyl_Serine,
                "SCY"  : ResType.S_Acetyl_Cysteine,    
}

abbrevDict = {
    ResType.Alanine:        "ALA",
    ResType.Cysteine:       "CYS",
    ResType.AsparticAcid:   "ASP",
    ResType.GlutamicAcid:   "GLU",
    ResType.Phenylalanine:  "PHE",
    ResType.Glycine:        "GLY",
    ResType.Histidine:      "HIS",
    ResType.Isoleucine:     "ILE",
    ResType.Leucine:        "LEU",
    ResType.Lysine:         "LYS",
    ResType.Methionine:     "MET",                
    ResType.Asparagine:     "ASN",
    ResType.Pyrrolysine:    "PYL",
    ResType.Proline:        "PRO",
    ResType.Glutamine:      "GLN",
    ResType.Arginine:       "ARG",
    ResType.Serine:         "SER",
    ResType.Threonine:      "THR",
    ResType.Selenocysteine: "SEC",
    ResType.Valine:         "VAL",
    ResType.Tryptophan:     "TRP",
    ResType.Tyrosine:       "TYR",
    
    ResType.Piperidinic_Acid:               "4ABU",
    ResType._2_Aminoadipic_Acid:            "AAD",
    ResType._2_Aminobutyric_Acid:           "ABU",
    ResType._6_Aminocaproic_Acid:           "ACP",
    ResType._2_Aminoheptanoic_Acid:         "AHE",
    ResType.Allo_Hydroxylysine:             "AHYL",
    ResType._2_Aminoisobutyric_Acid:        "AIB",
    ResType.Allo_Isoleucine:                "AILE",
    ResType.N6_Accetyllysine:               "ALY",
    ResType._2_Aminopimelic_Acid:           "APM",
    ResType._3_Aminoadipic_Acid:            "BAAD",
    ResType._3_Aminoisobutyric_Acid:        "BAIB",
    ResType.BetaAlanine:                    "BALA",
    ResType.S_DimethylArsenic_Cysteine:     "CAS",
    ResType.S_MethylMercury_L_Cysteine:     "CMH",
    ResType.S_MercaptoCysteine:             "CSS",
    ResType.D_Arginine:                     "DAR",
    ResType.D_AsparticAcid:                 "DAS",
    ResType._2_4_Diaminobutyric_Acid:       "DBU",
    ResType.Desmosine:                      "DES",
    ResType.D_Glutamine:                    "DGN",
    ResType.D_Lysine:                       "DLY",
    ResType._2_2_Diaminopimelic_Acid:       "DPM",
    ResType._2_3_Diaminoproprionic_Acid:    "DPR",
    ResType.D_Serine:                       "DSN",
    ResType.N_Ethylglycine:                 "ETGLY",
    ResType.N_Ethylasparagine:              "ETASN",
    ResType.Fluorotryptophane:              "FTR",
    ResType.Hydroxylysine:                  "HYL",
    ResType._3_Hydroxyproline:              "HY3",
    ResType._4_Hydroxyproline:              "HYP",
    ResType.Isodesmosine:                   "IDE",
    ResType.Hydroxy_L_Methionine:           "ME0",
    ResType.D_Methionine:                   "MED",
    ResType.N_Methylglycine:                "MEGLY",
    ResType.N_Methylisoleucine:             "MEILE",
    ResType._6_N_Methyllysine:              "MELYS",
    ResType.N_Methylvaline:                 "MEVAL",
    ResType.N_Dimethyl_Lysine:              "MLY",
    ResType.Selenomethionine:               "MSE",
    ResType.Norvaline:                      "NVA",
    ResType.Norleucine:                     "NLE",
    ResType.Ornithine:                      "ORN",
    ResType.N_Acetyl_Serine:                "SAC",
    ResType.S_Acetyl_Cysteine:              "SCY",
    ResType.Unknown:                        "UNK",
    }

letterDict = {
    ResType.Alanine:        "A",
    ResType.Cysteine:       "C",
    ResType.AsparticAcid:   "D",
    ResType.GlutamicAcid:   "E",
    ResType.Phenylalanine:  "F",
    ResType.Glycine:        "G",
    ResType.Histidine:      "H",
    ResType.Isoleucine:     "I",
    ResType.Leucine:        "L",
    ResType.Lysine:         "K",
    ResType.Methionine:     "M",                
    ResType.Asparagine:     "N",
    ResType.Pyrrolysine:    "O",
    ResType.Proline:        "P",
    ResType.Glutamine:      "Q",
    ResType.Arginine:       "R",
    ResType.Serine:         "S",
    ResType.Threonine:      "T",
    ResType.Selenocysteine: "U",
    ResType.Valine:         "V",
    ResType.Tryptophan:     "W",
    ResType.Unknown:        "X",
    ResType.Tyrosine:       "Y",
    }

standardizationDict = {
    ResType.Alanine:        ResType.Alanine,
    ResType.Cysteine:       ResType.Cysteine,
    ResType.AsparticAcid:   ResType.AsparticAcid,
    ResType.GlutamicAcid:   ResType.GlutamicAcid,
    ResType.Phenylalanine:  ResType.Phenylalanine,
    ResType.Glycine:        ResType.Glycine,
    ResType.Histidine:      ResType.Histidine,
    ResType.Isoleucine:     ResType.Isoleucine,
    ResType.Leucine:        ResType.Leucine,
    ResType.Lysine:         ResType.Lysine,
    ResType.Methionine:     ResType.Methionine,                
    ResType.Asparagine:     ResType.Asparagine,
    ResType.Pyrrolysine:    ResType.Pyrrolysine,
    ResType.Proline:        ResType.Proline,
    ResType.Glutamine:      ResType.Glutamine,
    ResType.Arginine:       ResType.Arginine,
    ResType.Serine:         ResType.Serine,
    ResType.Threonine:      ResType.Threonine,
    ResType.Selenocysteine: ResType.Selenocysteine,
    ResType.Valine:         ResType.Valine,
    ResType.Tryptophan:     ResType.Tryptophan,
    ResType.Tyrosine:       ResType.Tyrosine,
    
    ResType.Piperidinic_Acid:               ResType.Unknown,
    ResType._2_Aminoadipic_Acid:            ResType.Unknown,
    ResType._2_Aminobutyric_Acid:           ResType.Unknown,
    ResType._6_Aminocaproic_Acid:           ResType.Unknown,
    ResType._2_Aminoheptanoic_Acid:         ResType.Unknown,
    ResType.Allo_Hydroxylysine:             ResType.Lysine,
    ResType._2_Aminoisobutyric_Acid:        ResType.Unknown,
    ResType.Allo_Isoleucine:                ResType.Leucine,
    ResType.N6_Accetyllysine:               ResType.Lysine,
    ResType._2_Aminopimelic_Acid:           ResType.Unknown,
    ResType._3_Aminoadipic_Acid:            ResType.Unknown,
    ResType._3_Aminoisobutyric_Acid:        ResType.Unknown,
    ResType.BetaAlanine:                    ResType.Alanine,
    ResType.S_DimethylArsenic_Cysteine:     ResType.Cysteine,
    ResType.S_MethylMercury_L_Cysteine:     ResType.Cysteine,
    ResType.S_MercaptoCysteine:             ResType.Cysteine,
    ResType.D_Arginine:                     ResType.Arginine,
    ResType.D_AsparticAcid:                 ResType.AsparticAcid,
    ResType._2_4_Diaminobutyric_Acid:       ResType.Unknown,
    ResType.Desmosine:                      ResType.Unknown,
    ResType.D_Glutamine:                    ResType.Glutamine,
    ResType.D_Lysine:                       ResType.Lysine,
    ResType._2_2_Diaminopimelic_Acid:       ResType.Unknown,
    ResType._2_3_Diaminoproprionic_Acid:    ResType.Unknown,
    ResType.D_Serine:                       ResType.Serine,
    ResType.N_Ethylglycine:                 ResType.Glycine,
    ResType.N_Ethylasparagine:              ResType.Asparagine,
    ResType.Fluorotryptophane:              ResType.Tryptophan,
    ResType.Hydroxylysine:                  ResType.Lysine,
    ResType._3_Hydroxyproline:              ResType.Proline,
    ResType._4_Hydroxyproline:              ResType.Proline,
    ResType.Isodesmosine:                   ResType.Unknown,
    ResType.Hydroxy_L_Methionine:           ResType.Methionine,
    ResType.D_Methionine:                   ResType.Methionine,
    ResType.N_Methylglycine:                ResType.Glycine,
    ResType.N_Methylisoleucine:             ResType.Leucine,
    ResType._6_N_Methyllysine:              ResType.Lysine,
    ResType.N_Methylvaline:                 ResType.Valine,
    ResType.N_Dimethyl_Lysine:              ResType.Lysine,
    ResType.Selenomethionine:               ResType.Methionine,
    ResType.Norvaline:                      ResType.Valine,
    ResType.Norleucine:                     ResType.Leucine,
    ResType.Ornithine:                      ResType.Unknown,
    ResType.N_Acetyl_Serine:                ResType.Serine,
    ResType.S_Acetyl_Cysteine:              ResType.Cysteine,
    ResType.Unknown:                        ResType.Unknown,
    }

__columns__ = [ "A", "N", "D", "R", "C", "Q", "G", "E", "H", "I", "L", "K",
               "M", "F", "S", "P", "W", "T", "Y", "V", "O", "U" ]

vectorDict = {
    ResType.Alanine:        [1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Asparagine:     [0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.AsparticAcid:   [0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Arginine:       [0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Cysteine:       [0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Glutamine:      [0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Glycine:        [0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.GlutamicAcid:   [0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Histidine:      [0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Isoleucine:     [0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Leucine:        [0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0],
    ResType.Lysine:         [0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0],
    ResType.Methionine:     [0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0],
    ResType.Phenylalanine:  [0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0],
    ResType.Serine:         [0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0],
    ResType.Proline:        [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0],
    ResType.Tryptophan:     [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0],
    ResType.Threonine:      [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0],
    ResType.Tyrosine:       [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0],
    ResType.Valine:         [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0],
    ResType.Pyrrolysine:    [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0],
    ResType.Selenocysteine: [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1],
}

'''
Parse a String (str) into an ResType
'''
def parse(argument: str):
    try:
        return parsingDict[argument.upper()]
    except KeyError:
        return ResType.Unknown

'''
Encode a standardized residue type with one-hot encoding
''' 
def vectorize(sideChain: ResType):
    try:
        return vectorDict[sideChain]
    except KeyError:
        return [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
