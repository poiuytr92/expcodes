#! /usr/bin/env python
#coding=utf-8

''' AthleteList模型 '''

class AthleteList(list):
    
    """ bgn: __init__ - 构造函数 """
    def __init__(self, aname, abirthday=None, atimes=[]):
        list.__init__([])
        self.name = aname;
        self.birthday = abirthday
        self.extend(atimes)
    """ end: __init__ """
        
        
    def top3(self):
        return sorted(set([AthleteList.sanitize(item) for item in self]))[0:3]
        
    """ bgn: sanitize - 格式化时间字符串 """
    @staticmethod
    def sanitize(time_string):
        if '-' in time_string:  
            splitter = '-'
        elif ':' in time_string:
            splitter = ':'
        else:
            return time_string
        
        (min, sec) = time_string.split(splitter)
        return min + '.' + sec
    """ end: sanitize """
    