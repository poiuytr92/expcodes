#! /usr/bin/env python
#coding=utf-8

''' 数据转换 '''


# 切换工作目录，会影响全局所有路径（工作目录就是启动目录）
#import sys
#sys.path.append("..")

from bean.athletelist import AthleteList
import pickle

""" bgn: get_coach_data - 从文件中读取数据并标准化为AthleteList实例 """
def get_coach_data(filename):
    try:
        with open(filename) as txtfile:
            data = txtfile.readline()
            
        dlist = data.split(',')
        dlist = [item.strip() for item in dlist]
        
        name = dlist.pop(0)
        birthday = dlist.pop(0)
        times = dlist
        athlete = AthleteList(name, birthday, times)
        return athlete
    
    except IOError as err:
        print 'Read File[', filename, '] Error: ', err
        return None
""" end: get_coach_data """



""" bgn: put_to_store - 把字典中的数据转存到pickle """
def put_to_store(file_list):
    all_athletes = {}
    
    # 用txt文件中的数据填充字典
    for filename in file_list:
        athlete = get_coach_data(filename)
        if athlete:
            all_athletes[athlete.name] = athlete
        # end if
    # end for
    
    # 把字典保存到pickle文件中
    try:
        with open('data/athletes.pickle', 'wb') as pfile:
            pickle.dump(all_athletes, pfile)
        
    except IOError as err:
        print 'Pickle Dump Error: ', err
        
    return all_athletes
""" end: put_to_store """



""" bgn: get_from_store - 把pickle中的数据还原为字典 """
def get_from_store():
    all_athletes = {}
    
    try:
        with open('data/athletes.pickle', 'rb') as pfile:
            all_athletes = pickle.load(pfile)
            
    except IOError as err:
        print 'Pickle Load Error: ', err
    
    return all_athletes
""" end: get_from_store """

