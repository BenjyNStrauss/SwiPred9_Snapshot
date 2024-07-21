import sys

from neuralnet.regressorNN import SwiPredRegressorNN
from neuralnet.dataset import LearnSet, TestSet
from neuralnet.logging import recordParams
from util.LocalToolBase import log

import pandas as pd
import torch
import torch.nn as nn
from torch.utils.data import DataLoader
from torch.optim.adam import Adam
#from torcheval.metrics.functional import
from torchmetrics.classification import F1Score

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler 
import sklearn.metrics as metrics

'''
Created on Jul 17, 2023
TODO: under development

@author: Benjamin Strauss
'''

#does the neural network
def predict(frameX: pd.DataFrame, frameY: pd.DataFrame, layer_sizes, outFile,
             device = "cpu", funct=nn.Mish(), epochs=16, learning_rate=0.0001, batchSize=80,
             testSize = 0.2, randomState=42):
    log(outFile, "Starting NN model-637")
    
    model = SwiPredRegressorNN(layer_sizes, function=funct)
    model.to(device);
    predict = frameY['isSwitch'];
    
    X_train, X_test, y_train, y_test = train_test_split(frameX, predict, test_size=testSize, random_state=randomState)
    
    loss_function = nn.BCEWithLogitsLoss()
    if outFile is not None:
        recordParams(outFile, epochs, batchSize, layer_sizes, loss_function, 
                    funct, learning_rate, randomState, testSize)
        
    #validate_model(frameX, model)   
        
    optimizer: Adam = Adam(model.parameters(), lr=learning_rate)
    
    scaler = StandardScaler();
    #X_train: x_train with mean 0 std_dev 1
    X_train = scaler.fit_transform(X_train);
    #X_test: x_test with mean 0 std_dev 1
    X_test = scaler.transform(X_test);
    
    #convert everything into tensors
    X_train = torch.FloatTensor(X_train)
    y_train = torch.FloatTensor(y_train.values)
    X_test = torch.FloatTensor(X_test)
    y_test = torch.FloatTensor(y_test.values)
    
    #create data sets
    train_data = LearnSet(X_train, y_train);
    test_data = TestSet(torch.FloatTensor(X_test));
    
    #create data loaders
    train_loader = DataLoader(dataset=train_data, batch_size=batchSize, shuffle=True);
    test_loader = DataLoader(dataset=test_data);
    
    #Run the prediction loop
    for e in range(epochs):
        runEpoch(train_loader, model, optimizer, loss_function, outFile, ep=e)
        
    y_pred_list = []
    model.eval()
    with torch.no_grad():
        for X_batch in test_loader:
            X_batch = X_batch.to(device)
            y_test_pred = model(X_batch)
            y_test_pred = torch.sigmoid(y_test_pred)
            y_pred_tag = torch.round(y_test_pred)
            y_pred_list.append(y_pred_tag.cpu().numpy())
    
    y_pred_list = [a.squeeze().tolist() for a in y_pred_list]

    #TODO: do these make sense?
    log(outFile, f'Test: MAE: {metrics.mean_absolute_error(y_test, y_pred_list):.4f}')
    log(outFile, f'Test: MSE: {metrics.mean_squared_error(y_test, y_pred_list):.4f}')
    with torch.no_grad():
        f1 = metrics.f1_score(y_test, y_pred_list)
        log(outFile, f'Test: f1: {f1:.4f}, target: 1')
        try:
            auc = metrics.roc_auc_score(y_test, y_pred_list)
        except ValueError:
            auc = float('NaN')
        log(outFile, f'Test: AUC: {auc:.4f}')
    
    #print(classification_report(y_test, y_pred_list))
    log(outFile, "Neural Network Complete.\n")

def runEpoch(train_loader, model, optimizer, loss_function, outfile, device="cpu", ep=-1):
    #total loss for the epoch
    epoch_loss = 0;
    #total accuracy for the epoch
    epoch_f1_m = 0;
    #epoch_f1_e = 0;
    
    #all_preds = []
    #all_true = []
    #iterate through train_loader, one batch at a time
    for X_batch, y_batch in train_loader:
        X_batch, y_batch = X_batch.to(device), y_batch.to(device)
        
        optimizer.zero_grad()
        
        y_logits = model(X_batch);
        y_pred = y_logits.squeeze()
        #y_pred = torch.softmax(y_logits, dim=1).argmax(dim=1).type(torch.FloatTensor)
        
        loss: torch.Tensor = loss_function(y_pred, y_batch)
        
        f1_torchmetrics: torch.Tensor = F1Score()(y_pred, y_batch)

        #f1_torcheval: torch.Tensor = binary_f1_score(y_pred, y_batch)
        
        #Computes the derivatives
        loss.backward()
        #Adjusts the weights
        optimizer.step()
        
        #Gets the data for the last batch of loss and acc as numbers
        epoch_loss += loss.item()
        epoch_f1_m += f1_torchmetrics.item()
        #epoch_f1_e += f1_torcheval.item()
        
        #all_true, all_preds = view.extractAndAppend(all_true, all_preds, y_batch_usq, y_pred)
    
    f1 = -1
    auc = 0
    with torch.no_grad():
        #training auc is unusual to measure, but it technically works, or so I'm told…
        try:
            auc = metrics.roc_auc_score(y_batch, y_pred)
        except ValueError:
            auc = float('NaN')
        #sys.stderr.write("X")
        try:
            #print(y_pred)
            #neural.containsNonBinary(y_pred)
            #y_pred includes values like "[-1.4554]"
            f1 = metrics.f1_score(y_batch, y_pred, zero_division=0)
        except ValueError as ve:
            sys.stderr.write("> "+str(ve)+"\n")
            f1 = float('NaN')
        
    loss_val = epoch_loss/len(train_loader)
    acc_m = epoch_f1_m/len(train_loader)
    #acc_e = epoch_f1_e/len(train_loader)
    
    log(outfile, f'Epoch {ep+0:03}: | Loss: {loss_val:.5f} | AccM: {acc_m:.3f} | AccE: {acc_e:.3f} | AUC: {auc:.3f} | f1: {f1:.3f}')
