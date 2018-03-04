#! /usr/bin/env python
#coding=utf-8

from sanitize import sanitize

class AthleteList(list):  # 继承list
    
    def __init__(self, a_name, a_dob=None, a_times=[]):
        list.__init__([])   # 初始化父类的构造函数
        self.name = a_name
        self.dob = a_dob
        self.extend(a_times)
        
    def top3(self):
        return sorted(set([sanitize(item) for item in self]))[0:3]
        
    def print_info(self):
        print self.name, "' fastest times are: ", self.top3()
 
# end class ExAthlete


def get_coach_data(filename):
    with open(filename) as data:
        alist = data.readline()
    
    alist = alist.strip().split(',')
    return AthleteList(alist.pop(0), alist.pop(0), alist)
    
# end: get_coach_data



sarah_athlete = get_coach_data('../file/sarah.txt')
sarah_athlete.print_info()

sarah_athlete.append('1.01')
sarah_athlete.extend(['1.00', '0.22'])

sarah_athlete.print_info()

        
        
    
