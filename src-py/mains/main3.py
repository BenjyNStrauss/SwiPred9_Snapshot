import os;

import regression.pipelines as pipes
import regression.regressions as regs

'''
Created on May 28, 2023

Note: install with '/usr/bin/pip3'

@author: Benjamin Strauss
'''

def main():
    os.chdir("../..")
    outfile = open("log.txt", "w")
    
    for ii in range (2, 7):
        filename = "output/s9-acetyl-sirt-esm-v"+str(ii)+".csv"
        frameX, frameY = pipes.pipeline_01(filename)
    
        print("Loaded data")
    
        result = regs.polyReg(frameX, frameY, start=1, stop=2, outFile=outfile)
        print(result)

if __name__ == "__main__":
    main()
    print("Completed.")
