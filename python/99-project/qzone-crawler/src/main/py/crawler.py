# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


from src.main.py.core.lander import Lander


def crawler():
    '''
    执行QQ空间爬虫
    :return: None
    '''

    username = '272629724'
    password = 'xyzxyzxyz'
    QQ = '87654321'
    print('登陆QQ账号: %s' % username)
    print('登陆QQ密码: %s' % password)
    print('爬取数据的目标QQ号: %s' % QQ)

    # 登陆
    lander = Lander(username, password)
    is_ok = lander.execute()


if __name__ == '__main__':
    crawler()
