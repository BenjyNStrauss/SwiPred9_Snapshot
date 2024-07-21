import torch
import torch.nn as nn

from util.LocalToolBase import log

'''
Created on Jul 17, 2023

@author: Benjamin Strauss
'''

class SwiPredRegressorNN(nn.Module):
    #, num_seq: int, num_classes: int
    def __init__(self, layer_sizes: list, function=nn.ReLU()):
        super(SwiPredRegressorNN, self).__init__()
        
        if not isinstance(layer_sizes, list):
            raise TypeError("\"layer_sizes\" must be a list!")
        
        self.function = function
        self.layer_sizes: list = layer_sizes
        if self.layer_sizes[len(layer_sizes)-1] != 1:
            layer_sizes.append(1)
        
        self.input_layer = None
        self.layers = nn.ModuleList()
        
        for index in range(1, len(layer_sizes)):
            inSize = self.layer_sizes[index-1]
            outSize = self.layer_sizes[index]
            layer = nn.Linear(inSize, outSize)
            self.layers.append(layer)
        
        self.sigmoid = nn.Sigmoid()
            
        #print(self.layers)
    
    def forward(self, inputs: torch.tensor, debug=False):
        if(debug):
            print(inputs)
            print(type(inputs))
            print(inputs.shape)
        
        inSize = 0
        if hasattr(inputs, "shape"):
            inSize = inputs.shape[1] if len(inputs.shape) == 2 else inputs.shape[0]
        else:
            inSize = inputs
        
        if self.input_layer is None:
            self.input_layer = nn.Linear(inSize, self.layer_sizes[0])
        
        meta = self.function(self.input_layer(inputs))
        for layer in self.layers:
            meta = self.function(layer(meta))
        
        meta = self.sigmoid(meta)
        
        return meta
    
    def __len__(self):
        return len(self.landmarks_frame)
    
    def reset_weights(self, outFile = None, verbose = False):
        if self.input_layer is None:
            return
        self.input_layer.reset_parameters()
        for layer in self.layers:
            layer.reset_parameters()
            if outFile is not None and verbose:
                log(outFile, f'Reset trainable parameters of layer = {layer}')
        
        if outFile is not None and not verbose:
            log(outFile, f'Reset model\'s trainable parameters')
