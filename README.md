# expcodes
　经验代码库

------


## 简介

<font color="red">编辑中</font>


## 环境

![](https://img.shields.io/badge/Platform-Windows|Linux-brightgreen.svg) <br/>
![](https://img.shields.io/badge/IDE-VS2010|Eclipse|PyCharm-brightgreen.svg) <br/>
![](https://img.shields.io/badge/C%2B%2B-VC10-brightgreen.svg) ![](https://img.shields.io/badge/JDK-1.6%2B-brightgreen.svg) ![](https://img.shields.io/badge/Python-3.5-brightgreen.svg) <br/>


## 项目相关

- 经验库导航页 : [Navigation-Page](https://lyy289065406.github.io/api-online/)
- 软件授权插件 : [Software-Certificate](https://github.com/lyy289065406/certificate)
- 自动升级插件 : [Auto-Upgrader](https://github.com/lyy289065406/auto-upgrader)


## 核心子项目

- [ACM](https://github.com/lyy289065406/expcodes/tree/master/c/02-algorithm/ACM) [ACM-POJ训练（C/C++版）]
- [mojo-archetype](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/mojo-archetype) [Maven项目规范骨架]
- [mojo-web-archetype](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/mojo-web-archetype) [Maven项目规范骨架（Web版）]
- [mojo-release-plugin](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin/mojo-release-plugin) [Maven项目发布插件]
- [exp-libs](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs) [经验库构件（Java版）] ： `此项目被本站多数项目调用`
- [account-mgr](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/account-mgr) [帐密管理工具]
- [top-baidu-tieba](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/top-baidu-tieba) [百度贴吧顶贴机]
- [bilibili-plugin](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/bilibili-plugin) [哔哩哔哩插件姬]
- [qzone-crawler](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/qzone-crawler) [QQ空间爬虫（Java版）]
- [qzone-crawler](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/qzone-crawler) [QQ空间爬虫（Python版）]
- [sina-crawler](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/sina-crawler) [新浪微博爬虫]
- [tensorflow-captcha-train](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-captcha-train) [任意验证码图片训练]
 

## 全子项目索引

[root](https://github.com/lyy289065406/expcodes) <br/>
　┣ ━ ━ ━ [java](https://github.com/lyy289065406/expcodes/tree/master/java) <br/>
　┃　　　　┣ ━ ━ ━ [00-exp-libs](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs)：*经验库* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [exp-libs](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs)：*经验库构件 `（被本站多数项目调用）`* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`多年经验积累整理的常用组件API库，适用于敏捷开发：`[在线 Javadoc](https://lyy289065406.github.io/api-online/javadoc/exp-libs/1.0/index.html)* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [exp-libs-base](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs-base)：*经验库构件（基础包）* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`exp-libs 的子项目，过滤了一些相对少用的第三方构件`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [jvm-agent](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/jvm-agent)：*JVM代理构件* <br/>
　┃　　　　┃　　　　　　　　└ ─ *用于授权虚拟机级别的开发功能（如虚拟机监控、Java类操作等）* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [01-framework](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework)：*架构/框架* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [mojo-archetype](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/mojo-archetype)：*Maven项目规范骨架* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *默认已配置： [经验库构件](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs)、混淆打包插件、[Maven项目发布插件](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin/mojo-release-plugin)、基线发布插件* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [mojo-web-archetype](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/mojo-web-archetype)：*Maven项目规范骨架（Web版）* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *默认已配置： [经验库构件](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs)、混淆打包插件、[Maven项目发布插件](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin/mojo-release-plugin)、基线发布插件* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [zookeeper-demo](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/zookeeper-demo)：*入门示例代码* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *配套博文：《[快速部署单机zookeeper集群（win环境）](http://exp-blog.com/2018/08/03/pid-2173/)》* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [kafka-demo](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/kafka-demo)：*入门示例代码* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *配套博文：《[快速部署单机kafka集群（win环境）](http://exp-blog.com/2018/08/03/pid-2187/)》* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [thrift](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/thrift)：*入门示例代码* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [02-pattern](https://github.com/lyy289065406/expcodes/tree/master/java/02-pattern)：*设计模式* <br/>
　┃　　　　┃　　　　　└ ─ *《Head First : 设计模式》（课后习题）* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [03-plugin](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin)：*插件* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [mojo-release-plugin](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin/mojo-release-plugin)：*Maven项目发布插件* <br/>
　┃　　　　┃　　　　　　　　└ ─ *支持一键发布项目，自动组织：Jar（支持混淆）、配置、文档、版本、启动/停止脚本* <br/>
　┃　　　　┃<br/>
　┃　　　　┗ ━ ━ ━ [99-project](https://github.com/lyy289065406/expcodes/tree/master/java/99-project)：*项目* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [account-mgr](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/account-mgr)：*帐密管理工具* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [top-baidu-tieba](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/top-baidu-tieba)：*百度贴吧顶贴机* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [bilibili-plugin](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/bilibili-plugin)：*哔哩哔哩插件姬* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [dynamic-token](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/dynamic-token)：*嵌入式：动态令牌API* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [exp-xml-paper](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/exp-xml-paper)：*XML文本编辑器* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [file-port-forwarding](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/file-port-forwarding)：*双机文件流端口转发程序* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [github-tools](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/github-tools)：*Github工具包* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [goas](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/goas)：*政府在线自动化办公系统* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [pssms](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/pssms)：*进销存管理系统* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [P2P-file-sharing-system](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/P2P-file-sharing-system)：*P2P文件共享系统* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [qzone-crawler](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/qzone-crawler)：*QQ空间爬虫* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┗ ━ [ui-regex-debug](https://github.com/lyy289065406/expcodes/tree/master/java/99-project)：*正则测试工具（UI版）* <br/>
　┃　　　　　　　　　　　　　└ ─ *xxx* <br/>
　┃<br/>
　┣ ━ ━ ━ [python](https://github.com/lyy289065406/expcodes/tree/master/python) <br/>
　┃　　　　┣ ━ ━ ━ [00-exp-libs](https://github.com/lyy289065406/expcodes/tree/master/python/00-exp-libs)：*经验库* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [01-deep-learning](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning)：*深度学习* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [tensorflow-animal](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-animal)：*图像识别训练-动物识别* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [tensorflow-captcha-demo](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-captcha-demo)：*验证码识别训练-示例代码* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [tensorflow-captcha-train](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-captcha-train)：*任意验证码图片训练* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [tensorflow-digit-train](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-digit-train)：*数字图片识别训练* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [tensorflow-mnist](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-mnist)：*机器学习入门-MNIST* <br/>
　┃　　　　┃　　　　　　　　└ ─ *xxx* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [02-algorithm](https://github.com/lyy289065406/expcodes/tree/master/python/02-algorithm)：*算法* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [ACM](https://github.com/lyy289065406/expcodes/tree/master/python/02-algorithm/ACM)：*ACM-OJ训练* <br/>
　┃　　　　┃　　　　　　　　└ ─ *配套博文：《[北大ACM – POJ试题分类](http://exp-blog.com/2018/06/28/pid-38/)》* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [03-lovely-python](https://github.com/lyy289065406/expcodes/tree/master/python/03-lovely-python) <br/>
　┃　　　　┃　　　　　└ ─ *《可爱的python》（课后习题）* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [04-HeadFirst](https://github.com/lyy289065406/expcodes/tree/master/python/04-HeadFirst) <br/>
　┃　　　　┃　　　　　└ ─ *《Head First : Python》（课后习题）* <br/>
　┃　　　　┃<br/>
　┃　　　　┗ ━ ━ ━ [99-project](https://github.com/lyy289065406/expcodes/tree/master/python/99-project)：*项目* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [django-web](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/django-web)：*django入门-CMS网站* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [qzone-crawler](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/qzone-crawler)：*QQ空间爬虫* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [sina-crawler](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/sina-crawler)：*新浪微博爬虫* <br/>
　┃　　　　　　　　　┃　　　└ ─ *xxx* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┗ ━ [wx-exp-backstage](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/wx-exp-backstage)：*微信公众平台后台* <br/>
　┃　　　　　　　　　　　　　└ ─ *xxx* <br/>
　┃<br/>
　┗ ━ ━ ━ [C/C++](https://github.com/lyy289065406/expcodes/tree/master/c) <br/>
　　　　　　┣ ━ ━ ━ [00-exp-libs](https://github.com/lyy289065406/expcodes/tree/master/c/00-exp-libs/00-exp-libs)：*经验库* <br/>
　　　　　　┃　　　　┃<br/>
　　　　　　┃　　　　┗ ━ [exp-libs](https://github.com/lyy289065406/expcodes/tree/master/c/00-exp-libs)：*经验库构件* <br/>
　　　　　　┃　　　　　　　　└ ─ *DLL动态链接库* <br/>
　　　　　　┃<br/>
　　　　　　┣ ━ ━ ━ [01-reverse-engineering](https://github.com/lyy289065406/expcodes/tree/master/c/01-reverse-engineering)：*逆向工程* <br/>
　　　　　　┃<br/>
　　　　　　┣ ━ ━ ━ [02-algorithm](https://github.com/lyy289065406/expcodes/tree/master/c/02-algorithm)：*算法* <br/>
　　　　　　┃　　　　┃<br/>
　　　　　　┃　　　　┗ ━ [ACM](https://github.com/lyy289065406/expcodes/tree/master/c/02-algorithm/ACM)：*ACM-POJ训练* <br/>
　　　　　　┃　　　　　　　　└ ─ *配套博文：《[北大ACM – POJ试题分类](http://exp-blog.com/2018/06/28/pid-38/)》* <br/>
　　　　　　┃<br/>
　　　　　　┣ ━ ━ ━ [03-The C++ Programming Language](https://github.com/lyy289065406/expcodes/tree/master/c/03-The%20C%2B%2B%20Programming%20Language) <br/>
　　　　　　┃　　　　　└ ─ *《C++程序设计语言-十周年纪念版》（课后习题）* <br/>
　　　　　　┃<br/>
　　　　　　┗ ━ ━ ━ [99-project](https://github.com/lyy289065406/expcodes/tree/master/c/99-project)：*项目* <br/>
　　　　　　　　　　　┃<br/>
　　　　　　　　　　　┗ ━ [dt_otp](https://github.com/lyy289065406/expcodes/tree/master/c/99-project/dt_otp)：*嵌入式：动态令牌-动态链接库* <br/>
　　　　　　　　　　　　　　　└ ─ *xxx* <br/>


## 版权声明

　Copyright (C) 2016-2018 By EXP

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------
