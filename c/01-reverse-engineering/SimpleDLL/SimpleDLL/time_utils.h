/*****************************************
  Description: 日期时间工具包
  Authod     : liaoquanbin
  Modify By  : None
  Date       : 2015-07-20
******************************************/

#pragma once;  

#ifndef __TIME_UTILS_H_
#define __TIME_UTILS_H_

	#include "stdafx.h"

	namespace TIME_UTILS {
		
		/*
		 * 获取1970年至今UTC的微秒时间值
		 * （与java的当前系统时间函数System.currentTimeMillis()等价的时间值）
		 * @return 当前系统时间
		 */
		DLL_API const long long getCurrentTimeMillis();

	}

#endif