#! /usr/bin/env python
#coding=utf-8

# 构造数据字典

from sanitize import sanitize
from Athlete import Athlete

def get_coach_data(filename):
    with open(filename) as data:
        alist = data.readline()
    
    alist = alist.strip().split(',')
    adict = {}
    adict['Name'] = alist.pop(0)
    adict['DOB'] = alist.pop(0)
    adict['Times'] = [sanitize(item) for item in alist]
    return Athlete(adict['Name'], adict['DOB'], adict['Times'] )
# end: get_coach_data