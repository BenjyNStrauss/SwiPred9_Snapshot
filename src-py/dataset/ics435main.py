import pandas as pd
import os

from sklearn.preprocessing import PolynomialFeatures
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
import sklearn.metrics as metrics

from sklearn import neighbors as nh, tree
from dataset import loader, nn_regression, LocalToolBase as tb
#from torcheval.metrics.functional import r2_score

import time
'''
Created on Mar 22, 2023

The goal of this project is to predict protein secondary structure variability
from a set of proteins used in the transferring of acetyl groups
("acetyltransferaces" and "sirtuins"/"de-acetylaces")

Methods used are:
    (1) Logistic Regression
    (2) Polynomial Regression
    (3) Neural Network

Issues:
1.1: What to replace Logistic Regression with?
    1.1.1 How does one do Ordinal regression?
3.1: What does negative loss mean

@author: Benjamin Strauss
'''

EPOCHS = 32;
BATCH_SIZE = 64;
LEARNING_RATE = 0.001;

TEST_SIZE = 0.20
R_STATE = 42
DEVICE: str = "cpu"

debug = False

#does the polynomial regression
def polyReg(frameX: pd.DataFrame, frameY: pd.DataFrame, start=1, stop=4, trials=1):
    
    predict = frameY['isSwitchVK'];
    X_train, X_test, y_train, y_test = train_test_split(frameX, predict, test_size=TEST_SIZE, random_state=R_STATE)
    tb.log(out, "Split data into train and test for Polynomial Regression:\n")
    
    for _degree_ in range(start,stop):
        for trial in range(trials):
            tb.log(out, "PolyReg: Degree: "+str(_degree_) + " - Trial #"+ str(trial+1))
            start = time.time()
            transformer = PolynomialFeatures(degree=_degree_, include_bias=False)
            
            X_train_t = transformer.fit_transform(X_train)
            X_test_t = transformer.fit_transform(X_test)
            #y_train_t = transformer.fit_transform(y_train)
            if debug:
                print("flag 0")
                print(X_train_t.shape)
            model = LinearRegression()
            if debug:
                print("flag 1")
            model.fit(X_train_t, y_train)
            if debug:
                print("flag 2")
            
            tb.log(out, "Features:        "+str(X_train_t.shape[1]))
            
            y_pred = model.predict(X_test_t)
            r_sq = model.score(X_test_t, y_test)
            
            tb.log(out, "PolyReg: MAE:    "+ str(metrics.mean_absolute_error(y_test, y_pred)))
            tb.log(out, "PolyReg: MSE:    "+ str(metrics.mean_squared_error(y_test, y_pred)))
            tb.log(out, "PolyReg: Score:  "+ str(r_sq))
            end = time.time()
            tb.log(out, "Time Elapsed:    "+ str(end - start) + "\n")
        
    tb.log(out, "Polynomial Regression Complete.\n")

#a K-nearest neighbors regression
def knnReg(frameX: pd.DataFrame, frameY: pd.DataFrame):
    predict = frameY['isSwitchVK'];
    X_train, X_test, y_train, y_test = train_test_split(frameX, predict, test_size=TEST_SIZE, random_state=R_STATE)
    
    tb.log(out, "Split data into train and test for K-nearest neighbors Regression:\n")
    for neighbors in range(2, 16):
        model = nh.KNeighborsRegressor(n_neighbors = neighbors)
        model.fit(X_train, y_train)
            
        y_pred = model.predict(X_test)
        #print(r2_score(y_pred, y_test))
        r_sq = model.score(X_test, y_test)
        
        tb.log(out, "KNN Reg: Neighbors: "+ str(neighbors))
        tb.log(out, "KNN Reg: MAE:       "+ str(metrics.mean_absolute_error(y_test, y_pred)))
        tb.log(out, "KNN Reg: MSE:       "+ str(metrics.mean_squared_error(y_test, y_pred)))
        tb.log(out, "KNN Reg: Score:     "+ str(r_sq))
        tb.log(out, "");
        
#a decision tree regression
def decisionReg(frameX: pd.DataFrame, frameY: pd.DataFrame, trials=16):
    predict = frameY['isSwitchVK'];
    X_train, X_test, y_train, y_test = train_test_split(frameX, predict, test_size=TEST_SIZE, random_state=R_STATE)
    
    tb.log(out, "Split data into train and test for Decision Tree Regression:\n")
    for trial in range(trials):
        model = tree.DecisionTreeRegressor()
        model.fit(X_train, y_train)
            
        y_pred = model.predict(X_test)
        r_sq = model.score(X_test, y_test)
        
        tb.log(out, "DTree Reg #"+ str(trial))
        tb.log(out, "DTree Reg: MAE:       "+ str(metrics.mean_absolute_error(y_test, y_pred)))
        tb.log(out, "DTree Reg: MSE:       "+ str(metrics.mean_squared_error(y_test, y_pred)))
        tb.log(out, "DTree Reg: Score:     "+ str(r_sq))
        tb.log(out, "");

frameX, frameY = loader.getFrame(include_unassigned=False)
os.chdir("uhm-logs/")
out = open("ics435-reg-log.txt", "a")

knnReg(frameX, frameY)
polyReg(frameX, frameY, 1, 4)
decisionReg(frameX, frameY)
#nn_regression.predictRandomized(frameX, frameY, 24, base_log_filename="log435-", runstart=0)

tb.log(out, "Process completed.")



