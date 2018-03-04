#! /usr/bin/env python
#coding=utf-8

''' CGI脚本-Web界面-选手时间清单 '''
''' CGI脚本只能放在cgi-bin目录下，否则页面会找不到脚本 '''


# 启用CGI跟踪技术，在Web页面打印异常信息（建议仅调试时开启）
import cgitb
cgitb.enable()

# 获取表单数据
import cgi
form_data = cgi.FieldStorage()  
althete_name = form_data['which_athlete'].value # 在前一个CGI表单中定义的单选组件名字

# 提取选手成绩信息
from ctrl.athletemodel import get_from_store
althetes = get_from_store()
althete = althetes[althete_name]

# CGI页面生成
from ctrl.yate import *
print start_response()
print include_header(althete.name + "'s Timing Data")
print header("Althete: " + althete.name + ", DOB: " + althete.birthday + ".")
print header("The top3 time of the althete are:")
print u_list(althete.top3())
print include_footer({
        "Home": "/index.html", 
        "Select Other Althete": "generate_list.py"})
