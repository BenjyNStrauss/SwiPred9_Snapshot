from bio import ResType

'''
Created on Jun 29, 2022

@author: Benjamin Strauss
'''

class Amino:
    __type__: ResType
    __embeddings__ : dict = { }
    
    def __init__(self, aminoType: ResType):
        self.__type__ = aminoType
    
    def setEmbedding(self, embeddingType, embedding):
        self.__embeddings__[embeddingType] = embedding
    
    def getEmbedding(self, embeddingType):
        return self.__embeddings__[embeddingType]
    
    def __str__(self):
        return self.__type__.__str__()
    
    def toChar(self):
        return self.__type__.toChar()

'''
Verifies that the amino object has a given type
'''
def isValid(amino: Amino):
    if amino is not None:
        if amino.__type__ is not None:
            return True
    return False