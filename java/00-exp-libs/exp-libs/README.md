# exp-libs
经验构件库 （[在线Javadoc](https://lyy289065406.github.io/api-online/javadoc/exp-libs/1.0/index.html)）


> *既然别人不甘造轮子，那我就来奠造基石*<br/>*他人会用，只是用。我要用，则随心所欲*

------


## 环境

![](https://img.shields.io/badge/Platform-Windows|Linux-brightgreen.svg) ![](https://img.shields.io/badge/IDE-Eclipse-brightgreen.svg) ![](https://img.shields.io/badge/Maven-3.2.5-brightgreen.svg) ![](https://img.shields.io/badge/JDK-1.6%2B-brightgreen.svg)

## 简介

此构件库为本人多年编程总结提炼而成，把常用的功能模块作为原子API进行封装；另外也借用了不少出色的第三方构件，在其之上进行二次封装。

过程中尽量确保了低耦合、高性能、强稳健、高复用、更易用等，确保能够满足日常开发需要、提高开发效率。


## 功能模块


| 主模块 | 子模块 | 组件包/类 | 说明 | 测试<br/>示例 |
|:---:|:---:|:---:|:---|:---:|
| **常用工具包**<br/>[`exp.libs.utils`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils) | 编码工具<br/>[`encode`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode) | [`Base64`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/Base64.java) | Base64编解码 | &nbsp; |
| &nbsp; | &nbsp; | [`CharsetUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CharsetUtils.java) | 字符集编码转换 | &nbsp; |
| &nbsp; | &nbsp; | [`CompressUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CompressUtils.java) | 压缩/解压 (zip,gzip,tar,bz2) | &nbsp; |
| &nbsp; | &nbsp; | [`CryptoUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/encode/CryptoUtils.java) | 加解密 (MD5,DES,RSA) | &nbsp; |
| &nbsp; | &nbsp; | [`TXTUtils`]() | 任意文件与txt文件互转 | &nbsp; |
| &nbsp; | 格式转换工具<br/>[`format`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format) | [`ESCUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/ESCUtils.java) | 数据格式转换 (转义字符,BCP,CSV,TSV) | &nbsp; |
| &nbsp; | &nbsp; | [`JsonUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/JsonUtils.java) | JSON数据处理 | &nbsp; |
| &nbsp; | &nbsp; | [`XmlUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/XmlUtils.java) | XML数据处理 | &nbsp; |
| &nbsp; | &nbsp; | [`StandardUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/format/StandardUtils.java) | 标准化处理 | &nbsp; |
| &nbsp; | 图像工具<br/>[`img`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/img) | [`ImageUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/img/ImageUtils.java) | 图像处理 | &nbsp; |
| &nbsp; | &nbsp; | [`QRCodeUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/img/QRCodeUtils.java) | 二维码生成/解析 | &nbsp; |
| &nbsp; | 读写工具<br/>[`io`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io) | [`FileUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io/FileUtils.java) | 磁盘文件处理 | &nbsp; |
| &nbsp; | &nbsp; | [`IOUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io/IOUtils.java) | IO流处理 | &nbsp; |
| &nbsp; | &nbsp; | [`JarUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/io/JarUtils.java) | Jar文件处理 | &nbsp; |
| &nbsp; | 数值工具<br/>[`num`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num) | [`BODHUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/BODHUtils.java) | 进制处理 | &nbsp; |
| &nbsp; | &nbsp; | [`IDUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/IDUtils.java) | 唯一性ID生成器 | &nbsp; |
| &nbsp; | &nbsp; | [`NumUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/NumUtils.java) | 数值处理 | &nbsp; |
| &nbsp; | &nbsp; | [`UnitUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/num/UnitUtils.java) | 单位转换 | &nbsp; |
| &nbsp; | 系统工具<br/>[`os`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os) | [`ExitUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/ExitUtils.java) | 程序终止控制 | &nbsp; |
| &nbsp; | &nbsp; | [`JavaUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/JavaUtils.java) | Java语言处理 | &nbsp; |
| &nbsp; | &nbsp; | [`OSUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/OSUtils.java) | 系统环境参数处理 | &nbsp; |
| &nbsp; | &nbsp; | [`ThreadUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/os/ThreadUtils.java) | 线程处理 | &nbsp; |
| &nbsp; | 日期/时间工具<br/>[`time`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/time) | [`DateUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/time/DateUtils.java) | 日期工具 | &nbsp; |
| &nbsp; | &nbsp; | [`TimeUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/time/TimeUtils.java) | 时间工具 | &nbsp; |
| &nbsp; | 校验工具<br/>[`verify`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/verify) | [`RegexUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/verify/RegexUtils.java) | 正则表达式处理 | &nbsp; |
| &nbsp; | &nbsp; | [`VerifyUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/verify/VerifyUtils.java) | 数据格式校验 | &nbsp; |
| &nbsp; | 其他工具<br/>[`other`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other) | [`AnnotationUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/AnnotationUtils.java) | 神ta喵注释生成器 | &nbsp; |
| &nbsp; | &nbsp; | [`BoolUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/BoolUtils.java) | 布尔值处理 | &nbsp; |
| &nbsp; | &nbsp; | [`JSUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/JSUtils.java) | JavaScript脚本处理 | &nbsp; |
| &nbsp; | &nbsp; | [`ListUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/ListUtils.java) | 队列/集合操作 | &nbsp; |
| &nbsp; | &nbsp; | [`LogUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/LogUtils.java) | 日志设置。<br/>基于`org.slf4j(1.7.5)`接口与<br/>`ch.qos.logback(1.0.13)`封装 | &nbsp; |
| &nbsp; | &nbsp; | [`ObjUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/ObjUtils.java) | 对象处理 | &nbsp; |
| &nbsp; | &nbsp; | [`PathUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/PathUtils.java) | 路径处理 | &nbsp; |
| &nbsp; | &nbsp; | [`RandomUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/RandomUtils.java) | 随机生成器 | &nbsp; |
| &nbsp; | &nbsp; | [`StrUtils`](https://github.com/lyy289065406/expcodes/blob/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/utils/other/StrUtils.java) | 字符串处理 | &nbsp; |
| **二次封装组件**<br/>[`exp.libs.warp`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp) | 版本管理组件<br/>[`ver`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ver) | &nbsp; | 借助Sqlite以UI方式管理项目版本信息，<br/>[`Maven项目发布插件`](https://github.com/lyy289065406/expcodes/tree/master/java/03-plugin/mojo-release-plugin)与[`自动化升级插件`](https://github.com/lyy289065406/auto-upgrader)<br/>的部分功能也依赖此组件实现 | &nbsp; |
| &nbsp; | 函数解析组件<br/>[`cep`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/cep) | &nbsp; | 基于`com.singularsys.jep(3.3.1)`封装，<br/>去除时效限制，并新增多种自定义函数 | &nbsp; |
| &nbsp; | 命令行组件<br/>[`cmd`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/cmd) | &nbsp; | 封装系统命令行操作 | &nbsp; |
| &nbsp; | 配置解析组件<br/>[`cmd`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf) | xml配置文件解析<br/>[`xml`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf/xml) | 基于`org.dom4j(1.6.1)`封装，支持定时<br/>刷新配置项、加载固有格式的配置区块。<br/>[`数据库组件`](#db)、[`网络组件`](#net)等均利用此组件<br/>加载独立的配置区块 | &nbsp; |
| &nbsp; | &nbsp; | ini配置文件解析<br/>[`ini`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf/ini) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | kv配置文件解析<br/>[`kv`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/conf/kv) | 未实装 | &nbsp; |
| &nbsp; | <p id="db">数据库组件</p>[`db`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/db) | 关系型数据库工具<br/>[`sql`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/db/sql) | 基于`com.cloudhopper.proxool(0.9.1)`<br/>封装，支持mysql/oracle/sqlite等。<br/>提供连接池与JDBC两种数据库连接方<br/>式、及多种常用的增删改查操作，且可<br/>根据物理表模型反向生成JavaBean代码 | &nbsp; |
| &nbsp; | &nbsp; | 非关系型数据库工具<br/>[`nosql`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/db/nosql) | 基于`redis.clients.jedis(2.7.3)`封装，<br/>支持连接池或常规方式获取连接实例 | &nbsp; |
| &nbsp; | <p id="net">网络组件</p>[`net`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net) | Cookie组件<br/>[`cookie`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/cookie) | 用于解析HTTP响应头中的Set-Cookie参数 | &nbsp; |
| &nbsp; | &nbsp; | FTP组件<br/>[`ftp`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/ftp) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | HTTP/HTTPS组件<br/>[`http`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/http) | 提供在HTTP/HTTPS协议下，以长/短<br/>连接实现的GET、POST、Download<br/>方法，并支持自动解析Gzip流。<br/>基于`java.net.HttpURLConnection`与<br/>`commons-httpclient(3.1-rc1)`封装，<br/>更借助`org.bouncycastle.bcprov`<br/>`-jdk15on(1.54)`使得可以在JDK1.6+环<br/>境均支持HTTPS-TLSv1.2协议[`(详见)`](#TLSv12) | &nbsp; |
| &nbsp; | &nbsp; | Email组件<br/>[`mail`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/mail) | 基于`javax.mail(1.4.1)`封装，<br/>可用于邮件发送/抄送（支持加密） | &nbsp; |
| &nbsp; | &nbsp; | MQ组件<br/>[`mq`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/mq) | 未实装（jms/kafka） | &nbsp; |
| &nbsp; | &nbsp; | 端口转发器<br/>[`pf`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/pf) | 利用Socket实现的端口转发程序 | &nbsp; |
| &nbsp; | &nbsp; | Ping组件<br/>[`ping`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/ping) | 利用系统命令实现的ping/tracert，支持<br/>解析中/英文的win/linux系统的结果集 | &nbsp; |
| &nbsp; | &nbsp; | Socket组件<br/>[`sock`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/sock) | 封装IO/NIO模式的Socket客户端/服务<br/>端的交互行为，只需实现业务逻辑 | &nbsp; |
| &nbsp; | &nbsp; | Telnet组件<br/>[`telnet`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/telnet) | 未实装 | &nbsp; |
| &nbsp; | &nbsp; | Webkit组件<br/>[`webkit`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/webkit) | 基于`org.seleniumhq.selenium(2.53.0)`<br/>封装，主要提供无头浏览器<br/>`com.codeborne.phantomjsdriver(1.2.1)`<br/>的常用操作 | &nbsp; |
| &nbsp; | &nbsp; | WebSocket客户端<br/>[`websock`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/websock) | 基于`org.java-websocket(1.3.4)`封装，<br/>支持ws与wss，提供数据帧的收发接口 | &nbsp; |
| &nbsp; | &nbsp; | WebServices组件<br/>[`wsdl`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/net/wsdl) | 基于`net.webservices.client(1.6.2)`封<br/>装，支持http/axis2/cxf，支持SSL模式 | &nbsp; |
| &nbsp; | IO组件<br/>[`io`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io) | 流式读取器<br/>[`flow`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io/flow) | 流式读取超大文件/字符串 | &nbsp; |
| &nbsp; | &nbsp; | 文件监听器<br/>[`listn`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io/listn) | 可监听并触发指定目录树下所有<br/>文件/文件夹的增删改事件 | &nbsp; |
| &nbsp; | &nbsp; | 批量序列化读写器<br/>[`serial`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/io/serial) | 批量序列化/反序列任意实现了<br/>`java.io.Serializable`接口的对象 | &nbsp; |
| &nbsp; | OCR图文识别<br/>[`ocr`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ocr) | &nbsp; | 基于`com.sun.media.jai-imageio`<br/>`(1.1-alpha)`封装。<br/>可识别图片中打印体的文字<br/>（文字不能旋转、变形，越正规的<br/>文字识别率越高） | &nbsp; |
| &nbsp; | 定时任务调度<br/>[`task`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/task) | [`在线生成cron表达式`](https://lyy289065406.github.io/cron-expression/) | 基于`org.quartz-scheduler(2.2.1)`<br/>封装。仅保留了simple与cron调度器，<br/>并提供cron表达式换算对象 | &nbsp; |
| &nbsp; | 线程组件<br/>[`thread`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/thread) | &nbsp; | 提供抽象循环线程、回调线程池组件 | &nbsp; |
| &nbsp; | 模板文件组件<br/>[`tpl`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/tpl) | &nbsp; | 可定制含占位符的内容模板文件 | &nbsp; |
| &nbsp; | Swing界面工具<br/>[`ui`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ui) | Swing组件<br/>[`cpt`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ui/cpt) | 部分功能基于`org.jb.beauty-eye(3.7)`<br/>封装。可美化Swing外观，提供主窗口、<br/>浮动窗口、面板、表单、选框、系统托盘、<br/>布局样式等常用组件 | &nbsp; |
| &nbsp; | &nbsp; | 拓扑图绘制器<br/>[`topo`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/ui/topo) | 基于`org.eclipse.draw2d(1.0.0)`<br/>与`org.jgraph.jGraph(1.0.0)`封装。<br/>通过输入邻接矩阵（可含源宿点/必经<br/>点）自动根据边权换算边距，同时映射到<br/>极坐标系，绘制对应的拓扑图 | &nbsp; |
| &nbsp; | Excel组件<br/>[`xls`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/warp/xls) | &nbsp; | 基于`org.apache.poi.poi-ooxml(3.9)`<br/>封装。支持对xls/xlsx文件操作 | &nbsp; |
| **算法包**<br/>[`exp.libs.algorithm`](https://github.com/lyy289065406/expcodes/tree/master/java/00-exp-libs/exp-libs/trunk/src/main/java/exp/libs/algorithm) | &nbsp; | &nbsp; | &nbsp; | &nbsp; |


## 第三方构件修正记录

xxxxx


## 版权声明

　[![Copyright (C) 2016-2018 By EXP](https://img.shields.io/badge/Copyright%20(C)-2006~2018%20By%20EXP-blue.svg)](http://exp-blog.com)　[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
  

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------
