from proteinBERT.swipred_dataset_2_beta import SwipredSequence

'''
Created on Feb 5, 2024

@author: Benjamin Strauss
'''
    


def simplifySecondary(line):
    line = line.replace("2", "1")
    line = line.replace("3", "1")
    line = line.replace("4", "1")
    line = line.replace("5", "2")
    line = line.replace("6", "2")
    line = line.replace("7", "3")
    line = line.replace("8", "3")
    line = line.replace("9", "3")
    return line
    
with open("../../input/swipredbert/seq-lib.txt") as infile:
    line = infile.readline();
    seq_obj = SwipredSequence(line);
    print(simplifySecondary(seq_obj.tokens))
