#! /usr/bin/env python
#coding=utf-8

# 处理数据

with open('../file/james.txt') as data:
    james = data.readline()
    james = james.strip().split(',')
    
with open('../file/julie.txt') as data:
    julie = data.readline()
    julie = julie.strip().split(',')

with open('../file/mikey.txt') as data:
    mikey = data.readline()
    mikey = mikey.strip().split(',')

with open('../file/sarah.txt') as data:
    sarah = data.readline()
    sarah = sarah.strip().split(',')

print james
print julie
print mikey
print sarah
print "=============================="

# 复制排序 (默认升序排序)
sJames = sorted(james, reverse=True)    # 降序排序
sJulie = sorted(julie, reverse=True)  
sMikey = sorted(mikey, reverse=True)  
sSarah = sorted(sarah, reverse=True)  

print sJames
print sJulie
print sMikey
print sSarah
print "=============================="

from sanitize import sanitize

newJames = []
for time_string in sJames:
    newJames.append(sanitize(time_string)) 
    
newJulie = []
for time_string in sJulie:
    newJulie.append(sanitize(time_string)) 

newMikey = []
for time_string in sMikey:
    newMikey.append(sanitize(time_string)) 

newSarah = []
for time_string in sSarah:
    newSarah.append(sanitize(time_string)) 

# 原地排序
newJames.sort()
newJulie.sort()
newMikey.sort()
newSarah.sort()

print newJames
print newJulie
print newMikey
print newSarah
print "=============================="
