'''
Created on Aug 22, 2024
Manages save files to standardize file names

@author: Benjamin Strauss
'''

SAVE_PATH = "./files/saved/swipredbert"

GLOBAL_ACTIVE_SAVE_FILE = None

class SPBertSaveFile:
    def __init__(self, key, database, epochs, redundancy, sample = 0):
        self.key = key
        self.database = database
        self.epochs = epochs
        self.redundancy = redundancy
        self.sample = sample
        self.run_no = 0
    
    def getFileName(self):
        if self.database == "pdb":
            self.database = "dssp"
        filename = self.key+"_epoch_"+str(self.epochs)+"_"+self.database+"_"+self.redundancy
        
        if self.run_no != 0:
            filename += "_run#"+str(self.run_no)
        
        filename += ".pkl"
        return filename

def test_GLOBAL_ACTIVE_SAVE_FILE():
    if GLOBAL_ACTIVE_SAVE_FILE is None:
        raise ValueError("GLOBAL_ACTIVE_SAVE_FILE not initialized!!!")
        exit(1)
