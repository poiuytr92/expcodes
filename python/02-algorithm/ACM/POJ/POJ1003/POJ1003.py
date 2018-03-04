#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
    Author:     Exp
	Date:       2015-04-19
    Code:       POJ 1003
    Problem:    Hangover
    URL:
"""

import sys

lenTable = [0, 0.5]     # 长度表，lenTable[i] 表示i张卡片可以叠加的长度
tableLen = 2            # 长度表的长度

while True:
    line = sys.stdin.readline()
    if not line:
        break

    length = float(line)
    if length == 0:
        break

    idx = 1
    while lenTable[idx] < length:   # 服务器打表
        idx += 1
        if tableLen <= idx:
            lenTable.append(lenTable[idx - 1] + 1.0 / (idx + 1))
            tableLen += 1

    print "{} card(s)".format(idx)



