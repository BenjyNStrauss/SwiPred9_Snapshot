'''
Created on Apr 19, 2023

@author: Benjamin Strauss

'''

def log(outFile=None, text: str = ""):
    print(f'{text}')
    if outFile is not None:
        outFile.write(f'{text}\n')

def recordParams(outFile, epochs, batchSize, layer_sizes, loss_function, 
                 non_linear_funct, learning_rate, randomState, testSize,
                 valSize="Not Used"):
    outFile.write(f"Epochs:          {epochs}\n")
    outFile.write(f"Batch Size:      {batchSize}\n")
    outFile.write(f"Layer sizes:     {layer_sizes}\n")
    outFile.write(f"Loss function:   {loss_function}\n")
    outFile.write(f"Nonlinearizer:   {non_linear_funct}\n")
    outFile.write(f"Learning rate:   {learning_rate}\n")
    outFile.write(f"Random State:    {randomState}\n")
    outFile.write(f"Test Size:       {testSize}\n")
    outFile.write(f"Validation Size: {valSize}\n\n")
