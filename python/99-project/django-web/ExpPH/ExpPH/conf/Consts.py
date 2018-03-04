# -*- coding: utf8 -*-
'''
常量表
Created on 2014年5月17日

@author: Exp
'''

PRJ_NAME = 'ExpPH'  # 项目名称
SAE_ROOT_URL = 'http://expph.sinaapp.com/' # SAE根URL地址
LOCAL_ROOT_URL = 'http://127.0.0.1/'       # 本地 根URL地址

CHARSET_DEFAULT = 'utf8'    # 默认编码
CHARSET_PRGM = 'utf8'       # 程序编码
CHAARSET_DB = 'utf8'        # 数据库编码
CHARSET_MAIL = 'gbk'        # 邮件编码
CHAARSET_WEB = 'gbk'        # 网页编码

ADMIN_USER = 'ExpPH'                # admin后台用户名
ADMIN_PSWD = 'RXhwMTIzNDU2UEg='     # admin后台密码
ADMIN_MAIL = 'ExpertPH@126.com'     # admin后台邮箱

DB_TYPE_MYSQL = 'mysql'
DB_TYPE_SQLITE = 'sqlite3'
DB_TYPE_ORACLE = 'oracle'

DB_ENGINE_MYSQL = "%s%s" % ('django.db.backends.', DB_TYPE_MYSQL)     # mysql数据库引擎
DB_ENGINE_SQLITE = "%s%s" % ('django.db.backends.', DB_TYPE_SQLITE)   # sqlite3数据库引擎


