#! /usr/bin/env python
#coding=utf-8

# 列表去重

from sanitize import sanitize

with open('../file/james.txt') as data:
    james = data.readline()
    james = james.strip().split(',')
    sJames = sorted([sanitize(item) for item in james]) # 列表推导 + 排序
    unique = []                 # 去重
    for item in sJames:
        if item not in unique:
            unique.append(item)
    print unique[0:3]           # 列表分片，取前三
    
    
with open('../file/julie.txt') as data:
    julie = data.readline()
    julie = julie.strip().split(',')
    sJulie = sorted([sanitize(item) for item in julie]) # 列表推导 + 排序
    unique = []                 # 去重
    for item in sJulie:
        if item not in unique:
            unique.append(item)
    print unique[0:3]           # 列表分片，取前三
    
    
with open('../file/mikey.txt') as data:
    mikey = data.readline()
    mikey = mikey.strip().split(',')
    sMikey = sorted([sanitize(item) for item in mikey]) # 列表推导 + 排序
    unique = []                 # 去重
    for item in sMikey:
        if item not in unique:
            unique.append(item)
    print unique[0:3]           # 列表分片，取前三
        
        
with open('../file/sarah.txt') as data:
    sarah = data.readline()
    sarah = sarah.strip().split(',')
    sSarah = sorted([sanitize(item) for item in sarah]) # 列表推导 + 排序
    unique = []                 # 去重
    for item in sSarah:
        if item not in unique:
            unique.append(item)
    print unique[0:3]           # 列表分片，取前三
    
    




