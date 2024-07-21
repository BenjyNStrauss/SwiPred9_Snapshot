import sys

import dataset.preprocess as spp
import dataset.LocalToolBase as tb
from dataset import neural2 as neural
#from dataset.errors import PreprocessingErrorException

import pandas as pd

import torch
import torch.nn as nn
from torch.utils.data import DataLoader, SubsetRandomSampler, ConcatDataset
from torch.optim.adam import Adam

from torcheval.metrics.functional import binary_f1_score
from torchmetrics.classification import BinaryF1Score
from sklearn.model_selection import train_test_split, KFold
from sklearn.metrics import roc_auc_score
#from sklearn.preprocessing import StandardScaler 
#import sklearn.metrics as metrics
import random

'''
Created on Mar 22, 2023

@author: Benjamin Strauss
'''

#does the neural network
def predict(frameX: pd.DataFrame, frameY: pd.DataFrame, layer_sizes, outFile, 
             threshold, device = "cpu", funct=nn.Mish(), epochs=16,
             learning_rate=0.0001, batchSize=80, testSize = 0.2, k_folds=8,
             randomStateTest=random.random(), randomStateVal=random.random(),
             isSwitchCol='isSwitch', loss_function=nn.BCELoss(), 
             validation_size=0.08, modelNo='NaN'):
    
    outFile.write('\n')
    tb.log(outFile, "Starting NN model-637 for threshold = " + str(threshold))
    
    #print("device:",device)
    
    #if not len(frameX.columns) == 37:
    #    raise PreprocessingErrorException()
    
    #if outFile is not None:
    #    tb.recordParams(outFile, epochs, batchSize, layer_sizes, loss_function, 
    #                funct, learning_rate, randomState, testSize)
    
    model = neural.SwiPredNN637(layer_sizes, function=funct)
    try:
        model.to(device)
    except RuntimeError:
        print("Error: device variable was: " + device)
        exit()
    predict = frameY[isSwitchCol];
    
    randomStateVal = int(round(randomStateVal, 2) * 100)
    randomStateTest = int(round(randomStateTest, 2) * 100)
    
    #print(len(frameX))
    #print(len(frameY['isSwitchVK']))
    #print(len(predict))
    X_train, X_val, y_train, y_val = train_test_split(frameX, predict, test_size=validation_size, random_state=randomStateVal)
    
    X_train, X_test, y_train, y_test = train_test_split(X_train, y_train, test_size=testSize, random_state=randomStateTest)
    
    reformatted_train = spp.mnistify(X_train, y_train)
    reformatted_test = spp.mnistify(X_test, y_test)
    
    #print("X_train",X_train)
    #print("y_train",y_train)
    #print("X_test",X_test)
    #print("y_test",y_test)
    #print("X_val",X_val)
    #print("y_val",y_val)
    
    #Validation starts here
    kfold = KFold(n_splits=k_folds, shuffle=True)
    #TODO is this just FrameX or also the targets?
    
    dataset = ConcatDataset([reformatted_train, reformatted_test])
    #dataset2 = ConcatDataset([reformatted_train, reformatted_test, reformatted_val])
    results = {}
    
    for fold, (train_ids, test_ids) in enumerate(kfold.split(dataset)):
        outFile.write('\n')
        tb.log(outFile, f'Model {modelNo}: Fold: {fold+1} of {k_folds}')
        outFile.write('\n')
        
        # Sample elements randomly from a given list of ids, no replacement.
        train_subsampler = SubsetRandomSampler(train_ids)
        test_subsampler = SubsetRandomSampler(test_ids)
        #val_subsampler = SubsetRandomSampler(val_ids)
        
        # Define data loaders for training and testing data in this fold
        trainloader = DataLoader(dataset,  batch_size=batchSize, sampler=train_subsampler)
        testloader = DataLoader(dataset, batch_size=batchSize, sampler=test_subsampler)
        #validationLoader = DataLoader(dataset2, batch_size=batchSize)
        
        model.reset_weights(outFile)
        
        #Start the training here…
        optimizer: Adam = Adam(model.parameters(), lr=learning_rate)
        
        #––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
        last_accs = [0,0,0,0,0,0,0,0,0,0]
        lastAccAvg = 0.0
        print("epochs:",epochs)
        
        #Run the prediction loop
        for e in range(epochs):
            #print("flag_0")
            trainingLoss = runEpoch(e, trainloader, optimizer, model, loss_function, outFile)
            
            acc = validate(X_val, y_val, model, batchSize)
            trainLossStr = f'{trainingLoss:8.4f}'
            accStr = str(round(acc,4))
            
            tb.log(outFile, "Epoch " + str(e+1) + " training loss: " + trainLossStr + " | validation acc: " +accStr)
                        
            last_accs[e % len(last_accs)] = acc
            
            if lastAccAvg >= avg(last_accs):
                #print(f'{avg(last_accs)} + vs {lastAccAvg}')
                outFile.write('\n')
                tb.log(outFile, "Stopped Early - Last Epoch: " + str(e+1))
                break
            else:
                lastAccAvg = avg(last_accs)
        
        f1_scores_metrics = []
        f1_scores_eval = []
        
        correct, total = 0, 0
        
        with torch.no_grad():
            for i, data in enumerate(testloader, 0):

                # Get inputs
                inputs, targets = data

                # Generate outputs
                outputs = model(inputs)
                
                #new version!
                predicted2 = torch.round(outputs)
                
                total += targets.size(0)
                for index in range(len(predicted2)):
                    if predicted2[index] == targets[index]:
                        correct += 1
                    total += 1
                
                f1_m = BinaryF1Score()(preds=predicted2.squeeze(1), target=targets)
                f1_scores_metrics.append(f1_m.item())
               
                f1_e = binary_f1_score(predicted2.squeeze(1).data, targets.data)
                f1_scores_eval.append(f1_e.item())
                
                roc_auc = float('NaN')
                try:
                    roc_auc = roc_auc_score(targets, predicted2)
                except:
                    __format__ = '> ' if i < 10 else '>'
                    msg = __format__+f"[{i}]: Only one class present in y_true: {targets[0]}. ROC AUC score undefined.\n"
                    sys.stderr.write(msg)
                    outFile.write(msg)
                    
            # Print accuracy
            
            acc = 100.0 * correct / total
            tb.log(outFile, 'Accuracy for fold %d:          %d %%' % (fold, acc))
            tb.log(outFile, f'Average Torchmetric F1 Score: {avg(f1_scores_metrics)}')
            tb.log(outFile, f'Average Torcheval   F1 Score: {avg(f1_scores_eval)}')
            tb.log(outFile, f'Test ROC-AUC:                 {roc_auc}')
            results[fold] = acc
            
            if acc < 0.01:
                tb.log(outFile, 'Model will not be saved due to abysmal accuracy.')
            elif roc_auc != roc_auc:
                tb.log(outFile, 'Model will not be saved due to NaN ROC-AUC predictions.')
            elif roc_auc == 0.5:
                tb.log(outFile, 'Model will not be saved due to random ROC-AUC predictions.')
            else:
                tb.log(outFile, 'Saving trained model.')
            
                save_path = f'./model-{modelNo}-fold-{fold}-threshold-{threshold}.pth'
                torch.save(model.state_dict(), save_path)
            
            if allZero(predicted2):
                tb.log(outFile, "Only 0s were predicted!")
            elif allOne(predicted2):
                tb.log(outFile, "Only 1s were predicted!")
            else:
                tb.log(outFile, "Mix of predictions!")
            
            tb.log(outFile, '------------------------------------------')
            
    return results
            
def runEpoch(epoch: int, trainLoader, optimizer, model, loss_function, outFile, print_loss_interval = 100):
    #print(f'Starting epoch {epoch+1}')
    current_loss = 0.0
    epoch_loss = 0.0
    #print("flag_2")
    
    for i, data in enumerate(trainLoader, 0):
        #print("flag_3")
        inputs, targets = data
        optimizer.zero_grad()
        outputs = model(inputs)
        targets_usq = targets.unsqueeze(1)
        
        #print(type(outputs))
        #print(type(targets_usq))
        #print("flag_4")
        loss = loss_function(outputs.float(), targets_usq.float())
        loss.backward()
        optimizer.step()
        #print("flag_5")
        
        current_loss += loss.item()
        epoch_loss += loss.item()
        if i % print_loss_interval == (print_loss_interval-1):
            loss_val = current_loss / print_loss_interval
            
            tb.log(outFile, 'Loss after mini-batch %5d: %.3f' % (i + 1, loss_val))
            current_loss = 0.0
        
    return (epoch_loss / len(trainLoader))
                
    # Iterate over the DataLoader for training data

def validate(X_val, y_val, model, batch_size):
    reformatted_val = spp.mnistify(X_val, y_val)
    
    correct, total = 0, 0
    
    vLoader = DataLoader(reformatted_val, batch_size=batch_size)
    
    with torch.no_grad():
        #original which failed: "for i, data in enumerate(validationLoader, 0):"
        #print(len(enumerate(validationLoader)))
        
        for i, data in enumerate(vLoader, 0):
            
            # Get inputs
            #print("len(data)",len(data))
            inputs = data[0]
            targets = data[1]
            #print("inputs ", inputs)
            #print("targets", targets)

            # Generate outputs
            outputs = model(inputs, debug=False)
            
            # Set total and correct
            _, predicted = torch.max(outputs.data, 1)
            total += targets.size(0)
            correct += (predicted == targets).sum().item()
        
    retval = 100.0 * correct / total
    return retval

def avg(values: list):
    #print("values:",values)
    
    total = 0
    for val in values:
        total += val
    total /= len(values)
    return total

def allZero(tensor):
    for value in tensor:
        if value != 0:
            return False
    return True

def allOne(tensor):
    for value in tensor:
        if value != 1:
            return False
    return True

def vector_max(vect):
    _max_ = -10000
    
    for value in vect:
        #print("_max_",_max_)
        #print("value.item()",value.item())
        if value.item() > _max_:
            _max_ = value.item()
    
    return _max_
