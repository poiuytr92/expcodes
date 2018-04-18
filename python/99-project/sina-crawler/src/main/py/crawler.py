# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-29 20:17'


import src.main.py.utils.account as account
from src.main.py.core.lander import Lander
from src.main.py.core.album import AlbumAnalyzer
import requests
import src.main.py.utils.xhr as xhr
import src.main.py.config as cfg
import re
import json
from src.main.py.bean.photo import Photo

def remove_emoji(text):
    emoji_pattern = re.compile(
        u'(\ud83d[\ude00-\ude4f])|' # emoticons
        u'(\ud83c[\udf00-\uffff])|' # symbols & pictographs (1 of 2)
        u'(\ud83d[\u0000-\uddff])|' # symbols & pictographs (2 of 2)
        u'(\ud83d[\ude80-\udeff])|' # transport & map symbols
        u'(\ud83c[\udde0-\uddff])|' # flags (iOS)
        u'(\ud83e[\udd00-\uddff])'  # emoji表情字符
        '+', flags=re.UNICODE)
    return emoji_pattern.sub('', text)


def crawler():
    '''
    执行新浪微博爬虫
    :return: None
    '''
    username, password, album_url = account.load()
    if not username or not password or not album_url :
        username = input('请输入 [新浪微博账号] : ').strip()
        password = input('请输入 [新浪微博密码] : ').strip()
        album_url = input('请输入 [爬取的相册专辑地址](如 http://photo.weibo.com/000000/albums?rd=1) : ').strip()

    print('新浪微博账号: %s' % username)
    print('新浪微博密码: %s' % password)
    print('爬取的相册专辑地址: %s' % album_url)

    # 登陆
    lander = Lander(username, password)
    if lander.execute() == True:

        # 保存登陆信息
        account.save(username, password, album_url)

        # 下载相册
        # analyzer = AlbumAnalyzer(lander.cookie, album_url)
        # analyzer.execute()

        url = 'http://photo.weibo.com/photos/get_all?uid=1915189502&album_id=3563507157945853&count=30&page=2&type=3&__rnd=1524022832816'
        response = requests.get(url=url, headers=xhr.get_headers(lander.cookie.to_nv()))
        # response.encoding = cfg.CHARSET_UNICODE
        text = response.text
        with open('./tmp2.dat', 'w') as file:
            file.write(text)






if __name__ == '__main__':
    # crawler()

    with open('./tmp2.dat', 'r') as file:
        text = file.read()
        print(text)
        # print(len(text))
        # text = remove_emoji(text.encode(cfg.CHARSET_UTF8).decode(cfg.CHARSET_UNICODE))
        # print(len(text))
        # # print(text)
        #
        # text = text.encode(cfg.CHARSET_UNICODE).decode(cfg.CHARSET_UNICODE)
        root = json.loads(text, encoding=cfg.CHARSET_UNICODE)
        data = root['data']
        photo_list = data['photo_list']
        for photo in photo_list :
            desc = photo.get('caption_render', '')
            print(desc)
            desc = remove_emoji(desc)

            upload_time = photo.get('updated_at', '')
            pic_host = photo.get('pic_host', '')
            pic_name = photo.get('pic_name', '')
            url = cfg.PHOTO_URL(pic_host, pic_name)
            photo = Photo(desc, upload_time, url)

            print(photo.to_str(True))

