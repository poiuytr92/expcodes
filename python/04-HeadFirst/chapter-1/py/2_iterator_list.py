#! /usr/bin/env python
#coding=utf-8

# 处理列表数据

movies = [
    "The Holy Grail", 
    "The Life Og Brian", 
    "The Meaning of Life"
]

print "\nfor list:"
for movie in movies:
    print movie
    
    
print '\nwhile list:'
cnt = 0
while cnt < len(movies):
    print movies[cnt]
    cnt = cnt + 1
