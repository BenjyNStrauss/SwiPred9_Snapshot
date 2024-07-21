import sys

from scipy import spatial

'''
Created on Aug 4, 2022
needs to access and call libraries...
@author: Benjamin Strauss

'''

funct = sys.argv[1]

vec_str1 = sys.argv[2]
vec_str2 = sys.argv[3]

meta1 = vec_str1.split(",")
meta2 = vec_str2.split(",")

vec1 = []
vec2 = []

for val in meta1:
    vec1.append(float(val))
for val in meta2:
    vec2.append(float(val))

if funct == "cosine":
    val = spatial.distance.cosine(vec1, vec2)
    val = 1 - val
    print(val)
else:
    print("function not recognized")



