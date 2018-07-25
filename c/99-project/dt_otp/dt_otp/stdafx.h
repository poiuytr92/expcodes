// stdafx.h : 标准系统包含文件的包含文件，
// 或是经常使用但不常更改的
// 特定于项目的包含文件
//

#pragma once

#include "targetver.h"

#define WIN32_LEAN_AND_MEAN             // 从 Windows 头中排除极少使用的资料

// Windows 头文件:
//#ifdef _WIN32
//	#include <windows.h>	// 目前仅获取系统时间需要该库,，考虑跨平台兼容，尽量不用该库
//#endif

// TODO: 在此处引用程序需要的其他头文件

// 常用库文件
#include <stdio.h>
#include <stdlib.h>
#include <exception>