# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'

import src.main.py.config as cfg
import re
import execjs
import requests



def initCookieEnv():
    """
    初始化登陆用的Cookie环境参数.
    主要提取SIG值（属性名为:pt_login_sig）
    """

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
    cookies = response.headers['Set-Cookie']
    for cookie in cookies.split(', '):
        rgx = re.search('pt_login_sig=([^;]+)', cookie)
        sig = rgx.group(1)
        print(sig)
        if(sig != ''):
            break

    print(sig)
    return sig


if __name__ == '__main__':
    initCookieEnv()

