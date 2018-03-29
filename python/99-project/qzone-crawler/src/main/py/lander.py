# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


import src.main.py.config as cfg



"""
初始化登陆用的Cookie环境参数.
主要提取SIG值（属性名为:pt_login_sig）
"""
def initCookieEnv():
    print(cfg.SIG_URL)



if __name__ == '__main__':
    initCookieEnv()