# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-30 22:53'


import time
import re


def get_page_num(total, batch):
    '''
    计算页数
    :param total: 对象总数
    :param batch: 分页对象数
    :return: 页数
    '''
    total = 0 if total < 0 else total
    batch = 1 if batch <= 0 else batch

    page = int(total / batch)
    if total % batch != 0 :
        page += 1
    return page


def get_pic_name(idx, desc):
    '''
    生成图片名称
    :param idx: 图片索引
    :param desc: 图片描述
    :return: 图片名称
    '''
    time.sleep(0.001)   # 保证所生成的文件名时序索引至少相差1ms
    name = '[%d]-[%s] %s' % (int(time.time() * 1000), idx, desc)# 构造文件名
    name = re.sub('[/\\:\*"<>\|\?\r\n\t\0]', '', name)          # 移除无效的文件名字符
    name = '%s...' % name[0:128] if len(name) > 128 else name   # 避免文件名过长
    return '%s.png' % name  # 添加后缀


def get_user_id(http_album_url):
    '''
    从相册专辑地址提取用户ID
    :param album_url: http相册专辑地址, 如： http://photo.weibo.com/000000/albums?rd=1
    :return: 新浪用户ID
    '''
    if http_album_url :
        user_id = re.search('com/(\d+)/albums', http_album_url).group(1)
    else:
        user_id = ''
    return user_id
