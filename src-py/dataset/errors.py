import sys
from pandas import DataFrame

'''
Created on Apr 28, 2023

@author: Benjamin Strauss
'''

class PreprocessingErrorException(Exception):
    def __init__(self, msg = None):
        self.msg = msg
        
def assertZeros(frame: DataFrame, isSwitchCol: str):
    foundZero = False
    for value in frame[isSwitchCol]:
        if value == 0:
            foundZero = True
    if not foundZero:
        sys.stderr.write("Preprocessing Error Found - No Zeros!\n")
        exit()