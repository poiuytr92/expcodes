#! /usr/bin/env python
#coding=utf-8

class Athlete:
    
    # 构造函数
    def __init__(self, a_name, a_dob=None, a_times=[]):
        self.name = a_name
        self.dob = a_dob
        self.times = a_times
        
    def top3(self):
        return str(sorted(list(set(self.times)))[0:3])
        
    def print_info(self):
        print self.name, "(", self.dob, \
                ")' fastest times are: ", self.top3()
                
    def add_time(self, time_value):
        self.times.append(time_value)
        
    def add_times(self, list_of_times):
        self.times.extend(list_of_times)
    
# end class Athlete


        
        
    
