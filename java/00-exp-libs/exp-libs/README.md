# exp-libs
经验构件库 （[在线Javadoc](https://lyy289065406.github.io/api-online/javadoc/exp-libs/1.0/index.html)）


------


## 环境

![](https://img.shields.io/badge/Platform-Windows|Linux-brightgreen.svg) ![](https://img.shields.io/badge/IDE-Eclipse-brightgreen.svg) ![](https://img.shields.io/badge/Maven-3.2.5-brightgreen.svg) ![](https://img.shields.io/badge/JDK-1.6%2B-brightgreen.svg)

## 简介

此构件库为本人多年编程总结提炼而成，把常用的功能模块作为原子API进行封装；另外也借用了不少出色的第三方构件，在其之上进行二次封装。

过程中尽量确保了低耦合、高性能、强稳健、高复用、更易用等，确保能够满足日常开发需要、提高开发效率。


## 功能模块


| 主模块 | 子模块 | 组件包/类 | 说明 | 测试/<br/>示例 |
|:---:|:---:|:---:|:---|:---:|
| **常用工具包**<br/>[`exp.libs.utils`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils) | 编码工具<br/>[`encode`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode) | [`Base64`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/Base64.java) | Base64编解码 | &nbsp; |
| &nbsp; | &nbsp; | [`CharsetUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CharsetUtils.java) | 字符集编码转换 | &nbsp; |
| &nbsp; | &nbsp; | [`CompressUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CompressUtils.java) | 压缩/解压(zip,gzip,tar,bz2) | &nbsp; |
| &nbsp; | &nbsp; | [`CryptoUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CryptoUtils.java) | 加解密(MD5,DES,RSA) | &nbsp; |
| &nbsp; | &nbsp; | [`TXTUtils`]() | 任意文件与txt文件互转 | &nbsp; |
| &nbsp; | 格式转换工具<br/>[`format`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format) | [`ESCUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/ESCUtils.java) | 数据格式转换(转义字符,BCP,CSV,TSV) | &nbsp; |
| &nbsp; | &nbsp; | [`JsonUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/JsonUtils.java) | JSON数据处理 | &nbsp; |
| &nbsp; | &nbsp; | [`XmlUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/XmlUtils.java) | XML数据处理 | &nbsp; |
| &nbsp; | &nbsp; | [`StandardUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/StandardUtils.java) | 标准化处理 | &nbsp; |
| &nbsp; | 图像工具<br/>[`img`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/img) | [`ImageUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/img/ImageUtils.java) | 图像处理 | &nbsp; |
| &nbsp; | &nbsp; | [`QRCodeUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/img/QRCodeUtils.java) | 二维码生成/解析 | &nbsp; |
| &nbsp; | 读写工具<br/>[`io`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io) | [`FileUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io/FileUtils.java) | 文件处理 | &nbsp; |
| &nbsp; | &nbsp; | [`IOUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io/IOUtils.java) | IO流处理 | &nbsp; |
| &nbsp; | &nbsp; | [`JarUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io/JarUtils.java) | Jar文件处理 | &nbsp; |
| &nbsp; | 数值工具<br/>[`num`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num) | [`BODHUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/BODHUtils.java) | 进制处理 | &nbsp; |
| &nbsp; | &nbsp; | [`IDUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/IDUtils.java) | 唯一性ID生成器 | &nbsp; |
| &nbsp; | &nbsp; | [`NumUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/NumUtils.java) | 数值处理 | &nbsp; |
| &nbsp; | &nbsp; | [`UnitUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/UnitUtils.java) | 单位转换 | &nbsp; |
| &nbsp; | 系统工具<br/>[`os`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os) | [`ExitUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/ExitUtils.java) | 程序终止控制 | &nbsp; |
| &nbsp; | &nbsp; | [`JavaUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/JavaUtils.java) | Java代码处理 | &nbsp; |
| &nbsp; | &nbsp; | [`OSUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/OSUtils.java) | 系统属性处理 | &nbsp; |
| &nbsp; | &nbsp; | [`ThreadUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/ThreadUtils.java) | 线程处理 | &nbsp; |
| &nbsp; | 日期/时间工具<br/>[`time`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/time) | [`DateUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/time/DateUtils.java) | 日期工具 | &nbsp; |
| &nbsp; | &nbsp; | [`TimeUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/time/TimeUtils.java) | 时间工具 | &nbsp; |
| &nbsp; | 校验工具<br/>[`verify`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/verify) | [`RegexUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/verify/RegexUtils.java) | 正则表达式处理 | &nbsp; |
| &nbsp; | &nbsp; | [`VerifyUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/verify/VerifyUtils.java) | 数据校验 | &nbsp; |
| &nbsp; | 其他工具<br/>[`other`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other) | [`AnnotationUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/AnnotationUtils.java) | 神ta喵注释生成器 | &nbsp; |
| &nbsp; | &nbsp; | [`BoolUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/BoolUtils.java) | 布尔值处理 | &nbsp; |
| &nbsp; | &nbsp; | [`JSUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/JSUtils.java) | JavaScript脚本处理 | &nbsp; |
| &nbsp; | &nbsp; | [`ListUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/ListUtils.java) | 队列/集合操作 | &nbsp; |
| &nbsp; | &nbsp; | [`LogUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/LogUtils.java) | 日志设置 | &nbsp; |
| &nbsp; | &nbsp; | [`ObjUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/ObjUtils.java) | 对象处理 | &nbsp; |
| &nbsp; | &nbsp; | [`PathUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/PathUtils.java) | 路径处理 | &nbsp; |
| &nbsp; | &nbsp; | [`RandomUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/RandomUtils.java) | 随机生成器 | &nbsp; |
| &nbsp; | &nbsp; | [`StrUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/StrUtils.java) | 字符串处理 | &nbsp; |
| **二次封装组件**<br/>[`exp.libs.warp`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp) | 版本管理组件<br/>[`ver`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ver) | &nbsp; | 借助Sqlite以UI方式管理项目版本信息，<br/>[`Maven项目发布插件`](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin/mojo-release-plugin)与[`自动化升级插件`](https://github.com/lyy289065406/auto-upgrader)<br/>的部分功能也依赖此组件实现 | &nbsp; |
| &nbsp; | 函数解析组件<br/>[`cep`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/cep) | &nbsp; | 基于com.singularsys.jep(3.3.1)封装，<br/>去除时效限制，并新增多种自定义函数 | &nbsp; |
| &nbsp; | 命令行组件<br/>[`cmd`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/cmd) | &nbsp; | 封装系统命令行操作 | &nbsp; |
| &nbsp; | 配置解析组件<br/>[`cmd`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf) | xml配置文件解析<br/>[`xml`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf/xml) | 基于org.dom4j(1.6.1)封装，支持定时<br/>刷新配置项、加载固有格式的配置区块。<br/>[`数据库组件`](#db)、[`网络组件`](#net)等均利用此组件<br/>加载独立的配置区块 | &nbsp; |
| &nbsp; | &nbsp; | ini配置文件解析<br/>[`ini`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf/ini) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | kv配置文件解析<br/>[`kv`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf/kv) | 未实装 | &nbsp; |
| &nbsp; | <p id="db">数据库组件</p>[`db`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/db) | 关系型数据库工具<br/>[`sql`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/db/sql) | 基于com.cloudhopper.proxool(0.9.1)<br/>封装，支持mysql/oracle/sqlite等。<br/>提供连接池与JDBC两种数据库连接方<br/>式、及多种常用的增删改查操作，且可<br/>根据物理表模型反向生成JavaBean代码 | &nbsp; |
| &nbsp; | &nbsp; | 非关系型数据库工具<br/>[`nosql`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/db/nosql) | 基于redis.clients.jedis(2.7.3)封装，<br/>支持连接池或常规方式获取连接实例 | &nbsp; |
| &nbsp; | <p id="net">网络组件</p>[`net`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net) | Cookie组件<br/>[`cookie`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/cookie) | 用于解析HTTP响应头中的Set-Cookie<br/>参数 | &nbsp; |
| &nbsp; | &nbsp; | FTP组件<br/>[`ftp`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/ftp) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | HTTP/HTTPS组件<br/>[`http`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/http) | 提供在HTTP/HTTPS协议下，以长/短<br/>连接实现的GET、POST方法，并支持<br/>Download、Gzip等非文本形式的响应<br/>流解析。<br/>基于java.net.HttpURLConnection与<br/>commons-httpclient(3.1-rc1)封装，<br/>更借助org.bouncycastle.bcprov<br/>-jdk15on(1.54)使得可以在JDK1.6+环<br/>境均支持HTTPS-TLSv1.2协议（[`详见`](#TLSv12)） | &nbsp; |
| &nbsp; | &nbsp; | Email组件<br/>[`mail`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/mail) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | MQ组件<br/>[`mq`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/mq) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | 端口转发器<br/>[`pf`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/pf) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | Ping组件<br/>[`ping`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/ping) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | Socket组件<br/>[`sock`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/sock) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | Telnet组件<br/>[`telnet`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/telnet) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | Webkit组件<br/>[`webkit`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/webkit) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | WebSocket客户端组件<br/>[`websock`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/websock) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | WebServices组件<br/>[`wsdl`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/wsdl) | 未实装 | &nbsp; |
| &nbsp; | IO组件<br/>[`io`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io) | 流式读取器<br/>[`flow`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io/flow) | &nbsp; | &nbsp; |
| &nbsp; | &nbsp; | 文件监听器<br/>[`listn`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io/listn) | &nbsp; | &nbsp; |
| &nbsp; | &nbsp; | 批量序列化读写器<br/>[`serial`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io/serial) | &nbsp; | &nbsp; |
| &nbsp; | OCR图像文字识别<br/>[`ocr`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ocr) | &nbsp; | 未实装 | &nbsp; |
| &nbsp; | 定时任务调度器<br/>[`task`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/task) | [`cron表达式在线生成器`](https://lyy289065406.github.io/cron-expression/) | 未实装 | &nbsp; |
| &nbsp; | 线程组件<br/>[`thread`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/thread) | &nbsp; | 未实装 | &nbsp; |
| &nbsp; | 模板文件组件<br/>[`tpl`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/tpl) | &nbsp; | 未实装 | &nbsp; |
| &nbsp; | Swing界面工具<br/>[`ui`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ui) | Swing组件<br/>[`cpt`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ui/cpt) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | 布局样式<br/>[`layout`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ui/layout) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | 拓扑图绘制器<br/>[`topo`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ui/topo) | 未实装 | &nbsp; |
| &nbsp; | Excel组件<br/>[`xls`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/xls) | &nbsp; | 未实装 | &nbsp; |
| **算法包**<br/>[`exp.libs.algorithm`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/algorithm) | &nbsp; | &nbsp; | &nbsp; | &nbsp; |


## 第三方构件修正记录

xxxxx


## 版权声明

　[![Copyright (C) 2016-2018 By EXP](https://img.shields.io/badge/Copyright%20(C)-2006~2018%20By%20EXP-blue.svg)](http://exp-blog.com)　[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
  

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------
