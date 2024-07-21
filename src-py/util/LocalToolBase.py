'''
Created on Jul 17, 2023

@author: Benjamin Strauss
'''

def log(outFile=None, text: str = ""):
    print(f'{text}')
    if outFile is not None:
        outFile.write(f'{text}\n')
