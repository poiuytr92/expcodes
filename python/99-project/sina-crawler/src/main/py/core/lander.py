# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


import time
import base64
import rsa
import binascii
import requests
import json
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
        self.username = '' if not username else username
        self.password = '' if not password else password
        self.cookie = SinaCookie()


    def execute(self):
        '''
        执行登陆操作
        :return: True:登陆成功; False:登陆失败
        '''
        is_ok = False
        try:
            base64_username, servertime, nonce, pubkey, rsakv = self.init_cookie_env()
            rsa_pwd = self.to_rsa(self.password, servertime, nonce, pubkey)
            vcode = self.get_vcode()
            self.login(base64_username, rsa_pwd, vcode, servertime, nonce, rsakv)

        except:
            print('登陆新浪微博账号 [%s] 失败: XHR协议异常' % self.username)
            traceback.print_exc()

        return is_ok


    def init_cookie_env(self):
        '''
        {
          "retcode": 0,
          ="servertime": 1523778685,
          "pcid": "tc-23d819f965f02b2ed448af871b3c294864a6",
          ="nonce": "5GQD1E",
          ="pubkey": "EB2A38568661887FA180BDDB5CABD5F21C7BFD59C090CB2D245A87AC253062882729293E5506350508E7F9AA3BB77F4333231490F915F6D63C55FE2F08A49B353F444AD3993CACC02DB784ABBB8E42A9B1BBFFFB38BE18D78E87A0E41B9B8F73A928EE0CCEE1F6739884B9777E4FE9E88A1BBE495927AC4A799B3181D6442443",
          ="rsakv": "1330428213",
          "is_openlock": 0,
          "showpin": 1,
          "exectime": 53
        }
        :return:
        '''
        base64_username = self.to_base64(self.username)
        url = 'http://login.sina.com.cn/sso/prelogin.php'
        params = {
            'su' : base64_username,
            '_' : str(int(time.time() * 1000)),
            'callback' : 'sinaSSOController.preloginCallBack',
            'client' : 'ssologin.js(v1.4.18)',
            'entry' : 'weibo',
            'rsakt' : 'mod',
            'checkpin' : '1'
        }
        response = requests.get(url, params=params)
        root = json.loads(xhr.to_json(response.text))

        servertime = root.get('servertime', '')
        nonce = root.get('nonce', '')
        pubkey = root.get('pubkey', '')
        rsakv = root.get('rsakv', '')
        return base64_username, servertime, nonce, pubkey, rsakv


    def to_base64(self, username):
        '''

        :param username:
        :return:
        '''
        un = quote_plus(username)       # 对账号进行URL编码 (邮箱账号存在 '@' 等特殊字符需要转移)
        byte = un.encode(cfg.DEFAULT_CHARSET)
        return base64.b64encode(byte)


    def to_rsa(self, password, servertime, nonce, pubkey):
        '''

        :param password:
        :param servertime:
        :param nonce:
        :param pubkey:
        :return:
        '''
        int_pubkey = int(pubkey, 16)
        rsa_pubkey = rsa.PublicKey(int_pubkey, 65537)  # 创建公钥
        data = '%s\t%s\n%s' % (servertime, nonce, password)  # 拼接明文js加密文件中得到
        data = data.encode("utf-8")
        rsa_pwd = rsa.encrypt(data, rsa_pubkey)  # 加密
        return binascii.b2a_hex(rsa_pwd)  # 将加密信息转换为16进制


    def get_vcode(self):
        url = 'https://login.sina.com.cn/cgi/pin.php'
        is_ok, set_cookie = xhr.download_pic(url, {}, {}, cfg.VCODE_PATH)
        if is_ok == True :
            self.cookie.add(set_cookie)

            with Image.open(cfg.VCODE_PATH) as image:
                image.show()
                vcode = input("请输入验证码:").strip()
        else:
            vcode = ''

        return vcode


    def login(self, base64_username, rsa_pwd, vcode, servertime, nonce, rsakv):
        '''

        :return:
        '''
        url = 'https://login.sina.com.cn/sso/login.php'
        params = {
            'entry' : 'weibo',
            'gateway' : '1',
            'from' : '',
            'savestate' : '7',
            'qrcode_flag' : 'false',
            'useticket' : 'useticket',
            'pagerefer' : 'https://login.sina.com.cn/crossdomain2.php?action=logout&r=https%3A%2F%2Fweibo.com%2Flogout.php%3Fbackurl%3D%252F',
            'pcid' : self.cookie.ulogin_img,
            'door' : vcode,
            'vsnf' : '1',
            'su' : base64_username,
            'service' : 'miniblog',
            'servertime' : servertime,
            'nonce' : nonce,
            'pwencode' : 'rsa2',
            'rsakv' : rsakv,
            'sp' : rsa_pwd,
            'sr' : '1366*768',
            'encoding' : 'UTF-8',
            'prelt' : '7873',
            'url' : 'https://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack',
            'returntype' : 'META',
        }
        response = requests.get(url, params=params)
        print(response.text)



if __name__ == '__main__':
    lander = Lander('play00002@126.com', 'test00002')
    lander.execute()