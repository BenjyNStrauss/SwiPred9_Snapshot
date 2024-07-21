import torch
'''
Created on Apr 10, 2023

@author: Benjamin Strauss
'''

class ArrayLengthMismatchException(Exception):
    def __init__(self, arr1, arr2):
        self.len1 = len(arr1)
        self.len2 = len(arr2)
        
class InternalErrorException(Exception):
    def __init__(self, msg=None):
        self.msg = msg

def displayTrueVPred(y_true, y_pred):
    print("****************************")
    for index in range(len(y_pred)):
        y_pred_val = y_pred[index].item()
        y_true_val = y_true[index].item()
        print(f'pred {y_pred_val:.5f} vs true {y_true_val:.5f}')

def extractAndAppend(y_true_list: list, y_pred_list:list,
                      y_true_vec: torch.tensor, y_pred_vec: torch.tensor):
    for index in range(len(y_true_vec)):
        y_true_list.append(y_true_vec[index].item())
        y_pred_list.append(y_pred_vec[index].item())
        
        
    return y_true_list, y_pred_list

'''
def getAuc(y_true: list, y_pred: list, interval):
    if len(y_true) != len(y_pred):
        raise ArrayLengthMismatchException(y_true, y_pred)
    
    #print(">",y_true)
    #print(">",y_pred)
    #exit()
    
    roc_aucs = []
    auc = 0
    threshold = interval
    while threshold <= 1:
        tp, fp, tn, fn = doComparisons(y_true, y_pred, threshold)
        tpr = tp / (tp+fn) if (tp+fn) > 0 else 0
        fpr = fp / (fp+tn)
        roc_aucs.append((fpr, tpr))
        
        auc += tpr*interval
        threshold += interval
        
    if auc == 0:
        print(">",y_true)
        print(">",y_pred)
    
    
    return auc
    
    #return None
def doComparisons(y_true: list, y_pred: list, threshold):
    #print("threshold",threshold)
    if len(y_true) != len(y_pred):
        raise ArrayLengthMismatchException(y_true, y_pred)
    
    tp = 0
    fp = 0
    tn = 0
    fn = 0
    
    for index in range(len(y_true)):
        if y_true[index] >= threshold and y_pred[index] >= threshold:
            tp += 1
        elif y_true[index] >= threshold and y_pred[index] < threshold:
            fn += 1
        elif y_true[index] < threshold and y_pred[index] < threshold:
            tn += 1
        elif y_true[index] < threshold and y_pred[index] >= threshold:
            fp += 1
        else:
            raise InternalErrorException()
    
    return tp, fp, tn, fn
'''
