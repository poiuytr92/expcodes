# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


import src.main.py.utils.account as account
from src.main.py.core.lander import Lander


def crawler():
    '''
    执行新浪微博爬虫
    :return: None
    '''
    username, password, url = account.load()
    if not username or not password or not QQ :
        username = input('请输入 [新浪微博账号] : ').strip()
        password = input('请输入 [新浪微博密码] : ').strip()
        url = input('请输入 [爬取的相册专辑地址] : ').strip()

    print('新浪微博账号: %s' % username)
    print('新浪微博密码: %s' % password)
    print('爬取的相册专辑地址: %s' % url)

    # 登陆
    lander = Lander(username, password)
    if lander.execute() == True:

        # 保存登陆信息
        account.save(username, password, QQ)



if __name__ == '__main__':
    crawler()

