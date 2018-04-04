# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


from src.main.py.core.lander import Lander


def crawler():
    '''
    执行QQ空间爬虫
    :return: None
    '''

    QQ = '123742030'
    password = 'xxxxxxxx'

    # 登陆
    lander = Lander(QQ, password)
    lander.execute()


if __name__ == '__main__':
    crawler()



