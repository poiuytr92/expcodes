#! /usr/bin/env python
#coding=utf-8

# 列表中的额外数据

from coach_data import get_coach_data
sarah_athlete = get_coach_data('../file/sarah.txt')
sarah_athlete.print_info()

sarah_athlete.add_time(1.01)
sarah_athlete.add_times([1.00, 0.22])

print sarah_athlete.top3()




from Athlete import Athlete
unknow = Athlete("who am I", a_times=[1, '2'])
unknow.print_info()
print unknow.name
print unknow.dob
print unknow.times