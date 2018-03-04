#! /usr/bin/env python
#coding=utf-8

# 列表推导

mins = [1, 2, 3]
secs = [m * 60 for m in mins]
print secs

meters = [1, 10, 3]
feet = [m * 3.281 for m in meters]
print feet

lower = ["I", "don't", "like", "spam"]
upper = [s.upper() for s in lower]
print upper

snum = ["2.01", "2.02", "2.22"]
num = [float(s) for s in snum]
print num