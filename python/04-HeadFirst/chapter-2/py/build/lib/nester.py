#! /usr/bin/env python
#coding=utf-8


"""
    递归打印嵌套列表
"""
def printList(alist):
    '''alist: 任意层次的列表 '''
    
    for item in alist:
        if isinstance(item, list):
            printList(item)
        else:
            print item
# end: printList
