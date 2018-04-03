# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'

import src.main.py.config as cfg
import execjs
import requests

def crawler():
    response = requests.get(url=cfg.SIG_URL)
    response.encoding = 'utf-8' # Header默认是ISO编码，需要转码
    print(response.text)


if __name__ == '__main__':
    crawler()



