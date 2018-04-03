# -*- coding: utf-8 -*-
__author__ = 'EXP (272629724@qq.com)'
__date__ = '2018-03-30 22:53'


import re
from abc import ABCMeta, abstractmethod # python不存在抽象类的概念， 需要引入abc模块实现


class _HttpCookie:
    '''
    HTTP Response 单个 Set-Cookie 容器对象
    '''

    name = ''               # cookie键名
    value = ''              # cookie键值
    DOMAIN = "Domain"       # cookie域名
    domain = ''             # cookie域值
    PATH = "Path"           # cookie路径名
    path = ''               # cookie路径值
    EXPIRE = "Expires"      # cookie有效期名
    expire = ''             # cookie有效期值（英文GMT格式, 如: Thu, 01-Jan-1970 08:00:00 GMT+08:00）
    SECURE = "Secure"       # cookie属性：若出现该关键字表示该cookie只会在HTTPS中进行会话验证
    is_secure = False       # 是否出现了Secure关键字
    HTTPONLY = "HttpOnly"   # cookie属性：若出现该关键字表示该cookie无法被JS等脚本读取, 可防止XSS攻击
    is_http_only = False    # 是否出现了HttpOnly关键字


    def __init__(self, set_cookie) :
        '''
        构造函数：拆解set_cookie中的各个参数
        :param set_cookie: HTTP响应头中的 Set-Cookie, 格式如：JSESSIONID=4F12EEF0E5CC6E8B239906B29919D40E; Domain=www.baidu.com; Path=/; Expires=Mon, 29-Jan-2018 09:08:16 GMT+08:00; Secure; HttpOnly;
        :return: None
        '''
        for idx, kvs in enumerate(set_cookie.split(';')) :
            kv = kvs.split('=')
            size = len(kv)
            if size == 2 :
                key = kv[0].strip()
                val = kv[1].strip()

                if idx == 0 :
                    self.name = key
                    self.value = val

                else:
                    if self.DOMAIN.upper() == key.upper() :
                        self.domain = val

                    elif self.PATH.upper() == key.upper() :
                        self.path = val

                    elif self.EXPIRE.upper() == key.upper() :
                        self.expire = val

            elif size == 1 :
                key = kv[0].strip()

                if self.SECURE.upper() == key.upper() :
                    self.is_secure = True

                elif self.HTTPONLY.upper() == key.upper() :
                    self.is_http_only = True



    def is_vaild(self) :
        '''
        判断当前所解析的Cookie是否有效
        :return: True:有效, False:无效
        '''
        return not not self.name.strip()


    def to_nv(self) :
        '''
        生成该Cookie的名值对.
        在与服务端校验cookie会话时, 只需对name与value属性进行校验, 其他属性无需校验, 保存在本地即可.
        :return: name=value
        '''
        return '%s=%s' % (self.name, self.value) if self.is_vaild() else ''


    def to_header(self) :
        '''
        生成该cookie在Header中的字符串形式.
        :return: 形如：JSESSIONID=4F12EEF0E5CC6E8B239906B29919D40E; Domain=www.baidu.com; Path=/; Expires=Mon, 29-Jan-2018 09:08:16 GMT+08:00; Secure; HttpOnly;
        '''
        return '%(name)s=%(value)s; %(DOMAIN)s=%(domain)s; %(PATH)s=%(path)s; %(EXPIRE)s=%(expire)s; %(SECURE)s %(HTTPONLY)s' % {
            'name' : self.name,
            'value' : self.value,
            'DOMAIN' : self.DOMAIN,
            'domain' : self.domain,
            'PATH' : self.PATH,
            'path' : self.path,
            'EXPIRE' : self.EXPIRE,
            'expire' : self.expire,
            'SECURE' : ('%s;' % self.SECURE) if self.is_secure else '',
            'HTTPONLY' : ('%s;' % self.HTTPONLY) if self.is_http_only else ''
        }





class HttpCookie :
    '''
    HTTP Response 多个 Set-Cookie 容器对象
    '''
    __metaclass__ = ABCMeta # 定义为抽象类

    cookies = []    # 存储多个_HttpCookie的列表


    def __init__(self, set_cookies) :
        '''
        构造函数：拆解set_cookies中的多个set_cookie
        :param set_cookies: HTTP响应头中的 Set-Cookie集合, 使用 ;, 分隔
        :return: None
        '''
        for set_cookie in set_cookies.split(';,') :
            self.add(set_cookie)



    def add(self, set_cookie) :
        cookie = _HttpCookie(set_cookie)
        if cookie.is_vaild() :
            if self.take_cookie_nve(cookie.name, cookie.value, cookie.expire) :
                self.cookies.append(cookie)


    @abstractmethod
    def take_cookie_nve(self, name, value, expire) :
        # TODO in sub class
        return True


    def is_vaild(self) :
        return len(self.cookies) > 0





class QQCookie(HttpCookie) :

    SIG_KEY = 'pt_login_sig'
    sig = ''

    def __init__(self, set_cookies) :
        super(QQCookie, self).__init__(set_cookies)


    def take_cookie_nve(self, name, value, expire):
        is_keep = True
        if self.SIG_KEY.upper() == name.upper() :
            self.sig = value

        return is_keep

