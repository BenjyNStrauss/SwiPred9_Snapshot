import os;
import regression.pipelines as pipes
import regression.regressions as regs
import regression.swipred_frame as spf

'''
Created on May 28, 2023

Note: install with '/usr/bin/pip3'

@author: Benjamin Strauss
'''

def main():
    os.chdir("../..")
    
    frameX, frameY = pipes.pipeline_01("output/s9-acetyl-sirt-esm-v2.csv")
    
    print("Loaded data")
    #print(frameX.columns)
    #print(frameY.name)
    
    result = regs.polyReg(frameX, frameY, start=1, stop=3)
    print(result)

if __name__ == "__main__":
    main()
    print("Completed.")
