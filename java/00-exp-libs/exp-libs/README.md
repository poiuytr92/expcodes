# exp-libs
经验构件库 （[在线Javadoc](https://lyy289065406.github.io/api-online/javadoc/exp-libs/1.0/index.html)）


------


## 环境

![](https://img.shields.io/badge/Platform-Windows|Linux-brightgreen.svg) ![](https://img.shields.io/badge/IDE-Eclipse-brightgreen.svg) ![](https://img.shields.io/badge/Maven-3.2.5-brightgreen.svg) ![](https://img.shields.io/badge/JDK-1.6%2B-brightgreen.svg)

## 简介

此构件库为本人多年编程总结提炼而成，把常用的功能模块作为原子API进行封装；另外也借用了不少出色的第三方构件，在其之上进行二次封装。

过程中尽量确保了低耦合、高性能、强稳健、高复用、更易用等，确保能够满足日常开发需要、提高开发效率。


## 功能模块

| 主模块 | 子模块 | 组件包/类 | 说明 |
|:---:|:---:|:---|:---|
| 常用工具包<br/>[`exp.libs.utils`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils) | 编码工具 | [`exp.libs.utils.encode.Base64`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/Base64.java) | Base64编解码 |
| &nbsp; | &nbsp; | [`exp.libs.utils.encode.CharsetUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CharsetUtils.java) | 字符集编码转换 |
| &nbsp; | &nbsp; | [`exp.libs.utils.encode.CompressUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CompressUtils.java) | 压缩/解压(zip,gzip,tar,bz2) |
| &nbsp; | &nbsp; | [`exp.libs.utils.encode.CryptoUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CryptoUtils.java) | 加解密(MD5,DES,RSA) |
| &nbsp; | 格式转换工具 | [`exp.libs.utils.format.ESCUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/ESCUtils.java) | 数据格式转换(转义字符,BCP,CSV,TSV) |
| &nbsp; | &nbsp; | [`exp.libs.utils.format.JsonUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/JsonUtils.java) | Json数据处理 |
| 二次封装组件<br/>[`exp.libs.warp`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp) | &nbsp; | &nbsp; | &nbsp; |
| 算法包<br/>[`exp.libs.algorithm`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/algorithm) | &nbsp; | &nbsp; | &nbsp; |


## 第三方构件修正记录

xxxxx


## 版权声明

　[![Copyright (C) 2016-2018 By EXP](https://img.shields.io/badge/Copyright%20(C)-2006~2018%20By%20EXP-blue.svg)](http://exp-blog.com)　[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
  

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------
