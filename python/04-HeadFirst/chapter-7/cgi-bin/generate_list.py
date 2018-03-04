#! /usr/bin/env python
#coding=utf-8

''' CGI脚本-Web界面-页面列表生成器 '''
''' CGI脚本只能放在cgi-bin目录下，否则页面会找不到脚本 '''

# 启用CGI跟踪技术，在Web页面打印异常信息（建议仅调试时开启）
import cgitb
cgitb.enable()

import glob                 # 用于向OS查询文件名列表
from ctrl.athletemodel import *  # 引入模型转换器
from ctrl.yate import *          # 引入界面工具包


# 读入并转换原始数据
file_list = glob.glob("file/*.txt")
athletes = put_to_store(file_list)

# 生成Web界面
print start_response()
print include_header("Exp's List of Athletes")
print start_form("generate_timing_data.py")
print para("Select an athlete from the list to work with:")
for key in athletes:
    print radio_button("which_athlete", athletes[key].name)
print end_form("Select")
print include_footer({"Home": "/index.html"})

