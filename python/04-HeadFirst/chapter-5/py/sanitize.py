#! /usr/bin/env python
#coding=utf-8

# 格式化时间字符串
def sanitize(time_string):
    if '-' in time_string:  
        splitter = '-'
    elif ':' in time_string:
        splitter = ':'
    else:
        return time_string
    
    (min, sec) = time_string.split(splitter)
    return min + '.' + sec
# end: sanitize
