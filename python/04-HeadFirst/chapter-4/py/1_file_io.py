#! /usr/bin/env python
#coding=utf-8

# 程序生成数据写入外存


# /////////////////////////////////////////////////////
# Read:
import sys

man = []
other = []

try:
    in_file = open('../file/sketch.txt')
    for line in in_file:
        try:
            (role, msg) = line.split(':', 1)
            msg = msg.strip()   # 去除前后空字符
            if role == 'Man':
                man.append(msg)
            elif role == 'Other Man':
                other.append(msg)
        except ValueError:
            pass
    
    in_file.close()
except IOError:
    print 'the file is missing!'
    print sys.exc_info()
    
print man
print other


# /////////////////////////////////////////////////////
# Write:
    


file_name = '../file/msg.txt'
try:
    out_file = open(file_name, 'w')     # 以写模式打开文件
    # print('This is a Title', file=out_file)   # py3 特性
    out_file.write('This is a Title\n')
except IOError:
    print 'File I/O Error'
finally:
    if file_name in locals():  
        out_file.close()

try:
    out_file = open(file_name, 'a')     # 以写模式（追加）打开文件
    out_file.write(str(man))
    out_file.write("\n")
except IOError:
    print 'File I/O Error'
finally:
    if file_name in locals():
        out_file.close()
    
# 返回当前作用域中定义的所有名（对象、变量名等）的集合
print locals()


# with 替代 finally
# with 是python中的上下文管理协议技术，无需再担心 out_file 因为IO异常不能close
# 此时就不用再使用 close语句了, with会自动执行
try:
    with open(file_name, 'a') as out_file:
        out_file.write(str(other))
        out_file.write("\n")
except IOError as err:
    print 'File I/O Error', str(err)
