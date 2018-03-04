#! /usr/bin/env python
#coding=utf-8

movies = [
    "The Holy Grail", 1975, 
    ["The Life Og Brian", 
        ["A", "B"]
    ]
]


# from nester import printList
# printList(movies)

import nester
nester.printList(movies)    # 利用默认值
nester.printList(movies, True, 0)
nester.printList(movies, True, -10)   # 利用负值关闭缩进(只要嵌套不超过10层)
nester.printList(movies, False)       # 利用参数关闭缩进
nester.printList(movies, level=1)