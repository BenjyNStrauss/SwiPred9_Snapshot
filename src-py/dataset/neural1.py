import torch
import torch.nn as nn

'''
Created on Mar 27, 2023

@author: Benjamin Strauss
'''

class SwiPredNN(nn.Module):
    #, num_seq: int, num_classes: int
    def __init__(self, layer_sizes: list, function = nn.ReLU()):
        super(SwiPredNN, self).__init__()
        self.function = function
        
        if not isinstance(layer_sizes, list):
            raise TypeError("\"layer_sizes\" must be a list!")
        
        self.layer_sizes: list = layer_sizes
        self.input_layer = None
        self.layers = nn.ModuleList()
        
        for index in range(1, len(layer_sizes)):
            inSize = self.layer_sizes[index-1]
            outSize = self.layer_sizes[index]
            layer = nn.Linear(inSize, outSize)
            self.layers.append(layer)
            
        #print(self.layers)
    
    def forward(self, inputs: torch.tensor):
        self.input_layer = nn.Linear(inputs.shape[1], self.layer_sizes[0])
        
        meta = self.function(self.input_layer(inputs))
        for layer in self.layers:
            meta = self.function(layer(meta))
        
        return meta
    
    def __len__(self):
        return len(self.landmarks_frame)


