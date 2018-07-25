/*****************************************
  Description: 定义 DLL 应用程序的导出函数
  Authod     : EXP | http://exp-blog.com
  Modify By  : None
  Date       : 2015-07-20
******************************************/

// 下列 ifdef 块是创建使从 DLL 导出更简单的
// 宏的标准方法。此 DLL 中的所有文件都是用命令行上定义的 DT_OTP_EXPORTS
// 符号编译的。在使用此 DLL 的
// 任何其他项目上不应定义此符号。这样，源文件中包含此文件的任何其他项目都会将
// DT_OTP_API 函数视为是从 DLL 导入的，而此 DLL 则将用此宏定义的
// 符号视为是被导出的。
#ifdef DT_OTP_EXPORTS
#define DT_OTP_API __declspec(dllexport)
#else
#define DT_OTP_API __declspec(dllimport)
#endif

// 导入java接口定义的文件
#include "exp_token_otp__OTP_CAPI.h"
