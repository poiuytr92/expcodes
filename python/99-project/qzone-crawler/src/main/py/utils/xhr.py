# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


import requests


def get_headers(nv_cookies=''):
    '''
    获取HTTP请求头(主要设置HTTP代理, 避免被反爬)
    :return: HTTP请求头
    '''
    headers = {
        'Accept' : 'image/webp,image/*,*/*;q=0.8',
        'Accept-Encoding' : 'gzip, deflate, sdch',
        'Accept-Language' : 'zh-CN,zh;q=0.8,en;q=0.6',
        'Connection' : 'keep-alive',
        'Cookie' : nv_cookies,
        'User-Agent' : 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36'
    }
    return headers



def add_response_cookies(qqcookie, response):
    '''
    把HTTP响应头中的Set-Cookie添加到QQCookie中
    :param qqcookie: QQCookie对象
    :param response: HTTP的响应对象
    :return:
    '''
    qqcookie.adds(response.headers['Set-Cookie'])


def download_pic(pic_url, headers, params, save_path):
    '''
    下载图片
    :param pic_url: 图片URL
    :param save_path: 图片保存路径
    :return: HTTP响应cookie
    '''
    response = requests.get(pic_url, headers=headers, params=params, stream=True)
    if response.status_code == 200:
        with open(save_path, 'wb') as pic:
            for chunk in response:
                pic.write(chunk)
        set_cookie = response.headers['Set-Cookie']
    else:
        set_cookie = ''

    return set_cookie

