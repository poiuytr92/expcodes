#! /usr/bin/env python
#coding=utf-8

# 读写程序外部的数据

import os
print os.getcwd()    # 当前工作目录
os.chdir('../file')  # 切换工作目录
print os.getcwd()

data = open('sketch.txt')   # 打开文件
print data.readline()       # 打印单行
print data.readline(),      # 打印单行（不打印行尾换行, 逗号会把换行变成空格）
print '==================='

# 打印所有行
data.seek(0)        # 退回文件首部
for line in data:   
    print line,
print '\n==================='

# 打印split BIF的API说明
help("".split)

# 切割打印所有行 
data.seek(0)
for line in data:
#   (role, msg) = line.split(':')       # 当一行的冒号超过 1 个就会报错
    if line.find(':') > 0:
        (role, msg) = line.split(':', 1)    # 最多切割一次
        print role, 'said', msg,
    else:
        print line


data.close()