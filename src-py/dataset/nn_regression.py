import pandas as pd
import os

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import sklearn.metrics as metrics

import dataset.LocalToolBase as tb
import dataset.neural1 as neural
from dataset.randomOptions import getRandomParams

import torch
import torch.nn as nn
from torch.utils.data import DataLoader
from torch.optim.adam import Adam

from dataset import nnTools

'''
Created on Apr 10, 2023

@author: Benjamin Strauss
'''

default_layer_sizes = [ 38, 272, 1024, 24, 512, 369, 4, 1 ]

def predictRandomized(frameX, frameY, runs=1, base_log_filename="log-", runstart = 0):
    abs_path = os.getcwd()
    
    for run in range(runstart, runs):
        
        EPOCHS, batch_size, r_state, layer_sizes, learningRate, test_size, function = getRandomParams(38,1)
        _filename_ = base_log_filename + str(run)
        outFile = open(f'{abs_path}/{_filename_}.txt', "a")
        tb.log(outFile, "Run #"+ str(run))
        
        predict(frameX, frameY, layer_sizes, outFile, learning_rate=learningRate,
                          funct=function, testSize=test_size, randomState=r_state,
                          batchSize=batch_size, epochs=EPOCHS)
        
        outFile.close()

#does the neural network
def predict(frameX: pd.DataFrame, frameY: pd.DataFrame, layer_sizes=default_layer_sizes,
            outFile=None, device = "cpu", funct=nn.Mish(), epochs=16,
            learning_rate=0.0001, batchSize=80, testSize = 0.2, randomState=42):
    tb.log(outFile, "Starting NN model (Note: Loss=MSE)")
    
    model = neural.SwiPredNN(layer_sizes, function=funct)
    model.to(device);
    predict = frameY['isSwitchVK'];
    X_train, X_test, y_train, y_test = train_test_split(frameX, predict, test_size=testSize, random_state=randomState)
    
    loss_function = nn.MSELoss()
    
    if outFile is not None:
        tb.recordParams(outFile, epochs, batchSize, layer_sizes, loss_function, 
                    funct, learning_rate, randomState, testSize)
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
    train_data = nnTools.LearnSet(X_train, y_train);
    test_data = nnTools.TestSet(torch.FloatTensor(X_test));
    
    #create data loaders
    train_loader = DataLoader(dataset=train_data, batch_size=batchSize, shuffle=True);
    test_loader = DataLoader(dataset=test_data);
    
    #print(len(frameY.columns))
    #print(test_data)
    
    #Run the prediction loop
    for e in range(epochs):
        runEpoch(train_loader, model, optimizer, loss_function, device, ep=e, outFile=outFile)
    
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
    mae = metrics.mean_absolute_error(y_test, y_pred_list)
    mse = metrics.mean_squared_error(y_test, y_pred_list)
    tb.log(outFile, f'@Test Set: | MSE: {mse:.5f} | MAE: {mae:.5f}')
    
    tb.log(outFile, "Neural Network Complete.\n")
    
def runEpoch(train_loader, model, optimizer, loss_function, device="cpu", ep="?",
             outFile = None):
    #initialize epoch loss, accuracy to zero
    #total loss for the epoch
    epoch_loss = 0;
    
    mae = []
    mae_val = 0
    #iterate through train_loader, one batch at a time
    for X_batch, y_batch in train_loader:
        X_batch, y_batch = X_batch.to(device), y_batch.to(device)
        
        optimizer.zero_grad()
        y_pred = model(X_batch);
        
        loss: torch.Tensor = loss_function(y_pred, y_batch.unsqueeze(1))
        #This will be trouble later...
        
        y_batch2 = y_batch.unsqueeze(1)
        
        batch_loss = loss.item()
        
        #Computes the derivatives
        loss.backward()
        #Adjusts the weights?
        optimizer.step()
        
        #Gets the data for the last batch of loss and acc as numbers
        epoch_loss += batch_loss
        
        with torch.no_grad():
            mae_temp = metrics.mean_absolute_error(y_pred, y_batch2)
            mae.append(mae_temp)
            mae_val += mae_temp
        
    mae_val /= len(mae)
    
    #Loss is the SAME as the MSE!
    tb.log(outFile, f'Epoch {ep+0:03}: | MSE: {epoch_loss/len(train_loader):.5f} | MAE: {mae_val:.5f}')

