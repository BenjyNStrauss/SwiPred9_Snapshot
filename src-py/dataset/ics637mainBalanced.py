import os
import dataset.preprocess as spp
import dataset.LocalToolBase as tb
import dataset.ics637train_v4 as train

from dataset import loader, errors, swipred_pca
from dataset.randomOptions import getRandomParams

import torch.nn as nn

'''
Created on Mar 22, 2023

@author: Benjamin Strauss
'''

filename = "log-637-"
validation_size = 0.08
loss_function = nn.BCELoss() #roc_star_loss #
doPCA = False
dropExtra = False
start_model = 0
num_models = 16
vectors=8

def main():
    frameX, frameY = loader.getFrame(include_unassigned=False)
    
    if doPCA:
        frameX = swipred_pca.applyPCA(frameX, frameY, vectors=vectors, verbose=True)
    elif dropExtra:
        frameX.drop(['IsU[3]-Window', 'E6-Window[3]', 'E20',
       'E20-Window[3]', 'Amber95-Window[3]', 'VK-local', 'VK-original',
       'VK-orig-weight-charge', 'res_A', 'res_C', 'res_D', 'res_E', 'res_F',
       'res_G', 'res_H', 'res_I', 'res_K', 'res_L', 'res_M', 'res_N', 'res_P',
       'res_Q', 'res_R', 'res_S', 'res_T', 'res_V', 'res_W', 'res_Y', 'res_O',
       'res_U', 'res_X'], axis=1, inplace=True)
    
    
    os.chdir("uhm-logs/")
    abs_path = os.getcwd()
    
    #print(len(frameX))
    #exit()
    for run in range(start_model, num_models):
        print(f"Run #{run} - Balanced Version")
        EPOCHS, batch_size, randomState, layer_sizes, learning_rate, test_size, function = getRandomParams(37,1)
        
        _filename_ = filename + str(run)
        outFile = None
        if doPCA:
            outFile = open(f'{abs_path}/{_filename_}-bal-pca.txt', "a")
        else:
            outFile = open(f'{abs_path}/{_filename_}-bal.txt', "a")
        
        #redefine some params
        learning_rate=0.00001
        layer_sizes = [56, 54, 24]
        
        tb.recordParams(outFile, EPOCHS, batch_size, layer_sizes, loss_function, 
                        function, learning_rate, randomState, test_size, validation_size)
        
        #print(frameY)
        
        for value in range(0,4):
            bool_threshold = value * 0.05
            tb.log(outFile, f"\nClassifying on threshold of: {bool_threshold:.2f}")
            
            #print()
            str_threshold = str(int(round(bool_threshold*100)))
            
            #2 redundant cols!
            isSwitchCol = "isSwitch"+str_threshold
            
            frameY2 = spp.makeIsSwitch(frameY, addVK = False, threshold=bool_threshold,
                                       include_unassigned=False, isSwitchCol=isSwitchCol,
                                       realVKCol="isSwitchVK")
            
            frameX_bal, frameY2_bal = spp.shrink_balance_dataset(frameX, frameY2, balanceCol=isSwitchCol)
            
            #errors.assertZeros(frameY, isSwitchCol)
        
            train.predict(frameX_bal, frameY2_bal, layer_sizes, outFile, str_threshold,
                          modelNo=run, funct=function, learning_rate=learning_rate,
                          testSize=test_size, batchSize=batch_size, epochs=EPOCHS,
                          isSwitchCol=isSwitchCol, loss_function=loss_function)
        
        outFile.close()
        #exit()

if __name__ == "__main__":
    main()
    print("Completed.")



