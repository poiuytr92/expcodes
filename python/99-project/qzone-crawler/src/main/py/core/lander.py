# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'

import requests

import src.main.py.config as cfg
from src.main.py.bean.cookie import QQCookie


class Lander(object):
    '''
    QQ空间登陆器.
    ========================================================
    QQ空间XHR登陆分析参考(原文所说的方法已失效, 此处做过修正)：
        登陆流程拆解：https://blog.csdn.net/M_S_W/article/details/70193899
        登陆参数分析：https://blog.csdn.net/zhujunxxxxx/article/details/29412297
        登陆参数分析：http://www.vuln.cn/6454
        加密脚本抓取： https://baijiahao.baidu.com/s?id=1570118073573921&wfr=spider&for=pc
        重定向BUG修正: http://jingpin.jikexueyuan.com/article/13992.html
    '''

    QQ = ''             # 所登陆的QQ
    password = ''       # 所登陆的QQ密码
    qqcookie = None     # 登陆成功后保存的cookie

    def __init__(self, QQ, password):
        '''
        构造函数
        :param QQ: 所登陆的QQ
        :param password: 所登陆的QQ密码
        :return:
        '''
        self.QQ = '0' if not QQ else QQ.strip()
        self.password = '' if not password else password


    def execute(self):
        '''
        执行登陆操作
        :return: True:登陆成功; False:登陆失败
        '''
        try:
            self.initCookieEnv()

        except:
            print('登陆QQ [%s] 失败: XHR协议异常' % self.QQ)




    def initCookieEnv(self):
        '''
        初始化登陆用的cookie环境参数.
        主要提取SIG值（cookie属性名为:pt_login_sig）
        :return:
        '''
        params = {
            'proxy_url' : 'https://qzs.qq.com/qzone/v6/portal/proxy.html',
            's_url' : 'https://qzs.qzone.qq.com/qzone/v5/loginsucc.html?para=izone&from=iqq',
            'pt_qr_link' : 'http://z.qzone.com/download.html',
            'self_regurl' : 'https://qzs.qq.com/qzone/v6/reg/index.html',
            'pt_qr_help_link' : 'http://z.qzone.com/download.html',
            'qlogin_auto_login' : '1',
            'low_login' : '0',
            'no_verifyimg' : '1',
            'daid' : '5',
            'appid' : '549000912',
            'hide_title_bar' : '1',
            'style' : '22',
            'target' : 'self',
            'pt_no_auth' : '0',
            'link_target' : 'blank'
        }
        response = requests.get(url=cfg.SIG_URL, params=params)
        set_cookies = response.headers['Set-Cookie']
        self.cookie = QQCookie(set_cookies)

        print('已获得本次登陆的SIG码: %s' % self.cookie.sig)


