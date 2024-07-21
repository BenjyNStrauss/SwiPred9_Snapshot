import sys
import torch
from torch import tensor

'''
Created on June 9, 2022

@author: Benjamin Strauss
'''

__filepath__ = "files/tmp/"
filename = sys.argv[1]
layer = sys.argv[2]

model: dict = torch.load(filename)
model_rep: dict = model["representations"]
all_reps: tensor = model_rep[int(layer)]

with open(__filepath__+'esm-plaintext.txt', 'a') as f:
    for subtensor in all_reps:
        #print(subtensor.size())
        val_list: list = []
        for value in subtensor:
            val_list.append(value.item())
        f.write(",".join(map(str, val_list))+"\n")

print("ESM model translated to CSV!")

