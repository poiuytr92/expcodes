#! /usr/bin/env python
#coding=utf-8

# 字典

# 创建空字典的两种方法
cleese = {}
print type(cleese)

palin = dict()
print type(palin)


# 往字典添加元素
cleese['Name'] = 'Jone Cleese'
cleese['Occupations'] = ['actor', 'comedian', 'writer']
print cleese
print cleese['Name']
print cleese['Occupations'][1]  # 索引串链：从左往右

palin = {'Name':'Michael Plain', 'Occupations':['tv', 'film prodeucer']}
print palin
print palin['Name']
print palin['Occupations'][-1]  # 索引串链：从右往左
