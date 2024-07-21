import math

import numpy
import pandas as pd

import regression.swipred_frame as spf

from regression.errors import SwiPredPipelineException

'''
Created on May 28, 2023

@author: Benjamin Strauss
'''

def pipeline_00(filepath: str):
    spdf = spf.SwiPredFrame(filepath)
    
    spdf.dropIDs()
    
    spdf.encodeRes1Hot()
    spdf.dropRawVKPredCols()
    spdf.removeNANs()
    spdf.makeSwitchiness(newCol="isSwitch")
    
    drop_cols = ['NUM_H', 'NUM_S', 'NUM_O', 'NUM_U', 'IsU[3]-Window',
                 'E6-Window[3]', 'E20-Window[3]', 'Amber95-Window[3]',
                 '# homologues', 'num_homologues']
    
    
    for col in drop_cols:
        spdf.dropCol(col)
    
    purify(spdf)
    yFrame = spdf['isSwitch']
    spdf.dropCol('isSwitch')
    
    return spdf, yFrame

def pipeline_01(filepath: str):
    spdf = spf.SwiPredFrame(filepath)
    spdf.dropIDs()
    spdf.makeSwitchiness(newCol="isSwitch")
    
    drop_cols = ['NUM_H', 'NUM_S', 'NUM_O', 'NUM_U', 'Res', 
                 '# homologues', 'num_homologues']
    
    for col in drop_cols:
        spdf.dropCol(col)
    
    drop_cols_2 = [26,74,77,98,102,110,112,154,158,190,200,204,264,283,340,374,424,
                   437,451,458,491,505,514,517,525,546,556,557,563,567,571,575,603,
                   613,653,666,682,690,695,705,719,742,752,763,767,782,789,831,854,
                   908,913,937,947,963,970,973,1013,1014,1015,1029,1030,1052,1064,
                   1070,1138,1147,1181,1203,1244,1246,1252,1274]
    
    spdf.dropColsNo(drop_cols_2)
    
    purify(spdf)
    yFrame = spdf['isSwitch']
    spdf.dropCol('isSwitch')
    
    return spdf, yFrame

def purify(frame: spf.SwiPredFrame):
    dropList = []
    
    for ii in range(len(frame.index)):
        row = frame.iloc[ii]
        includeRow = False
        
        for value in row:
            if invalidValue(value):
                includeRow = True
                break
        
        dropList.append(includeRow)
    
    frame.drop(frame[dropList].index, inplace = True)
    

def invalidValue(value):
    if not isinstance(value, float) and not isinstance(value, numpy.float64):
        return True
    if value != value or math.isnan(value):
        return True
    if not math.isfinite(value):
        return True
    return False

    