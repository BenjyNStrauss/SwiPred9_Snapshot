from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler

from dataset import LocalToolBase as tb
#from sklearn.model_selection import train_test_split
import pandas as pd
#from sklearn.linear_model import LogisticRegression
#from sklearn.metrics import accuracy_score
#import matplotlib.pyplot as plt
#import warnings
#warnings.filterwarnings('ignore')

'''
Created on May 10, 2023

@author: Benjamin Strauss
'''

def applyPCA(X: pd.DataFrame, y: pd.DataFrame = None, pred_col: str = None, vectors=4,
             logFile = None, verbose: bool = False):
    X_Scale = None
    
    if y is not None:
        if pred_col is not None:
            X_Scale = StandardScaler().fit_transform(X.drop([pred_col], inplace = False), y)
        else:
            X_Scale = StandardScaler().fit_transform(X, y)
    else:
        if pred_col is not None:
            X_Scale = StandardScaler().fit_transform(X.drop([pred_col], inplace = False))
        else:
            X_Scale = StandardScaler().fit_transform(X)
    
    pca_obj = PCA(n_components=vectors)
    pcs = pca_obj.fit_transform(X_Scale)
    columns = []
    for ii in range(vectors):
        columns.append("pc"+str(ii))
    
    principalDf = pd.DataFrame(data = pcs, columns = columns)
    
    if pred_col is not None:
        principalDf[pred_col] = X[pred_col]
    
    if logFile is not None:
        tb.log(logFile, "> PCA applied with "+str(vectors)+" vectors")
    elif verbose:
        print("> PCA applied with "+str(vectors)+" vectors")
    
    return principalDf

