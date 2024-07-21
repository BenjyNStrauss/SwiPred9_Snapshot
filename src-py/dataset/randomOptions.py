import torch.nn as nn
from random import randint, sample, uniform

'''
Created on Apr 13, 2023

@author: Benjamin Strauss
'''

functions = [ nn.ReLU(), nn.ReLU6(), nn.LeakyReLU(0.1), nn.SELU(), nn.CELU(),
                nn.GELU(), nn.Sigmoid(), nn.SiLU(), nn.Mish(), nn.Tanh(),
                nn.Softsign()]


EPOCH_MIN = 24
EPOCH_MAX = 64

BATCH_SIZE_MIN = 32;
BATCH_SIZE_MAX = 144;

LEARNING_RATE_MIN = 0.00005;
LEARNING_RATE_MAX = 0.002;

TEST_SIZE_MIN = 0.12
TEST_SIZE_MAX = 0.32

R_STATE_MIN = 1
R_STATE_MAX = 99

LAYER_SIZE_MIN = 24
LAYER_SIZE_MAX = 256

LAYERS_MIN = 2
LAYERS_MAX = 4

def getRandomParams(cols: int, out_channels: int):
    epochs:     int = randint(EPOCH_MIN, EPOCH_MAX)
    batch_size: int = randint(BATCH_SIZE_MIN, BATCH_SIZE_MAX)
    r_state:    int = randint(R_STATE_MIN, R_STATE_MAX)
    num_layers: int = randint(LAYERS_MIN, LAYERS_MAX)
    
    layer_sizes: list = sample(range(LAYER_SIZE_MIN, LAYER_SIZE_MAX), num_layers)
    layer_sizes.insert(0, cols)
    layer_sizes.append(out_channels)
    
    learning_rate: float = round(uniform(LEARNING_RATE_MIN, LEARNING_RATE_MAX), 5)
    test_size: float = round(uniform(TEST_SIZE_MIN, TEST_SIZE_MAX), 5)
    function = functions[randint(0,len(functions)-1)]
    
    return epochs, batch_size, r_state, layer_sizes, learning_rate, test_size, function
    
#a,b,c,d,e,f = getRandomParams(38,1)
#print(d)