import pandas as pd
import dataset.preprocess as spp
import matplotlib.pyplot as plt

'''
Created on Mar 14, 2023

@author: Benjamin Strauss
'''

def getAverageVKDiff(table: pd.DataFrame, vk_col1: str, vk_col2: str):
    _sum = 0
    for index in range(0, len(table)):
        diff = abs(table[vk_col1][index] - table[vk_col2][index])
        _sum += diff
    _sum /= len(table)
    return _sum

def plotVKByResNum(table: pd.DataFrame, _id: str):
    for index in range(0, len(table)):
        if(table['Protein'].loc[index] != _id):
            table.drop(index, axis=0, inplace=True);
    
    table = spp.normalizeVK(table, 'VK-local');
    table = spp.normalizeVK(table, 'VK-original');
    table.plot.scatter(x='No.', y=["VK-local", "VK-original"])
    plt.show()
    
    
    
    