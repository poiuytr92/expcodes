# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


import time
import base64
import requests
import re
import execjs
import traceback
from urllib.parse import quote_plus
import src.main.py.config as cfg
import src.main.py.utils.xhr as xhr
from src.main.py.bean.cookie import SinaCookie
from PIL import Image


class Lander(object):
    '''
    新浪微博登陆器.
    '''

    username = ''   # 新浪微博账号
    password = ''   # 新浪微博密码
    cookie = None   # 登陆成功后保存的cookie

    def __init__(self, username, password):
        '''
        构造函数
        :param username: 新浪微博账号
        :param password: 新浪微博密码
        :return: None
        '''
        self.username = 'play00002@126.com' if not username else username
        self.password = 'test00002' if not password else password
        self.cookie = SinaCookie()


    def execute(self):
        '''
        执行登陆操作
        :return: True:登陆成功; False:登陆失败
        '''
        is_ok = False
        try:
            pass

        except:
            print('登陆新浪微博账号 [%s] 失败: XHR协议异常' % self.username)
            traceback.print_exc()

        return is_ok


    def to_base64(self, username):
        un = quote_plus(username)       # 对账号进行URL编码 (邮箱账号存在 '@' 等特殊字符需要转移)
        byte = un.encode(cfg.DEFAULT_CHARSET)
        return base64.b64encode(byte)


    def init_cookie_env(self):
        url = 'http://login.sina.com.cn/sso/prelogin.php'
        params = {
            'su' : self.to_base64(self.username),
            'entry' : 'weibo',
            'callback' : 'sinaSSOController.preloginCallBack',
            'rsakt' : 'mod',
            'checkpin' : '1',
            'client' : 'ssologin.js(v1.4.18)',
            '_' : str(int(time.time() * 1000))
        }

