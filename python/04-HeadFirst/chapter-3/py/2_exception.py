#! /usr/bin/env python
#coding=utf-8

# 异常捕获

import sys
try:
    data = open('sketch.txt')
    
except IOError as err: # 声明异常类型
    print 'the file [sketch.txt] is missing: ', err
    print sys.exc_info()    # 打印异常信息
    
except:     # 捕获所有未知异常
    pass    # 空语句





import os
fine_path = '../file/sketch.txt'
if os.path.exists(fine_path):
    print 'the file [', fine_path, '] is exist.'
    data = open(fine_path)
    for line in data:
        try:
            (role, msg) = line.split(':')
            print role, 'said', msg,
        except:
            print line,
            
    data.close()
else:
    print 'the file [', fine_path, '] is missing',
    

