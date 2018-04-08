# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


import os
import src.main.py.config as cfg
import src.main.py.utils.des as des
from src.main.py.core.lander import Lander
from src.main.py.core.album import AlbumAnalyzer
from src.main.py.core.mood import MoodAnalyzer



def load_account():
    '''
    加载上次登陆信息
    :return: username, password, QQ
    '''
    username = ''
    password = ''
    QQ = ''

    if os.path.exists(cfg.ACCOUNT_PATH) :
        with open(cfg.ACCOUNT_PATH, 'r') as account:
            lines = account.readlines()
            if len(lines) == 3 :
                username = des.decrypt(lines[0].strip())
                password = des.decrypt(lines[1].strip())
                QQ = des.decrypt(lines[2].strip())

    return username, password, QQ


def save_account(username, password, QQ):
    '''
    保存本次登陆信息
    :param username: 登陆QQ账号
    :param password: 登陆QQ密码
    :param QQ: 爬取数据的目标QQ号
    :return:
    '''
    data = '%s\n%s\n%s\n' % (
        des.encrypt(username),
        des.encrypt(password),
        des.encrypt(QQ)
    )

    with open(cfg.ACCOUNT_PATH, 'w') as account:
        account.write(data)


def crawler():
    '''
    执行QQ空间爬虫
    :return: None
    '''

    # 获取上次登陆信息(若未登陆过则直接修改此处代码)
    username, password, QQ = load_account()
    if not username or not password or not QQ :
        username = '272629724'
        password = 'your QQ password'
        QQ = '987654321'

    print('登陆QQ账号: %s' % username)
    print('登陆QQ密码: %s' % password)
    print('爬取数据的目标QQ号: %s' % QQ)


    # 登陆
    lander = Lander(username, password)
    if lander.execute() == True:

        # 保存登陆信息
        save_account(username, password, QQ)

        # 下载相册
        album = AlbumAnalyzer(lander.cookie, QQ)
        album.execute()

        # 下载说说
        mood = MoodAnalyzer(lander.cookie,QQ)
        mood.execute()


if __name__ == '__main__':
    crawler()

