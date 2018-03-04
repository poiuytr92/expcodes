#! /usr/bin/env python
#coding=utf-8

# 列表中的额外数据

from sanitize import sanitize

def get_coach_data(filename):
    with open(filename) as data:
        alist = data.readline()
    
    alist = alist.strip().split(',')
    adict = {}
    adict['Name'] = alist.pop(0)
    adict['DOB'] = alist.pop(0)
    adict['Times'] = [sanitize(item) for item in alist]
    adict['BestTime'] = str(sorted(list(set(adict['Times'])))[0:3])
    return adict
# end: fun



sarah_data = get_coach_data('../file/sarah.txt')

# \表示代码另起一行
print sarah_data['Name'], "(", sarah_data['DOB'], \
        ")' fastest times are: ", sarah_data['BestTime']
        