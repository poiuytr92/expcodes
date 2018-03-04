#! /usr/bin/env python
#coding=utf-8

# 二进制IO： dump保存 / load恢复

import pickle

file_name = '../file/mydata.pickle'

try:
    with open(file_name, 'wb') as data:
        pickle.dump([1, 2, 'three'], data)
        
    with open(file_name, 'rb') as data:
        a_list = pickle.load(data)
        print a_list
        
except IOError as err:
    print 'File error:', str(err)
