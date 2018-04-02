# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-30 22:53'

import re

class QQCookie:
    '''
    QQCoookie专用解析器
    '''
    sig = ''

    def __init__(self, set_cookies):
        print(set_cookies)
        for cookie in set_cookies.split(', '):  # FIXME 有效期里面也有这个标识
            rgx = re.search('pt_login_sig=([^;]+)', cookie)
            if(rgx == None):
                continue

            sig = rgx.group(1)
            if(sig != ''):
                self.sig = sig
                break