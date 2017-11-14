/*****************************************
  Description: 数字处理工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#pragma once;

#ifndef __UTILS_NUM_H_
#define __UTILS_NUM_H_

	#include "stdafx.h"

	namespace NUM_UTILS {

		/*
		 * 把数字字符串转换成long long数值
		 * @param str 数字字符串
		 * @return long long 数值
		 */
		DLL_API const long long toLongLong(const char* str);

		/*
		 * 把long long数值转换成数字字符串
		 * @param _long long long 数值
		 * @return 数字字符串
		 */
		DLL_API const char* toStr(const long long _long);

	}

#endif
