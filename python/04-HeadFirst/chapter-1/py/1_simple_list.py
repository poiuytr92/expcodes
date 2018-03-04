#! /usr/bin/env python
#coding=utf-8

# 创建简单的python列表

movies = [
    "The Holy Grail", 
    "The Life Og Brian", 
    "The Meaning of Life"
]

print movies[1]
print(movies[0])
print len(movies)

movies.append('Add a Movie on the end of list')
print movies

movies.pop()
print movies

movies.extend(["This is ", " a ExList"])
print movies

movies.remove("This is ")
print movies

movies.insert(0, "I'm First")
print movies