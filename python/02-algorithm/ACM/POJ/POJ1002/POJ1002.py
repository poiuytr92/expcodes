#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
    Author:     Exp
	Date:       2015-04-19
    Code:       POJ 1002
    Problem:    487-3276
    URL:
"""

import sys

numDict = dict([('A', '2'), ('B', '2'), ('C', '2'),
                ('D', '3'), ('E', '3'), ('F', '3'),
                ('G', '4'), ('H', '4'), ('I', '4'),
                ('J', '5'), ('K', '5'), ('L', '5'),
                ('M', '6'), ('N', '6'), ('O', '6'),
                ('P', '7'), ('R', '7'), ('S', '7'),
                ('T', '8'), ('U', '8'), ('V', '8'),
                ('W', '9'), ('X', '9'), ('Y', '9'),
                ('0', '0'), ('1', '1'), ('2', '2'), ('3', '3'),
                ('4', '4'), ('5', '5'), ('6', '6'), ('7', '7'),
                ('8', '8'), ('9', '9')])
telDict = {}

lineNum = int(sys.stdin.readline())
cnt = 0
while cnt < lineNum:
    cnt += 1
    orgTel = sys.stdin.readline().rstrip()

    # 获取每个字母的映射值，还原真实的电话号码
    tel = ''
    for c in orgTel[:]:
        v = numDict.get(c)
        if v:
            tel = tel + v
    tel = '-'.join([tel[:3], tel[3:]])

    # 记录电话号码的累计出现次数
    appear = telDict.get(tel)
    if not appear:
        appear = 0
    appear += 1
    telDict[tel] = appear

# 记录出现次数超过2次的电话号码
results = []
for tel in telDict.keys():
    appear = telDict.get(tel)
    if appear > 1:
        results.append(' '.join([tel, str(appear)]))
results = sorted(results)    # 升序排序

# 打印结果
if len(results) <= 0:
    print 'No duplicates.'
else:
    for result in results:
        print result






