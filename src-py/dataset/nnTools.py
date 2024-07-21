import torch
from torch.utils.data import Dataset

'''
Created on Apr 10, 2023

@author: Benjamin Strauss
'''

'''
Dataset Learn Class
'''
class LearnSet(Dataset):
    def __init__(self, X_tensor, y_tensor, transform = None):
        self.x = X_tensor
        self.y = y_tensor
        self.len = len(X_tensor)
        self.transform = transform
        
    def __getitem__(self, index):
        sample = (self.x[index], self.y[index])
        if self.transform:
            sample = self.transform(sample)
        return sample;
    
    def __len__(self):
        return self.len
    
'''
Dataset Test Class
'''
class TestSet(Dataset):
    def __init__(self, data_tensor):
        self.x = data_tensor
        
    def __getitem__(self, index):
        return self.x[index]
        
    def __len__ (self):
        return len(self.x)

def binary_acc(y_pred, y_test):
    y_pred_tag = torch.round(torch.sigmoid(y_pred))

    correct_results_sum = (y_pred_tag == y_test).sum().float()
    acc = correct_results_sum/y_test.shape[0]
    acc = torch.round(acc * 100)
    
    return acc