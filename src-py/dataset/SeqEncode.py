from bio.Protein import Protein

'''
Created on Mar 28, 2022

@author: Benjamin Strauss
'''

aminoEncodingDict = {
    "A" : 1,
    "C" : 2,
    "D" : 3,
    "E" : 4,
    "F" : 5,
    "G" : 6,
    "H" : 7,
    "I" : 8,
    "K" : 9,
    "L" : 10,
    "M" : 11,
    "N" : 12,
    "P" : 13,
    "Q" : 14,
    "R" : 15,
    "S" : 16,
    "T" : 17,
    "V" : 18,
    "W" : 19,
    "Y" : 20,
    "O" : 21,
    "U" : 22,
    "X" : 23
}

aminoE6EncodingTypes = ['a','b','c','d','e','f','g']

'''
Positive = a
Negative = b
Polar = c
Hydrophobic = d
Special = e
Aromatic = f
Unknown = g
'''
aminoE6EncodingDict = {
    "A" : 'd',
    "C" : 'e',
    "D" : 'b',
    "E" : 'e',
    "F" : 'f',
    "G" : 'e',
    "H" : 'a',
    "I" : 'd',
    "K" : 'a',
    "L" : 'd',
    "M" : 'd',
    "N" : 'c',
    "P" : 'e',
    "Q" : 'c',
    "R" : 'a',
    "S" : 'c',
    "T" : 'c',
    "V" : 'e',
    "W" : 'f',
    "Y" : 'f',
    "O" : 'a',
    "U" : 'e',
    "X" : 'g'
}

def encodeSeq(prot: Protein, padTo: int = -1):
    seq: str = prot.sequence;
    encoding = [];
    for letter in seq:
        encoding.append(aminoEncodingDict.get(letter, 22));
    if padTo > 0:
        while len(encoding) < padTo:
            encoding.append(0);
    return encoding;

'''
Gets all of the 2-mers based on E6 encodings
'''
def get_2mersE6_prot(prot: Protein):
    return get_2mersE6(prot.sequence);

'''
Gets all of the 2-mers based on E6 encodings
'''
def get_2mersE6(seq: str):
    #print(seq);
    encoding = [];
    for letter in seq:
        encoding.append(aminoE6EncodingDict.get(letter, 'g'));
    encoding = "".join(encoding);
    #print(encoding);
    kmer_list = [];
    for letter1 in aminoE6EncodingTypes:
        for letter2 in aminoE6EncodingTypes:
            key: str = letter1+letter2;
            value: int = encoding.count(key);
            kmer_list.append(value);
    return kmer_list;

#_seq: str = "SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRALDYSFTILNLHKIYLHVAVENPKAVHLYEECGFVEEGHLVEEFFINGRYQDVKRMYILQSKYLNRSE";

#print(get_2mersE6(_seq));

