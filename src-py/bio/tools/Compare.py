from bio.Amino import Amino
from bio.Protein import Protein

'''
Created on Jul 6, 2022

@author: Benjamin Strauss
'''

def avg_rmsd(a1: Amino, a2: Amino, model, power: int):
    _sum = 0
    vec1 = a1.getEmbedding(model)
    vec2 = a2.getEmbedding(model)
    minLen: int = min(len(vec1), len(vec2))
    for index2 in range(0, minLen):
        val = vec1[index2] - vec2[index2]
        val = val ** power
        _sum += val
    _sum = (_sum / minLen);
    _sum ** (1/power)
    return _sum

def avg_rmsd_blank(a1: Amino, vector, model, power: int):
    _sum = 0
    vec1 = a1.getEmbedding(model)
    vec2 = vector
    minLen: int = min(len(vec1), len(vec2))
    for index2 in range(0, minLen):
        val = vec1[index2] - vec2[index2]
        val = val ** power
        _sum += val
    _sum = (_sum / minLen);
    _sum ** (1/power)
    return _sum

def getAvgRMSD(p1: Protein, p2: Protein, model, power: int = 2, gapPenalty: []):
    #do alignment
    
    _sum = 0
    minLen: int = min(p1.length(), p2.length())
    for index in range(0, minLen):
        if p1.get(index) is None and p2.get(index) is None:
            continue
        elif p1.get(index) is None and p2.get(index) is not None:
            _sum += avg_rmsd(p2.get(index), gapPenalty, model, power)
        elif p1.get(index) is not None and p2.get(index) is None:
            _sum += avg_rmsd(p1.get(index), gapPenalty, model, power)
        else:
            _sum += avg_rmsd(p1.get(index), p2.get(index), model, power)
    _sum = (_sum / minLen);
    return _sum
        
#TODO: finish



        


