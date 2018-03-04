#! /usr/bin/env python
#coding=utf-8

# 集合去重

from sanitize import sanitize

def get_coach_data(filename):
    with open(filename) as data:
        alist = data.readline()
        
    alist = alist.strip().split(',')
    alist = [sanitize(item) for item in alist] # 列表推导 + 格式标准化
#    aset = set(alist)   # 集合去重
#    alist = sorted(list(aset))  # [集合 -> 列表] + 排序
#    return alist[0:3]   # 列表分片，取前三
    return sorted(list(set(alist)))[0:3]    # 函数串链
# end: fun

whos = [
    '../file/james.txt', 
    '../file/julie.txt', 
    '../file/mikey.txt', 
    '../file/sarah.txt'
]
for who in whos:
    print who, ': ', get_coach_data(who)
