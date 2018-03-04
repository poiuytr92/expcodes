#! /usr/bin/env python
#coding=utf-8

# 列表中的额外数据

from sanitize import sanitize

def get_coach_data(filename):
    with open(filename) as data:
        alist = data.readline()
        
    alist = alist.strip().split(',')
    (name, dob) = alist.pop(0), alist.pop(0) # 弹出列表开头的元素（额外数据：名字和生日）
    
    alist = [sanitize(item) for item in alist]
    score = str(sorted(list(set(alist)))[0:3])
    
    print name, "(", dob, ")' fastest times are: ", score
# end: fun

get_coach_data('../file/sarah.txt')
