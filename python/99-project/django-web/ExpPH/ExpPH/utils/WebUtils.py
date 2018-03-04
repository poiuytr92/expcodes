# -*- coding: utf8 -*-
'''
网页工具
Created on 2014年5月13日

@author: Exp
'''

import sae.kvdb
from ExpPH.utils.BaseUtils import isLocalEnvironment
from ExpPH.utils.DBUtils import LocalKVDB
from ExpPH.conf.Keys import PAGE_VIEW


''' 获取访问网站的用户IP '''
def getRemoteIp(request):
    remoteIp = request.META['REMOTE_ADDR']
    return remoteIp
# End Fun getRemoteIp()


''' 当用户使用了代理服务器或负载均衡时，获取访问网站的用户的真正IP '''
def getTrueRemoteIp(request):
    remoteIp = request.META['REMOTE_ADDR']
    
    if request.META.has_key('HTTP_X_FORWARDED_FOR'):
        remoteIp = request.META['HTTP_X_FORWARDED_FOR']
        
    return remoteIp
# End Fun getRemoteIp()


''' 获取页面访问量+1 '''
def getPageView():
    # SAE环境，通过 KVDB 获取页面访问量
    if not isLocalEnvironment():
        kv = sae.kvdb.KVClient()
    # 本地环境，通过 本地kv.conf配置文件 获取页面访问量
    else: 
        kv = LocalKVDB()
         
    pvv = kv.get(PAGE_VIEW)
    if not pvv:
        pvv = 0
        
    return int(str(pvv).strip())
# End Fun getPageView()


''' 页面访问量+1 '''
def addPageView():
    # SAE环境，通过 KVDB 获取页面访问量
    if not isLocalEnvironment():
        kv = sae.kvdb.KVClient()
    # 本地环境，通过 本地kv.conf配置文件 获取页面访问量
    else: 
        kv = LocalKVDB()
         
    pvv = kv.get(PAGE_VIEW)
    if not pvv:
        pvv = 0
        
    pvv = int(str(pvv).strip()) + 1
    kv.set(PAGE_VIEW, pvv)
    return pvv
# End Fun addPageView()


''' 当访客人数达到  baseNum 的整数倍时，发送通知邮件到我的邮箱 '''
def sendNotifyMail(baseNum = 100):
    curPageView = int(getPageView())
    
    if curPageView % baseNum == 0:
        mail_tolist = ["272629724@qq.com"]  # 我接收通知的邮箱
        subject = "%s%i"%("恭喜访问量增加", baseNum)
        content = "%s%i"%("您的网站 http://expph.sinaapp.com/index/ 访问量已达到:", curPageView) 
        return sendMailBy126(mail_tolist, subject, content)
    return None
# End Fun sendNotifyMail()
    
    
''' 发送本网站的推广邮件到指定邮箱列表 '''
def sendAdMail(mail_tolist):
    subject = "欢迎来访Exp的个人主页"
    content = "%s%i%s"%("Exp的网站 http://expph.sinaapp.com/index/ ,您是第 ", getPageView(), " 位访客!")
    return sendMailBy126(mail_tolist, subject, content)
# End Fun sendNotifyMail()
    
    
''' 使用126邮箱发送邮件 '''
def sendMailBy126(tolist, subject, content):
    from ExpPH.utils.BaseUtils import decrypt
    isSentMail = False
    
    mail_host = "smtp.126.com"      # 发信邮箱 SMTP 服务器
    mail_port = 25                  # 发信邮箱 SMTP 服务器端口
    mail_user = "ExpertPH"          # 发信邮箱账号
    mail_postfix = "126.com"        # 发信邮箱后缀
    mail_pass = decrypt("RXhwMTIzNDU2UEg=")     # 发信邮箱密码
    sender = mail_user + "@" + mail_postfix     # 发信者
    
    # SAE 环境
    if not isLocalEnvironment:
        from sae.mail import send_mail
        try:
            send_mail(tolist, subject, content,
                      (mail_host, mail_port, sender, mail_pass, False))
            isSentMail = True
        except Exception, e:
            print "发送邮件失败:", str(e)
            
    # 本地环境
    else:
        import smtplib
        from email.mime.text import MIMEText
        from ExpPH.conf.Consts import CHARSET_PRGM
        
        sender = "".join([sender, "<", sender, ">"])
        msg = MIMEText(content, _charset = CHARSET_PRGM)
        msg['Subject'] = subject
        msg['From'] = sender
        msg['To'] = ";".join(tolist)
        
        try:
            server = smtplib.SMTP()
            server.connect(mail_host)
            server.login(mail_user, mail_pass)
            server.sendmail(sender, tolist, msg.as_string())
            server.close()
            isSentMail = True
        except Exception, e:
            print "发送邮件失败:", str(e)
            
    return isSentMail
# End Fun sendMailBy126()

