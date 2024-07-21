import os
import dataset.preprocess as spp
import dataset.LocalToolBase as tb
import dataset.ics637train_v4 as train

from dataset import loader#, errors, swipred_pca
from dataset.randomOptions import getRandomParams
#from dataset.rocloss.rocstar import roc_star_loss

import torch.nn as nn

'''
Created on Mar 22, 2023

@author: Benjamin Strauss
'''

filename = "log-637-"
validation_size = 0.08
loss_function = nn.BCEWithLogitsLoss() #roc_star_loss #

start_model = 0
num_models = 32
vectors=8

def main():
    frameX, frameY = loader.getFrame(include_unassigned=False)
    #frameX = swipred_pca.applyPCA(frameX, frameY, vectors=vectors, verbose=True)
    
    os.chdir("uhm-logs/")
    abs_path = os.getcwd()
    
    #print(len(frameX))
    #exit()
    for run in range(start_model, num_models):
        print("Run #",run)
        EPOCHS, batch_size, randomState, layer_sizes, learning_rate, test_size, function = getRandomParams(37,1)
        
        _filename_ = filename + str(run)
        outFile = open(f'{abs_path}/{_filename_}-upgraded.txt', "a")
        tb.log(outFile, f"Evaluation metrics are for the test set unless otherwise stated.")
        
        tb.recordParams(outFile, EPOCHS, batch_size, layer_sizes, loss_function, 
                        function, learning_rate, randomState, test_size, validation_size)
        
        #print(frameY)
        
        for value in range(0,3):
            bool_threshold = value * 0.05
            tb.log(outFile, f"\nClassifying on threshold of: {bool_threshold:.2f}")
            
            #print()
            str_threshold = str(int(round(bool_threshold*100)))
            
            #2 redundant cols!
            isSwitchCol = "isSwitch"+str_threshold
            
            frameY2 = spp.makeIsSwitch(frameY, addVK = False, threshold=bool_threshold,
                                       include_unassigned=False, isSwitchCol=isSwitchCol,
                                       realVKCol="isSwitchVK")
            
            #errors.assertZeros(frameY, isSwitchCol)
        
            train.predict(frameX, frameY2, layer_sizes, outFile, str_threshold,
                          modelNo=run, funct=function, learning_rate=learning_rate,
                          testSize=test_size, batchSize=batch_size, epochs=EPOCHS,
                          isSwitchCol=isSwitchCol, loss_function=loss_function)
        
        outFile.close()
        #exit()

if __name__ == "__main__":
    main()
    print("Completed.")



