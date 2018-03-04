#! /usr/bin/env python
#coding=utf-8

# 打印python内建函数清单

bifs = dir(__builtins__)
for bif in bifs:
    print bif
    
    
import sys
bif_dirs = sys.path
for bif_dir in bif_dirs:
    print bif_dir
