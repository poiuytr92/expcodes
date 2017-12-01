// stdafx.h : 标准系统包含文件的包含文件，
// 或是经常使用但不常更改的
// 特定于项目的包含文件
//

#pragma once

#include "targetver.h"

#include <stdio.h>
#include <tchar.h>



// TODO: 在此处引用程序需要的其他头文件


// 宏DLL_IMPLEMENT在*.cpp中定义  
#ifdef DLL_IMPLEMENT  
#define DLL_API __declspec(dllexport)  // 在dll项目内部使用时则导出 
#else  
#define DLL_API __declspec(dllimport)  // 在dll项目外部使用时则导入
#endif
