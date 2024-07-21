import warnings
warnings.simplefilter(action='ignore', category=UserWarning)

import pandas as pd
from pandas.core.frame import DataFrame

import regression.SeqEncode as se
from util.unsupported_error import NotYetSupportedError

'''
Created on May 28, 2023

@author: Benjamin Strauss
'''

__vkpred_col_names__ = ['gor1', 'gor3', 'dpm', 'predator_pr',
       'sspro_2', 'psipred', 'jnet', 'phd', 'profsec', 'dsc', 'hnn', 'mlrc',
       'sopm', 'jpred', 'yaspin', 'chou_fasman', 'sspro_5', 'gor4',
       'dsc_l(dsc)', 'jnet_l(jnet)', 'psipred_l(psipred)']

class SwiPredFrame(DataFrame):
    data_file: str
    assist_label: str
    
    def __init__(self, data_path: str, label: str = "unlabeled", dropIDs = False):
        temp = pd.read_csv(data_path)
            
        self._mgr = temp._mgr
        self._flags = temp._flags
        self._attrs = {}
        self._item_cache = {}
        
        self = temp[temp.columns]
        self.assist_label = label
        self.data_file = data_path
        
        if dropIDs:
            self.dropIDs()

    def dropIDs(self, protID_colName = "Protein", chainID_colName = "Chain",
                resNo_colName = "No."):
        ''' Drops ID columns – columns that provide no value in predicting
        switch-like regions
        '''
        if protID_colName in self.columns:
            self.drop(protID_colName, axis=1, inplace=True)
        if chainID_colName in self.columns:
            self.drop(chainID_colName, axis=1, inplace=True)
        if resNo_colName in self.columns:
            self.drop(resNo_colName, axis=1, inplace=True)
        
    def encodeRes1Hot(self, column_name: str = "Res", encodingDict = se.aminoEncodingDict):
        col = self[column_name]
        
        resTable = []
        
        for index in range(0, len(col)):
            encoding = [0] * len(encodingDict)
            amino_index = encodingDict[col[index]]-1
            encoding[amino_index] = 1
            resTable.append(encoding)
        
        self.drop(column_name, axis=1, inplace=True)
        
        for index in range(0, len(encodingDict)):
            _list = []
            res_type = [key for key, value in encodingDict.items() if value == index+1]
            for index2 in range(0, len(resTable)):
                _list.append(resTable[index2][index])
            
            series = pd.Series(_list, dtype="int32")
            self["res_" + res_type[0]] = series
        
    def removeNANs(self, colNames = ["E6"]):
        #why does this take so long??
        '''Drop entries for which a value could not be computed'''
        for col_name in colNames:
            for index in range(len(self)):
                if self.loc[index][col_name] != self.loc[index][col_name]:
                    self.drop(index, axis=0, inplace = True)
        
        self.reset_index(inplace = True)
        self._purify_()
        
    def normalizeVK(self, column_name: str = "Vkbat", sec_str_types = 3):
        '''Create a normalized Vkbat column'''
        max_val: int = sec_str_types ** 2 - 1
        
        normalized = []
        for index in range(0, len(self[column_name])):
            val = (self[column_name][index] - 1) / max_val
            normalized.append(val)
        
        self["norm_" + column_name] = normalized
        
    def encodeVKPred1Hot(self, column_name: str, pred_type = 3):
        if pred_type == 3:
            '''encodes a column of vkbat predictions as a 1-hot encoding'''
            col = self[column_name]
            h_col = []
            s_col = []
            o_col = []
            
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
                
            self.drop(column_name, axis=1, inplace = True)
            self[column_name +"_h"] = h_col
            self[column_name +"_s"] = s_col
            self[column_name +"_o"] = o_col
        else:
            raise NotYetSupportedError("1-hot encoding for "+str(pred_type)+"-class predictions are not yet supported")

    def dropRawVKPredCols(self, column_names = __vkpred_col_names__):
        for col in column_names:
            if col in self.columns:
                self.drop(col, axis=1, inplace = True)

    def makeSwitchiness(self, H_col: str = 'NUM_H', S_col: str = 'NUM_S',
            O_col: str = 'NUM_O', U_col: str = 'NUM_U', include_unassigned=True,
            newCol: str = "switchiness", drop_nan = True, normalize = True):
    
        vkbats = []
        max_val_for_norm = 15 if include_unassigned else 8
        
        #print(">> realVKCol not in table.columns")
        for index in range(0, len(self)):
            #print(index)
            #print(table.loc[index])
            h = self.loc[index][H_col]
            s = self.loc[index][S_col]
            o = self.loc[index][O_col]
            u = self.loc[index][U_col]
            
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
                if normalize:  
                    vkbat = (vkbat - 1)/max_val_for_norm
                vkbats.append(vkbat)
                
        #add vkbat data
        tempSeries = pd.Series(vkbats)
        tempSeries.name = newCol
        self[newCol] = tempSeries
            
        if not include_unassigned and drop_nan:
            self.removeNANs(newCol)
    
    def _purify_(self):
        if 'index' in self.columns:
            self.drop(['index'], axis=1, inplace = True)
        if 'level_0' in self.columns:
            self.drop(['level_0'], axis=1, inplace = True)
    
    def classifyIsSwitchOnThreshold(self, threshold: float, switchiness_col: str, newColName = None):
        #generate the binary switch data
        isSwitchBin = []
        if newColName is None: 
            newColName = switchiness_col+"@"+str(threshold)
        
        for vk_val in self[switchiness_col]:
                if vk_val > threshold:
                    isSwitchBin.append(1)
                else:
                    isSwitchBin.append(0)
            
        tempSeries = pd.Series(isSwitchBin)
        self[newColName] = tempSeries
    
    def dropCol(self, colName):
        if colName in self.columns:
            self.drop(colName, axis=1, inplace = True)
    
    def dropColNo(self, colNumber):
        self.drop(self.columns[colNumber], axis=1, inplace = True)
        
    def dropColsNo(self, colNumbers):
        offset = 0
        for no in colNumbers:
            self.drop(self.columns[no-offset], axis=1, inplace = True)
            offset += 1
    
    '''def splitForRegression(self, y_cols: list, drop_cols: list = []):
        for col_name in drop_cols:
            if col_name in self.columns:
                self.drop(col_name, axis=1, inplace=True)
                
        yFrame = SwiPredFrame(self.data_file, label = self.assist_label + "_y", readFile=False)
        for col_name in y_cols:
            if col_name in self.columns:
                yFrame[col_name] = self[col_name]
                self.drop(col_name, axis=1, inplace=True)
        
        self.assist_label = self.assist_label +"_X"
        return self, yFrame'''
    
        
    