import bio.ResType as ResType;
import bio.Amino as Amino

'''
Created on Jun 29, 2022

@author: Benjamin Strauss
'''

class Protein:
    protid: str = ""
    chain: str = ""
    sequence: list = []

    def __init__(self, protid: str, chain: str, sequence: str = None):
        self.protid = protid.strip()
        self.chain  = chain.strip()
        sequence    = sequence.strip()
        
        for letter in sequence:
            self.sequence.append(Amino.Amino(ResType.parse(letter)))
    
    def length(self):
        return len(self.sequence)
    
    def toSequence(self):
        seq: str = ""
        for amino in self.sequence:
            if amino is not None:
                seq += amino.toChar()
            else:
                seq += "_"
        return seq
    
    def toSequenceNonNull(self):
        seq: str = ""
        for amino in self.sequence:
            if amino is not None:
                seq += amino.toChar()
        return seq
    
    def get(self, index: int):
        return self.sequence[index]
    
    def add(self, index: int, value):
        if value is None:
            self.sequence.insert(index, value)
        elif isinstance(value, Amino.Amino):
            self.sequence.insert(index, value)
        elif isinstance(value, ResType.ResType):
            self.sequence.insert(index, Amino(value))
        elif isinstance(value, str):
            self.sequence.insert(index, Amino(ResType.parse(value)))
        else:
            raise TypeError
        
    def remove(self, index: int):
        self.sequence.remove(index)
    
    def __str__(self):
        return "("+self.protid+":"+self.chain+") "+self.toSequence()
    
    def fetchable(self):
        return self.protid+":"+self.chain
    
    def as_loaded(self):
        return self.protid+"_"+self.chain
