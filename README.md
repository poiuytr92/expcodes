# expcodes
　经验代码库

> *一个代码就是一个脚印，这些脚印就是寻找过程中留下的人生*

------


## 简介

此代码仓库本质上是由多个有代表性的子项目整合而成的超级集索引，这里记录了我自学习编程以来所学所做的冰山一角。

相关的子项目代码目前以 C/C++、Java、Python 为主。<br/>涉及到的领域谈不上包罗万象，但毕竟是我多年来点滴积累的成果，而且大多都通过了生产环境的考验。

承诺不会放弃更新这个仓库，所以包含的子项目必定会越来越多。

但是**如果某些子项目已经足够成熟，会将其抽离成独立项目，以便更好地开源共享**。


## 环境

![](https://img.shields.io/badge/Platform-Windows|Linux-brightgreen.svg) <br/>
![](https://img.shields.io/badge/IDE-VS2010|Eclipse|PyCharm-brightgreen.svg) <br/>
![](https://img.shields.io/badge/C%2B%2B-VC10-brightgreen.svg) ![](https://img.shields.io/badge/JDK-1.6%2B-brightgreen.svg) ![](https://img.shields.io/badge/Python-3.5-brightgreen.svg) <br/>


## 项目相关

- 经验库导航页 : [Navigation-Page](https://lyy289065406.github.io/api-online/)
- 软件授权插件 : [Software-Certificate](https://github.com/lyy289065406/certificate)
- 自动升级插件 : [Auto-Upgrader](https://github.com/lyy289065406/auto-upgrader)
- 环境包下载页 : [Environment-Download](https://lyy289065406.github.io/environment/)


## 核心子项目

- **[exp-libs](https://github.com/lyy289065406/exp-libs)** [**经验构件库**（Java版）] ： *`作为此仓库多个Java项目的基础功能组件包被使用，相关API详见：`[`在线 Javadoc`](https://lyy289065406.github.io/api-online/javadoc/exp-libs/1.1/index.html)* <br/>
- [account-mgr](https://github.com/lyy289065406/account-mgr) [帐密管理工具] ： *`轻松管理大量个人帐密信息（不联网且信息加密）`* <br/>
- [top-baidu-tieba](https://github.com/lyy289065406/top-baidu-tieba) [百度贴吧顶贴机] ： *`指定一个贴吧（最好无吧务）和一个帖子，可实时检测其状态并将其置顶`* <br/>
- [bilibili-plugin](https://github.com/lyy289065406/bilibili-plugin) [哔哩哔哩插件姬] ： *`多号自动挂机(抽奖/宝箱/日常/投喂/成就)，自动答谢、晚安、公告等数十种功能`* <br/>
- [jzone-crawler](https://github.com/lyy289065406/jzone-crawler) [QQ空间爬虫（Java版）] ： *`可爬取QQ相册和说说的图文数据并进行归整`* <br/>
- [pyzone-crawler](https://github.com/lyy289065406/pyzone-crawler) [QQ空间爬虫（Python版）] ： *`可爬取QQ相册和说说的图文数据并进行归整`* <br/>
- [sina-crawler](https://github.com/lyy289065406/sina-crawler) [新浪微博爬虫] ： *`可爬取新浪微博相册的图文数据并进行归整`* <br/>
- [mojo-archetype](https://github.com/lyy289065406/mojo-archetype) [Maven项目规范骨架] ： <br/>　　　　*`骨架中默认已配置： `[`经验构件库`](#exp-libs)`、混淆打包插件、`[`Maven项目发布插件`](#mojo-release-plugin)`、基线发布插件`* <br/>
- [mojo-web-archetype](https://github.com/lyy289065406/mojo-web-archetype) ) [Maven项目规范骨架（Web版）] ： <br/>　　　　*`骨架中默认已配置： `[`经验构件库`](#exp-libs)`、混淆打包插件、`[`Maven项目发布插件`](#mojo-release-plugin)`、基线发布插件`* <br/>
- [mojo-release-plugin](https://github.com/lyy289065406/mojo-release-plugin) [Maven项目发布插件] ： <br/>　　　　*`支持一键发布，自动组织并整合到项目：Jar（支持混淆打包）、配置、文档、版本、启动/停止脚本等`* <br/>
- [tensorflow-captcha-train](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-captcha-train) [任意验证码图片训练] ： <br/>　　　　*`本人在`[`tensorflow-captcha-demo`](#tensorflow-captcha-demo)`基础上改进，可训练任意大小的图片验证码`* <br/>
- [ACM](https://github.com/lyy289065406/expcodes/tree/master/c/02-algorithm/ACM) [ACM-POJ训练（C/C++版）] ： <br/>　　　　*`本人大学期间用于参加ACM竞赛的训练题目源码，解题报告索引详见：《`[`北大ACM – POJ试题分类`](http://exp-blog.com/2018/06/28/pid-38/)`》`* <br/>


## 全子项目索引

[root](https://github.com/lyy289065406/expcodes) <br/>
　┣ ━ ━ ━ [java](https://github.com/lyy289065406/expcodes/tree/master/java) <br/>
　┃　　　　┣ ━ ━ ━ [00-exp-libs](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs)：*经验库* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ <a name="exp-libs" href="https://github.com/lyy289065406/exp-libs">exp-libs</a>：*经验构件库 `（作为此仓库多个Java项目的基础功能组件包被使用）`* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`多年经验积累整理的常用组件API库，适用于敏捷开发：`[`在线 Javadoc`](https://lyy289065406.github.io/api-online/javadoc/exp-libs/1.1/index.html)* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [jvm-agent](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/jvm-agent)：*JVM代理构件* <br/>
　┃　　　　┃　　　　　　　　└ ─ *`用于授权虚拟机级别的开发功能（如虚拟机监控、Java类操作等）`* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [01-framework](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework)：*架构/框架* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [mojo-archetype](https://github.com/lyy289065406/mojo-archetype)：*Maven项目规范骨架* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`骨架中默认已配置： `[`经验构件库`](#exp-libs)`、混淆打包插件、`[`Maven项目发布插件`](#mojo-release-plugin)`、基线发布插件`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [mojo-web-archetype](https://github.com/lyy289065406/mojo-web-archetype)：*Maven项目规范骨架（Web版）* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`骨架中默认已配置： `[`经验构件库`](#exp-libs)`、混淆打包插件、`[`Maven项目发布插件`](#mojo-release-plugin)`、基线发布插件`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [zookeeper-demo](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/zookeeper-demo)：*入门示例代码* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`配套博文：《`[`快速部署单机zookeeper集群（win环境）`](http://exp-blog.com/2018/08/03/pid-2173/)`》`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [kafka-demo](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/kafka-demo)：*入门示例代码* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`配套博文：《`[`快速部署单机kafka集群（win环境）`](http://exp-blog.com/2018/08/03/pid-2187/)`》`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [dubbo-demo](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/dubbo-demo)：*入门示例代码* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [thrift-demo](https://github.com/lyy289065406/expcodes/tree/master/java/01-framework/thrift-demo)：*入门示例代码* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [02-pattern](https://github.com/lyy289065406/expcodes/tree/master/java/02-pattern)：*设计模式* <br/>
　┃　　　　┃　　　　　└ ─ *`《 Head First : 设计模式 》（23种设计模式实例与习题）`* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [03-plugin](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin)：*插件* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ <a name="mojo-release-plugin" href="https://github.com/lyy289065406/mojo-release-plugin">mojo-release-plugin</a>：*Maven项目发布插件* <br/>
　┃　　　　┃　　　　　　　　└ ─ *`支持一键发布项目，自动组织：Jar（支持混淆）、配置、文档、版本、启动/停止脚本`* <br/>
　┃　　　　┃<br/>
　┃　　　　┗ ━ ━ ━ [99-project](https://github.com/lyy289065406/expcodes/tree/master/java/99-project)：*项目* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [account-mgr](https://github.com/lyy289065406/account-mgr)：*帐密管理工具 （含Swing界面）* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`轻松管理大量个人帐密信息（不联网、信息加密）`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [top-baidu-tieba](https://github.com/lyy289065406/top-baidu-tieba)：*百度贴吧顶贴机* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`指定一个贴吧（最好无吧务）和一个帖子，可实时检测其状态并将其置顶`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [bilibili-plugin](https://github.com/lyy289065406/bilibili-plugin)：*哔哩哔哩插件姬 （含Swing界面）* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`多号自动挂机(抽奖/宝箱/日常/投喂/成就)，自动答谢、晚安、公告等数十种功能`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ <a name="dynamic-token" href="https://github.com/lyy289065406/expcodes/tree/master/java/99-project/dynamic-token">dynamic-token</a>：*动态令牌生成/校验API* <br/>
　┃　　　　　　　　　┃　　　├ ─ *`仿 QQ等登陆令牌 实现的 API构件，生成或校验在误差时间范围内有效的动态令牌`* <br/>
　┃　　　　　　　　　┃　　　├ ─ *`此项目仅对外提供接口能力，需配合另一个C++的动态链接库项目 (`[`dt_otp`](#dt_otp)`) 使用`* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`配套博文：《`[`嵌入式开发学习笔记 ( java – c/c++：从入门到入门 )`](http://exp-blog.com/2018/07/25/pid-2090/)`》`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [exp-xml-paper](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/exp-xml-paper)：*XML文本编辑器 （含Swing界面）* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`用于编辑项目中的xml配置文件，更效率地增删改配置项`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [file-port-forwarding](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/file-port-forwarding)：*双机文件流端口转发程序* <br/>
　┃　　　　　　　　　┃　　　├ ─ *`适用场景（如宿主虚拟机）：机器A在内网、机器B在外网，AB间被限制网络、仅能共享文件`* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`通过文本流方式在两台机器之间搭建端口转发，实现内外网跨越访问`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [github-tools](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/github-tools)：*Github工具包* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`当前功能：(1)提交Github项目前填充空文件夹；(2)自动修正DNS实现国内加速访问Github`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [goas](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/goas)：*政府在线自动化办公系统 （含BS界面）* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`本人大四时在实训基地做的 Java-web 协同项目，通过 struct 实现的一个简单 OA系统`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [pssms](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/pssms)：*进销存管理系统 （含Swing界面）* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`本人大三时的课程设计协同项目，具备角色权限、仓管、统计等功能的进销存管理系统`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [P2P-file-sharing-system](https://github.com/lyy289065406/expcodes/tree/master/java/99-project/P2P-file-sharing-system)：*P2P文件共享系统 （含Swing界面）* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`本人的其中一个毕业设计项目，参照P2P原理实现的文件共享工具`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [jzone-crawler](https://github.com/lyy289065406/jzone-crawler)：*QQ空间爬虫 （含Swing界面）* <br/>
　┃　　　　　　　　　┃　　　├ ─ *`可爬取QQ相册和说说的图文数据并进行归整`* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`含 phantomjs仿真 与 纯http协议 两种实现方式`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┗ ━ [ui-regex-debug](https://github.com/lyy289065406/expcodes/tree/master/java/99-project)：*正则测试工具 （含Swing界面）* <br/>
　┃　　　　　　　　　　　　　└ ─ *`用于测试正则表达式的可用性，另有功能相似的 `[`在线版本`](https://lyy289065406.github.io/site-package/tool/regex/index.html)* <br/>
　┃<br/>
　┣ ━ ━ ━ [python](https://github.com/lyy289065406/expcodes/tree/master/python) <br/>
　┃　　　　┣ ━ ━ ━ [00-exp-libs](https://github.com/lyy289065406/expcodes/tree/master/python/00-exp-libs)：*经验库* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [01-deep-learning](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning)：*深度学习* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [tensorflow-animal](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-animal)：*动物图像识别训练* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`由第三方共享的图像识别训练的参考代码，本人仅对其梳理`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ <a name="tensorflow-captcha-demo" href="https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-captcha-demo">tensorflow-captcha-demo</a>：*验证码图片识别训练-示例代码* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`由第三方共享的图像识别训练的参考代码，本人仅对其梳理`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [tensorflow-captcha-train](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-captcha-train)：*任意验证码图片识别训练* <br/>
　┃　　　　┃　　　　┃　　　├ ─ *`本人在`[`tensorflow-captcha-demo`](#tensorflow-captcha-demo)`基础上改进，可训练任意大小的验证码`* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`备：在`[`Java版的经验构件库(exp-libs)`](#exp-libs)`中已封装了针对tensorflow训练库的加载和使用的API`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┣ ━ [tensorflow-digit-train](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-digit-train)：*数字图片识别训练* <br/>
　┃　　　　┃　　　　┃　　　└ ─ *`由第三方共享的图像识别训练的参考代码，本人仅对其梳理`* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [tensorflow-mnist](https://github.com/lyy289065406/expcodes/tree/master/python/01-deep-learning/tensorflow-mnist)：*MNIST* <br/>
　┃　　　　┃　　　　　　　　└ ─ *`机器学习的入门样例代码，由第三方共享，本人仅对其梳理`* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [02-algorithm](https://github.com/lyy289065406/expcodes/tree/master/python/02-algorithm)：*算法* <br/>
　┃　　　　┃　　　　┃<br/>
　┃　　　　┃　　　　┗ ━ [ACM](https://github.com/lyy289065406/expcodes/tree/master/python/02-algorithm/ACM)：*ACM-OJ训练* <br/>
　┃　　　　┃　　　　　　　　├ ─ *`本人大学期间用于参加ACM竞赛的训练题目源码`* <br/>
　┃　　　　┃　　　　　　　　└ ─ *`配套博文：《`[`北大ACM – POJ试题分类`](http://exp-blog.com/2018/06/28/pid-38/)`》`* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [03-lovely-python](https://github.com/lyy289065406/expcodes/tree/master/python/03-lovely-python) <br/>
　┃　　　　┃　　　　　└ ─ *`《可爱的python》（课后习题）`* <br/>
　┃　　　　┃<br/>
　┃　　　　┣ ━ ━ ━ [04-HeadFirst](https://github.com/lyy289065406/expcodes/tree/master/python/04-HeadFirst) <br/>
　┃　　　　┃　　　　　└ ─ *`《 Head First : Python 》（课后习题）`* <br/>
　┃　　　　┃<br/>
　┃　　　　┗ ━ ━ ━ [99-project](https://github.com/lyy289065406/expcodes/tree/master/python/99-project)：*项目* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [django-web](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/django-web)：*django入门-CMS网站 `（已废弃）`* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`尝试用 django 搭建个人网站时写的一个简单 CMS`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [pyzone-crawler](https://github.com/lyy289065406/pyzone-crawler)：*QQ空间爬虫* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`可爬取QQ相册和说说的图文数据并进行归整`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┣ ━ [sina-crawler](https://github.com/lyy289065406/sina-crawler)：*新浪微博爬虫* <br/>
　┃　　　　　　　　　┃　　　└ ─ *`可爬取新浪微博相册的图文数据并进行归整`* <br/>
　┃　　　　　　　　　┃<br/>
　┃　　　　　　　　　┗ ━ [wx-exp-backstage](https://github.com/lyy289065406/expcodes/tree/master/python/99-project/wx-exp-backstage)：*微信公众平台后台 `（已废弃）`* <br/>
　┃　　　　　　　　　　　　　└ ─ *`使用 python + PHP 实现微信订阅号后台的部分 API 功能`* <br/>
　┃<br/>
　┗ ━ ━ ━ [C/C++](https://github.com/lyy289065406/expcodes/tree/master/c) <br/>
　　　　　　┣ ━ ━ ━ [00-exp-libs](https://github.com/lyy289065406/expcodes/tree/master/c/00-exp-libs/00-exp-libs)：*经验库* <br/>
　　　　　　┃　　　　┃<br/>
　　　　　　┃　　　　┗ ━ [exp-libs](https://github.com/lyy289065406/expcodes/tree/master/c/00-exp-libs)：*经验构件库 `（完善中）`* <br/>
　　　　　　┃　　　　　　　　└ ─ *`本质是一个 DLL动态链接库，意图封装为常用组件 API库，以用于敏捷开发`* <br/>
　　　　　　┃<br/>
　　　　　　┣ ━ ━ ━ [01-reverse-engineering](https://github.com/lyy289065406/expcodes/tree/master/c/01-reverse-engineering)：*逆向工程* <br/>
　　　　　　┃　　　　　├ ─ *`入门逆向工程开发的一些相关代码`* <br/>
　　　　　　┃　　　　　├ ─ *`配套博文：《`[`WINDOWS内核学习顺序指引清单`](http://exp-blog.com/2018/07/26/pid-2124/)`》`* <br/>
　　　　　　┃　　　　　├ ─ *`配套博文：《`[`驱动开发入门-之一：Win7 SP1 x64 驱动开发环境搭建`](http://exp-blog.com/2018/07/26/pid-2137/)`》`* <br/>
　　　　　　┃　　　　　└ ─ *`配套博文：《`[`驱动开发入门-之二：Win7x64 + VMWare(Win7x64) + WinDbg 双机调试环境搭建`](http://exp-blog.com/2018/07/26/pid-2146/)`》`* <br/>
　　　　　　┃<br/>
　　　　　　┣ ━ ━ ━ [02-algorithm](https://github.com/lyy289065406/expcodes/tree/master/c/02-algorithm)：*算法* <br/>
　　　　　　┃　　　　┃<br/>
　　　　　　┃　　　　┗ ━ [ACM](https://github.com/lyy289065406/expcodes/tree/master/c/02-algorithm/ACM)：*ACM-POJ训练* <br/>
　　　　　　┃　　　　　　　　├ ─ *`本人大学期间用于参加ACM竞赛的训练题目源码`* <br/>
　　　　　　┃　　　　　　　　└ ─ *`配套博文：《`[`北大ACM – POJ试题分类`](http://exp-blog.com/2018/06/28/pid-38/)`》`* <br/>
　　　　　　┃<br/>
　　　　　　┣ ━ ━ ━ [03-The C++ Programming Language](https://github.com/lyy289065406/expcodes/tree/master/c/03-The%20C%2B%2B%20Programming%20Language) <br/>
　　　　　　┃　　　　　└ ─ *`《 C++程序设计语言-十周年纪念版 》（课后习题）`* <br/>
　　　　　　┃<br/>
　　　　　　┗ ━ ━ ━ [99-project](https://github.com/lyy289065406/expcodes/tree/master/c/99-project)：*项目* <br/>
　　　　　　　　　　　┃<br/>
　　　　　　　　　　　┗ ━ <a name="dt_otp" href="https://github.com/lyy289065406/expcodes/tree/master/c/99-project/dt_otp">dt_otp</a>：*动态令牌-dll/so实现库* <br/>
　　　　　　　　　　　　　　　├ ─ *`仿 QQ等登陆令牌 实现，可生成或校验在误差时间范围内有效的动态令牌`* <br/>
　　　　　　　　　　　　　　　├ ─ *`本质是 DLL动态链接库, 可在 win/linux 环境编译成 32/64 位（已内置 make 脚本）`* <br/>
　　　　　　　　　　　　　　　├ ─ *`此项目仅实现核心功能，需配合另一个Java项目 (`[`dynamic-token`](#dynamic-token)`) 对外提供接口能力使用`* <br/>
　　　　　　　　　　　　　　　└ ─ *`配套博文：《`[`嵌入式开发学习笔记 ( java – c/c++：从入门到入门 )`](http://exp-blog.com/2018/07/25/pid-2090/)`》`* <br/>


## 版权声明

　[![Copyright (C) 2016-2019 By EXP](https://img.shields.io/badge/Copyright%20(C)-2016~2019%20By%20EXP-blue.svg)](http://exp-blog.com)　[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------
