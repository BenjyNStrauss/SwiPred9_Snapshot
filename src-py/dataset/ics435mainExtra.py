import pandas as pd

from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression

'''
Created on Apr 9, 2023

@author: Benjamin Strauss
'''

TEST_SIZE = 0.20
R_STATE = 42

#Does the logistic regression
def logReg(frameX: pd.DataFrame, frameY: pd.DataFrame):
    predict = frameY['isSwitch']
    print(predict)
    
    X_train, X_test, y_train, y_test = train_test_split(frameX, predict, test_size=TEST_SIZE, random_state=R_STATE)
    print("Split data into train and test")
    
    #print(X_train.head())
    print(y_train)
    
    model = LogisticRegression(random_state=0).fit(X_train, y_train)
    
    y_pred = model.predict(X_test)
    
    
    print("Created Logistic Regression Model")
    
    print("Logistic Regression Complete.\n")
