/*****************************************
  Description: 日志工具包
  Authod     : EXP
  Modify By  : None
  Date       : 2017-11-14
******************************************/

#pragma once;

#ifndef __UTILS_LOG_H_
#define __UTILS_LOG_H_

	#include "stdafx.h"

	namespace LOG_UTILS {

		DLL_API bool log(const char* msg, bool isAppend);

		DLL_API bool log(const char* logFilePath, const char* msg, bool isAppend);

	}

#endif