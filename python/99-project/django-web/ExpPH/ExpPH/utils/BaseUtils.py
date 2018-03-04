# -*- coding: utf8 -*-
'''
基本工具
Created on 2014年5月14日

@author: Exp
'''


''' 获取系统时间 '''
def getSysTime(format = "%Y-%m-%d %H:%M:%S"):
    import time
    return time.strftime(format)
# End Fun getSysTime()


''' 判断是否为本地运行环境，否则为SAE运行环境 '''
def isLocalEnvironment():
    from os import environ
    return not environ.get("APP_NAME", "")
# End Fun isLocalEnvironment()


''' 加密字符串 '''
def encrypt(plaintext):
    import base64
    return base64.encodestring(plaintext)
# End Fun encrypt()


''' 解密字符串 '''
def decrypt(ciphertext):
    import base64
    return base64.decodestring(ciphertext)
# End Fun decrypt()


''' 简单编码转换，把未知编码的orgStr转码为aimCharset，其中orgStr的源编码由系统自动判断 '''
def simpleTranscoding(orgStr, aimCharset):
    import chardet
    orgCharset = chardet.detect(orgStr)['encoding'] #自动判断编码
    return transcoding(orgStr, orgCharset, aimCharset)
# End Fun simpleTranscoding()


''' 编码转换，把源编码为orgCharset的orgStr，转码为aimCharset '''
def transcoding(orgStr, orgCharset, aimCharset):
    unicodeStr = orgStr.decode(orgCharset)
    return unicodeStr.encode(aimCharset)
# End Fun transcoding()


