import pandas as pd
import time
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression, LogisticRegression
from sklearn.preprocessing import PolynomialFeatures
import sklearn.metrics as metrics
from sklearn import neighbors as nh, tree

from dataset import LocalToolBase as tb
from regression.swipred_frame import SwiPredFrame
import statsmodels.api as sm

'''
Created on May 28, 2023

@author: Benjamin Strauss
'''

DEFAULT_TEST_SIZE = 0.2
DEFAULT_RANDOM_STATE = 42

def polyReg(frameX: SwiPredFrame, frameY: SwiPredFrame, outFile=None, start=1, stop=4,
            test_size = DEFAULT_TEST_SIZE, rand_state = DEFAULT_RANDOM_STATE):
    '''Does a polynomial regression'''
    
    retval = {}
    
    X_train, X_test, y_train, y_test = train_test_split(frameX, frameY, test_size=test_size, random_state=rand_state)
    if outFile is not None:
        tb.log(outFile, "Starting Polynomial Regression:\n")
    
    for _degree_ in range(start,stop):
        #print(_degree_)
        if outFile is not None:
            tb.log(outFile, "PolyReg: Degree: "+str(_degree_))
        start = time.time()
        
        results = {}
        
        transformer = PolynomialFeatures(degree=_degree_, include_bias=False)
        
        X_train_t = transformer.fit_transform(X_train)
        X_test_t = transformer.fit_transform(X_test)
        #y_train_t = transformer.fit_transform(y_train)
        model = LinearRegression()
        model.fit(X_train_t, y_train)
        
        results["features"] = str(X_train_t.shape[1])
        if outFile is not None:
            tb.log(outFile, "Features:        "+str(X_train_t.shape[1]))
        
        y_pred = model.predict(X_test_t)
        
        results["r2"] = model.score(X_test_t, y_test)
        results["MAE"] = metrics.mean_absolute_error(y_test, y_pred)
        results["MSE"] = metrics.mean_squared_error(y_test, y_pred)
        
        end = time.time()
        results["time"] = end - start
        
        #This is in beta
        pFinder = sm.OLS(y_pred, X_test_t).fit()
        results["p"] = pFinder.summary2().tables[1]['P>|t|']
        
        if outFile is not None:
            tb.log(outFile, "PolyReg: MAE:    "+ str(results["MAE"]))
            tb.log(outFile, "PolyReg: MSE:    "+ str(results["MSE"]))
            tb.log(outFile, "PolyReg: R^2:    "+ str(results["r2"]))
            tb.log(outFile, "Time Elapsed:    "+ str(results["time"]) + "\n")
    
        retval[_degree_] = results
        
    if outFile is not None:
        tb.log(outFile, "Polynomial Regression Complete.\n")

    return retval

def logReg(frameX: pd.DataFrame, frameY: pd.DataFrame, threshhold=0, outFile=None,
            trials=4, test_size = DEFAULT_TEST_SIZE, rand_state = DEFAULT_RANDOM_STATE):
    '''Does a logistic regression -- ? incomplete ?'''
    
    if threshhold >= 0 and threshhold < 1:
        for index in range(len(frameY)):
            if frameY.iloc[index] <= threshhold:
                frameY.iloc[index] = 0
            else:
                frameY.iloc[index] = 1 
    
    X_train, X_test, y_train, y_test = train_test_split(frameX, frameY, test_size=test_size, random_state=rand_state)
    
    retval = {}
    
    for trial in range(trials):
        results = {}
        model = LogisticRegression()
        model.fit(X_train, y_train)
            
        y_pred = model.predict(X_test)
        
        results["r2"] = model.score(X_test, y_test)
        results["MAE"] = metrics.mean_absolute_error(y_test, y_pred)
        results["MSE"] = metrics.mean_squared_error(y_test, y_pred)
        
        #This is in beta
        pFinder = sm.OLS(y_pred, X_test).fit()
        results["p"] = pFinder.summary2().tables[1]['P>|t|']
        
        results[trial] = results
        
        if outFile is not None:
            tb.log(outFile, "LogReg Reg #"+ str(trial))
            tb.log(outFile, "LogReg Reg: MAE:       "+ str(results["MAE"]))
            tb.log(outFile, "LogReg Reg: MSE:       "+ str(results["MSE"]))
            tb.log(outFile, "LogReg Reg: Score:     "+ str(results["r2"])+"\n")
            
    return retval

def knnReg(frameX: SwiPredFrame, frameY: SwiPredFrame, outFile=None, start=2, stop=16,
           test_size = DEFAULT_TEST_SIZE, rand_state = DEFAULT_RANDOM_STATE):
    '''Does a K-nearest neighbors regression'''
    X_train, X_test, y_train, y_test = train_test_split(frameX, frameY, test_size=test_size, random_state=rand_state)
    
    retval = {}
    
    for neighbors in range(start, stop):
        results = {}
        model = nh.KNeighborsRegressor(n_neighbors = neighbors)
        model.fit(X_train, y_train)
            
        y_pred = model.predict(X_test)
        
        results["r2"] = model.score(X_test, y_test)
        results["MAE"] = metrics.mean_absolute_error(y_test, y_pred)
        results["MSE"] = metrics.mean_squared_error(y_test, y_pred)
        
        #This is in beta
        pFinder = sm.OLS(y_pred, X_test).fit()
        results["p"] = pFinder.summary2().tables[1]['P>|t|']
        
        if outFile is not None:
            tb.log(outFile, "KNN Reg: Neighbors: "+ str(neighbors))
            tb.log(outFile, "KNN Reg: MAE:       "+ str(results["MAE"]))
            tb.log(outFile, "KNN Reg: MSE:       "+ str(results["MSE"]))
            tb.log(outFile, "KNN Reg: Score:     "+ str(results["r2"])+"\n")
        
        retval[neighbors] = results
        
    return retval

def decisionReg(frameX: pd.DataFrame, frameY: pd.DataFrame, outFile=None, trials=16,
                test_size = DEFAULT_TEST_SIZE, rand_state = DEFAULT_RANDOM_STATE):
    '''Does a decision tree regression'''
    X_train, X_test, y_train, y_test = train_test_split(frameX, frameY, test_size=test_size, random_state=rand_state)
    
    retval = {}
    
    for trial in range(trials):
        results = {}
        model = tree.DecisionTreeRegressor()
        model.fit(X_train, y_train)
            
        y_pred = model.predict(X_test)
        
        results["r2"] = model.score(X_test, y_test)
        results["MAE"] = metrics.mean_absolute_error(y_test, y_pred)
        results["MSE"] = metrics.mean_squared_error(y_test, y_pred)
        
        #This is in beta
        pFinder = sm.OLS(y_pred, X_test).fit()
        results["p"] = pFinder.summary2().tables[1]['P>|t|']
        
        if outFile is not None:
            tb.log(outFile, "DTree Reg #"+ str(trial))
            tb.log(outFile, "DTree Reg: MAE:       "+ str(results["MAE"]))
            tb.log(outFile, "DTree Reg: MSE:       "+ str(results["MSE"]))
            tb.log(outFile, "DTree Reg: Score:     "+ str(results["r2"])+"\n")
            
    return retval
