import pandas as pd
import os

from sklearn.preprocessing import PolynomialFeatures
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
import sklearn.metrics as metrics

#from sklearn import neighbors as nh, tree
import time

'''
Created on Apr 24, 2023

@author: Benjamin Strauss
'''

debug = True
TEST_SIZE = 0.20
R_STATE = 42

acetyl_sirt = "output/s8-acetyl+sirt-output.csv"

aminoEncodingDict = {
    "A" : 1,
    "C" : 2,
    "D" : 3,
    "E" : 4,
    "F" : 5,
    "G" : 6,
    "H" : 7,
    "I" : 8,
    "K" : 9,
    "L" : 10,
    "M" : 11,
    "N" : 12,
    "P" : 13,
    "Q" : 14,
    "R" : 15,
    "S" : 16,
    "T" : 17,
    "V" : 18,
    "W" : 19,
    "Y" : 20,
    "O" : 21,
    "U" : 22,
    "X" : 23
}

def log(outFile=None, text: str = ""):
    print(f'{text}')
    if outFile is not None:
        outFile.write(f'{text}\n')

def polyReg(frameX: pd.DataFrame, frameY: pd.DataFrame, start=1, stop=8):
    
    predict = frameY['isSwitchVK'];
    X_train, X_test, y_train, y_test = train_test_split(frameX, predict, test_size=TEST_SIZE, random_state=R_STATE)
    log(out, "Split data into train and test for Polynomial Regression:\n")
    
    for _degree_ in range(start,stop):
        log(out, "PolyReg: Degree: "+str(_degree_))
        start = time.time()
        transformer = PolynomialFeatures(degree=_degree_, include_bias=False)
        
        X_train_t = transformer.fit_transform(X_train)
        X_test_t = transformer.fit_transform(X_test)
        #y_train_t = transformer.fit_transform(y_train)
        if debug:
            print("flag 0")
        model = LinearRegression().fit(X_train_t, y_train)
        if debug:
            print("flag 1")
        
        log(out, "Features:        "+str(X_train_t.shape[1]))
        
        r_sq = model.score(X_train_t, y_train)
        
        y_pred = model.predict(X_test_t)
        
        log(out, "PolyReg: MAE:    "+ str(metrics.mean_absolute_error(y_test, y_pred)))
        log(out, "PolyReg: MSE:    "+ str(metrics.mean_squared_error(y_test, y_pred)))
        log(out, "PolyReg: Score:  "+ str(r_sq))
        end = time.time()
        log(out, "Time Elapsed:    "+ str(end - start) + "\n")
        
    log(out, "Polynomial Regression Complete.\n")

def loadSwipredFrame(path: str):
    #navigate back to proper program directory
    #os.chdir("../..")
    #read in the table
    table = pd.read_csv(path)
    #drop unwanted misc columns
    table = table.loc[:, ~table.columns.str.contains('^Unnamed')]
    return table

def dropIDs(table: pd.DataFrame):
    table = table.drop("Protein", axis=1)
    table = table.drop("Chain", axis=1)
    table = table.drop("No.", axis=1)
    return table

def removeEntropyNANs(table: pd.DataFrame):
    #Drop entries for which entropy could not be computed
    for index in range(len(table)):
        if table.loc[index]["E6"] != table.loc[index]["E6"]:
            table.drop(index, axis=0, inplace = True)
    
    #print("Len: " + str(len(table)))
    table.reset_index(inplace = True)
    #print("Len: " + str(len(table)))
    print("Removed Lines where Entropy = 'NaN'")
    
    return table

def changeVKColTo1Hot(table: pd.DataFrame, column_name: str):
    col = table[column_name]
    h_col = []
    s_col = []
    o_col = []
    
    #print("**")
    #print(len(col))
    
    for index in range(0, len(col)):
        #print(type(col[index]))
        if isinstance(col[index], float):
            h_col.append(0)
            s_col.append(0)
            o_col.append(0)
        elif col[index].lower() == "helix" or col[index].lower() == "h":
            h_col.append(1)
            s_col.append(0)
            o_col.append(0)
        elif col[index].lower() == "sheet" or col[index].lower() == "s" or col[index].lower() == "e":
            h_col.append(0)
            s_col.append(1)
            o_col.append(0)
        elif col[index].lower() == "other" or col[index].lower() == "o" or col[index].lower() == "c":
            h_col.append(0)
            s_col.append(0)
            o_col.append(1)
        else:
            h_col.append(0)
            s_col.append(0)
            o_col.append(0)
        
    table.drop(column_name, axis=1)
    #print(h_col)
    table[column_name +"_h"] = h_col
    table[column_name +"_s"] = s_col
    table[column_name +"_o"] = o_col
    
    return table

def changeResColTo1Hot(table: pd.DataFrame, column_name: str):
    col = table[column_name]
    #print(type(col))
    
    resTable = []
    
    for index in range(0, len(col)):
        encoding = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
        amino_index = aminoEncodingDict[col[index]]-1
        encoding[amino_index] = 1
        resTable.append(encoding)
    
    table = table.drop(column_name, axis=1)
    
    for index in range(0, 23):
        _list = []
        res_type = [key for key, value in aminoEncodingDict.items() if value == index+1]
        for index2 in range(0, len(resTable)):
            _list.append(resTable[index2][index])
        
        series = pd.Series(_list, dtype="int32")
        table["res_" + res_type[0]] = series
    
    return table

def makeIsSwitch(table: pd.DataFrame, H_col: str = 'NUM_H', S_col: str = 'NUM_S',
                O_col: str = 'NUM_O', U_col: str = 'NUM_U', threshold = 0.0,
                realVKCol: str = 'isSwitchVK', isSwitchCol: str = 'isSwitch',
                addVK = False, addBinSwitch=True):
    
    vkbats = []
    isSwitchBin = []
    for index in range(0, len(table)):
        #print(index)
        #print(table.loc[index])
        h = table.loc[index][H_col]
        s = table.loc[index][S_col]
        o = table.loc[index][O_col]
        u = table.loc[index][U_col]
        
        #k = number of types of structures present [0-4]
        k = 0
        if h > 0:
            k += 1
        if s > 0:
            k += 1
        if o > 0:
            k += 1
        if u > 0:
            k += 1
        
        #N = total examples
        N = h+s+o+u
        
        #n1 most common example [1-N]
        n1 = max(h,s,o,u)
        
        if n1 == 0:
            vkbats.append(float('nan'))
            isSwitchBin.append(float('nan'))
        else:
            vkbat = k * N / n1
            vkbats.append(vkbat)
            
            #normalize the vkbat
            vkbat -= 1
            vkbat /= 15
            if vkbat > threshold:
                isSwitchBin.append(1)
            else:
                isSwitchBin.append(0)
    
    if addVK:
        tempSeries = pd.Series(vkbats)
        tempSeries.name = realVKCol
        table = pd.concat([table, tempSeries.to_frame()], axis=1)
    if addBinSwitch:
        tempSeries2 = pd.Series(isSwitchBin)
        tempSeries2.name = isSwitchCol
        table[isSwitchCol] = tempSeries2
    return table

def getFrame(dataset_path: str=acetyl_sirt, bool_threshold = 0.0):
    #Load the data
    frame = loadSwipredFrame(dataset_path)
    print("Read Swipred csv");
    
    #Drop the ids
    frame = dropIDs(frame)
    
    frame = changeResColTo1Hot(frame, "Res")
    frame = frame.drop(['gor1', 'gor3', 'dpm', 'predator_pr',
       'sspro_2', 'psipred', 'jnet', 'phd', 'profsec', 'dsc', 'hnn', 'mlrc',
       'sopm', 'jpred', 'yaspin', 'chou_fasman', 'sspro_5', 'gor4',
       'dsc_l(dsc)', 'jnet_l(jnet)', 'psipred_l(psipred)'], axis=1)
    
    frame = removeEntropyNANs(frame)
    
    frameX = frame.drop(['NUM_H', 'NUM_S', 'NUM_O', 'NUM_U'], axis=1)
    frameY = frame[['NUM_H', 'NUM_S', 'NUM_O', 'NUM_U']]
    
    frameY = makeIsSwitch(frameY, addVK = True, threshold=bool_threshold)
    
    print("Loaded Frame")
    return (frameX, frameY)

#os.chdir("../..")
print(os.getcwd())

frameX, frameY = getFrame()

os.chdir("uhm-logs/")
out = open("ics435-reg-log.txt", "a")

polyReg(frameX, frameY, 5, 6)

