import dataset.preprocess as spp

'''
Created on Apr 10, 2023

@author: Benjamin Strauss
'''

acetyl_sirt = "output/s8-acetyl+sirt-output.csv"



def getFrame(dataset_path: str=acetyl_sirt, bool_threshold = 0.0, include_unassigned=True,
             isSwitchCol: str = 'isSwitch', realVKCol: str = 'isSwitchVK'):
    #Load the data
    frame = spp.loadSwipredFrame(dataset_path)
    print("Read Swipred csv");
    
    #Drop the ids
    frame = spp.dropIDs(frame)
    
    frame = spp.changeResColTo1Hot(frame, "Res")
    frame = frame.drop(['gor1', 'gor3', 'dpm', 'predator_pr',
       'sspro_2', 'psipred', 'jnet', 'phd', 'profsec', 'dsc', 'hnn', 'mlrc',
       'sopm', 'jpred', 'yaspin', 'chou_fasman', 'sspro_5', 'gor4',
       'dsc_l(dsc)', 'jnet_l(jnet)', 'psipred_l(psipred)'], axis=1)
    
    
    frame = spp.removeEntropyNANs(frame)
    
    spp.__purify__(frame)
    
    frame = spp.makeIsSwitch(frame, addVK = True, threshold=bool_threshold, isSwitchCol=isSwitchCol,
                              realVKCol=realVKCol, include_unassigned=include_unassigned, addBinSwitch=False)
    spp.__purify__(frame)
    
    yCols = ['NUM_H', 'NUM_S', 'NUM_O', 'NUM_U', realVKCol]
    
    frameX = frame.drop(yCols, axis=1)
    frameY = frame[yCols]
    
    print("Loaded Frame")
    
    return (frameX, frameY)
