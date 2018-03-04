#! /usr/bin/env python
#coding=utf-8


"""
    递归打印嵌套列表
"""
def printList(alist, use_tab=False, level=0):
    
    for item in alist:
        if isinstance(item, list):
            printList(item, use_tab, level+1)
        else:
            ''' 逐层自动缩进 '''
            if use_tab:
                for n in range(level):
                    print "\t",             # 这是py2打印不换行的语法
#                   print("\t", end="")    # 这是py3打印不换行的语法
            print item
# end: printList
