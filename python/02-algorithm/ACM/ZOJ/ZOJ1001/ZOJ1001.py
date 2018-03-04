#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
    Author:     Exp
	Date:       2015-03-13
    Code:       ZOJ 1001
    Problem:    A + B Problem
    URL:		http://acm.zju.edu.cn/onlinejudge/showProblem.do?problemId=1
"""

import sys

while True:
    line = sys.stdin.readline()
    if not line:    # 若是空行退出循环
        break

    params = line.split()
    print int(params[0]) + int(params[1])
