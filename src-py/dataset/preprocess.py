import pandas as pd
import dataset.SeqEncode as se
import os
import random
from pandas.core.frame import DataFrame
from torch import FloatTensor

'''
Created on Mar 7, 2023

@author: Benjamin Strauss
'''

def loadSwipredFrame(path: str):
    #navigate back to proper program directory
    os.chdir("../..")
    #read in the table
    table = pd.read_csv(path)
    #drop unwanted misc columns
    table = table.loc[:, ~table.columns.str.contains('^Unnamed')]
    
    return table

def dropIDs(table: DataFrame):
    table = table.drop("Protein", axis=1)
    table = table.drop("Chain", axis=1)
    table = table.drop("No.", axis=1)
    return table

def removeEntropyNANs(table: DataFrame):
    #Drop entries for which entropy could not be computed
    for index in range(len(table)):
        if table.loc[index]["E6"] != table.loc[index]["E6"]:
            table.drop(index, axis=0, inplace = True)
    
    #print("Len: " + str(len(table)))
    table.reset_index(inplace = True)
    #print("Len: " + str(len(table)))
    print("> Removed Lines where Entropy = 'NaN'")
    
    return table

def removeYvalNANs(table: DataFrame, isSwitchCol: str):
    #print(isSwitchCol)
    
    #Drop entries for which entropy could not be computed
    for index in range(len(table)):
        if table.loc[index][isSwitchCol] != table.loc[index][isSwitchCol]:
            table.drop(index, axis=0, inplace = True)
    
    #print("Len: " + str(len(table)))
    table.reset_index(inplace = True)
    
    __purify__(table)
    
    #print("Len: " + str(len(table)))
    print(f"> Removed Lines where {isSwitchCol} = 'NaN'")
    
    return table

def changeVKColTo1Hot(table: DataFrame, column_name: str):
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
    
def changeResColTo1Hot(table: DataFrame, column_name: str):
    col = table[column_name]
    #print(type(col))
    
    resTable = []
    
    for index in range(0, len(col)):
        encoding = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
        amino_index = se.aminoEncodingDict[col[index]]-1
        encoding[amino_index] = 1
        resTable.append(encoding)
    
    table = table.drop(column_name, axis=1)
    
    for index in range(0, 23):
        _list = []
        res_type = [key for key, value in se.aminoEncodingDict.items() if value == index+1]
        for index2 in range(0, len(resTable)):
            _list.append(resTable[index2][index])
        
        series = pd.Series(_list, dtype="int32")
        table["res_" + res_type[0]] = series
    
    return table

def normalizeVK(table: DataFrame, column_name: str, sec_str_types = 3):
    max_val: int = sec_str_types ** 2 - 1
    
    table._is_copy = False;
    normalized = []
    for index in range(0, len(table[column_name])):
        val = (table[column_name][index] - 1) / max_val
        normalized.append(val)
    
    table["norm_" + column_name] = normalized
    
    return table

def makeIsSwitch(table: DataFrame, H_col: str = 'NUM_H', S_col: str = 'NUM_S',
                O_col: str = 'NUM_O', U_col: str = 'NUM_U', threshold = 0.0,
                isSwitchCol: str = 'isSwitch', realVKCol: str = 'isSwitchVK', 
                addVK = False, addBinSwitch=True, include_unassigned=True):
    
    vkbats = []
    isSwitchBin = []
    
    if realVKCol not in table.columns:
        #print(">> realVKCol not in table.columns")
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
            if include_unassigned:
                if u > 0:
                    k += 1
            
            #N = total examples
            N = 0
            n1 = 0
            if include_unassigned:
                N = h+s+o+u
                n1 = max(h,s,o,u)
            else:
                N = h+s+o
                n1 = max(h,s,o)
            #n1 most common example [1-N]
            
            if not include_unassigned and N <= 1:
                vkbats.append(float('nan'))
            elif n1 == 0:
                vkbats.append(float('nan'))
            else:
                vkbat = k * N / n1
                vkbats.append(vkbat)
                    
        if addVK:
            #add vkbat data
            tempSeries = pd.Series(vkbats)
            tempSeries.name = realVKCol
            table[realVKCol] = tempSeries
            
            if not include_unassigned:
                removeYvalNANs(table, realVKCol)
    

    #by this point, we are guaranteed to have vkbats
    if addBinSwitch:
        #generate the binary switch data
        if len(vkbats) > 0:
            for vk_val in vkbats:
                if __normalizeVK__(vk_val, include_unassigned) > threshold:
                    isSwitchBin.append(1)
                else:
                    isSwitchBin.append(0)
        else:
            for vk_val in table[realVKCol]:
                if __normalizeVK__(vk_val, include_unassigned) > threshold:
                    isSwitchBin.append(1)
                else:
                    isSwitchBin.append(0)
        
        tempSeries2 = pd.Series(isSwitchBin)
        tempSeries2.name = isSwitchCol
        table[isSwitchCol] = tempSeries2
        
    #clean out NaNs
    if not include_unassigned:
        if addVK:
            removeYvalNANs(table, realVKCol)
        if addBinSwitch:
            removeYvalNANs(table, isSwitchCol)
    
    return table

def __normalizeVK__(vkbat, include_unassigned):
    vk_norm = vkbat - 1
    if include_unassigned:
        vk_norm /= 15
    else:
        vk_norm /=8
    return vk_norm

def mnistify(X: DataFrame, y: pd.Series):
    train_test_tuples = []
    for index in range(len(X)):
        tensor = FloatTensor(X.iloc[index])
        tt_tuple = (tensor, y.iloc[index])
        train_test_tuples.append(tt_tuple)
    #print(train_test_tuples)
    return train_test_tuples;
    
def __purify__(table: DataFrame):
    if 'index' in table.columns:
        table.drop(['index'], axis=1, inplace = True)
    if 'level_0' in table.columns:
        table.drop(['level_0'], axis=1, inplace = True)
        
def shrink_balance_dataset(X: DataFrame, y: DataFrame, balanceCol: str = "isSwitch"):
    zeros = 0
    ones = 0
    
    zeros_list = []
    ones_list = []
    
    for index in range(len(y)):
        if y.iloc[index][balanceCol] == 0:
            zeros += 1
            zeros_list.append((X.iloc[index], y.iloc[index]))
        elif y.iloc[index][balanceCol] == 1:
            ones += 1
            ones_list.append((X.iloc[index], y.iloc[index]))
    
    halfSize = min(zeros, ones)
    
    while len(zeros_list) > halfSize:
        randIndex = random.randint(0, len(zeros_list)-1)
        #print(f"randIndex: {randIndex}")
        zeros_list.pop(randIndex)
        
        #print(f"len(zeros_list): {len(zeros_list)}")
    
    while len(ones_list) > halfSize:
        randIndex = random.randint(0, len(ones_list)-1)
        ones_list.pop(randIndex)
    
    #recombine the lists
    xs = []
    ys = []
    for index in range(0, halfSize):
        xs.append(zeros_list[index][0])
        xs.append(ones_list[index][0])
        ys.append(zeros_list[index][1])
        ys.append(ones_list[index][1])
    
    new_X = DataFrame(xs, columns=X.columns)
    new_X.reset_index(inplace = True)
    
    new_y = DataFrame(ys, columns=y.columns)
    new_y.reset_index(inplace = True)
    
    return new_X, new_y