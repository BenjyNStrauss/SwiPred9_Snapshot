import pandas as pd
import matplotlib.pyplot as plt
import dataset.preprocess as spp

'''
Created on Mar 7, 2023

@author: Benjamin Strauss
'''

frame = spp.loadSwipredFrame("output/s8-acetyl+sirt-output.csv")

#an.plotVKByResNum(frame[['Protein', 'No.', 'VK-local','VK-original']], "5BTR")

vkCompFrame = frame[['Protein', 'No.', 'VK-local','VK-original']]

spp.normalizeVK(vkCompFrame, 'VK-local');
spp.normalizeVK(vkCompFrame, 'VK-original');

'''
vk_ratio = []
for index in range(len(vkCompFrame2)):
    vk_ratio.append(vkCompFrame2['VK-local'][index] / vkCompFrame2['VK-original'][index])

vkCompFrame2["vk-ratio"] = pd.Series(vk_ratio)
'''

for index in range(len(vkCompFrame)):
    if vkCompFrame['Protein'][index] != '5BTR':
        vkCompFrame.drop(index, axis=0, inplace=True)


#vkCompFrame2.plot(x='No.', y="vk-ratio")
#plt.show()
#print(len(vkCompFrame['No.']))
#print(len(vkCompFrame['VK-original']))
#print(len(vkCompFrame['VK-local']))

#vkCompFrame2.plot(x='No.', y="VK-local", z="VK-original")
vkCompFrame.plot(x='No.', y=['VK-original','VK-local'], marker="o")
#vkCompFrame.plot.scatter(x='No.', y='VK-local')
plt.show()



#for index in range(0, len(vkCompFrame2)):
#    if(vkCompFrame2['Protein'].loc[index] != '5BTR'):
#        vkCompFrame2.drop(index, axis=0, inplace=True)



#vkCompFrame2.plot(x='No.', y="VK-original")
#plt.show()

#print(vkCompFrame2)

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