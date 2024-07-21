#import os;
#import pandas as pd;
import matplotlib.pyplot as plt
import dataset.preprocess as spp
import dataset.analysis as an

'''
Created on Mar 7, 2023

@author: Benjamin Strauss
'''

def fixFrame(frame):
    frame = spp.removeEntropyNANs(frame)
    #local algorithms
    frame = spp.changeVKColTo1Hot(frame, "chou_fasman")
    frame = spp.changeVKColTo1Hot(frame, "sspro_5")
    frame = spp.changeVKColTo1Hot(frame, "gor4")
    frame = spp.changeVKColTo1Hot(frame, "dsc_l")
    frame = spp.changeVKColTo1Hot(frame, "jnet_l")
    frame = spp.changeVKColTo1Hot(frame, "psipred_l")
    #non-local (original): 15
    frame = spp.changeVKColTo1Hot(frame, "gor1")
    frame = spp.changeVKColTo1Hot(frame, "gor3")
    frame = spp.changeVKColTo1Hot(frame, "dpm")
    frame = spp.changeVKColTo1Hot(frame, "predator_pr")
    frame = spp.changeVKColTo1Hot(frame, "sspro_2")
    frame = spp.changeVKColTo1Hot(frame, "psipred")
    frame = spp.changeVKColTo1Hot(frame, "jnet")
    frame = spp.changeVKColTo1Hot(frame, "phd")
    frame = spp.changeVKColTo1Hot(frame, "profsec")
    frame = spp.changeVKColTo1Hot(frame, "dsc")
    frame = spp.changeVKColTo1Hot(frame, "hnn")
    frame = spp.changeVKColTo1Hot(frame, "mlrc")
    frame = spp.changeVKColTo1Hot(frame, "sopm")
    frame = spp.changeVKColTo1Hot(frame, "jpred")
    frame = spp.changeVKColTo1Hot(frame, "yaspin")
    #residues column
    frame = spp.changeResColTo1Hot(frame, "Res")
    return frame

frame = spp.loadSwipredFrame("output/s8-acetyl+sirt-output.csv")

#an.plotVKByResNum(frame[['Protein', 'No.', 'VK-local','VK-original']], "5BTR")

vkCompFrame2 = frame[['Protein', 'No.', 'VK-local','VK-original']]

spp.normalizeVK(vkCompFrame2, 'VK-local');
spp.normalizeVK(vkCompFrame2, 'VK-original');

vkCompFrame2[(vkCompFrame2['Protein'] != '5BTR')]

for index in range(0, len(vkCompFrame2)):
    if(vkCompFrame2['Protein'].loc[index] != '5BTR'):
        vkCompFrame2.drop(index, axis=0, inplace=True)

vkCompFrame2.plot(x='No.', y="VK-local")
plt.show()

vkCompFrame2.plot(x='No.', y="VK-original")
plt.show()

print(vkCompFrame2)

#print(type(frame['VK-local']))
'''
vkCompFrame = frame[['VK-local','VK-original']]

#col = pd.core.series.Series()
#vkCompFrame.append(col)

spp.normalizeVK(vkCompFrame, 'VK-local');
spp.normalizeVK(vkCompFrame, 'VK-original');

print(an.getAverageVKDiff(vkCompFrame, "norm_VK-local", "norm_VK-original"))

vkCompFrame.plot.scatter(y="VK-local", x="VK-original")
plt.show()
'''




print("Completed.")