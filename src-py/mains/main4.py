import os;
#import sys;

import regression.pipelines as pipes
import regression.regressions as regs

'''
Created on May 28, 2023

Note: install with '/usr/bin/pip3'

path: /Applications/Xcode.app/Contents/Developer/usr/bin/python3

@author: Benjamin Strauss
'''

def main():
    #print(sys.executable)
    
    #os.chdir("../..")
    print(os.getcwd())
    
    outfile = open("log-bon-uhm.txt", "w")
    
    filename = "output/bon-esm-full.csv"
    
    frameX, frameY = pipes.pipeline_01(filename)
    print("Loaded data")
    
    print(frameX)
    exit()
    
    result = regs.polyReg(frameX, frameY, start=1, stop=1, outFile=outfile)
    print(result)

if __name__ == "__main__":
    main()
    print("Completed.")
