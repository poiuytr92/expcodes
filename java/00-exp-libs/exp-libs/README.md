# exp-libs
经验构件库 （[在线Javadoc](https://lyy289065406.github.io/api-online/javadoc/exp-libs/1.0/index.html)）


------


## 环境

![](https://img.shields.io/badge/Platform-Windows|Linux-brightgreen.svg) ![](https://img.shields.io/badge/IDE-Eclipse-brightgreen.svg) ![](https://img.shields.io/badge/Maven-3.2.5-brightgreen.svg) ![](https://img.shields.io/badge/JDK-1.6%2B-brightgreen.svg)

## 简介

此构件库为本人多年编程总结提炼而成，把常用的功能模块作为原子API进行封装；另外也借用了不少出色的第三方构件，在其之上进行二次封装。

过程中尽量确保了低耦合、高性能、强稳健、高复用、更易用等，确保能够满足日常开发需要、提高开发效率。


## 子模块

根据功能分类，此构件库主要可划分为3个大模块，而每个模块又会延展出多个组件分支：
- 常用工具模块：exp.libs.utils
- 二次封装模块：exp.libs.warp
- 算法模块：exp.libs.algorithm

```flow 
st=>start: 开始 
e=>end: 登录 
io1=>inputoutput: 输入用户名密码 
sub1=>subroutine: 数据库查询子类 
cond=>condition: 是否有此用户 
cond2=>condition: 密码是否正确 
op=>operation: 读入用户信息

st->io1->sub1->cond 
cond(yes,right)->cond2 
cond(no)->io1(right) 
cond2(yes,right)->op->e 
cond2(no)->io1 
```


## 版权声明

　[![Copyright (C) 2016-2018 By EXP](https://img.shields.io/badge/Copyright%20(C)-2006~2018%20By%20EXP-blue.svg)](http://exp-blog.com)　[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
  

- Site: [http://exp-blog.com](http://exp-blog.com) 
- Mail: <a href="mailto:289065406@qq.com?subject=[EXP's Github]%20Your%20Question%20（请写下您的疑问）&amp;body=What%20can%20I%20help%20you?%20（需要我提供什么帮助吗？）">289065406@qq.com</a>


------
