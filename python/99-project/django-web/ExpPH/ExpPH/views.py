# -*- coding: utf-8 -*-
'''
网站主页
Created on 2014年5月13日

@author: Exp
'''
from django.shortcuts import render_to_response
from django.template import loader, Context
from django.http import HttpResponse
from ExpPH.settings import MEDIA_URL
from ExpPH.utils.WebUtils import *
from ExpPH.utils.BaseUtils import getSysTime, isLocalEnvironment
from ExpPH.utils.DBUtils import UseMysqlDB, getDbParams
import ExpPH.conf.Consts as ECC


''' 主页页面 '''
def homePage(request):
    addPageView()   # 页面访问量+1
    sysTime = getSysTime("%Y-%m-%d %H:%M:%S")   # 获取当前系统时间
    send_email_cnt = 10
    
    kwParams = {
        'MEDIA_URL': MEDIA_URL,
        'title': 'Welcome to Exp\'s Home',
        'url_album': (isLocalEnvironment() and ECC.LOCAL_ROOT_URL or ECC.SAE_ROOT_URL) + "album",
        'url_blog': (isLocalEnvironment() and ECC.LOCAL_ROOT_URL or ECC.SAE_ROOT_URL) + "blog",
        'ip': getRemoteIp(request),
        'pageview': getPageView(),
        'datetime': sysTime,
        'email_cnt': send_email_cnt - getPageView() % send_email_cnt,
        'email_msg': sendNotifyMail(send_email_cnt) and '发送 [通知邮件] 成功!' or '',
    }
            
    # 访问记录入库
    db_msg = '访问记录入库失败'
    mysqlDB = UseMysqlDB()
    if mysqlDB.connect(ECC.CHAARSET_DB, 
                       **getDbParams(ECC.DB_TYPE_MYSQL)):
        insertSql = "".join(["insert into t_visit_log(c_ip, d_time, c_remark) values('", 
                             " -> ".join(str(getTrueRemoteIp(request)).split(", ")), 
                             "', '", sysTime, "', '", 
                             kwParams.get('email_msg') and '触发邮件发送' or '一般网页浏览', "')"])
        
        if mysqlDB.insert(insertSql):
            mysqlDB.close()
            db_msg = '访问记录入库成功'
            
    kwParams['db_msg'] = db_msg
    return HttpResponse(render_to_response("index.html", kwParams))
#End homePage()


''' 视频页面 '''
def videoPage(request):
    kwParams = {
        'MEDIA_URL': MEDIA_URL,
        'homeURL' : (isLocalEnvironment() and ECC.LOCAL_ROOT_URL or ECC.SAE_ROOT_URL)
    }
    return HttpResponse(render_to_response("video.html", kwParams))
#End videoPage()
