# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'

import os
import execjs
import requests

def crawler():
    response = requests.get(url='https://www.baidu.com/')
    response.encoding = 'utf-8' # Header默认是ISO编码，需要转码
    print(response.text)




if __name__ == '__main__':
    # crawler()
    paths = os.path.dirname(__file__)
    jsPath = paths+"/MD5-RSA.js"
    print(paths)

    jiaMiPasswd = execjs.compile(open(jsPath).read()).call('toRSA', '111', '222', '333', '')
    print(jiaMiPasswd)


