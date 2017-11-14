/*****************************************
  Description: 日期时间工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#pragma once;  

#ifndef __UTILS_TIME_H_
#define __UTILS_TIME_H_

	#include "stdafx.h"

	namespace TIME_UTILS {
		
		/*
		 * 获取1970年至今UTC的毫秒时间值
		 * （与java的当前系统时间函数System.currentTimeMillis()等价的时间值）
		 * @return 当前系统时间
		 */
		DLL_API const long long getCurrentTimeMillis();

	}

#endif