#! /usr/bin/env python
#coding=utf-8

# 在列表中存储列表

movies = [
    "The Holy Grail", 1975, 
    ["The Life Og Brian", 
        ["A", "B"]
    ]
]

print movies
print movies[0]
print movies[2]
print movies[2][1]

# python3 要求递归深度不超过100
def printList(alist):
    for item in alist:
        if isinstance(item, list):
            printList(item)
        else:
            print item
# end: printList

print "\nAll Items:"
printList(movies)

